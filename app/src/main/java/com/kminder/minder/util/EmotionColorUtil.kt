package com.kminder.minder.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionResult
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
     * 복합 감정(ComplexEmotionType)에 해당하는 색상을 반환합니다.
     */
    fun getComplexEmotionColor(type: ComplexEmotionType): Color {
        val (primary, secondary) = type.composition
        
        return if (type.category == ComplexEmotionType.Category.SINGLE_EMOTION) {
            val baseColor = getEmotionColor(primary)
            when (type.intensity) {
                ComplexEmotionType.Intensity.STRONG -> blendColors(Color.Black, baseColor, 0.2f) // Slightly darker
                ComplexEmotionType.Intensity.WEAK -> blendColors(Color.White, baseColor, 0.4f) // Lighter
                else -> baseColor // Medium is base
            }
        } else {
            // 복합 감정은 두 구성 감정의 색상을 혼합
            val c1 = getEmotionColor(primary)
            val c2 = getEmotionColor(secondary)
            blendColors(c1, c2)
        }
    }

    /**
     * 복합 감정(ComplexEmotionType)의 한글 조작 이름 리소스 ID를 반환합니다.
     */
    fun getComplexEmotionNameResId(type: ComplexEmotionType): Int {
        return when(type) {
            // Primary Dyads
            ComplexEmotionType.LOVE -> com.kminder.minder.R.string.emotion_complex_love_title
            ComplexEmotionType.POSSESSIVENESS -> com.kminder.minder.R.string.emotion_complex_possessiveness_title
            ComplexEmotionType.SUBMISSION -> com.kminder.minder.R.string.emotion_complex_submission_title
            ComplexEmotionType.SERVILITY -> com.kminder.minder.R.string.emotion_complex_servility_title
            ComplexEmotionType.AWE -> com.kminder.minder.R.string.emotion_complex_awe_title
            ComplexEmotionType.ALARM -> com.kminder.minder.R.string.emotion_complex_alarm_title
            ComplexEmotionType.DISAPPROVAL -> com.kminder.minder.R.string.emotion_complex_disapproval_title
            ComplexEmotionType.SHOCK -> com.kminder.minder.R.string.emotion_complex_shock_title
            ComplexEmotionType.REMORSE -> com.kminder.minder.R.string.emotion_complex_remorse_title
            ComplexEmotionType.SELF_LOATHING -> com.kminder.minder.R.string.emotion_complex_self_loathing_title
            ComplexEmotionType.CONTEMPT -> com.kminder.minder.R.string.emotion_complex_contempt_title
            ComplexEmotionType.ENVY_CONTEMPT -> com.kminder.minder.R.string.emotion_complex_envy_contempt_title
            ComplexEmotionType.AGGRESSIVENESS -> com.kminder.minder.R.string.emotion_complex_aggressiveness_title
            ComplexEmotionType.VENGEANCE -> com.kminder.minder.R.string.emotion_complex_vengeance_title
            ComplexEmotionType.OPTIMISM -> com.kminder.minder.R.string.emotion_complex_optimism_title
            ComplexEmotionType.NAIVETY -> com.kminder.minder.R.string.emotion_complex_naivety_title

            // Secondary Dyads
            ComplexEmotionType.GUILT -> com.kminder.minder.R.string.emotion_complex_guilt_title
            ComplexEmotionType.SELF_DENIAL -> com.kminder.minder.R.string.emotion_complex_self_denial_title
            ComplexEmotionType.CURIOSITY -> com.kminder.minder.R.string.emotion_complex_curiosity_title
            ComplexEmotionType.AMBIVALENCE_CURIOSITY -> com.kminder.minder.R.string.emotion_complex_ambivalence_curiosity_title
            ComplexEmotionType.DESPAIR -> com.kminder.minder.R.string.emotion_complex_despair_title
            ComplexEmotionType.HELPLESSNESS -> com.kminder.minder.R.string.emotion_complex_helplessness_title
            ComplexEmotionType.UNBELIEF -> com.kminder.minder.R.string.emotion_complex_unbelief_title
            ComplexEmotionType.ABHORRENCE -> com.kminder.minder.R.string.emotion_complex_abhorrence_title
            ComplexEmotionType.ENVY -> com.kminder.minder.R.string.emotion_complex_envy_title
            ComplexEmotionType.RESENTMENT -> com.kminder.minder.R.string.emotion_complex_resentment_title
            ComplexEmotionType.CYNICISM -> com.kminder.minder.R.string.emotion_complex_cynicism_title
            ComplexEmotionType.DISTRUST -> com.kminder.minder.R.string.emotion_complex_distrust_title
            ComplexEmotionType.PRIDE -> com.kminder.minder.R.string.emotion_complex_pride_title
            ComplexEmotionType.HUBRIS -> com.kminder.minder.R.string.emotion_complex_hubris_title
            ComplexEmotionType.HOPE -> com.kminder.minder.R.string.emotion_complex_hope_title
            ComplexEmotionType.FATALISM -> com.kminder.minder.R.string.emotion_complex_fatalism_title

            // Tertiary Dyads
            ComplexEmotionType.DELIGHT -> com.kminder.minder.R.string.emotion_complex_delight_title
            ComplexEmotionType.CONFUSED_JOY -> com.kminder.minder.R.string.emotion_complex_confused_joy_title
            ComplexEmotionType.SENTIMENTALITY -> com.kminder.minder.R.string.emotion_complex_sentimentality_title
            ComplexEmotionType.SELF_PITY -> com.kminder.minder.R.string.emotion_complex_self_pity_title
            ComplexEmotionType.SHAME -> com.kminder.minder.R.string.emotion_complex_shame_title
            ComplexEmotionType.HUMILIATION -> com.kminder.minder.R.string.emotion_complex_humiliation_title
            ComplexEmotionType.OUTRAGE -> com.kminder.minder.R.string.emotion_complex_outrage_title
            ComplexEmotionType.DISORIENTATION -> com.kminder.minder.R.string.emotion_complex_disorientation_title
            ComplexEmotionType.PESSIMISM -> com.kminder.minder.R.string.emotion_complex_pessimism_title
            ComplexEmotionType.RESIGNATION -> com.kminder.minder.R.string.emotion_complex_resignation_title
            ComplexEmotionType.MORBIDNESS -> com.kminder.minder.R.string.emotion_complex_morbidness_title
            ComplexEmotionType.DERISION -> com.kminder.minder.R.string.emotion_complex_derision_title
            ComplexEmotionType.DOMINANCE -> com.kminder.minder.R.string.emotion_complex_dominance_title
            ComplexEmotionType.TYRANNY -> com.kminder.minder.R.string.emotion_complex_tyranny_title
            ComplexEmotionType.ANXIETY -> com.kminder.minder.R.string.emotion_complex_anxiety_title
            ComplexEmotionType.DREAD -> com.kminder.minder.R.string.emotion_complex_dread_title

            // Opposite Emotions
            ComplexEmotionType.BITTERSWEETNESS -> com.kminder.minder.R.string.emotion_complex_bittersweetness_title
            ComplexEmotionType.NOSTALGIA -> com.kminder.minder.R.string.emotion_complex_nostalgia_title
            ComplexEmotionType.AMBIVALENCE -> com.kminder.minder.R.string.emotion_complex_ambivalence_title
            ComplexEmotionType.MISTRUST -> com.kminder.minder.R.string.emotion_complex_mistrust_title
            ComplexEmotionType.FROZENNESS -> com.kminder.minder.R.string.emotion_complex_frozenness_title
            ComplexEmotionType.CONFUSION -> com.kminder.minder.R.string.emotion_complex_confusion_title

            // Single Emotions - Strong
            ComplexEmotionType.ECSTASY -> com.kminder.minder.R.string.emotion_detail_ecstasy
            ComplexEmotionType.ADMIRATION -> com.kminder.minder.R.string.emotion_detail_admiration
            ComplexEmotionType.TERROR -> com.kminder.minder.R.string.emotion_detail_terror
            ComplexEmotionType.AMAZEMENT -> com.kminder.minder.R.string.emotion_detail_amazement
            ComplexEmotionType.GRIEF -> com.kminder.minder.R.string.emotion_detail_grief
            ComplexEmotionType.LOATHING -> com.kminder.minder.R.string.emotion_detail_loathing
            ComplexEmotionType.RAGE -> com.kminder.minder.R.string.emotion_detail_rage
            ComplexEmotionType.VIGILANCE -> com.kminder.minder.R.string.emotion_detail_vigilance

            // Single Emotions - Medium
            ComplexEmotionType.JOY -> com.kminder.minder.R.string.emotion_joy
            ComplexEmotionType.TRUST -> com.kminder.minder.R.string.emotion_trust
            ComplexEmotionType.FEAR -> com.kminder.minder.R.string.emotion_fear
            ComplexEmotionType.SURPRISE -> com.kminder.minder.R.string.emotion_surprise
            ComplexEmotionType.SADNESS -> com.kminder.minder.R.string.emotion_sadness
            ComplexEmotionType.DISGUST -> com.kminder.minder.R.string.emotion_disgust
            ComplexEmotionType.ANGER -> com.kminder.minder.R.string.emotion_anger
            ComplexEmotionType.ANTICIPATION -> com.kminder.minder.R.string.emotion_anticipation

            // Single Emotions - Weak
            ComplexEmotionType.SERENITY -> com.kminder.minder.R.string.emotion_detail_serenity
            ComplexEmotionType.ACCEPTANCE -> com.kminder.minder.R.string.emotion_detail_acceptance
            ComplexEmotionType.APPREHENSION -> com.kminder.minder.R.string.emotion_detail_apprehension
            ComplexEmotionType.DISTRACTION -> com.kminder.minder.R.string.emotion_detail_distraction
            ComplexEmotionType.PENSIVENESS -> com.kminder.minder.R.string.emotion_detail_pensiveness
            ComplexEmotionType.BOREDOM -> com.kminder.minder.R.string.emotion_detail_boredom
            ComplexEmotionType.ANNOYANCE -> com.kminder.minder.R.string.emotion_detail_annoyance
            ComplexEmotionType.INTEREST -> com.kminder.minder.R.string.emotion_detail_interest
        }
    }

    /**
     * 감정 결과(EmotionResult)에 기초하여 UI에 사용할 최종 색상을 결정합니다.
     */
    fun getEmotionResultColor(result: EmotionResult): Color {
        val primaryColor = getEmotionColor(result.primaryEmotion)
        val secondaryColor = result.secondaryEmotion?.let { getEmotionColor(it) }

        return if (secondaryColor != null) {
            // 복합 감정(Dyad, Opposite 등)의 경우 두 색상을 블렌딩하여 최종 감정 색상 표현
            blendColors(primaryColor, secondaryColor)
        } else {
            // 단일 감정의 경우 강도(score)에 따라 색상 조정 (White와 블렌딩)
            // 최소 가시성(0.3) 보장 + 점수에 따른 채도 변화
            val intensity = 0.3f + (result.score * 0.7f)
            blendColors(Color.White, primaryColor, intensity)
        }
    }

    private fun isDyadCategory(category: ComplexEmotionType.Category): Boolean {
        return category == ComplexEmotionType.Category.PRIMARY_DYAD ||
               category == ComplexEmotionType.Category.SECONDARY_DYAD ||
               category == ComplexEmotionType.Category.TERTIARY_DYAD
    }

    /**
     * 입력받은 색상의 반전 색상을 반환합니다.
     */
    fun getInvertedColor(color: Color): Color {
        return Color(
            red = 1f - color.red,
            green = 1f - color.green,
            blue = 1f - color.blue,
            alpha = color.alpha
        )
    }
}
