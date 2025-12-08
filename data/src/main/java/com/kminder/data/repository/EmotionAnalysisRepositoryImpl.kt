package com.kminder.data.repository

import com.kminder.data.remote.api.GeminiApiClient
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.repository.EmotionAnalysisRepository
import javax.inject.Inject

/**
 * EmotionAnalysisRepository 구현체
 */
class EmotionAnalysisRepositoryImpl @Inject constructor(
    private val geminiApiClient: GeminiApiClient
) : EmotionAnalysisRepository {
    
    override suspend fun analyzeEmotion(text: String, language: String): Result<EmotionAnalysis> {
        return geminiApiClient.analyzeEmotion(text, language)
    }
}
