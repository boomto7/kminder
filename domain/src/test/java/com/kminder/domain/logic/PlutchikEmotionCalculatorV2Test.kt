package com.kminder.domain.logic

import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.DetailedEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
import com.kminder.domain.provider.EmotionStringProvider
import org.junit.Assert.assertEquals
import org.junit.Test

class PlutchikEmotionCalculatorV2Test {

    private val fakeStringProvider = object : EmotionStringProvider {
        override fun getEmotionName(type: EmotionType): String = when (type) {
            EmotionType.JOY -> "기쁨"
            EmotionType.TRUST -> "신뢰"
            EmotionType.FEAR -> "두려움"
            EmotionType.SURPRISE -> "놀람"
            EmotionType.SADNESS -> "슬픔"
            EmotionType.DISGUST -> "혐오"
            EmotionType.ANGER -> "분노"
            EmotionType.ANTICIPATION -> "기대"
        }

        override fun getDetailedEmotionName(type: DetailedEmotionType): String = type.name

        override fun getComplexEmotionTitle(type: ComplexEmotionType): String = when (type) {
            ComplexEmotionType.LOVE -> "사랑"
            ComplexEmotionType.GUILT -> "죄책감"
            else -> type.name
        }

        override fun getComplexEmotionDescription(type: ComplexEmotionType): String = "Description"
        override fun getAdvice(primaryEmotion: EmotionType): String = "Advice"
        override fun getActionKeywords(primaryEmotion: EmotionType): List<String> = emptyList()
        override fun getAnalysisImpossibleLabel(): String = "Impossible"
        override fun getAnalysisInsufficientDataMessage(): String = "Insufficient"
        override fun getAnalysisTryJournalingAction(): String = "Try Journaling"
        override fun getSingleEmotionDescription(emotionName: String): String = "Single Desc"
        override fun getConflictLabel(emotionName1: String, emotionName2: String): String = "${emotionName1}과(와) ${emotionName2}의 충돌"
        override fun getConflictDescription(): String = "Conflict Desc"
        override fun getHarmonyDescription(): String = "Harmony Desc"
        override fun getComplexEmotionDefaultLabel(): String = "Complex Label"
        override fun getComplexEmotionDefaultDescription(): String = "Complex Desc"
        override fun getComplicatedEmotionDefaultLabel(): String = "Complicated Label"
        override fun getComplicatedEmotionDefaultDescription(): String = "Complicated Desc"
    }

    @Test
    fun `analyze_secondary_dyad_should_return_Love`() {
        // 기쁨(Joy) + 신뢰(Trust) = 인접(거리 1) -> 사랑(Love)
        val input = EmotionAnalysis(
            joy = 0.8f,
            trust = 0.7f,
            anger = 0.1f // 나머지 낮음
        )

        val result = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(input, fakeStringProvider)

        println("Case 1 (2차 감정): ${result.label} (${result.category})")
        
        assertEquals(PlutchikEmotionCalculator.Category.PRIMARY_DYAD, result.category)
        assertEquals("사랑", result.label)
    }

    @Test
    fun `analyze_conflict_dyad_should_return_Conflict`() {
        // 기쁨(Joy) + 슬픔(Sadness) = 정반대(거리 4) -> 충돌
        val input = EmotionAnalysis(
            joy = 0.8f,
            sadness = 0.8f,
            trust = 0.1f
        )

        val result = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(input, fakeStringProvider)

        println("Case 2 (상반 감정): ${result.label} (${result.category})")

        assertEquals(PlutchikEmotionCalculator.Category.CONFLICT, result.category)
        assertEquals("기쁨과(와) 슬픔의 충돌", result.label)
    }
    
    @Test
    fun `analyze_tertiary_dyad_should_return_Complex`() {
        // 기쁨(Joy) + 두려움(Fear) = 거리 2 -> 3차/복합
        val input = EmotionAnalysis(
            joy = 0.8f,
            fear = 0.7f,
            trust = 0.1f
        )

        val result = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(input, fakeStringProvider)

        println("Case 3 (3차 감정): ${result.label} (${result.category})")

        // ComplexEmotionType에 'GUILT(죄책감)'이 정의되어 있으면 "죄책감", 아니면 "복합적인..."
        // 현재 ComplexEmotionType에 GUILT가 정의되어 있음.
        assertEquals(PlutchikEmotionCalculator.Category.SECONDARY_DYAD, result.category)
        // 만약 GUILT 정의를 뺐다면 "복합적인 기쁨과(와) 두려움"이 나와야 함
        assertEquals("죄책감", result.label) 
    }
    
    @Test
    fun `analyze_single_emotion_should_return_Single`() {
        // 기쁨 하나만 압도적으로 높음
        val input = EmotionAnalysis(
            joy = 0.9f,
            trust = 0.2f // 0.9의 50%인 0.45보다 낮음 -> 무시됨
        )
        
        val result = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(input, fakeStringProvider)
        
        println("Case 4 (단일 감정): ${result.label} (${result.category})")
        
        assertEquals(PlutchikEmotionCalculator.Category.SINGLE, result.category)
        assertEquals("기쁨", result.label)
    }
}

