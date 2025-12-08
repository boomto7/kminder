package com.kminder.domain.usecase.emotion

import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.repository.EmotionAnalysisRepository
import javax.inject.Inject

/**
 * 텍스트의 감정을 분석하는 UseCase
 */
class AnalyzeEmotionUseCase @Inject constructor(
    private val emotionAnalysisRepository: EmotionAnalysisRepository
) {
    /**
     * 텍스트를 분석하여 감정 분석 결과를 반환합니다.
     * 
     * @param text 분석할 텍스트
     * @param language 텍스트 언어 ("ko" 또는 "en")
     * @return 감정 분석 결과
     */
    suspend operator fun invoke(text: String, language: String = "ko"): Result<EmotionAnalysis> {
        if (text.isBlank()) {
            return Result.failure(IllegalArgumentException("텍스트가 비어있습니다."))
        }
        
        return emotionAnalysisRepository.analyzeEmotion(text, language)
    }
}
