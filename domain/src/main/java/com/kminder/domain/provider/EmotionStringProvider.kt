package com.kminder.domain.provider

import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.DetailedEmotionType
import com.kminder.domain.model.EmotionType

/**
 * 감정 관련 문자열 리소스를 제공하는 인터페이스.
 * Domain Layer가 Android Resources에 직접 의존하지 않도록 추상화합니다.
 */
interface EmotionStringProvider {
    // Basic Emotion Name
    fun getEmotionName(type: EmotionType): String

    // Detailed Emotion Name
    fun getDetailedEmotionName(type: DetailedEmotionType): String

    // Complex Emotion
    fun getComplexEmotionTitle(type: ComplexEmotionType): String
    fun getComplexEmotionDescription(type: ComplexEmotionType): String
    fun getComplexEmotionAdvice(type: ComplexEmotionType): String

    // Advice
    fun getAdvice(primaryEmotion: EmotionType): String
    fun getActionKeywords(primaryEmotion: EmotionType): List<String>

    // Formats & Messages
    fun getAnalysisImpossibleLabel(): String
    fun getAnalysisInsufficientDataMessage(): String
    fun getAnalysisTryJournalingAction(): String
    
    fun getSingleEmotionDescription(emotionName: String): String
    fun getConflictLabel(emotionName1: String, emotionName2: String): String
    fun getConflictDescription(): String
    
    fun getHarmonyDescription(): String
    fun getComplexEmotionDefaultLabel(): String
    fun getComplexEmotionDefaultDescription(): String
    fun getComplicatedEmotionDefaultLabel(): String
    fun getComplicatedEmotionDefaultDescription(): String
}
