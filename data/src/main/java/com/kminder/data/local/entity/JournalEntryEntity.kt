package com.kminder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kminder.domain.model.AnalysisStatus
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
    
    // 분석 상태
    val status: String
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
        status = status.name
    )
}

/**
 * Room Entity를 Domain JournalEntry로 변환
 * (relation 없이 단독변환 시 emotionAnalysis는 null)
 */
fun JournalEntryEntity.toDomain(): JournalEntry {
    return JournalEntry(
        id = id,
        content = content,
        entryType = EntryType.valueOf(entryType),
        question = question,
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt),
        status = try { AnalysisStatus.valueOf(status) } catch(e: Exception) { AnalysisStatus.NONE },
        emotionResult = null
    )
}
