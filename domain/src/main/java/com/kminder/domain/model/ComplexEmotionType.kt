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

    // --- JOY 계열 ---
    /** 황홀 (Strong) */
    ECSTASY(EmotionType.JOY to EmotionType.JOY, Category.SINGLE_EMOTION, Intensity.STRONG),
    /** 기쁨 (Medium) - 기본 */
    JOY(EmotionType.JOY to EmotionType.JOY, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    /** 평온 (Weak) */
    SERENITY(EmotionType.JOY to EmotionType.JOY, Category.SINGLE_EMOTION, Intensity.WEAK),

    // --- TRUST 계열 ---
    /** 감탄 (Strong) */
    ADMIRATION(EmotionType.TRUST to EmotionType.TRUST, Category.SINGLE_EMOTION, Intensity.STRONG),
    /** 신뢰 (Medium) - 기본 */
    TRUST(EmotionType.TRUST to EmotionType.TRUST, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    /** 용인 (Weak) */
    ACCEPTANCE(EmotionType.TRUST to EmotionType.TRUST, Category.SINGLE_EMOTION, Intensity.WEAK),

    // --- FEAR 계열 ---
    /** 공포 (Strong) */
    TERROR(EmotionType.FEAR to EmotionType.FEAR, Category.SINGLE_EMOTION, Intensity.STRONG),
    /** 두려움 (Medium) - 기본 */
    FEAR(EmotionType.FEAR to EmotionType.FEAR, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    /** 우려 (Weak) */
    APPREHENSION(EmotionType.FEAR to EmotionType.FEAR, Category.SINGLE_EMOTION, Intensity.WEAK),

    // --- SURPRISE 계열 ---
    /** 경악 (Strong) */
    AMAZEMENT(EmotionType.SURPRISE to EmotionType.SURPRISE, Category.SINGLE_EMOTION, Intensity.STRONG),
    /** 놀람 (Medium) - 기본 */
    SURPRISE(EmotionType.SURPRISE to EmotionType.SURPRISE, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    /** 산만 (Weak) */
    DISTRACTION(EmotionType.SURPRISE to EmotionType.SURPRISE, Category.SINGLE_EMOTION, Intensity.WEAK),

    // --- SADNESS 계열 ---
    /** 비탄 (Strong) */
    GRIEF(EmotionType.SADNESS to EmotionType.SADNESS, Category.SINGLE_EMOTION, Intensity.STRONG),
    /** 슬픔 (Medium) - 기본 */
    SADNESS(EmotionType.SADNESS to EmotionType.SADNESS, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    /** 수심 (Weak) */
    PENSIVENESS(EmotionType.SADNESS to EmotionType.SADNESS, Category.SINGLE_EMOTION, Intensity.WEAK),

    // --- DISGUST 계열 ---
    /** 증오 (Strong) */
    LOATHING(EmotionType.DISGUST to EmotionType.DISGUST, Category.SINGLE_EMOTION, Intensity.STRONG),
    /** 혐오 (Medium) - 기본 */
    DISGUST(EmotionType.DISGUST to EmotionType.DISGUST, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    /** 지루함 (Weak) */
    BOREDOM(EmotionType.DISGUST to EmotionType.DISGUST, Category.SINGLE_EMOTION, Intensity.WEAK),

    // --- ANGER 계열 ---
    /** 격노 (Strong) */
    RAGE(EmotionType.ANGER to EmotionType.ANGER, Category.SINGLE_EMOTION, Intensity.STRONG),
    /** 분노 (Medium) - 기본 */
    ANGER(EmotionType.ANGER to EmotionType.ANGER, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    /** 짜증 (Weak) */
    ANNOYANCE(EmotionType.ANGER to EmotionType.ANGER, Category.SINGLE_EMOTION, Intensity.WEAK),

    // --- ANTICIPATION 계열 ---
    /** 경계 (Strong) */
    VIGILANCE(EmotionType.ANTICIPATION to EmotionType.ANTICIPATION, Category.SINGLE_EMOTION, Intensity.STRONG),
    /** 기대 (Medium) - 기본 */
    ANTICIPATION(EmotionType.ANTICIPATION to EmotionType.ANTICIPATION, Category.SINGLE_EMOTION, Intensity.MEDIUM),
    /** 관심 (Weak) */
    INTEREST(EmotionType.ANTICIPATION to EmotionType.ANTICIPATION, Category.SINGLE_EMOTION, Intensity.WEAK),


    // =========================================================================================
    // 2. 복합 감정 (Complex Emotions / Dyads) - 28개
    // (Intensity는 기본적으로 NONE 또는 MIXED)
    // =========================================================================================

    // --- 1차 이중감정 (Primary Dyads, 거리 1) ---
    LOVE(EmotionType.JOY to EmotionType.TRUST, Category.PRIMARY_DYAD),
    SUBMISSION(EmotionType.TRUST to EmotionType.FEAR, Category.PRIMARY_DYAD),
    AWE(EmotionType.FEAR to EmotionType.SURPRISE, Category.PRIMARY_DYAD),
    DISAPPROVAL(EmotionType.SURPRISE to EmotionType.SADNESS, Category.PRIMARY_DYAD),
    REMORSE(EmotionType.SADNESS to EmotionType.DISGUST, Category.PRIMARY_DYAD),
    CONTEMPT(EmotionType.DISGUST to EmotionType.ANGER, Category.PRIMARY_DYAD),
    AGGRESSIVENESS(EmotionType.ANGER to EmotionType.ANTICIPATION, Category.PRIMARY_DYAD),
    OPTIMISM(EmotionType.ANTICIPATION to EmotionType.JOY, Category.PRIMARY_DYAD),

    // --- 2차 이중감정 (Secondary Dyads, 거리 2) ---
    GUILT(EmotionType.JOY to EmotionType.FEAR, Category.SECONDARY_DYAD),
    CURIOSITY(EmotionType.TRUST to EmotionType.SURPRISE, Category.SECONDARY_DYAD),
    DESPAIR(EmotionType.FEAR to EmotionType.SADNESS, Category.SECONDARY_DYAD),
    UNBELIEF(EmotionType.SURPRISE to EmotionType.DISGUST, Category.SECONDARY_DYAD),
    ENVY(EmotionType.SADNESS to EmotionType.ANGER, Category.SECONDARY_DYAD),
    CYNICISM(EmotionType.DISGUST to EmotionType.ANTICIPATION, Category.SECONDARY_DYAD),
    PRIDE(EmotionType.ANGER to EmotionType.JOY, Category.SECONDARY_DYAD),
    FATALISM(EmotionType.ANTICIPATION to EmotionType.TRUST, Category.SECONDARY_DYAD),

    // --- 3차 이중감정 (Tertiary Dyads, 거리 3) ---
    DELIGHT(EmotionType.JOY to EmotionType.SURPRISE, Category.TERTIARY_DYAD),
    SENTIMENTALITY(EmotionType.TRUST to EmotionType.SADNESS, Category.TERTIARY_DYAD),
    SHAME(EmotionType.FEAR to EmotionType.DISGUST, Category.TERTIARY_DYAD),
    OUTRAGE(EmotionType.SURPRISE to EmotionType.ANGER, Category.TERTIARY_DYAD),
    PESSIMISM(EmotionType.SADNESS to EmotionType.ANTICIPATION, Category.TERTIARY_DYAD),
    MORBIDNESS(EmotionType.DISGUST to EmotionType.JOY, Category.TERTIARY_DYAD),
    DOMINANCE(EmotionType.ANGER to EmotionType.TRUST, Category.TERTIARY_DYAD),
    ANXIETY(EmotionType.ANTICIPATION to EmotionType.FEAR, Category.TERTIARY_DYAD),

    // --- 반대 감정 (Opposite Emotions, 거리 4) ---
    BITTERSWEETNESS(EmotionType.JOY to EmotionType.SADNESS, Category.OPPOSITE),
    AMBIVALENCE(EmotionType.TRUST to EmotionType.DISGUST, Category.OPPOSITE),
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
