package com.kminder.domain.logic

import com.kminder.domain.model.EmotionAnalysis
import org.junit.Assert.assertEquals
import org.junit.Test

class PlutchikEmotionCalculatorV2Test {

    @Test
    fun `analyze_secondary_dyad_should_return_Love`() {
        // 기쁨(Joy) + 신뢰(Trust) = 인접(거리 1) -> 사랑(Love)
        val input = EmotionAnalysis(
            joy = 0.8f,
            trust = 0.7f,
            anger = 0.1f // 나머지 낮음
        )

        val result = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(input)

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

        val result = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(input)

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

        val result = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(input)

        println("Case 3 (3차 감정): ${result.label} (${result.category})")

        // ComplexEmotionType에 'GUILT(죄책감)'이 정의되어 있으면 "죄책감", 아니면 "복합적인..."
        // 현재 ComplexEmotionType에 GUILT가 정의되어 있음.
        assertEquals(PlutchikEmotionCalculator.Category.TERTIARY_DYAD, result.category)
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
        
        val result = PlutchikEmotionCalculator.analyzeDominantEmotionCombination(input)
        
        println("Case 4 (단일 감정): ${result.label} (${result.category})")
        
        assertEquals(PlutchikEmotionCalculator.Category.SINGLE, result.category)
        assertEquals("기쁨", result.label)
    }
}
