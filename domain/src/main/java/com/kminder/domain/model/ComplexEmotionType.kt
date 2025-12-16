package com.kminder.domain.model

/**
 * 플루치크 이론에 기반한 이중 감정 (Dyads)
 * 두 가지 기본 감정이 결합하여 형성되는 복합적인 심리 상태를 정의합니다.
 *
 * 거리(Distance)에 따라 1차, 2차, 3차 이중감정으로 분류됩니다.
 */
enum class ComplexEmotionType(
    val composition: Pair<EmotionType, EmotionType>,
    val category: Category
) {
    // =========================================================================================
    // 1차 이중감정 (Primary Dyads)
    // 인접한 감정의 결합 (거리 1)
    // =========================================================================================
    
    /** 기쁨 + 신뢰 */
    LOVE(EmotionType.JOY to EmotionType.TRUST, Category.PRIMARY_DYAD),
    
    /** 신뢰 + 두려움 */
    SUBMISSION(EmotionType.TRUST to EmotionType.FEAR, Category.PRIMARY_DYAD),
    
    /** 두려움 + 놀람 */
    AWE(EmotionType.FEAR to EmotionType.SURPRISE, Category.PRIMARY_DYAD),
    
    /** 놀람 + 슬픔 */
    DISAPPROVAL(EmotionType.SURPRISE to EmotionType.SADNESS, Category.PRIMARY_DYAD),
    
    /** 슬픔 + 혐오 */
    REMORSE(EmotionType.SADNESS to EmotionType.DISGUST, Category.PRIMARY_DYAD),
    
    /** 혐오 + 분노 */
    CONTEMPT(EmotionType.DISGUST to EmotionType.ANGER, Category.PRIMARY_DYAD),
    
    /** 분노 + 기대 */
    AGGRESSIVENESS(EmotionType.ANGER to EmotionType.ANTICIPATION, Category.PRIMARY_DYAD),
    
    /** 기대 + 기쁨 */
    OPTIMISM(EmotionType.ANTICIPATION to EmotionType.JOY, Category.PRIMARY_DYAD),

    
    // =========================================================================================
    // 2차 이중감정 (Secondary Dyads)
    // 하나 건너뛴 감정의 결합 (거리 2)
    // =========================================================================================
    
    /** 기쁨 + 두려움 */
    GUILT(EmotionType.JOY to EmotionType.FEAR, Category.SECONDARY_DYAD),

    /** 신뢰 + 놀람 */
    CURIOSITY(EmotionType.TRUST to EmotionType.SURPRISE, Category.SECONDARY_DYAD),
    
    /** 두려움 + 슬픔 */
    DESPAIR(EmotionType.FEAR to EmotionType.SADNESS, Category.SECONDARY_DYAD),
    
    /** 놀람 + 혐오 */
    UNBELIEF(EmotionType.SURPRISE to EmotionType.DISGUST, Category.SECONDARY_DYAD),
    
    /** 슬픔 + 분노 */
    ENVY(EmotionType.SADNESS to EmotionType.ANGER, Category.SECONDARY_DYAD),
    
    /** 혐오 + 기대 */
    CYNICISM(EmotionType.DISGUST to EmotionType.ANTICIPATION, Category.SECONDARY_DYAD),
    
    /** 분노 + 기쁨 */
    PRIDE(EmotionType.ANGER to EmotionType.JOY, Category.SECONDARY_DYAD),

    /** 기대 + 신뢰 */
    FATALISM(EmotionType.ANTICIPATION to EmotionType.TRUST, Category.SECONDARY_DYAD),


    // =========================================================================================
    // 3차 이중감정 (Tertiary Dyads)
    // 두 개 건너뛴 감정의 결합 (거리 3)
    // =========================================================================================

    /** 기쁨 + 놀람 */
    DELIGHT(EmotionType.JOY to EmotionType.SURPRISE, Category.TERTIARY_DYAD),

    /** 신뢰 + 슬픔 */
    SENTIMENTALITY(EmotionType.TRUST to EmotionType.SADNESS, Category.TERTIARY_DYAD),
    
    /** 두려움 + 혐오 */
    SHAME(EmotionType.FEAR to EmotionType.DISGUST, Category.TERTIARY_DYAD),
    
    /** 놀람 + 분노 */
    OUTRAGE(EmotionType.SURPRISE to EmotionType.ANGER, Category.TERTIARY_DYAD),
    
    /** 슬픔 + 기대 */
    PESSIMISM(EmotionType.SADNESS to EmotionType.ANTICIPATION, Category.TERTIARY_DYAD),
    
    /** 혐오 + 기쁨 */
    MORBIDNESS(EmotionType.DISGUST to EmotionType.JOY, Category.TERTIARY_DYAD),
    
    /** 분노 + 신뢰 */
    DOMINANCE(EmotionType.ANGER to EmotionType.TRUST, Category.TERTIARY_DYAD),
    
    /** 기대 + 두려움 */
    ANXIETY(EmotionType.ANTICIPATION to EmotionType.FEAR, Category.TERTIARY_DYAD);
    

    enum class Category {
        /** 1차 이중감정 (인접, 거리 1) */
        PRIMARY_DYAD,
        /** 2차 이중감정 (하나 건너, 거리 2) */
        SECONDARY_DYAD,
        /** 3차 이중감정 (둘 건너, 거리 3) */
        TERTIARY_DYAD,
    }

    companion object {
        /**
         * 두 기본 감정의 조합에 해당하는 복합 감정을 찾습니다.
         * 순서는 상관없습니다.
         */
        fun find(type1: EmotionType, type2: EmotionType): ComplexEmotionType? {
            return entries.find {
                (it.composition.first == type1 && it.composition.second == type2) ||
                (it.composition.first == type2 && it.composition.second == type1)
            }
        }
    }
}
