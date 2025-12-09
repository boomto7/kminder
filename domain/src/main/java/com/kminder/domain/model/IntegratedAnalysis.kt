package com.kminder.domain.model

/**
 * 통합 감정 분석 결과 (2/3차 분석 데이터)
 * 여러 개의 일기 데이터를 종합하여 분석한 결과입니다.
 */
data class IntegratedAnalysis(
    val recentEmotions: Map<EmotionType, Float>,
    val complexEmotionString: String, // 2차 복합 감정 (e.g., "낙관", "사랑")
    val keywords: List<String>,      // 3차 분석 키워드
    val summary: String,             // 3차 분석 요약 (인사이트)
    val suggestedAction: String,      // 3차 조언
    val complexEmotionType: ComplexEmotionType? = null, // 복합 감정 타입 (enum)
    val detailedEmotionType: DetailedEmotionType? = null // 단일/주 감정 상세 타입 (enum)
)
