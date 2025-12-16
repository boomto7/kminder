package com.kminder.domain.model

/**
 * 플루치크 감정 휠에 기반한 상세 감정 타입
 * 각 기본 감정(8가지)에 대해 강도별(강함, 중간, 약함)로 세분화된 감정을 정의합니다.
 *
 * @property baseType 기본 감정 타입 (8가지 원형)
 * @property intensityLevel 강도 레벨 (3: 강함, 2: 중간, 1: 약함)
 */
enum class DetailedEmotionType(
    val baseType: EmotionType,
    val intensityLevel: Int
) {
    // === JOY (기쁨) 계열 ===
    ECSTASY(EmotionType.JOY, 3),
    JOY(EmotionType.JOY, 2),
    SERENITY(EmotionType.JOY, 1),

    // === TRUST (신뢰) 계열 ===
    ADMIRATION(EmotionType.TRUST, 3),
    TRUST(EmotionType.TRUST, 2),
    ACCEPTANCE(EmotionType.TRUST, 1), 

    // === FEAR (두려움) 계열 ===
    TERROR(EmotionType.FEAR, 3),
    FEAR(EmotionType.FEAR, 2),
    APPREHENSION(EmotionType.FEAR, 1),

    // === SURPRISE (놀람) 계열 ===
    AMAZEMENT(EmotionType.SURPRISE, 3),
    SURPRISE(EmotionType.SURPRISE, 2),
    DISTRACTION(EmotionType.SURPRISE, 1),

    // === SADNESS (슬픔) 계열 ===
    GRIEF(EmotionType.SADNESS, 3),
    SADNESS(EmotionType.SADNESS, 2),
    PENSIVENESS(EmotionType.SADNESS, 1), 

    // === DISGUST (혐오) 계열 ===
    LOATHING(EmotionType.DISGUST, 3),
    DISGUST(EmotionType.DISGUST, 2),
    BOREDOM(EmotionType.DISGUST, 1),

    // === ANGER (분노) 계열 ===
    RAGE(EmotionType.ANGER, 3),
    ANGER(EmotionType.ANGER, 2),
    ANNOYANCE(EmotionType.ANGER, 1),

    // === ANTICIPATION (기대) 계열 ===
    VIGILANCE(EmotionType.ANTICIPATION, 3),
    ANTICIPATION(EmotionType.ANTICIPATION, 2),
    INTEREST(EmotionType.ANTICIPATION, 1);

    companion object {
        /**
         * 기본 감정과 강도(0.0~1.0)를 바탕으로 상세 감정을 반환합니다.
         */
        fun from(base: EmotionType, intensity: Float): DetailedEmotionType {
            val level = when {
                intensity >= 0.7f -> 3 // 강함
                intensity >= 0.4f -> 2 // 중간
                else -> 1              // 약함
            }
            
            return values().find { it.baseType == base && it.intensityLevel == level }
                ?: values().find { it.baseType == base && it.intensityLevel == 2 }!! // Fallback to medium
        }
    }
}
