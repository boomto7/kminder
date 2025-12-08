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
        require(startDate <= endDate) { "시작 날짜는 종료 날짜보다 이전이어야 합니다." }
        
        return journalRepository.getEmotionStatistics(period, startDate, endDate)
    }
}
