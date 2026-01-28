package com.kminder.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionKeyword
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.ComplexEmotionType

@Entity(
    tableName = "emotion_analyses",
    indices = [Index(value = ["journalId"])],
    foreignKeys = [
        ForeignKey(
            entity = JournalEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["journalId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EmotionAnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val journalId: Long,
    
    // EmotionResult fields
    val primaryEmotion: String = "",
    val secondaryEmotion: String? = null,
    val score: Float = 0f,
    val category: String = "",
    val complexEmotionType: String? = null,
    
    // Original EmotionAnalysis (source) fields
    val anger: Float = 0f,
    val anticipation: Float = 0f,
    val joy: Float = 0f,
    val trust: Float = 0f,
    val fear: Float = 0f,
    val sadness: Float = 0f,
    val disgust: Float = 0f,
    val surprise: Float = 0f,
    val keywords: List<EmotionKeyword> = emptyList(),

    // New detailed analysis fields
    val modifierEmotions: List<EmotionType> = emptyList(),
    val logicKey: String? = null
)

fun EmotionResult.toEntity(journalId: Long): EmotionAnalysisEntity {
    return EmotionAnalysisEntity(
        journalId = journalId,
        primaryEmotion = primaryEmotion.name,
        secondaryEmotion = secondaryEmotion?.name,
        score = score,
        category = category.name,
        complexEmotionType = complexEmotionType?.name,
        anger = source.anger,
        anticipation = source.anticipation,
        joy = source.joy,
        trust = source.trust,
        fear = source.fear,
        sadness = source.sadness,
        disgust = source.disgust,
        surprise = source.surprise,
        keywords = source.keywords,
        modifierEmotions = modifierEmotions,
        logicKey = logicKey
    )
}

fun EmotionAnalysisEntity.toDomain(): EmotionResult {
    return EmotionResult(
        primaryEmotion = try { EmotionType.valueOf(primaryEmotion) } catch (e: Exception) { EmotionType.JOY },
        secondaryEmotion = secondaryEmotion?.let { try { EmotionType.valueOf(it) } catch (e: Exception) { null } },
        score = score,
        category = try { ComplexEmotionType.Category.valueOf(category) } catch (e: Exception) { ComplexEmotionType.Category.SINGLE_EMOTION },
        complexEmotionType = complexEmotionType?.let { try { ComplexEmotionType.valueOf(it) } catch (e: Exception) { null } },
        source = EmotionAnalysis(
            anger = anger,
            anticipation = anticipation,
            joy = joy,
            trust = trust,
            fear = fear,
            sadness = sadness,
            disgust = disgust,
            surprise = surprise,
            keywords = keywords
        ),
        modifierEmotions = modifierEmotions,
        logicKey = logicKey
    )
}
