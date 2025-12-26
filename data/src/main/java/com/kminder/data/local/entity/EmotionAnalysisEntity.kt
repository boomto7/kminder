package com.kminder.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionKeyword

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
    val anger: Float = 0f,
    val anticipation: Float = 0f,
    val joy: Float = 0f,
    val trust: Float = 0f,
    val fear: Float = 0f,
    val sadness: Float = 0f,
    val disgust: Float = 0f,
    val surprise: Float = 0f,
    val keywords: List<EmotionKeyword> = emptyList()
)

fun EmotionAnalysis.toEntity(journalId: Long): EmotionAnalysisEntity {
    return EmotionAnalysisEntity(
        journalId = journalId,
        anger = anger,
        anticipation = anticipation,
        joy = joy,
        trust = trust,
        fear = fear,
        sadness = sadness,
        disgust = disgust,
        surprise = surprise,
        keywords = keywords
    )
}

fun EmotionAnalysisEntity.toDomain(): EmotionAnalysis {
    return EmotionAnalysis(
        anger = anger,
        anticipation = anticipation,
        joy = joy,
        trust = trust,
        fear = fear,
        sadness = sadness,
        disgust = disgust,
        surprise = surprise,
        keywords = keywords
    )
}
