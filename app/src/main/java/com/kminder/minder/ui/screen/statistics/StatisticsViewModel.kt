package com.kminder.minder.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.EmotionKeyword
import com.kminder.domain.usecase.statistics.GetKeywordNetworkDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val keywords: List<EmotionKeyword> = emptyList(),
    val periodLabel: String = "이번 달"
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getKeywordNetworkDataUseCase: GetKeywordNetworkDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 테스트용: 최근 30일 데이터 조회
                val endDate = LocalDateTime.now()
                val startDate = endDate.minus(30, ChronoUnit.DAYS)
                
                val keywords = getKeywordNetworkDataUseCase(startDate, endDate)
                
                // 중복 단어 처리: 동일 단어는 Score를 합산하거나 평균을 냄 (NetworkChart 시각화를 위해)
                // 여기서는 단순히 합산 후 상위 20개만 추출하는 로직 예시
                val mergedKeywords = keywords.groupBy { it.word }
                    .map { (word, list) ->
                        // 가장 빈도가 높은 감정을 대표 감정으로 선정
                        val dominantEmotion = list.groupingBy { it.emotion }
                            .eachCount()
                            .maxByOrNull { it.value }?.key ?: list.first().emotion
                            
                        // 점수는 평균 or 합산. 여기서는 평균 * 빈도수(=합산) 가중치
                        val avgScore = list.map { it.score }.average().toFloat()
                        // 빈도수가 많을수록, 점수가 높을수록 큼 -> 합산 점수
                        val totalScore = list.sumOf { it.score.toDouble() }.toFloat()
                        
                        EmotionKeyword(word, dominantEmotion, totalScore)
                    }
                    .sortedByDescending { it.score }
                    .take(20) // 상위 20개만 표시

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        keywords = mergedKeywords
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                // 에러 처리 필요
            }
        }
    }
}
