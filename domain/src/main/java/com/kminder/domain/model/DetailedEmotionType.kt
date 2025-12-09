package com.kminder.domain.model

/**
 * 플루치크 감정 휠에 기반한 상세 감정 타입
 * 각 기본 감정(8가지)에 대해 강도별(강함, 중간, 약함)로 세분화된 감정을 정의합니다.
 *
 * @property baseType 기본 감정 타입 (8가지 원형)
 * @property intensityLevel 강도 레벨 (3: 강함, 2: 중간, 1: 약함)
 * @property koreanName 한글명
 */
enum class DetailedEmotionType(
    val baseType: EmotionType,
    val intensityLevel: Int,
    val koreanName: String
) {
    // === JOY (기쁨) 계열 ===
    ECSTASY(EmotionType.JOY, 3, "황홀"),
    JOY(EmotionType.JOY, 2, "기쁨"),
    SERENITY(EmotionType.JOY, 1, "평온"),

    // === TRUST (신뢰) 계열 ===
    ADMIRATION(EmotionType.TRUST, 3, "존경"),
    TRUST(EmotionType.TRUST, 2, "신뢰"),
    ACCEPTANCE(EmotionType.TRUST, 1, "용인"), // 혹은 수용

    // === FEAR (두려움) 계열 ===
    TERROR(EmotionType.FEAR, 3, "공포"),
    FEAR(EmotionType.FEAR, 2, "두려움"),
    APPREHENSION(EmotionType.FEAR, 1, "우려"), // 혹은 근심

    // === SURPRISE (놀람) 계열 ===
    AMAZEMENT(EmotionType.SURPRISE, 3, "경탄"),
    SURPRISE(EmotionType.SURPRISE, 2, "놀람"),
    DISTRACTION(EmotionType.SURPRISE, 1, "산만"),

    // === SADNESS (슬픔) 계열 ===
    GRIEF(EmotionType.SADNESS, 3, "비탄"),
    SADNESS(EmotionType.SADNESS, 2, "슬픔"),
    PENSIVENESS(EmotionType.SADNESS, 1, "수심"), // 혹은 우수

    // === DISGUST (혐오) 계열 ===
    LOATHING(EmotionType.DISGUST, 3, "증오"),
    DISGUST(EmotionType.DISGUST, 2, "혐오"),
    BOREDOM(EmotionType.DISGUST, 1, "지루함"),

    // === ANGER (분노) 계열 ===
    RAGE(EmotionType.ANGER, 3, "격분"),
    ANGER(EmotionType.ANGER, 2, "분노"),
    ANNOYANCE(EmotionType.ANGER, 1, "짜증"),

    // === ANTICIPATION (기대) 계열 ===
    VIGILANCE(EmotionType.ANTICIPATION, 3, "경계"),
    ANTICIPATION(EmotionType.ANTICIPATION, 2, "기대"),
    INTEREST(EmotionType.ANTICIPATION, 1, "관심");

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
