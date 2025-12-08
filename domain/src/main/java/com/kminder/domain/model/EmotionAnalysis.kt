package com.kminder.domain.model

/**
 * 감정 분석 결과
 * 
 * @property anger 분노 강도 (0.0 ~ 1.0)
 * @property anticipation 기대 강도 (0.0 ~ 1.0)
 * @property joy 기쁨 강도 (0.0 ~ 1.0)
 * @property trust 신뢰 강도 (0.0 ~ 1.0)
 * @property fear 두려움 강도 (0.0 ~ 1.0)
 * @property sadness 슬픔 강도 (0.0 ~ 1.0)
 * @property disgust 혐오 강도 (0.0 ~ 1.0)
 * @property surprise 놀람 강도 (0.0 ~ 1.0)
 */
data class EmotionAnalysis(
    val anger: Float = 0f,
    val anticipation: Float = 0f,
    val joy: Float = 0f,
    val trust: Float = 0f,
    val fear: Float = 0f,
    val sadness: Float = 0f,
    val disgust: Float = 0f,
    val surprise: Float = 0f
) {
    /**
     * 특정 감정의 강도를 가져옵니다.
     */
    fun getEmotionIntensity(emotionType: EmotionType): Float {
        return when (emotionType) {
            EmotionType.ANGER -> anger
            EmotionType.ANTICIPATION -> anticipation
            EmotionType.JOY -> joy
            EmotionType.TRUST -> trust
            EmotionType.FEAR -> fear
            EmotionType.SADNESS -> sadness
            EmotionType.DISGUST -> disgust
            EmotionType.SURPRISE -> surprise
        }
    }
    
    /**
     * 가장 강한 감정을 반환합니다.
     */
    fun getDominantEmotion(): EmotionType {
        val emotions = mapOf(
            EmotionType.ANGER to anger,
            EmotionType.ANTICIPATION to anticipation,
            EmotionType.JOY to joy,
            EmotionType.TRUST to trust,
            EmotionType.FEAR to fear,
            EmotionType.SADNESS to sadness,
            EmotionType.DISGUST to disgust,
            EmotionType.SURPRISE to surprise
        )
        return emotions.maxByOrNull { it.value }?.key ?: EmotionType.JOY
    }
    
    /**
     * 모든 감정을 Map으로 반환합니다.
     */
    fun toMap(): Map<EmotionType, Float> {
        return mapOf(
            EmotionType.ANGER to anger,
            EmotionType.ANTICIPATION to anticipation,
            EmotionType.JOY to joy,
            EmotionType.TRUST to trust,
            EmotionType.FEAR to fear,
            EmotionType.SADNESS to sadness,
            EmotionType.DISGUST to disgust,
            EmotionType.SURPRISE to surprise
        )
    }
}
