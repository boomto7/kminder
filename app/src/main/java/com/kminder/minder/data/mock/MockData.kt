package com.kminder.minder.data.mock

import com.kminder.domain.logic.PlutchikEmotionDetailCalculator
import com.kminder.domain.model.AnalysisStatus
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.EntryType
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.model.EmotionType
import java.time.LocalDateTime
import kotlin.random.Random

object MockData {
    private val calculator = PlutchikEmotionDetailCalculator()
    var currentId = 1L

    // Initialize with comprehensive test data
    val mockJournalEntries: MutableList<JournalEntry> = generateEntries()

    private fun generateEntries(): MutableList<JournalEntry> {
        val entries = mutableListOf<JournalEntry>()

        // Helper to create basic analysis
        fun createAnalysis(
            joy: Float = 0f, trust: Float = 0f, fear: Float = 0f, surprise: Float = 0f,
            sadness: Float = 0f, disgust: Float = 0f, anger: Float = 0f, anticipation: Float = 0f
        ): EmotionAnalysis {
            return EmotionAnalysis(
                joy = joy, trust = trust, fear = fear, surprise = surprise,
                sadness = sadness, disgust = disgust, anger = anger, anticipation = anticipation,
                keywords = emptyList() // Keywords generated in classify if needed, or ignored for this test
            )
        }

        // --- 1. Single Emotions (8 Types x 3 Intensities = 24 cases) ---
        val singleTests = listOf(
            Triple(0.9f, "Strong", "강함"),
            Triple(0.5f, "Medium", "중간"),
            Triple(0.3f, "Weak", "약함")
        )
        
        EmotionType.entries.forEach { type ->
            singleTests.forEach { (score, intensityLabel, intensityKor) ->
                val scores = mutableMapOf<EmotionType, Float>()
                EmotionType.entries.forEach { scores[it] = 0f }
                scores[type] = score

                val analysis = createAnalysis(
                    joy = scores[EmotionType.JOY]!!, trust = scores[EmotionType.TRUST]!!,
                    fear = scores[EmotionType.FEAR]!!, surprise = scores[EmotionType.SURPRISE]!!,
                    sadness = scores[EmotionType.SADNESS]!!, disgust = scores[EmotionType.DISGUST]!!,
                    anger = scores[EmotionType.ANGER]!!, anticipation = scores[EmotionType.ANTICIPATION]!!
                )
                
                entries.add(createEntry("단일 감정 ($intensityKor): ${type.name}", analysis))
            }
        }

        // --- 2. Base Dyads (Standard Pairs) ---
        val baseDyads = ComplexEmotionType.entries.filter { 
            it.category != ComplexEmotionType.Category.SINGLE_EMOTION &&
            isStandardDyad(it)
        }

        baseDyads.forEach { type ->
            val (e1, e2) = type.composition
            val scores = mutableMapOf<EmotionType, Float>().withDefault { 0f }
            EmotionType.entries.forEach { scores[it] = 0f }
            
            scores[e1] = 0.7f
            scores[e2] = 0.7f
            
            val analysis = createAnalysis(
                joy = scores[EmotionType.JOY]!!, trust = scores[EmotionType.TRUST]!!,
                fear = scores[EmotionType.FEAR]!!, surprise = scores[EmotionType.SURPRISE]!!,
                sadness = scores[EmotionType.SADNESS]!!, disgust = scores[EmotionType.DISGUST]!!,
                anger = scores[EmotionType.ANGER]!!, anticipation = scores[EmotionType.ANTICIPATION]!!
            )
            entries.add(createEntry("${type.category} 기본: ${type.name}", analysis))
        }

        // --- 3. Nuance Variants (26 cases) ---
        val nuanceCases = listOf(
            // Primary Variants
            NuanceCase(EmotionType.JOY, EmotionType.TRUST, EmotionType.FEAR, 0.6f, "POSSESSIVENESS"), // Love + Fear
            NuanceCase(EmotionType.TRUST, EmotionType.FEAR, EmotionType.DISGUST, 0.5f, "SERVILITY"), // Submission + Disgust
            NuanceCase(EmotionType.FEAR, EmotionType.SURPRISE, EmotionType.ANGER, 0.5f, "ALARM"), // Awe + Anger
            NuanceCase(EmotionType.SURPRISE, EmotionType.SADNESS, EmotionType.FEAR, 0.7f, "SHOCK"), // Disapproval + Fear
            NuanceCase(EmotionType.SADNESS, EmotionType.DISGUST, EmotionType.ANGER, 0.6f, "SELF_LOATHING"), // Remorse + Anger
            NuanceCase(EmotionType.DISGUST, EmotionType.ANGER, EmotionType.SADNESS, 0.6f, "ENVY_CONTEMPT"), // Contempt + Sadness
            NuanceCase(EmotionType.ANGER, EmotionType.ANTICIPATION, EmotionType.SADNESS, 0.6f, "VENGEANCE"), // Aggressiveness + Sadness
            NuanceCase(EmotionType.ANTICIPATION, EmotionType.JOY, EmotionType.TRUST, 0.8f, "NAIVETY", mapOf(EmotionType.FEAR to 0.1f)),

            // Secondary Variants
            NuanceCase(EmotionType.JOY, EmotionType.FEAR, EmotionType.DISGUST, 0.6f, "SELF_DENIAL"),
            NuanceCase(EmotionType.TRUST, EmotionType.SURPRISE, EmotionType.DISGUST, 0.5f, "AMBIVALENCE_CURIOSITY"),
            NuanceCase(EmotionType.FEAR, EmotionType.SADNESS, EmotionType.ANTICIPATION, 0.2f, "HELPLESSNESS", mapOf(EmotionType.DISGUST to 0.4f)), 
            NuanceCase(EmotionType.SURPRISE, EmotionType.DISGUST, EmotionType.ANGER, 0.6f, "ABHORRENCE"),
            NuanceCase(EmotionType.SADNESS, EmotionType.ANGER, EmotionType.DISGUST, 0.7f, "RESENTMENT"),
            NuanceCase(EmotionType.DISGUST, EmotionType.ANTICIPATION, EmotionType.SADNESS, 0.7f, "DISTRUST"),
            NuanceCase(EmotionType.ANGER, EmotionType.JOY, EmotionType.TRUST, 0.2f, "HUBRIS", mapOf(EmotionType.DISGUST to 0.4f)),
            NuanceCase(EmotionType.ANTICIPATION, EmotionType.TRUST, EmotionType.SADNESS, 0.5f, "FATALISM", mapOf(EmotionType.JOY to 0.2f)),

            // Tertiary Variants
            NuanceCase(EmotionType.JOY, EmotionType.SURPRISE, EmotionType.FEAR, 0.6f, "CONFUSED_JOY"),
            NuanceCase(EmotionType.TRUST, EmotionType.SADNESS, EmotionType.DISGUST, 0.5f, "SELF_PITY"),
            NuanceCase(EmotionType.FEAR, EmotionType.DISGUST, EmotionType.ANGER, 0.6f, "HUMILIATION"),
            NuanceCase(EmotionType.SURPRISE, EmotionType.ANGER, EmotionType.ANTICIPATION, 0.2f, "DISORIENTATION", mapOf(EmotionType.FEAR to 0.4f)),
            NuanceCase(EmotionType.SADNESS, EmotionType.ANTICIPATION, EmotionType.TRUST, 0.6f, "RESIGNATION"),
            NuanceCase(EmotionType.DISGUST, EmotionType.JOY, EmotionType.ANGER, 0.5f, "DERISION"),
            NuanceCase(EmotionType.ANGER, EmotionType.TRUST, EmotionType.DISGUST, 0.6f, "TYRANNY"),
            NuanceCase(EmotionType.ANTICIPATION, EmotionType.FEAR, EmotionType.SADNESS, 0.6f, "DREAD"),

            // Opposite Variants
            NuanceCase(EmotionType.JOY, EmotionType.SADNESS, EmotionType.ANTICIPATION, 0.2f, "NOSTALGIA", mapOf(EmotionType.TRUST to 0.4f)),
            NuanceCase(EmotionType.TRUST, EmotionType.DISGUST, EmotionType.FEAR, 0.6f, "MISTRUST")
        )

        nuanceCases.forEach { case ->
            val scores = mutableMapOf<EmotionType, Float>().withDefault { 0f }
            EmotionType.entries.forEach { scores[it] = 0f }
            
            // Base
            scores[case.base1] = 0.9f
            scores[case.base2] = 0.9f
            
            // Primary Modifier
            scores[case.modifier] = case.modifierValue
            
            // Extra Modifiers
            case.extras.forEach { (t, v) -> scores[t] = v }

            val analysis = createAnalysis(
                joy = scores[EmotionType.JOY]!!, trust = scores[EmotionType.TRUST]!!,
                fear = scores[EmotionType.FEAR]!!, surprise = scores[EmotionType.SURPRISE]!!,
                sadness = scores[EmotionType.SADNESS]!!, disgust = scores[EmotionType.DISGUST]!!,
                anger = scores[EmotionType.ANGER]!!, anticipation = scores[EmotionType.ANTICIPATION]!!
            )
            entries.add(createEntry("뉘앙스 테스트: ${case.expectLabel}", analysis))
        }

        return entries.reversed().toMutableList()
    }

