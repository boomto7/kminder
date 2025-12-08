package com.kminder.domain.repository

import com.kminder.domain.model.EmotionAnalysis

/**
 * 감정 분석 Repository Interface
 */
interface EmotionAnalysisRepository {
    
    /**
     * 텍스트를 분석하여 감정 분석 결과를 반환합니다.
     * 
     * @param text 분석할 텍스트
     * @param language 텍스트 언어 ("ko" 또는 "en")
     * @return 감정 분석 결과
     */
    suspend fun analyzeEmotion(text: String, language: String = "ko"): Result<EmotionAnalysis>
}
