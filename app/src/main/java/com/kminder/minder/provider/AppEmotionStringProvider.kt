package com.kminder.minder.provider

import android.content.Context
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.DetailedEmotionType
import com.kminder.domain.model.EmotionType
import com.kminder.domain.provider.EmotionStringProvider
import com.kminder.minder.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppEmotionStringProvider @Inject constructor(
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
            EmotionType.UNKNOWN -> R.string.emotion_unknown
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

    override fun getComplexEmotionTitle(type: ComplexEmotionType): String {
        val resId = when (type) {
            ComplexEmotionType.LOVE -> R.string.emotion_complex_love_title
            ComplexEmotionType.SUBMISSION -> R.string.emotion_complex_submission_title
            ComplexEmotionType.AWE -> R.string.emotion_complex_awe_title
            ComplexEmotionType.DISAPPROVAL -> R.string.emotion_complex_disapproval_title
            ComplexEmotionType.REMORSE -> R.string.emotion_complex_remorse_title
            ComplexEmotionType.CONTEMPT -> R.string.emotion_complex_contempt_title
            ComplexEmotionType.AGGRESSIVENESS -> R.string.emotion_complex_aggressiveness_title
            ComplexEmotionType.OPTIMISM -> R.string.emotion_complex_optimism_title
            ComplexEmotionType.GUILT -> R.string.emotion_complex_guilt_title
            ComplexEmotionType.CURIOSITY -> R.string.emotion_complex_curiosity_title
            ComplexEmotionType.DESPAIR -> R.string.emotion_complex_despair_title
            ComplexEmotionType.UNBELIEF -> R.string.emotion_complex_unbelief_title
            ComplexEmotionType.ENVY -> R.string.emotion_complex_envy_title
            ComplexEmotionType.CYNICISM -> R.string.emotion_complex_cynicism_title
            ComplexEmotionType.PRIDE -> R.string.emotion_complex_pride_title
            ComplexEmotionType.FATALISM -> R.string.emotion_complex_fatalism_title
            ComplexEmotionType.DELIGHT -> R.string.emotion_complex_delight_title
            ComplexEmotionType.SENTIMENTALITY -> R.string.emotion_complex_sentimentality_title
            ComplexEmotionType.SHAME -> R.string.emotion_complex_shame_title
            ComplexEmotionType.OUTRAGE -> R.string.emotion_complex_outrage_title
            ComplexEmotionType.PESSIMISM -> R.string.emotion_complex_pessimism_title
            ComplexEmotionType.MORBIDNESS -> R.string.emotion_complex_morbidness_title
            ComplexEmotionType.DOMINANCE -> R.string.emotion_complex_dominance_title
            ComplexEmotionType.ANXIETY -> R.string.emotion_complex_anxiety_title

            // Opposite Emotions
            ComplexEmotionType.BITTERSWEETNESS -> R.string.emotion_complex_bittersweetness_title
            ComplexEmotionType.AMBIVALENCE -> R.string.emotion_complex_ambivalence_title
            ComplexEmotionType.FROZENNESS -> R.string.emotion_complex_frozenness_title
            ComplexEmotionType.CONFUSION -> R.string.emotion_complex_confusion_title

            // Single Emotions (Detailed) - Strong
            ComplexEmotionType.ECSTASY -> R.string.emotion_detail_ecstasy
            ComplexEmotionType.ADMIRATION -> R.string.emotion_detail_admiration
            ComplexEmotionType.TERROR -> R.string.emotion_detail_terror
            ComplexEmotionType.AMAZEMENT -> R.string.emotion_detail_amazement
            ComplexEmotionType.GRIEF -> R.string.emotion_detail_grief
            ComplexEmotionType.LOATHING -> R.string.emotion_detail_loathing
            ComplexEmotionType.RAGE -> R.string.emotion_detail_rage
            ComplexEmotionType.VIGILANCE -> R.string.emotion_detail_vigilance

            // Single Emotions (Detailed) - Medium (Normal)
            ComplexEmotionType.JOY -> R.string.emotion_joy
            ComplexEmotionType.TRUST -> R.string.emotion_trust
            ComplexEmotionType.FEAR -> R.string.emotion_fear
            ComplexEmotionType.SURPRISE -> R.string.emotion_surprise
            ComplexEmotionType.SADNESS -> R.string.emotion_sadness
            ComplexEmotionType.DISGUST -> R.string.emotion_disgust
            ComplexEmotionType.ANGER -> R.string.emotion_anger
            ComplexEmotionType.ANTICIPATION -> R.string.emotion_anticipation

            // Single Emotions (Detailed) - Weak
            ComplexEmotionType.SERENITY -> R.string.emotion_detail_serenity
            ComplexEmotionType.ACCEPTANCE -> R.string.emotion_detail_acceptance
            ComplexEmotionType.APPREHENSION -> R.string.emotion_detail_apprehension
            ComplexEmotionType.DISTRACTION -> R.string.emotion_detail_distraction
            ComplexEmotionType.PENSIVENESS -> R.string.emotion_detail_pensiveness
            ComplexEmotionType.BOREDOM -> R.string.emotion_detail_boredom
            ComplexEmotionType.ANNOYANCE -> R.string.emotion_detail_annoyance
            ComplexEmotionType.INTEREST -> R.string.emotion_detail_interest
        }
        return context.getString(resId)
    }

    override fun getComplexEmotionDescription(type: ComplexEmotionType): String {
        val resId = when (type) {
            ComplexEmotionType.LOVE -> R.string.emotion_complex_love_desc
            ComplexEmotionType.SUBMISSION -> R.string.emotion_complex_submission_desc
            ComplexEmotionType.AWE -> R.string.emotion_complex_awe_desc
            ComplexEmotionType.DISAPPROVAL -> R.string.emotion_complex_disapproval_desc
            ComplexEmotionType.REMORSE -> R.string.emotion_complex_remorse_desc
            ComplexEmotionType.CONTEMPT -> R.string.emotion_complex_contempt_desc
            ComplexEmotionType.AGGRESSIVENESS -> R.string.emotion_complex_aggressiveness_desc
            ComplexEmotionType.OPTIMISM -> R.string.emotion_complex_optimism_desc
            ComplexEmotionType.GUILT -> R.string.emotion_complex_guilt_desc
            ComplexEmotionType.CURIOSITY -> R.string.emotion_complex_curiosity_desc
            ComplexEmotionType.DESPAIR -> R.string.emotion_complex_despair_desc
            ComplexEmotionType.UNBELIEF -> R.string.emotion_complex_unbelief_desc
            ComplexEmotionType.ENVY -> R.string.emotion_complex_envy_desc
            ComplexEmotionType.CYNICISM -> R.string.emotion_complex_cynicism_desc
            ComplexEmotionType.PRIDE -> R.string.emotion_complex_pride_desc
            ComplexEmotionType.FATALISM -> R.string.emotion_complex_fatalism_desc
            ComplexEmotionType.DELIGHT -> R.string.emotion_complex_delight_desc
            ComplexEmotionType.SENTIMENTALITY -> R.string.emotion_complex_sentimentality_desc
            ComplexEmotionType.SHAME -> R.string.emotion_complex_shame_desc
            ComplexEmotionType.OUTRAGE -> R.string.emotion_complex_outrage_desc
            ComplexEmotionType.PESSIMISM -> R.string.emotion_complex_pessimism_desc
            ComplexEmotionType.MORBIDNESS -> R.string.emotion_complex_morbidness_desc
            ComplexEmotionType.DOMINANCE -> R.string.emotion_complex_dominance_desc
            ComplexEmotionType.ANXIETY -> R.string.emotion_complex_anxiety_desc

            // Opposite Emotions
            ComplexEmotionType.BITTERSWEETNESS -> R.string.emotion_complex_bittersweetness_desc
            ComplexEmotionType.AMBIVALENCE -> R.string.emotion_complex_ambivalence_desc
            ComplexEmotionType.FROZENNESS -> R.string.emotion_complex_frozenness_desc
            ComplexEmotionType.CONFUSION -> R.string.emotion_complex_confusion_desc

            // Single Emotions - Reuse single emotion description format or specific resources if added
            // For now, mapping to a default generic description or specific if available
            // Assuming we use the same resources or just fallback to simple names for now to avoid compilation error.
            // Ideally should have specific descriptions like R.string.emotion_detail_ecstasy_desc
            
            // Using title resource temporarily as placeholder for desc if specific desc not available, 
            // OR mapped to a generic single emotion desc.
            // But since return type implies unique resId, let's map to TITLE for now to fix compile error 
            // and user can add proper desc later.
            
            ComplexEmotionType.ECSTASY -> R.string.emotion_detail_ecstasy
            ComplexEmotionType.ADMIRATION -> R.string.emotion_detail_admiration
            ComplexEmotionType.TERROR -> R.string.emotion_detail_terror
            ComplexEmotionType.AMAZEMENT -> R.string.emotion_detail_amazement
            ComplexEmotionType.GRIEF -> R.string.emotion_detail_grief
            ComplexEmotionType.LOATHING -> R.string.emotion_detail_loathing
            ComplexEmotionType.RAGE -> R.string.emotion_detail_rage
            ComplexEmotionType.VIGILANCE -> R.string.emotion_detail_vigilance

            ComplexEmotionType.JOY -> R.string.emotion_joy
            ComplexEmotionType.TRUST -> R.string.emotion_trust
            ComplexEmotionType.FEAR -> R.string.emotion_fear
            ComplexEmotionType.SURPRISE -> R.string.emotion_surprise
            ComplexEmotionType.SADNESS -> R.string.emotion_sadness
            ComplexEmotionType.DISGUST -> R.string.emotion_disgust
            ComplexEmotionType.ANGER -> R.string.emotion_anger
            ComplexEmotionType.ANTICIPATION -> R.string.emotion_anticipation

            ComplexEmotionType.SERENITY -> R.string.emotion_detail_serenity
            ComplexEmotionType.ACCEPTANCE -> R.string.emotion_detail_acceptance
            ComplexEmotionType.APPREHENSION -> R.string.emotion_detail_apprehension
            ComplexEmotionType.DISTRACTION -> R.string.emotion_detail_distraction
            ComplexEmotionType.PENSIVENESS -> R.string.emotion_detail_pensiveness
            ComplexEmotionType.BOREDOM -> R.string.emotion_detail_boredom
            ComplexEmotionType.ANNOYANCE -> R.string.emotion_detail_annoyance
            ComplexEmotionType.INTEREST -> R.string.emotion_detail_interest
        }
        return context.getString(resId)
    }

    override fun getAdvice(primaryEmotion: EmotionType): String {
        val resId = when (primaryEmotion) {
            EmotionType.JOY -> R.string.advice_joy
            EmotionType.SADNESS -> R.string.advice_sadness
            EmotionType.ANGER -> R.string.advice_anger
            EmotionType.TRUST -> R.string.advice_trust
            EmotionType.FEAR -> R.string.advice_fear
            EmotionType.ANTICIPATION -> R.string.advice_anticipation
            EmotionType.DISGUST -> R.string.advice_disgust
            EmotionType.SURPRISE -> R.string.advice_surprise
            EmotionType.UNKNOWN -> R.string.advice_joy // Default fallback
        }
        return context.getString(resId)
    }

    override fun getActionKeywords(primaryEmotion: EmotionType): List<String> {
        val resIds = when (primaryEmotion) {
            EmotionType.JOY -> listOf(R.string.keyword_energy, R.string.keyword_achievement)
            EmotionType.SADNESS -> listOf(R.string.keyword_comfort, R.string.keyword_rest)
            EmotionType.ANGER -> listOf(R.string.keyword_resolve, R.string.keyword_exercise)
            EmotionType.TRUST -> listOf(R.string.keyword_stability, R.string.keyword_relationship)
            EmotionType.FEAR -> listOf(R.string.keyword_courage, R.string.keyword_preparation)
            EmotionType.ANTICIPATION -> listOf(R.string.keyword_plan, R.string.keyword_flutter)
            EmotionType.DISGUST -> listOf(R.string.keyword_distance, R.string.keyword_ventilation)
            EmotionType.SURPRISE -> listOf(R.string.keyword_discovery, R.string.keyword_transition)
            EmotionType.UNKNOWN -> emptyList()
        }
        return resIds.map { context.getString(it) }
    }

    override fun getAnalysisImpossibleLabel(): String = context.getString(R.string.analysis_impossible)
    override fun getAnalysisInsufficientDataMessage(): String = context.getString(R.string.analysis_insufficient_data)
    override fun getAnalysisTryJournalingAction(): String = context.getString(R.string.analysis_try_journaling)
    
    override fun getSingleEmotionDescription(emotionName: String): String =
        context.getString(R.string.analysis_single_desc_format, emotionName)
        
    override fun getConflictLabel(emotionName1: String, emotionName2: String): String =
        context.getString(R.string.analysis_conflict_format, emotionName1, emotionName2)
        
    override fun getConflictDescription(): String = context.getString(R.string.analysis_conflict_desc)
    
    override fun getHarmonyDescription(): String = context.getString(R.string.analysis_harmony_desc_default)
    override fun getComplexEmotionDefaultLabel(): String = context.getString(R.string.analysis_complex_label_default)
    override fun getComplexEmotionDefaultDescription(): String = context.getString(R.string.analysis_complex_desc_default)
    override fun getComplicatedEmotionDefaultLabel(): String = context.getString(R.string.analysis_complicated_label_default)
    override fun getComplicatedEmotionDefaultDescription(): String = context.getString(R.string.analysis_complicated_desc_default)
}
