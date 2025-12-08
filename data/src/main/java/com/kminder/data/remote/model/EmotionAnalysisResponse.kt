package com.kminder.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Gemini API 감정 분석 응답
 */
data class EmotionAnalysisResponse(
    @SerializedName("analysis")
    val analysis: EmotionData
)

/**
 * 감정 데이터
 */
data class EmotionData(
    @SerializedName("anger")
    val anger: Float = 0f,
    
    @SerializedName("anticipation")
    val anticipation: Float = 0f,
    
    @SerializedName("joy")
    val joy: Float = 0f,
    
    @SerializedName("trust")
    val trust: Float = 0f,
    
    @SerializedName("fear")
    val fear: Float = 0f,
    
    @SerializedName("sadness")
    val sadness: Float = 0f,
    
    @SerializedName("disgust")
    val disgust: Float = 0f,
    
    @SerializedName("surprise")
    val surprise: Float = 0f
)
