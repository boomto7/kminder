package com.kminder.domain.model

/**
 * 플루치크 이론에 기반한 2차/3차 복합 감정
 */
enum class ComplexEmotionType(
    val title: String,
    val description: String,
    val composition: Pair<EmotionType, EmotionType>
) {
    // === 2차 감정 (Primary Dyads: 인접한 감정의 결합) ===
    
    /** 기쁨 + 신뢰 */
    LOVE("사랑", "기쁨과 신뢰가 만나 따뜻하고 깊은 애정을 형성합니다.", EmotionType.JOY to EmotionType.TRUST),
    
    /** 신뢰 + 두려움 */
    SUBMISSION("굴복/수용", "신뢰와 두려움이 섞여 상황을 받아들이거나 복종하는 마음입니다.", EmotionType.TRUST to EmotionType.FEAR),
    
    /** 두려움 + 놀람 */
    AWE("경외감", "두려움과 놀람이 결합되어 압도적이거나 신비로운 대상을 향한 느낌입니다.", EmotionType.FEAR to EmotionType.SURPRISE),
    
    /** 놀람 + 슬픔 */
    DISAPPROVAL("못마땅함", "놀람과 슬픔이 만나, 예상치 못한 부정적 상황에 대한 거부감을 줍니다.", EmotionType.SURPRISE to EmotionType.SADNESS),
    
    /** 슬픔 + 혐오 */
    REMORSE("후회", "슬픔과 혐오가 섞여 과거의 행동이나 상황에 대한 깊은 뉘우침을 만듭니다.", EmotionType.SADNESS to EmotionType.DISGUST),
    
    /** 혐오 + 분노 */
    CONTEMPT("경멸", "혐오와 분노가 결합되어 대상을 낮잡아보거나 적대시하는 마음입니다.", EmotionType.DISGUST to EmotionType.ANGER),
    
    /** 분노 + 기대 */
    AGGRESSIVENESS("공격성", "분노와 기대가 만나 적극적으로 대항하거나 도전하려는 에너지입니다.", EmotionType.ANGER to EmotionType.ANTICIPATION),
    
    /** 기대 + 기쁨 */
    OPTIMISM("낙관", "기대와 기쁨이 어우러져 미래에 대한 긍정적이고 희망찬 태도를 만듭니다.", EmotionType.ANTICIPATION to EmotionType.JOY),

    
    // === 3차 감정 (Secondary Dyads: 하나 건너뛴 감정의 결합 - 선택적 구현) ===
    
    /** 기쁨 + 두려움 */
    GUILT("죄책감", "기쁨을 추구했으나 두려움이 개입되어 느끼는 마음의 짐입니다.", EmotionType.JOY to EmotionType.FEAR),
    
    /** 기쁨 + 분노 */
    PRIDE("자부심", "기쁨과 분노(에너지)가 만나 자신에 대한 강한 확신을 가집니다.", EmotionType.JOY to EmotionType.ANGER),
    
    /** 신뢰 + 놀람 */
    CURIOSITY("호기심", "신뢰와 놀람이 결합되어 대상을 더 알고 싶어하는 마음입니다.", EmotionType.TRUST to EmotionType.SURPRISE),
    
    /** 슬픔 + 분노 */
    ENVY("질투/비탄", "슬픔과 분노가 섞여 타인의 행복을 보며 느끼는 복합적 고통입니다.", EmotionType.SADNESS to EmotionType.ANGER)
}
