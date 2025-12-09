package com.kminder.minder.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.IntegratedAnalysis
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.analysis.AnalyzeIntegratedEmotionUseCase
import com.kminder.domain.usecase.journal.GetAllJournalEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 홈 화면 ViewModel
 * 최근 5개의 일기를 보여주고, 통합 감정 분석을 수행합니다.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllJournalEntriesUseCase: GetAllJournalEntriesUseCase,
    private val analyzeIntegratedEmotionUseCase: AnalyzeIntegratedEmotionUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // Dominant Mood Color State (Used for background animation)
    private val _moodColor = MutableStateFlow<androidx.compose.ui.graphics.Color>(com.kminder.minder.ui.theme.MinderBackground)
    val moodColor: StateFlow<androidx.compose.ui.graphics.Color> = _moodColor.asStateFlow()

    init {
        loadRecentEntries()
    }
    
    /**
     * 최근 일기 목록 로드 및 통합 분석
     */
    fun loadRecentEntries() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            // 목업 데이터에서 최근 5개만 가져옴
            val allMockEntries = com.kminder.minder.data.mock.MockData.mockJournalEntries
            val recentEntries = allMockEntries.take(5)

            if (recentEntries.isEmpty()) {
                _uiState.value = HomeUiState.Empty
            } else {
                // 1. 통합 감정 분석 실행 (2/3차 분석)
                val analysisResult = analyzeIntegratedEmotionUseCase(recentEntries)
                
                // 2. 배경 분위기 색상 결정 (Dominant Emotion 기반)
                updateMoodColor(analysisResult.recentEmotions)

                _uiState.value = HomeUiState.Success(
                    recentEntries = recentEntries,
                    analysis = analysisResult
                )
            }
        }
    }

    private fun updateMoodColor(emotions: Map<com.kminder.domain.model.EmotionType, Float>) {
        val dominantEmotion = emotions.maxByOrNull { it.value }?.key
        
        val color = when (dominantEmotion) {
            com.kminder.domain.model.EmotionType.ANGER -> com.kminder.minder.ui.theme.EmotionAnger
            com.kminder.domain.model.EmotionType.ANTICIPATION -> com.kminder.minder.ui.theme.EmotionAnticipation
            com.kminder.domain.model.EmotionType.JOY -> com.kminder.minder.ui.theme.EmotionJoy
            com.kminder.domain.model.EmotionType.TRUST -> com.kminder.minder.ui.theme.EmotionTrust
            com.kminder.domain.model.EmotionType.FEAR -> com.kminder.minder.ui.theme.EmotionFear
            com.kminder.domain.model.EmotionType.SADNESS -> com.kminder.minder.ui.theme.EmotionSadness
            com.kminder.domain.model.EmotionType.DISGUST -> com.kminder.minder.ui.theme.EmotionDisgust
            com.kminder.domain.model.EmotionType.SURPRISE -> com.kminder.minder.ui.theme.EmotionSurprise
            null -> com.kminder.minder.ui.theme.MinderBackground
        }
        _moodColor.value = color
    }
}

/**
 * 홈 화면 UI 상태
 */
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(
        val recentEntries: List<JournalEntry>,
        val analysis: IntegratedAnalysis? = null
    ) : HomeUiState
}
