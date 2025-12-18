package com.kminder.domain.logic

import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
import com.kminder.domain.provider.EmotionStringProvider
import kotlin.math.abs
import kotlin.math.min

/**
 * 플루치크 이론에 기반하여 사용자의 심층 감정 상태(2차/3차/충돌)를 분석하는 계산기
 */
object PlutchikEmotionCalculator {

    // 플루치크 휠의 감정 순서 (시계 방향)
    // 이 순서에 따라 인접(거리 1), 하나 건너뜀(거리 2), 정반대(거리 4)를 계산합니다.
    private val PLUTCHIK_WHEEL_ORDER = listOf(
        EmotionType.JOY,          // 기쁨
        EmotionType.TRUST,        // 신뢰
        EmotionType.FEAR,         // 두려움
        EmotionType.SURPRISE,     // 놀람
        EmotionType.SADNESS,      // 슬픔
        EmotionType.DISGUST,      // 혐오
        EmotionType.ANGER,        // 분노
        EmotionType.ANTICIPATION  // 기대
    )

    data class EmotionResult(
        val label: String,           // 사용자에게 보여줄 최종 감정 이름 (예: "낙관", "기쁨과 슬픔의 충돌")
        val description: String,     // 설명
        val primaryEmotion: EmotionType,
        val secondaryEmotion: EmotionType?,
        val score: Float,            // 해당 조합의 점수 (평균)
        val category: ComplexEmotionType.Category,      // 분류 (2차, 3차, 충돌 등)
        val complexEmotionType: ComplexEmotionType? = null // 매칭된 복합 감정 타입
    )

    /**
     * 감정 분석 결과에서 상위 2개 감정을 추출하여 그 관계를 분석합니다.
     */
    fun analyzeDominantEmotionCombination(
        analysis: EmotionAnalysis,
        stringProvider: EmotionStringProvider,
        singleEmotionThresholdRatio: Float = 0.5f // 2등 점수가 1등 점수의 50% 미만이면 단일 감정으로 처리할 비율
    ): EmotionResult {
        // 1. 모든 감정을 점수 내림차순으로 정렬
        val sortedEmotions = analysis.toMap().toList()
            .sortedByDescending { it.second }

        val first = sortedEmotions[0] // 1등 (Type, Score)
        val second = sortedEmotions[1] // 2등 (Type, Score)

        // 2. 단일 감정 처리 로직
        // 2등 감정이 1등 감정에 비해 너무 미미하거나, 2등 점수 자체가 너무 낮으면(예: 0.1 미만) 단일 감정으로 봅니다.
        if (second.second < 0.1f || second.second < (first.second * singleEmotionThresholdRatio)) {
            val score = first.second
            
            // 점수에 따른 강도(Intensity) 결정
            val intensity = when {
                score >= 0.8f -> ComplexEmotionType.Intensity.STRONG
                score <= 0.4f -> ComplexEmotionType.Intensity.WEAK
                else -> ComplexEmotionType.Intensity.MEDIUM
            }

            // 단일 감정 타입 찾기 (예: JOY + STRONG -> ECSTASY)
            val singleComplexType = ComplexEmotionType.findSingle(first.first, intensity)
            
            // 이름과 설명 가져오기 (만약 singleComplexType이 있으면 그것을 우선 사용)
            val emotionName = singleComplexType?.let { stringProvider.getComplexEmotionTitle(it) } 
                ?: stringProvider.getEmotionName(first.first)
                
            val emotionDesc = singleComplexType?.let { stringProvider.getComplexEmotionDescription(it) }
                ?: stringProvider.getSingleEmotionDescription(emotionName)

            return EmotionResult(
                label = emotionName,
                description = emotionDesc,
                primaryEmotion = first.first,
                secondaryEmotion = null,
                score = score,
                category = ComplexEmotionType.Category.SINGLE_EMOTION,
                complexEmotionType = singleComplexType
            )
        }

        // 3. 거리 계산 (Circular Distance)
        val index1 = PLUTCHIK_WHEEL_ORDER.indexOf(first.first)
        val index2 = PLUTCHIK_WHEEL_ORDER.indexOf(second.first)
        val rawDistance = abs(index1 - index2)
        // 원형 큐이므로 8 - distance와 비교하여 더 짧은 거리를 선택 (예: 0번과 7번은 거리 1)
        val distance = min(rawDistance, 8 - rawDistance)

        // 평균 점수
        val avgScore = (first.second + second.second) / 2
        
        // Find Complex Type first if possible
        val complexType = findComplexEmotionType(first.first, second.first)

        val emotionName1 = stringProvider.getEmotionName(first.first)
        val emotionName2 = stringProvider.getEmotionName(second.first)

        return when (distance) {
            1 -> {
                // === 1차 이중감정 (Primary Dyad) ===
                EmotionResult(
                    label = complexType?.let { stringProvider.getComplexEmotionTitle(it) } ?: "$emotionName1 + $emotionName2",
                    description = complexType?.let { stringProvider.getComplexEmotionDescription(it) } ?: stringProvider.getHarmonyDescription(),
                    primaryEmotion = first.first,
                    secondaryEmotion = second.first,
                    score = avgScore,
                    category = ComplexEmotionType.Category.PRIMARY_DYAD,
                    complexEmotionType = complexType
                )
            }
            4 -> {
                // === 상반 감정 (정반대) ===
                EmotionResult(
                    label = stringProvider.getConflictLabel(emotionName1, emotionName2),
                    description = stringProvider.getConflictDescription(),
                    primaryEmotion = first.first,
                    secondaryEmotion = second.first,
                    score = avgScore,
                    category = ComplexEmotionType.Category.OPPOSITE,
                    complexEmotionType = complexType
                )
            }
            2 -> {
                // === 2차 이중감정 (Secondary Dyad) ===
                 EmotionResult(
                    label = complexType?.let { stringProvider.getComplexEmotionTitle(it) } ?: stringProvider.getComplexEmotionDefaultLabel(),
                    description = complexType?.let { stringProvider.getComplexEmotionDescription(it) } ?: stringProvider.getComplexEmotionDefaultDescription(),
                    primaryEmotion = first.first,
                    secondaryEmotion = second.first,
                    score = avgScore,
                    category = ComplexEmotionType.Category.SECONDARY_DYAD,
                    complexEmotionType = complexType
                )
            }
            else -> {
                // === 3차 이중감정 (Tertiary Dyad) ===
                EmotionResult(
                    label = complexType?.let { stringProvider.getComplexEmotionTitle(it) } ?: stringProvider.getComplicatedEmotionDefaultLabel(),
                    description = complexType?.let { stringProvider.getComplexEmotionDescription(it) } ?: stringProvider.getComplicatedEmotionDefaultDescription(),
                    primaryEmotion = first.first,
                    secondaryEmotion = second.first,
                    score = avgScore,
                    category = ComplexEmotionType.Category.TERTIARY_DYAD,
                    complexEmotionType = complexType
                )
            }
        }
    }

    private fun findComplexEmotionType(type1: EmotionType, type2: EmotionType): ComplexEmotionType? {
        return ComplexEmotionType.find(type1, type2)
    }
}
