package com.kminder.domain.usecase.analysis

import com.kminder.domain.logic.PlutchikEmotionCalculator
import com.kminder.domain.model.DetailedEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.IntegratedAnalysis
import com.kminder.domain.model.JournalEntry
import javax.inject.Inject

/**
 * 통합 감정 분석 UseCase
 * 여러 개의 일기 엔트리를 기반으로 2/3차 심층 분석을 수행합니다.
 * (현재는 Mock 알고리즘/데이터를 사용합니다)
 */
class AnalyzeIntegratedEmotionUseCase @Inject constructor() {

    operator fun invoke(entries: List<JournalEntry>): IntegratedAnalysis {
        if (entries.isEmpty()) {
            return IntegratedAnalysis(
                recentEmotions = emptyMap(),
                complexEmotionString = "분석 불가",
                keywords = emptyList(),
                summary = "데이터가 부족하여 분석할 수 없습니다.",
                suggestedAction = "일기를 작성해보세요!"
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
        val emotionResult = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(averageEmotionAnalysis)
        
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
        baseKeywords.add(detailedPrimary.koreanName) // e.g., "황홀", "기쁨", "평온"
        if (detailedSecondary != null) {
            baseKeywords.add(detailedSecondary.koreanName)
        }
        
        // 추가 키워드 및 조언
        val (extraKeywords, advice) = when (primary) {
            EmotionType.JOY -> Pair(
                listOf("에너지", "성취"), 
                "지금의 좋은 기분을 기록으로 남겨두세요."
            )
            EmotionType.SADNESS -> Pair(
                listOf("위로", "휴식"),
                "따뜻한 차를 마시며 잠시 쉬어가는 건 어떨까요?"
            )
            EmotionType.ANGER -> Pair(
                listOf("해소", "운동"), // "스트레스" removed, redundant
                "심호흡을 크게 3번 하고 산책을 다녀오세요."
            )
            EmotionType.TRUST -> Pair(
                listOf("안정", "관계"),
                "소중한 사람들에게 안부 인사를 전해보세요."
            )
            EmotionType.FEAR -> Pair(
                listOf("용기", "준비"),
                "걱정거리를 종이에 적고 하나씩 지워보세요."
            )
            EmotionType.ANTICIPATION -> Pair(
                listOf("계획", "설렘"),
                "미래를 위한 작은 계획을 세워보는 게 어때요?"
            )
            EmotionType.DISGUST -> Pair(
                listOf("거리두기", "환기"),
                "잠시 불편한 상황에서 벗어나 나만의 시간을 가지세요."
            )
            EmotionType.SURPRISE -> Pair(
                listOf("발견", "전환"),
                "예상치 못한 변화를 즐겁게 받아들여 보세요."
            )
        }
        
        baseKeywords.addAll(extraKeywords)

        return Triple(
            baseKeywords,
            result.description, 
            advice
        )
    }
}
