package com.kminder.data.repository

import com.kminder.data.local.dao.JournalEntryDao
import com.kminder.data.local.entity.*
import com.kminder.data.local.model.toDomain
import com.kminder.domain.model.AnalysisStatus
import com.kminder.domain.model.ChartPeriod
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionStatistics
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

/**
 * JournalRepository 구현체
 */
class JournalRepositoryImpl @Inject constructor(
    private val journalEntryDao: JournalEntryDao
) : JournalRepository {
    
    override suspend fun insertEntry(entry: JournalEntry): Long {
        return journalEntryDao.insert(entry.toEntity())
    }
    
    override suspend fun updateEntry(entry: JournalEntry) {
        journalEntryDao.update(entry.toEntity())
    }
    
    override suspend fun deleteEntry(entryId: Long) {
        journalEntryDao.deleteById(entryId)
    }
    
    override suspend fun getEntryById(entryId: Long): JournalEntry? {
        return journalEntryDao.getById(entryId)?.toDomain()
    }
    
    override suspend fun saveEmotionAnalysis(journalId: Long, result: com.kminder.domain.model.EmotionResult?, status: AnalysisStatus) {
        if (result != null) {
            journalEntryDao.insertAnalysis(result.toEntity(journalId))
        }
        journalEntryDao.updateStatus(journalId, status.name)
    }

    override suspend fun updateAnalysisStatus(journalId: Long, status: AnalysisStatus) {
        journalEntryDao.updateStatus(journalId, status.name)
    }
    
    override fun getJournalEntriesStream(limit: Int): Flow<List<JournalEntry>> {
        return journalEntryDao.getJournalEntriesStream(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllEntries(): Flow<List<JournalEntry>> {
        return journalEntryDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getEntriesByDate(date: LocalDate): List<JournalEntry> {
        val startDateTime = LocalDateTime.of(date, LocalTime.MIN)
        val endDateTime = LocalDateTime.of(date, LocalTime.MAX)
        
        return journalEntryDao.getByDate(
            startDateTime.toString(),
            endDateTime.toString()
        ).map { it.toDomain() }
    }
    
    override suspend fun getEntriesByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<JournalEntry> {
        return journalEntryDao.getByDateRange(
            startDate.toString(),
            endDate.toString()
        ).map { it.toDomain() }
    }
    
    override suspend fun getEntriesWithoutAnalysis(): List<JournalEntry> {
        return journalEntryDao.getEntriesWithoutAnalysis().map { it.toDomain() }
    }
    

}
