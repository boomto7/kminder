package com.kminder.minder.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionType

/**
 * 감정 타입에 따른 색상 테마 유틸리티
 */
object EmotionColorTheme {

    /**
     * 기본 감정(EmotionType)에 해당하는 색상을 반환합니다.
     */
    fun getBaseColor(type: EmotionType): Color {
        return when (type) {
            EmotionType.JOY -> EmotionJoy
            EmotionType.TRUST -> EmotionTrust
            EmotionType.FEAR -> EmotionFear
            EmotionType.SURPRISE -> EmotionSurprise
            EmotionType.SADNESS -> EmotionSadness
            EmotionType.DISGUST -> EmotionDisgust
            EmotionType.ANGER -> EmotionAnger
            EmotionType.ANTICIPATION -> EmotionAnticipation
        }
    }

    /**
     * 복합 감정(ComplexEmotionType)을 구성하는 두 가지 기본 감정 색상을 반환합니다.
     */
    fun getComplexColors(type: ComplexEmotionType): Pair<Color, Color> {
        val (first, second) = type.composition
        return getBaseColor(first) to getBaseColor(second)
    }

    /**
     * 복합 감정(ComplexEmotionType)의 그라디언트 브러쉬를 반환합니다.
     * (좌상단 -> 우하단 방향의 LinearGradient)
     */
    fun getComplexGradient(type: ComplexEmotionType): Brush {
        val (color1, color2) = getComplexColors(type)
        return Brush.linearGradient(
            colors = listOf(color1, color2)
        )
    }

    /**
     * 상세 감정(DetailedEmotionType)의 색상을 반환합니다.
     * 강도(Level)에 따라 색상의 진하기를 조절합니다.
     */
    fun getDetailedColor(type: com.kminder.domain.model.DetailedEmotionType): Color {
        val base = getBaseColor(type.baseType)
        return when (type.intensityLevel) {
            1 -> base.copy(alpha = 0.6f).compositeOver(Color.White) // 약함: 연하게 (흰색과 섞음)
            2 -> base // 중간: 기본 색상
            3 -> adjustBrightness(base, 0.85f) // 강함: 진하게 (밝기 조절)
            else -> base
        }
    }

    /**
     * 단일 감정의 그라디언트 브러쉬를 반환합니다.
     * (상세 색상 -> 약간 더 연한 버전)
     */
    fun getSingleEmotionGradient(type: com.kminder.domain.model.DetailedEmotionType): Brush {
        val mainColor = getDetailedColor(type)
        val subColor = mainColor.copy(alpha = 0.7f).compositeOver(Color.White)

        return Brush.linearGradient(
            colors = listOf(mainColor, subColor)
        )
    }

    private fun adjustBrightness(color: Color, factor: Float): Color {
        return Color(
            red = (color.red * factor).coerceIn(0f, 1f),
            green = (color.green * factor).coerceIn(0f, 1f),
            blue = (color.blue * factor).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    }
}
