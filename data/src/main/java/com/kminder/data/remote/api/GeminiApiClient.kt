package com.kminder.data.remote.api

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.gson.Gson
import com.kminder.data.remote.model.EmotionAnalysisResponse
import com.kminder.data.remote.prompt.EmotionAnalysisPrompt
import com.kminder.domain.model.EmotionAnalysis
import javax.inject.Inject

/**
 * Firebase Vertex AI (Gemini) 클라이언트
 * 
 * 참고: 기존 com.google.ai.client.generativeai는 deprecated되었습니다.
 * Firebase Vertex AI를 사용합니다.
 */
class GeminiApiClient @Inject constructor(
    private val apiKey: String
) {
    private val gson = Gson()
    
    /**
     * Gemini 모델 인스턴스 (Firebase Vertex AI)
     * 
     * 사용 가능한 모델:
     * - "gemini-1.5-flash" (빠르고 효율적)
     * - "gemini-1.5-pro" (더 강력한 분석)
     */
    private val generativeModel by lazy {
//        GenerativeModel(
//            modelName = "gemini-1.5-flash",
//            apiKey = apiKey,
//            generationConfig = generationConfig {
//                temperature = 0.7f
//                topK = 40
//                topP = 0.95f
//                maxOutputTokens = 1024
//            }
//        )
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel("gemini-2.5-flash")

    }
    
    /**
     * 텍스트의 감정을 분석합니다.
     * 
     * @param text 분석할 텍스트
     * @param language 텍스트 언어 ("ko" 또는 "en")
     * @return 감정 분석 결과
     */
    suspend fun analyzeEmotion(text: String, language: String = "ko"): Result<EmotionAnalysis> {
        return try {
            // 프롬프트 생성
            val prompt = EmotionAnalysisPrompt.getPrompt(language, text)
            
            // Firebase Vertex AI (Gemini) 호출
            val response = generativeModel.generateContent(prompt)
            val responseText = response.text ?: throw Exception("응답이 비어있습니다.")
            
            // JSON 파싱
            val cleanedJson = extractJsonFromResponse(responseText)
            val analysisResponse = gson.fromJson(cleanedJson, EmotionAnalysisResponse::class.java)
            
            // Domain 모델로 변환
            val emotionAnalysis = EmotionAnalysis(
                anger = analysisResponse.analysis.anger,
                anticipation = analysisResponse.analysis.anticipation,
                joy = analysisResponse.analysis.joy,
                trust = analysisResponse.analysis.trust,
                fear = analysisResponse.analysis.fear,
                sadness = analysisResponse.analysis.sadness,
                disgust = analysisResponse.analysis.disgust,
                surprise = analysisResponse.analysis.surprise
            )
            
            Result.success(emotionAnalysis)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 응답에서 JSON 부분만 추출합니다.
     */
    private fun extractJsonFromResponse(response: String): String {
        // ```json ... ``` 형식에서 JSON 추출
        val jsonRegex = "```json\\s*([\\s\\S]*?)\\s*```".toRegex()
        val match = jsonRegex.find(response)
        
        return if (match != null) {
            match.groupValues[1].trim()
        } else {
            // 마크다운 코드 블록이 없으면 전체 응답에서 JSON 찾기
            val startIndex = response.indexOf("{")
            val endIndex = response.lastIndexOf("}") + 1
            
            if (startIndex >= 0 && endIndex > startIndex) {
                response.substring(startIndex, endIndex)
            } else {
                response.trim()
            }
        }
    }
}
