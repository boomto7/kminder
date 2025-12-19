package com.kminder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EntryType
import com.kminder.domain.model.JournalEntry
import java.time.LocalDateTime

/**
 * Room Database용 일기 항목 Entity
 */
@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val entryType: String, // EntryType enum을 String으로 저장
    val question: String? = null,
    val createdAt: String, // LocalDateTime을 ISO String으로 저장
    val updatedAt: String,
    
    // 감정 분석 결과 (nullable)
    val anger: Float? = null,
    val anticipation: Float? = null,
    val joy: Float? = null,
    val trust: Float? = null,
    val fear: Float? = null,
    val sadness: Float? = null,
    val disgust: Float? = null,
    val disgust: Float? = null,
    val surprise: Float? = null,
    val keywords: List<String> = emptyList()
)

/**
 * Domain JournalEntry를 Room Entity로 변환
 */
fun JournalEntry.toEntity(): JournalEntryEntity {
    return JournalEntryEntity(
        id = id,
        content = content,
        entryType = entryType.name,
        question = question,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        anger = emotionAnalysis?.anger,
        anticipation = emotionAnalysis?.anticipation,
        joy = emotionAnalysis?.joy,
        trust = emotionAnalysis?.trust,
        fear = emotionAnalysis?.fear,
        sadness = emotionAnalysis?.sadness,
        disgust = emotionAnalysis?.disgust,
        surprise = emotionAnalysis?.surprise,
        keywords = emotionAnalysis?.keywords ?: emptyList()
    )
}

/**
 * Room Entity를 Domain JournalEntry로 변환
 */
fun JournalEntryEntity.toDomain(): JournalEntry {
    val emotionAnalysis = if (anger != null) {
        EmotionAnalysis(
            anger = anger,
            anticipation = anticipation ?: 0f,
            joy = joy ?: 0f,
            trust = trust ?: 0f,
            fear = fear ?: 0f,
            sadness = sadness ?: 0f,
            disgust = disgust ?: 0f,
            disgust = disgust ?: 0f,
            surprise = surprise ?: 0f,
            keywords = keywords
        )
    } else null
    
    return JournalEntry(
        id = id,
        content = content,
        entryType = EntryType.valueOf(entryType),
        question = question,
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt),
        emotionAnalysis = emotionAnalysis
    )
}
