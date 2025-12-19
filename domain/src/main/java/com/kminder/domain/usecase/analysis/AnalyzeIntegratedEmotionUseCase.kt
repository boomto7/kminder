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
        
        // 3. 키워드 및 조언 생성
        val (keywords, summary, action) = generateInsights(emotionResult, entries)

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

    private fun generateInsights(
        result: PlutchikEmotionCalculator.EmotionResult,
        entries: List<JournalEntry>
    ): Triple<List<String>, String, String> {
        val primary = result.primaryEmotion
        val secondary = result.secondaryEmotion
        val score = result.score
        
        // 1. 상세 감정 (Intensity 기반) 확인
        val detailedPrimary = DetailedEmotionType.from(primary, score)
        val detailedSecondary = secondary?.let { DetailedEmotionType.from(it, score) }

        // 2. 키워드 생성
        val keywords = mutableSetOf<String>() // 중복 방지
        
        // 2-1. 상세 감정 이름
        keywords.add(stringProvider.getDetailedEmotionName(detailedPrimary))
        if (detailedSecondary != null) {
            keywords.add(stringProvider.getDetailedEmotionName(detailedSecondary))
        }
        
        // 2-2. AI 추출 키워드 (각 일기에서 추출된 키워드 활용)
        // 최신 일기의 키워드를 우선적으로 포함
        val aiKeywords = entries.sortedByDescending { it.createdAt }
            .flatMap { it.emotionAnalysis?.keywords ?: emptyList() }
            .distinct()
            .take(4) // 최대 4개까지만 가져옴
            
        keywords.addAll(aiKeywords)
        
        // 2-3. 추가 키워드 (추천 행동) - 키워드가 부족할 경우 보강
        if (keywords.size < 5) {
            val extraKeywords = stringProvider.getActionKeywords(primary)
            keywords.addAll(extraKeywords)
        }
        
        // 조언 생성
        val advice = stringProvider.getAdvice(primary)

        return Triple(
            keywords.toList(),
            result.description, 
            advice
        )
    }
}

