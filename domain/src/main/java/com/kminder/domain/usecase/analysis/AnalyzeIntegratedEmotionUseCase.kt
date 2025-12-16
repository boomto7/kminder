package com.kminder.domain.usecase.analysis

import com.kminder.domain.logic.PlutchikEmotionCalculator
import com.kminder.domain.model.DetailedEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.IntegratedAnalysis
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.provider.EmotionStringProvider
import javax.inject.Inject

/**
 * 통합 감정 분석 UseCase
 * 여러 개의 일기 엔트리를 기반으로 2/3차 심층 분석을 수행합니다.
 * (현재는 Mock 알고리즘/데이터를 사용합니다)
 */
class AnalyzeIntegratedEmotionUseCase @Inject constructor(
    private val stringProvider: EmotionStringProvider
) {

    operator fun invoke(entries: List<JournalEntry>): IntegratedAnalysis {
        if (entries.isEmpty()) {
            return IntegratedAnalysis(
                recentEmotions = emptyMap(),
                complexEmotionString = stringProvider.getAnalysisImpossibleLabel(),
                keywords = emptyList(),
                summary = stringProvider.getAnalysisInsufficientDataMessage(),
                suggestedAction = stringProvider.getAnalysisTryJournalingAction()
            )
        }

        // 1. 감정 분포 계산 (평균)
        val totalEmotions = mutableMapOf<EmotionType, Float>()
        var entryCountWithEmotion = 0

        entries.forEach { entry ->
            entry.emotionAnalysis?.toMap()?.let { emotions ->
                emotions.forEach { (type, intensity) ->
                    totalEmotions[type] = (totalEmotions[type] ?: 0f) + intensity
                }
                entryCountWithEmotion++
            }
        }

        val averageEmotions = if (entryCountWithEmotion > 0) {
            totalEmotions.mapValues { it.value / entryCountWithEmotion }
        } else {
            emptyMap()
        }

        // 2. 통합 분석 실행 (PlutchikEmotionCalculator 사용)
        val averageEmotionAnalysis = EmotionAnalysis(
            anger = averageEmotions[EmotionType.ANGER] ?: 0f,
            anticipation = averageEmotions[EmotionType.ANTICIPATION] ?: 0f,
            joy = averageEmotions[EmotionType.JOY] ?: 0f,
            trust = averageEmotions[EmotionType.TRUST] ?: 0f,
            fear = averageEmotions[EmotionType.FEAR] ?: 0f,
            sadness = averageEmotions[EmotionType.SADNESS] ?: 0f,
            disgust = averageEmotions[EmotionType.DISGUST] ?: 0f,
            surprise = averageEmotions[EmotionType.SURPRISE] ?: 0f
        )
        
        // 상위 2개 감정 조합 분석
        val emotionResult = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(
            averageEmotionAnalysis,
            stringProvider
        )
        
        // 3. (Mock) 키워드 및 조언 생성
        // 실제로는 감정 조합에 따라 더 정교한 매핑이 필요하거나 Gemini에게 물어볼 부분이지만,
        // 현재는 계산된 2차 감정을 기반으로 간단한 룰 기반 매핑을 제공합니다.
        
        val (keywords, summary, action) = generateMockInsights(emotionResult)

        val detailedPrimary = DetailedEmotionType.from(emotionResult.primaryEmotion, emotionResult.score)

        return IntegratedAnalysis(
            recentEmotions = averageEmotions,
            complexEmotionString = emotionResult.label, // 계산된 복합 감정 이름 (예: "사랑", "낙관")
            keywords = keywords,
            summary = emotionResult.description, // 계산된 설명 사용
            suggestedAction = action,
            complexEmotionType = emotionResult.complexEmotionType,
            detailedEmotionType = detailedPrimary
        )
    }

    private fun generateMockInsights(result: PlutchikEmotionCalculator.EmotionResult): Triple<List<String>, String, String> {
        val primary = result.primaryEmotion
        val secondary = result.secondaryEmotion
        val score = result.score // This is average score. If single, it's the score of primary.
        
        // 1. 상세 감정 (Intensity 기반) 확인
        val detailedPrimary = DetailedEmotionType.from(primary, score)
        val detailedSecondary = secondary?.let { DetailedEmotionType.from(it, score) } // Note: Using avg score for secondary might be slightly inaccurate but acceptable for mock logic

        // 2. 키워드 생성
        val baseKeywords = mutableListOf<String>()
        baseKeywords.add(stringProvider.getDetailedEmotionName(detailedPrimary)) // e.g., "황홀", "기쁨", "평온"
        if (detailedSecondary != null) {
            baseKeywords.add(stringProvider.getDetailedEmotionName(detailedSecondary))
        }
        
        // 추가 키워드 및 조언
        val extraKeywords = stringProvider.getActionKeywords(primary)
        val advice = stringProvider.getAdvice(primary)
        
        baseKeywords.addAll(extraKeywords)

        return Triple(
            baseKeywords,
            result.description, 
            advice
        )
    }
}

