package com.kminder.domain.model

/**
 * 최종 감정 분석 결과 (가공된 데이터)
 * 
 * @property label 사용자에게 보여줄 최종 감정 이름 (예: "낙관", "기쁨과 슬픔의 충돌")
 * @property description 설명
 * @property primaryEmotion 주요 감정
 * @property secondaryEmotion 부가 감정 (있을 경우)
 * @property score 해당 조합의 점수 (평균)
 * @property category 분류 (단일, 2차 이중감정, 상반 감정 등)
 * @property complexEmotionType 매칭된 복합 감정 타입
 * @property source 결과 도출에 사용된 원본 데이터 (8가지 기본 감정 강도 및 키워드)
 */
data class EmotionResult(
    val primaryEmotion: EmotionType,
    val secondaryEmotion: EmotionType?,
    val score: Float,
    val category: ComplexEmotionType.Category,
    val complexEmotionType: ComplexEmotionType? = null,
    val source: EmotionAnalysis
)