    private fun createEntry(content: String, analysis: EmotionAnalysis): JournalEntry {
        // Use the real calculator logic!
        val result = calculator.classify(analysis)
        
        return JournalEntry(
            id = currentId++,
            content = content,
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusHours(currentId), 
            updatedAt = LocalDateTime.now().minusHours(currentId),
            emotionResult = result,
            status = AnalysisStatus.COMPLETED
        )
    }

    data class NuanceCase(
        val base1: EmotionType,
        val base2: EmotionType,
        val modifier: EmotionType,
        val modifierValue: Float,
        val expectLabel: String,
        val extras: Map<EmotionType, Float> = emptyMap()
    )

    private fun isStandardDyad(type: ComplexEmotionType): Boolean {
        val name = type.name
        return when(type.category) {
            ComplexEmotionType.Category.PRIMARY_DYAD -> name in listOf("LOVE", "SUBMISSION", "AWE", "DISAPPROVAL", "REMORSE", "CONTEMPT", "AGGRESSIVENESS", "OPTIMISM")
            ComplexEmotionType.Category.SECONDARY_DYAD -> name in listOf("GUILT", "CURIOSITY", "DESPAIR", "UNBELIEF", "ENVY", "CYNICISM", "PRIDE", "HOPE")
            ComplexEmotionType.Category.TERTIARY_DYAD -> name in listOf("DELIGHT", "SENTIMENTALITY", "SHAME", "OUTRAGE", "PESSIMISM", "MORBIDNESS", "DOMINANCE", "ANXIETY")     
            ComplexEmotionType.Category.OPPOSITE -> name in listOf("BITTERSWEETNESS", "AMBIVALENCE", "FROZENNESS", "CONFUSION")
            else -> false
        }
    }
}
