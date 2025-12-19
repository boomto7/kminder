package com.kminder.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.kminder.data.local.entity.EmotionAnalysisEntity
import com.kminder.data.local.entity.JournalEntryEntity
import com.kminder.data.local.entity.toDomain
import com.kminder.domain.model.JournalEntry

/**
 * 일기 항목과 감정 분석 결과를 포함하는 관계 객체
 */
data class JournalWithAnalysis(
    @Embedded val journal: JournalEntryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "journalId"
    )
    val analysis: EmotionAnalysisEntity?
)

/**
 * Domain Model로 변환
 */
fun JournalWithAnalysis.toDomain(): JournalEntry {
    val entry = journal.toDomain()
    val analysisDomain = analysis?.toDomain()
    return entry.copy(emotionAnalysis = analysisDomain)
}
