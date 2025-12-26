package com.kminder.domain.usecase.statistics

import com.kminder.domain.model.EmotionKeyword
import com.kminder.domain.repository.JournalRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 특정 기간 동안의 모든 감정 키워드를 수집하는 UseCase
 */
class GetKeywordNetworkDataUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    suspend operator fun invoke(startDate: LocalDateTime, endDate: LocalDateTime): List<EmotionKeyword> {
        // 1. 기간 내 모든 일기 조회
        val entries = journalRepository.getEntriesByDateRange(startDate, endDate)
        
        // 2. 일기에서 감정 분석 결과가 있는 경우 키워드 추출 및 통합
        return entries.mapNotNull { it.emotionAnalysis }
            .flatMap { it.keywords }
            // 옵션: 점수가 높은 순으로 정렬하거나, 중복 키워드를 합칠 수도 있음
            // 여기서는 원본 데이터를 그대로 반환하고, View에서 그룹화하도록 함.
            // 하지만 Network Chart의 특성상 동일 단어가 여러 번 나오면 합치는 게 좋음 (점수 합산 or 평균)
    }
}
