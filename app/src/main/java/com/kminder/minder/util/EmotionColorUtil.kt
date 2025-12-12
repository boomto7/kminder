package com.kminder.minder.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.kminder.domain.model.EmotionType
import com.kminder.minder.ui.theme.EmotionAnger
import com.kminder.minder.ui.theme.EmotionAnticipation
import com.kminder.minder.ui.theme.EmotionDisgust
import com.kminder.minder.ui.theme.EmotionFear
import com.kminder.minder.ui.theme.EmotionJoy
import com.kminder.minder.ui.theme.EmotionSadness
import com.kminder.minder.ui.theme.EmotionSurprise
import com.kminder.minder.ui.theme.EmotionTrust

/**
 * 감정 관련 색상 및 UI 유틸리티
 */
object EmotionColorUtil {

    /**
     * 두 가지 색상을 비율에 따라 섞습니다.
     * @param color1 첫 번째 색상
     * @param color2 두 번째 색상
     * @param ratio 두 번째 색상의 비율 (0.0 ~ 1.0). 기본값 0.5 (50:50).
     */
    fun blendColors(color1: Color, color2: Color, ratio: Float = 0.5f): Color {
        val argb1 = color1.toArgb()
        val argb2 = color2.toArgb()
        val blendedArgb = ColorUtils.blendARGB(argb1, argb2, ratio)
        return Color(blendedArgb)
    }

    /**
     * 기본 감정(EmotionType)에 해당하는 색상을 반환합니다.
     */
    fun getEmotionColor(type: EmotionType): Color {
        return when(type) {
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
     * 기본 감정의 한글 이름을 반환합니다.
     */
    fun getKoreanName(type: EmotionType): String {
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
