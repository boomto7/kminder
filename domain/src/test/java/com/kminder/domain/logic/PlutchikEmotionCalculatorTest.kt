package com.kminder.domain.logic

import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionAnalysis
import org.junit.Assert.assertEquals
import org.junit.Test

class PlutchikEmotionCalculatorTest {

//    @Test
//    fun `calculateComplexEmotions_should_return_correct_dyads_based_on_mock_data`() {
//        // Given: 사용자가 Gemini API 테스트에서 얻었던 것과 유사한 임시 데이터
//        // "오늘 드디어 그 발표를 끝냈다! 결과가 정말 기대돼서 잠이 안 올 것 같다..."
//        val mockAnalysis = EmotionAnalysis(
//            anger = 0.05f,
//            anticipation = 0.85f, // 매우 높음
//            joy = 0.70f,          // 높음
//            trust = 0.30f,
//            fear = 0.20f,
//            sadness = 0.05f,
//            disgust = 0.00f,
//            surprise = 0.40f
//        )
//
//        println("📊 [테스트 입력] 1차 감정 데이터:")
//        println(mockAnalysis)
//        println(" - 주요 감정: 기대(Anticipation)=0.85, 기쁨(Joy)=0.70")
//        println()
//
//        // When: 복합 감정 계산
//        val complexEmotions = PlutchikEmotionCalculator.calculateComplexEmotions(mockAnalysis)
//        val top3 = PlutchikEmotionCalculator.getTopComplexEmotions(mockAnalysis, 3)
//
//        // Then: 결과 검증 및 출력
//        println("🧪 [복합 감정 도출 결과]")
//        complexEmotions.forEachIndexed { index, (type, score) ->
//            println("${index + 1}. ${type.title} (${type.name}): ${String.format("%.3f", score)}")
//            println("   ㄴ 구성: ${type.composition.first} + ${type.composition.second} -> 설명: ${type.description}")
//        }
//        println()
//
//        println("🏆 [Top 3 선정]")
//        top3.forEach { (type, score) ->
//            println(" - ${type.title}: ${String.format("%.3f", score)}")
//        }
//
//        // 검증 1: 1위는 '낙관'(Optimism)이어야 함 (기대 + 기쁨)
//        // 기대(0.85) + 기쁨(0.70) = 1.55 / 2 = 0.775
//        assertEquals(ComplexEmotionType.OPTIMISM, top3[0].first)
//        assertEquals(0.775f, top3[0].second, 0.001f)
//
//        // 검증 2: 2위는 '사랑'(Love) 또는 '공격성'(Aggressiveness - 분노가 낮아서 아닐듯) 또는 '죄책감' 등
//        // 기쁨(0.70) + 신뢰(0.30) = 1.0 / 2 = 0.50 (사랑)
//        assertEquals(ComplexEmotionType.LOVE, top3[1].first)
//        assertEquals(0.50f, top3[1].second, 0.001f)
//    }
}
