package com.kminder.domain.model

/**
 * 플루치크 이론에 기반한 모든 감정 (단일 감정 24개 + 복합 감정 28개 = 총 52개)
 * 
 * - 단일 감정: 강도에 따라 3단계로 분류 (Strong, Medium, Weak)
 * - 복합 감정: 두 가지 기본 감정의 결합 (거리 1~4)
 */
enum class ComplexEmotionType(
    val composition: Pair<EmotionType, EmotionType>,
    val category: Category,
    val intensity: Intensity = Intensity.NONE // 강도 (단일 감정용)
) {
    // =========================================================================================
    // 1. 단일 감정 (Single Emotions) - 24개
    // =========================================================================================
    ECSTASY(EmotionType.JOY to EmotionType.JOY, Category.SINGLE_EMOTION, Intensity.STRONG),
    JOY(EmotionType.JOY to EmotionType.JOY, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    SERENITY(EmotionType.JOY to EmotionType.JOY, Category.SINGLE_EMOTION, Intensity.WEAK),

    ADMIRATION(EmotionType.TRUST to EmotionType.TRUST, Category.SINGLE_EMOTION, Intensity.STRONG),
    TRUST(EmotionType.TRUST to EmotionType.TRUST, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    ACCEPTANCE(EmotionType.TRUST to EmotionType.TRUST, Category.SINGLE_EMOTION, Intensity.WEAK),

    TERROR(EmotionType.FEAR to EmotionType.FEAR, Category.SINGLE_EMOTION, Intensity.STRONG),
    FEAR(EmotionType.FEAR to EmotionType.FEAR, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    APPREHENSION(EmotionType.FEAR to EmotionType.FEAR, Category.SINGLE_EMOTION, Intensity.WEAK),

    AMAZEMENT(EmotionType.SURPRISE to EmotionType.SURPRISE, Category.SINGLE_EMOTION, Intensity.STRONG),
    SURPRISE(EmotionType.SURPRISE to EmotionType.SURPRISE, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    DISTRACTION(EmotionType.SURPRISE to EmotionType.SURPRISE, Category.SINGLE_EMOTION, Intensity.WEAK),

    GRIEF(EmotionType.SADNESS to EmotionType.SADNESS, Category.SINGLE_EMOTION, Intensity.STRONG),
    SADNESS(EmotionType.SADNESS to EmotionType.SADNESS, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    PENSIVENESS(EmotionType.SADNESS to EmotionType.SADNESS, Category.SINGLE_EMOTION, Intensity.WEAK),

    LOATHING(EmotionType.DISGUST to EmotionType.DISGUST, Category.SINGLE_EMOTION, Intensity.STRONG),
    DISGUST(EmotionType.DISGUST to EmotionType.DISGUST, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    BOREDOM(EmotionType.DISGUST to EmotionType.DISGUST, Category.SINGLE_EMOTION, Intensity.WEAK),

    RAGE(EmotionType.ANGER to EmotionType.ANGER, Category.SINGLE_EMOTION, Intensity.STRONG),
    ANGER(EmotionType.ANGER to EmotionType.ANGER, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    ANNOYANCE(EmotionType.ANGER to EmotionType.ANGER, Category.SINGLE_EMOTION, Intensity.WEAK),

    VIGILANCE(EmotionType.ANTICIPATION to EmotionType.ANTICIPATION, Category.SINGLE_EMOTION, Intensity.STRONG),
    ANTICIPATION(EmotionType.ANTICIPATION to EmotionType.ANTICIPATION, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    INTEREST(EmotionType.ANTICIPATION to EmotionType.ANTICIPATION, Category.SINGLE_EMOTION, Intensity.WEAK),

    // =========================================================================================
    // 2. 복합 감정 (Complex Emotions / Dyads) - 뉘앙스 변종 포함
    // =========================================================================================

    // --- 1차 이중감정 (Primary Dyads) ---
    LOVE(EmotionType.JOY to EmotionType.TRUST, Category.PRIMARY_DYAD),
    POSSESSIVENESS(EmotionType.JOY to EmotionType.TRUST, Category.PRIMARY_DYAD), // 집착
    SUBMISSION(EmotionType.TRUST to EmotionType.FEAR, Category.PRIMARY_DYAD),
    SERVILITY(EmotionType.TRUST to EmotionType.FEAR, Category.PRIMARY_DYAD), // 비굴함
    AWE(EmotionType.FEAR to EmotionType.SURPRISE, Category.PRIMARY_DYAD),
    ALARM(EmotionType.FEAR to EmotionType.SURPRISE, Category.PRIMARY_DYAD), // 공황
    DISAPPROVAL(EmotionType.SURPRISE to EmotionType.SADNESS, Category.PRIMARY_DYAD),
    SHOCK(EmotionType.SURPRISE to EmotionType.SADNESS, Category.PRIMARY_DYAD), // 충격
    REMORSE(EmotionType.SADNESS to EmotionType.DISGUST, Category.PRIMARY_DYAD),
    SELF_LOATHING(EmotionType.SADNESS to EmotionType.DISGUST, Category.PRIMARY_DYAD), // 자기비하
    CONTEMPT(EmotionType.DISGUST to EmotionType.ANGER, Category.PRIMARY_DYAD),
    ENVY_CONTEMPT(EmotionType.DISGUST to EmotionType.ANGER, Category.PRIMARY_DYAD), // 1차 질투 (경멸적)
    AGGRESSIVENESS(EmotionType.ANGER to EmotionType.ANTICIPATION, Category.PRIMARY_DYAD),
    VENGEANCE(EmotionType.ANGER to EmotionType.ANTICIPATION, Category.PRIMARY_DYAD), // 복수심
    OPTIMISM(EmotionType.ANTICIPATION to EmotionType.JOY, Category.PRIMARY_DYAD),
    NAIVETY(EmotionType.ANTICIPATION to EmotionType.JOY, Category.PRIMARY_DYAD), // 순진함

    // --- 2차 이중감정 (Secondary Dyads) ---
    GUILT(EmotionType.JOY to EmotionType.FEAR, Category.SECONDARY_DYAD),
    SELF_DENIAL(EmotionType.JOY to EmotionType.FEAR, Category.SECONDARY_DYAD), // 자기부정
    CURIOSITY(EmotionType.TRUST to EmotionType.SURPRISE, Category.SECONDARY_DYAD),
    AMBIVALENCE_CURIOSITY(EmotionType.TRUST to EmotionType.SURPRISE, Category.SECONDARY_DYAD), // 의구심
    DESPAIR(EmotionType.FEAR to EmotionType.SADNESS, Category.SECONDARY_DYAD),
    HELPLESSNESS(EmotionType.FEAR to EmotionType.SADNESS, Category.SECONDARY_DYAD), // 무기력
    UNBELIEF(EmotionType.SURPRISE to EmotionType.DISGUST, Category.SECONDARY_DYAD),
    ABHORRENCE(EmotionType.SURPRISE to EmotionType.DISGUST, Category.SECONDARY_DYAD), // 혐오적 경악
    ENVY(EmotionType.SADNESS to EmotionType.ANGER, Category.SECONDARY_DYAD), // 2차 선망 (갈망적)
    RESENTMENT(EmotionType.SADNESS to EmotionType.ANGER, Category.SECONDARY_DYAD), // 울분
    CYNICISM(EmotionType.DISGUST to EmotionType.ANTICIPATION, Category.SECONDARY_DYAD),
    DISTRUST(EmotionType.DISGUST to EmotionType.ANTICIPATION, Category.SECONDARY_DYAD), // 불신
    PRIDE(EmotionType.ANGER to EmotionType.JOY, Category.SECONDARY_DYAD),
    HUBRIS(EmotionType.ANGER to EmotionType.JOY, Category.SECONDARY_DYAD), // 오만
    HOPE(EmotionType.ANTICIPATION to EmotionType.TRUST, Category.SECONDARY_DYAD),
    FATALISM(EmotionType.ANTICIPATION to EmotionType.TRUST, Category.SECONDARY_DYAD), // 운명론

    // --- 3차 이중감정 (Tertiary Dyads) ---
    DELIGHT(EmotionType.JOY to EmotionType.SURPRISE, Category.TERTIARY_DYAD),
    CONFUSED_JOY(EmotionType.JOY to EmotionType.SURPRISE, Category.TERTIARY_DYAD), // 혼란스러운 기쁨
    SENTIMENTALITY(EmotionType.TRUST to EmotionType.SADNESS, Category.TERTIARY_DYAD),
    SELF_PITY(EmotionType.TRUST to EmotionType.SADNESS, Category.TERTIARY_DYAD), // 자기연민
    SHAME(EmotionType.FEAR to EmotionType.DISGUST, Category.TERTIARY_DYAD),
    HUMILIATION(EmotionType.FEAR to EmotionType.DISGUST, Category.TERTIARY_DYAD), // 굴욕
    OUTRAGE(EmotionType.SURPRISE to EmotionType.ANGER, Category.TERTIARY_DYAD),
    DISORIENTATION(EmotionType.SURPRISE to EmotionType.ANGER, Category.TERTIARY_DYAD), // 당혹
    PESSIMISM(EmotionType.SADNESS to EmotionType.ANTICIPATION, Category.TERTIARY_DYAD),
    RESIGNATION(EmotionType.SADNESS to EmotionType.ANTICIPATION, Category.TERTIARY_DYAD), // 운명적 체념
    MORBIDNESS(EmotionType.DISGUST to EmotionType.JOY, Category.TERTIARY_DYAD),
    DERISION(EmotionType.DISGUST to EmotionType.JOY, Category.TERTIARY_DYAD), // 냉소적 조소
    DOMINANCE(EmotionType.ANGER to EmotionType.TRUST, Category.TERTIARY_DYAD),
    TYRANNY(EmotionType.ANGER to EmotionType.TRUST, Category.TERTIARY_DYAD), // 강압
    ANXIETY(EmotionType.ANTICIPATION to EmotionType.FEAR, Category.TERTIARY_DYAD),
    DREAD(EmotionType.ANTICIPATION to EmotionType.FEAR, Category.TERTIARY_DYAD), // 공포탄

    // --- 반대 감정 (Opposites) ---
    BITTERSWEETNESS(EmotionType.JOY to EmotionType.SADNESS, Category.OPPOSITE),
    NOSTALGIA(EmotionType.JOY to EmotionType.SADNESS, Category.OPPOSITE), // 우울한 향수
    AMBIVALENCE(EmotionType.TRUST to EmotionType.DISGUST, Category.OPPOSITE),
    MISTRUST(EmotionType.TRUST to EmotionType.DISGUST, Category.OPPOSITE), // 회의감
    FROZENNESS(EmotionType.FEAR to EmotionType.ANGER, Category.OPPOSITE),
    CONFUSION(EmotionType.SURPRISE to EmotionType.ANTICIPATION, Category.OPPOSITE);
    

    enum class Category {
        /** 단일 감정 (기본 8개 + 강도변화) */
        SINGLE_EMOTION,
        /** 1차 이중감정 (인접, 거리 1) */
        PRIMARY_DYAD,
        /** 2차 이중감정 (하나 건너, 거리 2) */
        SECONDARY_DYAD,
        /** 3차 이중감정 (둘 건너, 거리 3) */
        TERTIARY_DYAD,
        /** 반대 감정의 결합 (정반대, 거리 4) */
        OPPOSITE,
    }

    enum class Intensity {
        /** 해당 없음 (복합 감정 등) */
        NONE,
        /** 강함 (Level 3) */
        STRONG,
        /** 중간 (Level 2) - 기본 */
        MEDIUM,
        /** 약함 (Level 1) */
        WEAK
    }

    companion object {
        /**
         * 두 기본 감정의 조합에 해당하는 복합 감정을 찾습니다.
         * 순서는 상관없습니다.
         * 
         * 단일 감정(동일한 감정의 조합)은 'MEDIUM' (기본) 강도를 반환합니다.
         */
        fun find(type1: EmotionType, type2: EmotionType): ComplexEmotionType? {
            return entries.find {
                // 단일 감정은 기본(Medium)만 검색 (필요시 로직 수정)
                if (type1 == type2) {
                    it.category == Category.SINGLE_EMOTION && 
                    it.intensity == Intensity.MEDIUM &&
                    it.composition.first == type1
                } else {
                    // 복합 감정 검색
                    (it.composition.first == type1 && it.composition.second == type2) ||
                    (it.composition.first == type2 && it.composition.second == type1)
                }
            }
        }

        /**
         * 특정 기본 감정과 강도에 해당하는 단일 감정을 찾습니다.
         */
        fun findSingle(type: EmotionType, intensity: Intensity): ComplexEmotionType? {
            return entries.find {
                it.category == Category.SINGLE_EMOTION &&
                it.composition.first == type &&
                it.intensity == intensity
            }
        }
    }
}
