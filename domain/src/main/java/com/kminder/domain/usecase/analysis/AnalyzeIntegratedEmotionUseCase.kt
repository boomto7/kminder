package com.kminder.domain.usecase.analysis

import com.kminder.domain.logic.PlutchikEmotionCalculator
import com.kminder.domain.model.DetailedEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionKeyword
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
                suggestedAction = stringProvider.getAnalysisTryJournalingAction(),
                sourceAnalyses = emptyList()
            )
        }

        // sourceAnalyses 수집
        val sourceAnalyses = entries.mapNotNull { it.emotionAnalysis }

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
            detailedEmotionType = detailedPrimary,
            sourceAnalyses = sourceAnalyses
        )
    }

    private fun generateInsights(
        result: PlutchikEmotionCalculator.EmotionResult,
        entries: List<JournalEntry>
    ): Triple<List<EmotionKeyword>, String, String> {
        val primary = result.primaryEmotion
        val secondary = result.secondaryEmotion
        val score = result.score
        
        // 1. 상세 감정 (Intensity 기반) 확인
        val detailedPrimary = DetailedEmotionType.from(primary, score)
        val detailedSecondary = secondary?.let { DetailedEmotionType.from(it, score) }

        // 2. 키워드 생성 (EmotionKeyword 객체 리스트)
        val keywords = mutableListOf<EmotionKeyword>()
        val addedWords = mutableSetOf<String>()
        
        // 2-1. 상세 감정 이름 -> Keyword 변환
        val primaryLabel = stringProvider.getDetailedEmotionName(detailedPrimary)
        keywords.add(EmotionKeyword(word = primaryLabel, emotion = primary, score = score))
        addedWords.add(primaryLabel)

        if (detailedSecondary != null) {
            val secondaryLabel = stringProvider.getDetailedEmotionName(detailedSecondary)
            if (secondaryLabel !in addedWords) {
                keywords.add(EmotionKeyword(word = secondaryLabel, emotion = secondary ?: primary, score = score))
                addedWords.add(secondaryLabel)
            }
        }
        
        // 2-2. AI 추출 키워드 통합
        // 최신 일기에서 추출된 EmotionKeyword들을 가져옵니다.
        val aiKeywords = entries.sortedByDescending { it.createdAt }
            .flatMap { it.emotionAnalysis?.keywords ?: emptyList() }
            .filter { it.word !in addedWords }
            // 점수가 높은 순, 그리고 최신 순으로 정렬되어 있음 (flatMap 순서 덕분)
            .distinctBy { it.word } // 단어 중복 제거
            .take(4) // 최대 4개

        keywords.addAll(aiKeywords)
        aiKeywords.forEach { addedWords.add(it.word) }
        
        // 2-3. 추가 키워드 (추천 행동) - 부족할 경우 보강
        if (keywords.size < 5) {
            val extraKeywords = stringProvider.getActionKeywords(primary)
            for (word in extraKeywords) {
                if (keywords.size >= 6) break // 적당히 제한 (기본+AI+추천 합쳐서 6개 정도)
                if (word !in addedWords) {
                    // 추천 키워드는 점수를 매기기 애매하므로 0.0f 또는 기본 점수 부여
                    // 여기서는 시각적 구분을 위해 0.5f 정도 부여하거나 primaryEmotion을 따르게 함
                    keywords.add(EmotionKeyword(word = word, emotion = primary, score = 0.5f))
                    addedWords.add(word)
                }
            }
        }
        
        // 조언 생성
        val advice = stringProvider.getAdvice(primary)

        return Triple(
            keywords, // List<EmotionKeyword>
            result.description, 
            advice
        )
    }
}

