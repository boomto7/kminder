package com.kminder.domain.model

import java.time.LocalDateTime

/**
 * 일기 항목 엔티티
 * 
 * @property id 고유 ID
 * @property content 일기 내용
 * @property entryType 작성 모드 (자유 작성 / 문답)
 * @property question 문답 모드일 경우 질문 내용
 * @property createdAt 생성 일시
 * @property updatedAt 수정 일시
 * @property emotionAnalysis 감정 분석 결과 (nullable, 분석 전에는 null)
 */
data class JournalEntry(
    val id: Long = 0,
    val content: String,
    val entryType: EntryType,
    val question: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val emotionAnalysis: EmotionAnalysis? = null,
    val status: AnalysisStatus = AnalysisStatus.NONE
) {
    /**
     * 감정 분석이 완료되었는지 확인합니다.
     */
    fun hasEmotionAnalysis(): Boolean = emotionAnalysis != null
    
    /**
     * 문답 모드인지 확인합니다.
     */
    fun isQnAMode(): Boolean = entryType == EntryType.QNA
}
