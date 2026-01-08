package com.kminder.minder.ui.provider

import android.content.Context
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.DetailedEmotionType
import com.kminder.domain.model.EmotionType
import com.kminder.domain.provider.EmotionStringProvider
import com.kminder.minder.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidEmotionStringProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : EmotionStringProvider {

    override fun getEmotionName(type: EmotionType): String {
        val resId = when (type) {
            EmotionType.ANGER -> R.string.emotion_anger
            EmotionType.ANTICIPATION -> R.string.emotion_anticipation
            EmotionType.JOY -> R.string.emotion_joy
            EmotionType.TRUST -> R.string.emotion_trust
            EmotionType.FEAR -> R.string.emotion_fear
            EmotionType.SADNESS -> R.string.emotion_sadness
            EmotionType.DISGUST -> R.string.emotion_disgust
            EmotionType.SURPRISE -> R.string.emotion_surprise
        }
        return context.getString(resId)
    }

    override fun getDetailedEmotionName(type: DetailedEmotionType): String {
        val resId = when (type) {
            DetailedEmotionType.ECSTASY -> R.string.emotion_detail_ecstasy
            DetailedEmotionType.JOY -> R.string.emotion_detail_joy
            DetailedEmotionType.SERENITY -> R.string.emotion_detail_serenity
            DetailedEmotionType.ADMIRATION -> R.string.emotion_detail_admiration
            DetailedEmotionType.TRUST -> R.string.emotion_detail_trust
            DetailedEmotionType.ACCEPTANCE -> R.string.emotion_detail_acceptance
            DetailedEmotionType.TERROR -> R.string.emotion_detail_terror
            DetailedEmotionType.FEAR -> R.string.emotion_detail_fear
            DetailedEmotionType.APPREHENSION -> R.string.emotion_detail_apprehension
            DetailedEmotionType.AMAZEMENT -> R.string.emotion_detail_amazement
            DetailedEmotionType.SURPRISE -> R.string.emotion_detail_surprise
            DetailedEmotionType.DISTRACTION -> R.string.emotion_detail_distraction
            DetailedEmotionType.GRIEF -> R.string.emotion_detail_grief
            DetailedEmotionType.SADNESS -> R.string.emotion_detail_sadness
            DetailedEmotionType.PENSIVENESS -> R.string.emotion_detail_pensiveness
            DetailedEmotionType.LOATHING -> R.string.emotion_detail_loathing
            DetailedEmotionType.DISGUST -> R.string.emotion_detail_disgust
            DetailedEmotionType.BOREDOM -> R.string.emotion_detail_boredom
            DetailedEmotionType.RAGE -> R.string.emotion_detail_rage
            DetailedEmotionType.ANGER -> R.string.emotion_detail_anger
            DetailedEmotionType.ANNOYANCE -> R.string.emotion_detail_annoyance
            DetailedEmotionType.VIGILANCE -> R.string.emotion_detail_vigilance
            DetailedEmotionType.ANTICIPATION -> R.string.emotion_detail_anticipation
            DetailedEmotionType.INTEREST -> R.string.emotion_detail_interest
        }
        return context.getString(resId)
    }
    
    override fun getSingleEmotionDescription(emotionName: String): String {
        return context.getString(R.string.analysis_single_desc_format, emotionName)
    }

    override fun getComplexEmotionTitle(type: ComplexEmotionType): String {
        // Dynamic lookup: emotion_detail_[name] for Single, emotion_complex_[name]_title for others
        val key = if (type.category == ComplexEmotionType.Category.SINGLE_EMOTION) {
            "emotion_detail_${type.name.lowercase()}"
        } else {
            "emotion_complex_${type.name.lowercase()}_title"
        }
        
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        if (resId != 0) {
            return context.getString(resId)
        }
        // Fallback to capitalized enum name if resource missing
        return type.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    override fun getComplexEmotionDescription(type: ComplexEmotionType): String {
        // Dynamic lookup: emotion_desc_[name]
        val key = "emotion_desc_${type.name.lowercase()}"
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        if (resId != 0) {
            return context.getString(resId)
        }
        return "" 
    }

    override fun getComplexEmotionAdvice(type: ComplexEmotionType): String {
        val key = "emotion_advice_${type.name.lowercase()}"
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        if (resId != 0) {
            return context.getString(resId)
        }
        // Fallback: Use primary emotion's advice (Legacy behavior)
        return getAdvice(type.composition.first)
    }

    override fun getConflictLabel(emotionName1: String, emotionName2: String): String {
        return context.getString(R.string.analysis_conflict_format, emotionName1, emotionName2)
    }

    override fun getConflictDescription(): String {
        return context.getString(R.string.analysis_conflict_desc)
    }

    override fun getHarmonyDescription(): String {
        return context.getString(R.string.analysis_harmony_desc_default)
    }

    override fun getComplexEmotionDefaultLabel(): String {
        return context.getString(R.string.analysis_complex_label_default)
    }

    override fun getComplexEmotionDefaultDescription(): String {
        return context.getString(R.string.analysis_complex_desc_default)
    }

    override fun getComplicatedEmotionDefaultLabel(): String {
        return context.getString(R.string.analysis_complicated_label_default)
    }

    override fun getComplicatedEmotionDefaultDescription(): String {
        return context.getString(R.string.analysis_complicated_desc_default)
    }

    override fun getAdvice(primaryEmotion: EmotionType): String {
        val key = "emotion_advice_${primaryEmotion.name.lowercase()}"
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        if (resId != 0) {
            return context.getString(resId)
        }
        return ""
    }

    override fun getActionKeywords(primaryEmotion: EmotionType): List<String> {
        val keywordsResIds = when (primaryEmotion) {
            EmotionType.JOY -> listOf(R.string.keyword_energy, R.string.keyword_achievement, R.string.keyword_flutter)
            EmotionType.SADNESS -> listOf(R.string.keyword_comfort, R.string.keyword_rest)
            EmotionType.ANGER -> listOf(R.string.keyword_resolve, R.string.keyword_exercise)
            EmotionType.TRUST -> listOf(R.string.keyword_relationship, R.string.keyword_stability, R.string.keyword_comfort)
            EmotionType.FEAR -> listOf(R.string.keyword_courage, R.string.keyword_preparation, R.string.keyword_distance)
            EmotionType.ANTICIPATION -> listOf(R.string.keyword_plan, R.string.keyword_flutter, R.string.keyword_preparation)
            EmotionType.DISGUST -> listOf(R.string.keyword_ventilation, R.string.keyword_distance, R.string.keyword_transition)
            EmotionType.SURPRISE -> listOf(R.string.keyword_discovery, R.string.keyword_transition)
        }
        return keywordsResIds.map { context.getString(it) }
    }

    override fun getAnalysisImpossibleLabel(): String {
        return context.getString(R.string.analysis_impossible)
    }

    override fun getAnalysisInsufficientDataMessage(): String {
        return context.getString(R.string.analysis_insufficient_data)
    }

    override fun getAnalysisTryJournalingAction(): String {
        return context.getString(R.string.analysis_try_journaling)
    }
}
