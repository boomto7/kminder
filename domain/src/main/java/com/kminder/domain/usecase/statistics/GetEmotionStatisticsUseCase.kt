package com.kminder.domain.usecase.statistics

import com.kminder.domain.model.ChartPeriod
import com.kminder.domain.model.EmotionStatistics
import com.kminder.domain.repository.JournalRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * 감정 통계를 조회하는 UseCase
 */
class GetEmotionStatisticsUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * 특정 기간의 감정 통계를 조회합니다.
     * 
     * @param period 조회 기간 (일/주/월)
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 감정 통계 목록
     */
    suspend operator fun invoke(
        period: ChartPeriod,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<EmotionStatistics> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        
        val entries = journalRepository.getEntriesByDateRange(startDateTime, endDateTime)
        
        // Group by Date
        val groupedEntries = entries.groupBy { it.createdAt.toLocalDate() }
        
        val result = mutableListOf<EmotionStatistics>()
        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            val dailyEntries = groupedEntries[currentDate] ?: emptyList()
            // Filter entries that have analysis if strictly required? 
            // The requirement says "List<EmotionAnalysis>" previously, so implies analyzed ones.
            // But JournalEntry list is more flexible. Let's keep all, UI can filter.
            result.add(EmotionStatistics(currentDate, dailyEntries))
            currentDate = currentDate.plusDays(1)
        }
        
        return result
    }
}
