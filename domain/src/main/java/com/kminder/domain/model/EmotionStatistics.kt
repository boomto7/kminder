package com.kminder.domain.model

import java.time.LocalDate

/**
 * 감정 통계 데이터
 * 
 * @property date 날짜
 * @property emotionAnalysis 해당 날짜의 평균 감정 분석 결과
 * @property entryCount 해당 날짜의 일기 개수
 */
data class EmotionStatistics(
    val date: LocalDate,
    val entries: List<JournalEntry>
) {
    val entryCount: Int
        get() = entries.size
}
