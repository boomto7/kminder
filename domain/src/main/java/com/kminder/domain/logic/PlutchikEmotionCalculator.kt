package com.kminder.domain.logic

import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
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
        val category: Category       // 분류 (2차, 3차, 충돌 등)
    )

    enum class Category {
        SINGLE,    // 단일 감정 (하나가 압도적일 때)
        PRIMARY_DYAD, // 2차 감정 (인접: 조화로움)
        TERTIARY_DYAD, // 3차 감정 (거리 2~3: 복합적)
        CONFLICT   // 상반 감정 (거리 4: 충돌)
    }

    /**
     * 감정 분석 결과에서 상위 2개 감정을 추출하여 그 관계를 분석합니다.
     */
    fun analyzeDominantEmotionCombination(
        analysis: EmotionAnalysis,
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
            return EmotionResult(
                label = getKoreanName(first.first),
                description = "뚜렷하게 ${getKoreanName(first.first)}을(를) 느끼고 계시네요.",
                primaryEmotion = first.first,
                secondaryEmotion = null,
                score = first.second,
                category = Category.SINGLE
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

        return when (distance) {
            1 -> {
                // === 2차 감정 (인접) ===
                // 미리 정의된 ComplexEmotionType에서 찾습니다.
                val complexType = findComplexEmotionType(first.first, second.first)
                EmotionResult(
                    label = complexType?.title ?: "${getKoreanName(first.first)} + ${getKoreanName(second.first)}",
                    description = complexType?.description ?: "두 감정이 조화롭게 섞여 있습니다.",
                    primaryEmotion = first.first,
                    secondaryEmotion = second.first,
                    score = avgScore,
                    category = Category.PRIMARY_DYAD
                )
            }
            4 -> {
                // === 상반 감정 (정반대) ===
                EmotionResult(
                    label = "${getKoreanName(first.first)}과(와) ${getKoreanName(second.first)}의 충돌",
                    description = "서로 반대되는 감정이 마음속에서 충돌하고 있어 혼란스러울 수 있습니다.",
                    primaryEmotion = first.first,
                    secondaryEmotion = second.first,
                    score = avgScore,
                    category = Category.CONFLICT
                )
            }
            else -> {
                // === 3차 감정 (거리 2 or 3) ===
                // 필요하다면 여기서도 3차 감정용 매핑(Guilt 등)을 추가할 수 있습니다.
                val complexType = findComplexEmotionType(first.first, second.first) // 3차 감정도 일부 정의되어 있다면 찾음
                
                if (complexType != null) {
                     EmotionResult(
                        label = complexType.title,
                        description = complexType.description,
                        primaryEmotion = first.first,
                        secondaryEmotion = second.first,
                        score = avgScore,
                        category = Category.TERTIARY_DYAD
                    )
                } else {
                    EmotionResult(
                        label = "복합적인 ${getKoreanName(first.first)}과(와) ${getKoreanName(second.first)}",
                        description = "서로 다른 성격의 두 감정이 복잡하게 얽혀 있습니다.",
                        primaryEmotion = first.first,
                        secondaryEmotion = second.first,
                        score = avgScore,
                        category = Category.TERTIARY_DYAD
                    )
                }
            }
        }
    }

    private fun findComplexEmotionType(type1: EmotionType, type2: EmotionType): ComplexEmotionType? {
        return ComplexEmotionType.values().find {
            (it.composition.first == type1 && it.composition.second == type2) ||
            (it.composition.first == type2 && it.composition.second == type1)
        }
    }

    private fun getKoreanName(type: EmotionType): String {
        return when(type) {
            EmotionType.ANGER -> "분노"
            EmotionType.ANTICIPATION -> "기대"
            EmotionType.JOY -> "기쁨"
            EmotionType.TRUST -> "신뢰"
            EmotionType.FEAR -> "두려움"
            EmotionType.SADNESS -> "슬픔"
            EmotionType.DISGUST -> "혐오"
            EmotionType.SURPRISE -> "놀람"
        }
    }
}
