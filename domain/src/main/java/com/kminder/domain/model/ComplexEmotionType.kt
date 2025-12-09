package com.kminder.domain.model

/**
 * 플루치크 이론에 기반한 이중 감정 (Dyads)
 * 두 가지 기본 감정이 결합하여 형성되는 복합적인 심리 상태를 정의합니다.
 *
 * 거리(Distance)에 따라 1차, 2차, 3차 이중감정으로 분류됩니다.
 */
enum class ComplexEmotionType(
    val title: String,
    val description: String,
    val composition: Pair<EmotionType, EmotionType>,
    val category: Category
) {
    // =========================================================================================
    // 1차 이중감정 (Primary Dyads)
    // 인접한 감정의 결합 (거리 1)
    // =========================================================================================
    
    /** 기쁨 + 신뢰 */
    LOVE("사랑 (Love)", "기쁨과 신뢰가 만나 따뜻하고 깊은 애정을 형성합니다.", EmotionType.JOY to EmotionType.TRUST, Category.PRIMARY_DYAD),
    
    /** 신뢰 + 두려움 */
    SUBMISSION("수용 (Submission)", "신뢰와 두려움이 섞여 어쩔 수 없는 상황을 받아들이거나 순응하는 마음입니다.", EmotionType.TRUST to EmotionType.FEAR, Category.PRIMARY_DYAD),
    
    /** 두려움 + 놀람 */
    AWE("경외감 (Awe)", "두려움과 놀람이 결합되어 압도적이거나 신비로운 대상을 향한 느낌입니다.", EmotionType.FEAR to EmotionType.SURPRISE, Category.PRIMARY_DYAD),
    
    /** 놀람 + 슬픔 */
    DISAPPROVAL("실망 (Disapproval)", "놀람과 슬픔이 만나, 예상치 못한 부정적 결과에 대해 불만을 가집니다.", EmotionType.SURPRISE to EmotionType.SADNESS, Category.PRIMARY_DYAD),
    
    /** 슬픔 + 혐오 */
    REMORSE("후회 (Remorse)", "슬픔과 혐오가 섞여 과거의 행동이나 자신에 대한 깊은 자책감을 만듭니다.", EmotionType.SADNESS to EmotionType.DISGUST, Category.PRIMARY_DYAD),
    
    /** 혐오 + 분노 */
    CONTEMPT("경멸 (Contempt)", "혐오와 분노가 결합되어 대상을 낮잡아보거나 강한 거부감을 보입니다.", EmotionType.DISGUST to EmotionType.ANGER, Category.PRIMARY_DYAD),
    
    /** 분노 + 기대 */
    AGGRESSIVENESS("공격성 (Aggressiveness)", "분노와 기대가 만나 목표를 향해 거칠게 나아가거나 도전하려는 에너지입니다.", EmotionType.ANGER to EmotionType.ANTICIPATION, Category.PRIMARY_DYAD),
    
    /** 기대 + 기쁨 */
    OPTIMISM("낙관 (Optimism)", "기대와 기쁨이 어우러져 미래가 잘 풀릴 것이라는 긍정적인 희망을 가집니다.", EmotionType.ANTICIPATION to EmotionType.JOY, Category.PRIMARY_DYAD),

    
    // =========================================================================================
    // 2차 이중감정 (Secondary Dyads)
    // 하나 건너뛴 감정의 결합 (거리 2)
    // =========================================================================================
    
    /** 기쁨 + 두려움 */
    GUILT("죄책감 (Guilt)", "기쁨을 누리는 것 같으면서도 두려움이 개입되어 느끼는 마음의 불편함입니다.", EmotionType.JOY to EmotionType.FEAR, Category.SECONDARY_DYAD),

    /** 신뢰 + 놀람 */
    CURIOSITY("호기심 (Curiosity)", "믿음 안에서 새로운 것에 대해 놀라움을 느끼며 탐구하려는 마음입니다.", EmotionType.TRUST to EmotionType.SURPRISE, Category.SECONDARY_DYAD),
    
    /** 두려움 + 슬픔 */
    DESPAIR("절망 (Despair)", "두려움과 슬픔이 만나 희망을 잃고 깊은 어둠에 빠진 상태입니다.", EmotionType.FEAR to EmotionType.SADNESS, Category.SECONDARY_DYAD),
    
    /** 놀람 + 혐오 */
    UNBELIEF("불신 (Unbelief)", "놀람과 혐오가 섞여 상황을 믿을 수 없어 하거나 거부하는 태도입니다.", EmotionType.SURPRISE to EmotionType.DISGUST, Category.SECONDARY_DYAD),
    
    /** 슬픔 + 분노 */
    ENVY("질투 (Envy)", "슬픔과 분노가 섞여 타인의 행복을 보며 자신의 결핍에 괴로워합니다.", EmotionType.SADNESS to EmotionType.ANGER, Category.SECONDARY_DYAD),
    
    /** 혐오 + 기대 */
    CYNICISM("냉소 (Cynicism)", "혐오와 기대가 기묘하게 섞여, 결과에 대해 부정적으로 예측하고 비웃는 태도입니다.", EmotionType.DISGUST to EmotionType.ANTICIPATION, Category.SECONDARY_DYAD),
    
    /** 분노 + 기쁨 */
    PRIDE("자부심 (Pride)", "분노(자아 주장)와 기쁨이 결합되어 자신의 성취나 가치에 대해 강한 확신을 가집니다.", EmotionType.ANGER to EmotionType.JOY, Category.SECONDARY_DYAD),

    /** 기대 + 신뢰 */
    FATALISM("숙명 (Fatalism)", "기대와 신뢰가 섞여 미래의 일을 정해진 것으로 받아들이는 태도입니다.", EmotionType.ANTICIPATION to EmotionType.TRUST, Category.SECONDARY_DYAD),


    // =========================================================================================
    // 3차 이중감정 (Tertiary Dyads)
    // 두 개 건너뛴 감정의 결합 (거리 3)
    // =========================================================================================

    /** 기쁨 + 놀람 */
    DELIGHT("환희 (Delight)", "기쁨과 놀람이 만나 예상치 못한 즐거움에 크게 기뻐하는 상태입니다.", EmotionType.JOY to EmotionType.SURPRISE, Category.TERTIARY_DYAD),

    /** 신뢰 + 슬픔 */
    SENTIMENTALITY("감상 (Sentimentality)", "신뢰와 슬픔이 섞여 지나간 인연이나 추억에 젖어드는 마음입니다.", EmotionType.TRUST to EmotionType.SADNESS, Category.TERTIARY_DYAD),
    
    /** 두려움 + 혐오 */
    SHAME("수치심 (Shame)", "두려움과 혐오가 만나 자신의 모습에 대해 부끄러움을 느끼고 숨고 싶은 마음입니다.", EmotionType.FEAR to EmotionType.DISGUST, Category.TERTIARY_DYAD),
    
    /** 놀람 + 분노 */
    OUTRAGE("격분 (Outrage)", "놀람과 분노가 섞여 예상치 못한 일에 대해 강하게 화를 내는 상태입니다.", EmotionType.SURPRISE to EmotionType.ANGER, Category.TERTIARY_DYAD),
    
    /** 슬픔 + 기대 */
    PESSIMISM("비관 (Pessimism)", "슬픔과 기대가 섞여 미래에 대해 부정적인 결과를 예상하고 미리 슬퍼하는 마음입니다.", EmotionType.SADNESS to EmotionType.ANTICIPATION, Category.TERTIARY_DYAD),
    
    /** 혐오 + 기쁨 */
    MORBIDNESS("병적 흥미 (Morbidness)", "혐오스러운 대상에게서 묘한 기쁨을 느끼는 복잡한 심리입니다.", EmotionType.DISGUST to EmotionType.JOY, Category.TERTIARY_DYAD),
    
    /** 분노 + 신뢰 */
    DOMINANCE("지배 (Dominance)", "분노와 신뢰가 만나 자신의 주장을 믿고 타인을 통제하려는 태도입니다.", EmotionType.ANGER to EmotionType.TRUST, Category.TERTIARY_DYAD),
    
    /** 기대 + 두려움 */
    ANXIETY("불안 (Anxiety)", "기대와 두려움이 섞여 다가올 미래에 대해 알 수 없어 초조해하는 마음입니다.", EmotionType.ANTICIPATION to EmotionType.FEAR, Category.TERTIARY_DYAD);
    

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
