package com.kminder.domain.model

/**
 * 감정 키워드 상세 정보
 *
 * @property word 키워드 (예: "망친 시험", "비", "치킨")
 * @property emotion 연관된 감정 종류
 * @property score 감정 영향력 점수 (0.0 ~ 1.0)
 */
data class EmotionKeyword(
    val word: String,
    val emotion: EmotionType,
    val score: Float
)
