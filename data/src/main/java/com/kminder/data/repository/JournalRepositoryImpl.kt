package com.kminder.data.repository

import com.kminder.data.local.dao.JournalEntryDao
import com.kminder.data.local.entity.toDomain
import com.kminder.data.local.entity.toEntity
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
    
    override suspend fun getEmotionStatistics(
        period: ChartPeriod,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<EmotionStatistics> {
        val startDateTime = LocalDateTime.of(startDate, LocalTime.MIN)
        val endDateTime = LocalDateTime.of(endDate, LocalTime.MAX)
        
        val entries = journalEntryDao.getByDateRange(
            startDateTime.toString(),
            endDateTime.toString()
        ).map { it.toDomain() }
        
        // 기간별로 그룹화
        val groupedEntries = when (period) {
            ChartPeriod.DAY -> groupByDay(entries)
            ChartPeriod.WEEK -> groupByWeek(entries)
            ChartPeriod.MONTH -> groupByMonth(entries)
        }
        
        // 통계 계산
        return groupedEntries.map { (date, entriesInPeriod) ->
            val validEntries = entriesInPeriod.filter { it.hasEmotionAnalysis() }
            
            if (validEntries.isEmpty()) {
                EmotionStatistics(
                    date = date,
                    emotionAnalysis = EmotionAnalysis(),
                    entryCount = entriesInPeriod.size
                )
            } else {
                val avgEmotion = calculateAverageEmotion(validEntries)
                EmotionStatistics(
                    date = date,
                    emotionAnalysis = avgEmotion,
                    entryCount = entriesInPeriod.size
                )
            }
        }
    }
    
    /**
     * 일별로 그룹화
     */
    private fun groupByDay(entries: List<JournalEntry>): Map<LocalDate, List<JournalEntry>> {
        return entries.groupBy { it.createdAt.toLocalDate() }
    }
    
    /**
     * 주별로 그룹화 (월요일 시작)
     */
    private fun groupByWeek(entries: List<JournalEntry>): Map<LocalDate, List<JournalEntry>> {
        return entries.groupBy { entry ->
            val date = entry.createdAt.toLocalDate()
            // 해당 주의 월요일 날짜를 키로 사용
            date.minusDays(date.dayOfWeek.value.toLong() - 1)
        }
    }
    
    /**
     * 월별로 그룹화
     */
    private fun groupByMonth(entries: List<JournalEntry>): Map<LocalDate, List<JournalEntry>> {
        return entries.groupBy { entry ->
            val date = entry.createdAt.toLocalDate()
            // 해당 월의 1일을 키로 사용
            LocalDate.of(date.year, date.month, 1)
        }
    }
    
    /**
     * 평균 감정 계산
     */
    private fun calculateAverageEmotion(entries: List<JournalEntry>): EmotionAnalysis {
        val count = entries.size.toFloat()
        
        return EmotionAnalysis(
            anger = entries.sumOf { it.emotionAnalysis?.anger?.toDouble() ?: 0.0 }.toFloat() / count,
            anticipation = entries.sumOf { it.emotionAnalysis?.anticipation?.toDouble() ?: 0.0 }.toFloat() / count,
            joy = entries.sumOf { it.emotionAnalysis?.joy?.toDouble() ?: 0.0 }.toFloat() / count,
            trust = entries.sumOf { it.emotionAnalysis?.trust?.toDouble() ?: 0.0 }.toFloat() / count,
            fear = entries.sumOf { it.emotionAnalysis?.fear?.toDouble() ?: 0.0 }.toFloat() / count,
            sadness = entries.sumOf { it.emotionAnalysis?.sadness?.toDouble() ?: 0.0 }.toFloat() / count,
            disgust = entries.sumOf { it.emotionAnalysis?.disgust?.toDouble() ?: 0.0 }.toFloat() / count,
            surprise = entries.sumOf { it.emotionAnalysis?.surprise?.toDouble() ?: 0.0 }.toFloat() / count
        )
    }
}
