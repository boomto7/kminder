package com.kminder.minder.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.ChartPeriod
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionKeyword
import com.kminder.domain.model.EmotionStatistics
import com.kminder.domain.usecase.statistics.GetEmotionStatisticsUseCase
import com.kminder.domain.usecase.statistics.GetKeywordNetworkDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val selectedPeriod: ChartPeriod = ChartPeriod.WEEKLY, // WEEK or MONTH
    val anchorDate: LocalDate = LocalDate.now(), // 기준 날짜 (오늘이 포함된 주/월)
    val statistics: List<EmotionStatistics> = emptyList(), // 기간 내 일별 데이터를 일차적으로 저장
    val keywords: List<EmotionKeyword> = emptyList(),
    val aggregatedAnalysis: EmotionAnalysis = EmotionAnalysis(), // 도넛 차트용 합계
    val totalEntryCount: Int = 0
) {
    val periodLabel: String
        get() {
            return when (selectedPeriod) {
                ChartPeriod.DAILY -> "${anchorDate.monthValue}월 ${anchorDate.dayOfMonth}일"
                ChartPeriod.WEEKLY -> {
                    // X월 W주차
                    val weekFields = WeekFields.of(Locale.getDefault())
                    val weekOfMonth = anchorDate.get(weekFields.weekOfMonth())
                    "${anchorDate.monthValue}월 ${weekOfMonth}주차"
                }
                ChartPeriod.MONTHLY -> {
                    "${anchorDate.year}년 ${anchorDate.monthValue}월"
                }
                else -> ""
            }
        }
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getEmotionStatisticsUseCase: GetEmotionStatisticsUseCase,
    private val getKeywordNetworkDataUseCase: GetKeywordNetworkDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun setPeriod(period: ChartPeriod) {
        _uiState.update { it.copy(selectedPeriod = period, anchorDate = LocalDate.now()) }
        loadStatistics()
    }

    fun moveDate(offset: Int) {
        _uiState.update {
            val newDate = when (it.selectedPeriod) {
                ChartPeriod.DAILY -> it.anchorDate.plusDays(offset.toLong())
                ChartPeriod.WEEKLY -> it.anchorDate.plusWeeks(offset.toLong())
                ChartPeriod.MONTHLY -> it.anchorDate.plusMonths(offset.toLong())
                else -> it.anchorDate.plusDays(offset.toLong())
            }
            it.copy(anchorDate = newDate)
        }
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val currentState = _uiState.value
                val (startDate, endDate) = calculateDateRange(currentState.selectedPeriod, currentState.anchorDate)

                // 1. 통계 데이터 조회
                val stats = getEmotionStatisticsUseCase(currentState.selectedPeriod, startDate, endDate)
                
                // 2. 키워드 데이터 조회
                val keywords = getKeywordNetworkDataUseCase(startDate.atStartOfDay(), endDate.atTime(23, 59, 59))

                // 3. 통계 데이터 집계
                val allEntries = stats.flatMap { it.entries }
                val totalEntries = allEntries.size
                
                val analyzedEntries = allEntries.filter { it.hasEmotionAnalysis() && it.emotionResult != null }
                val analyzedCount = analyzedEntries.size

                val aggregatedAnalysis = if (analyzedCount > 0) {
                     var sumJoy = 0f
                     var sumTrust = 0f
                     var sumFear = 0f
                     var sumSurprise = 0f
                     var sumSadness = 0f
                     var sumDisgust = 0f
                     var sumAnger = 0f
                     var sumAnticipation = 0f
                     
                     analyzedEntries.forEach { entry ->
                         val analysis = entry.emotionResult!!.source
                         sumJoy += analysis.joy
                         sumTrust += analysis.trust
                         sumFear += analysis.fear
                         sumSurprise += analysis.surprise
                         sumSadness += analysis.sadness
                         sumDisgust += analysis.disgust
                         sumAnger += analysis.anger
                         sumAnticipation += analysis.anticipation
                     }
                     
                     val count = analyzedCount.toFloat()
                     EmotionAnalysis(
                         joy = sumJoy / count,
                         trust = sumTrust / count,
                         fear = sumFear / count,
                         surprise = sumSurprise / count,
                         sadness = sumSadness / count,
                         disgust = sumDisgust / count,
                         anger = sumAnger / count,
                         anticipation = sumAnticipation / count
                     )
                } else {
                    EmotionAnalysis()
                }

                // 4. 키워드 머지 로직 (기존 유지)
                val mergedKeywords = keywords.groupBy { it.word }
                    .map { (word, list) ->
                        val dominantEmotion = list.groupingBy { it.emotion }
                            .eachCount()
                            .maxByOrNull { it.value }?.key ?: list.first().emotion
                        // 점수는 합산 (빈도 반영)
                        val totalScore = list.sumOf { it.score.toDouble() }.toFloat()
                        EmotionKeyword(word, dominantEmotion, totalScore)
                    }
                    .sortedByDescending { it.score }
                    .take(20)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        statistics = stats,
                        keywords = mergedKeywords,
                        aggregatedAnalysis = aggregatedAnalysis,
                        totalEntryCount = totalEntries
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun calculateDateRange(period: ChartPeriod, anchor: LocalDate): Pair<LocalDate, LocalDate> {
        return when (period) {
            ChartPeriod.DAILY -> anchor to anchor
            ChartPeriod.WEEKLY -> {
                val fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek()
                val startDate = anchor.with(fieldISO, 1) // 월요일
                val endDate = anchor.with(fieldISO, 7)   // 일요일
                startDate to endDate
            }
            ChartPeriod.MONTHLY -> {
                val startDate = anchor.with(TemporalAdjusters.firstDayOfMonth())
                val endDate = anchor.with(TemporalAdjusters.lastDayOfMonth())
                startDate to endDate
            }
            else -> anchor to anchor // Fallback
        }
    }
}
