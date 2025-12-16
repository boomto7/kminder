package com.kminder.minder.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.kminder.domain.model.ComplexEmotionType
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
     * 기본 감정의 한글 이름 리소스 ID를 반환합니다.
     */
    fun getEmotionNameResId(type: EmotionType): Int {
        return when(type) {
            EmotionType.ANGER -> com.kminder.minder.R.string.emotion_anger
            EmotionType.ANTICIPATION -> com.kminder.minder.R.string.emotion_anticipation
            EmotionType.JOY -> com.kminder.minder.R.string.emotion_joy
            EmotionType.TRUST -> com.kminder.minder.R.string.emotion_trust
            EmotionType.FEAR -> com.kminder.minder.R.string.emotion_fear
            EmotionType.SADNESS -> com.kminder.minder.R.string.emotion_sadness
            EmotionType.DISGUST -> com.kminder.minder.R.string.emotion_disgust
            EmotionType.SURPRISE -> com.kminder.minder.R.string.emotion_surprise
        }
    }

    /**
     * 복합 감정(ComplexEmotionType)의 한글 조작 이름 리소스 ID를 반환합니다.
     */
    fun getComplexEmotionNameResId(type: ComplexEmotionType): Int {
        return when(type) {
            ComplexEmotionType.LOVE -> com.kminder.minder.R.string.emotion_complex_love_title
            ComplexEmotionType.SUBMISSION -> com.kminder.minder.R.string.emotion_complex_submission_title
            ComplexEmotionType.AWE -> com.kminder.minder.R.string.emotion_complex_awe_title
            ComplexEmotionType.DISAPPROVAL -> com.kminder.minder.R.string.emotion_complex_disapproval_title
            ComplexEmotionType.REMORSE -> com.kminder.minder.R.string.emotion_complex_remorse_title
            ComplexEmotionType.CONTEMPT -> com.kminder.minder.R.string.emotion_complex_contempt_title
            ComplexEmotionType.AGGRESSIVENESS -> com.kminder.minder.R.string.emotion_complex_aggressiveness_title
            ComplexEmotionType.OPTIMISM -> com.kminder.minder.R.string.emotion_complex_optimism_title
            ComplexEmotionType.GUILT -> com.kminder.minder.R.string.emotion_complex_guilt_title
            ComplexEmotionType.CURIOSITY -> com.kminder.minder.R.string.emotion_complex_curiosity_title
            ComplexEmotionType.DESPAIR -> com.kminder.minder.R.string.emotion_complex_despair_title
            ComplexEmotionType.UNBELIEF -> com.kminder.minder.R.string.emotion_complex_unbelief_title
            ComplexEmotionType.ENVY -> com.kminder.minder.R.string.emotion_complex_envy_title
            ComplexEmotionType.CYNICISM -> com.kminder.minder.R.string.emotion_complex_cynicism_title
            ComplexEmotionType.PRIDE -> com.kminder.minder.R.string.emotion_complex_pride_title
            ComplexEmotionType.FATALISM -> com.kminder.minder.R.string.emotion_complex_fatalism_title
            ComplexEmotionType.DELIGHT -> com.kminder.minder.R.string.emotion_complex_delight_title
            ComplexEmotionType.SENTIMENTALITY -> com.kminder.minder.R.string.emotion_complex_sentimentality_title
            ComplexEmotionType.SHAME -> com.kminder.minder.R.string.emotion_complex_shame_title
            ComplexEmotionType.OUTRAGE -> com.kminder.minder.R.string.emotion_complex_outrage_title
            ComplexEmotionType.PESSIMISM -> com.kminder.minder.R.string.emotion_complex_pessimism_title
            ComplexEmotionType.MORBIDNESS -> com.kminder.minder.R.string.emotion_complex_morbidness_title
            ComplexEmotionType.DOMINANCE -> com.kminder.minder.R.string.emotion_complex_dominance_title
            ComplexEmotionType.ANXIETY -> com.kminder.minder.R.string.emotion_complex_anxiety_title
        }
    }
}
