package com.kminder.domain.repository

import com.kminder.domain.model.AnalysisStatus
import com.kminder.domain.model.ChartPeriod
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.EmotionStatistics
import com.kminder.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 일기 데이터 Repository Interface
 */
interface JournalRepository {
    
    /**
     * 새로운 일기를 저장합니다.
     * 
     * @param entry 저장할 일기 항목
     * @return 저장된 일기의 ID
     */
    suspend fun insertEntry(entry: JournalEntry): Long
    
    /**
     * 일기를 수정합니다.
     * 
     * @param entry 수정할 일기 항목
     */
    suspend fun updateEntry(entry: JournalEntry)
    
    /**
     * 일기를 삭제합니다.
     * 
     * @param entryId 삭제할 일기의 ID
     */
    suspend fun deleteEntry(entryId: Long)
    
    /**
     * 특정 일기를 조회합니다.
     * 
     * @param entryId 조회할 일기의 ID
     * @return 일기 항목 (없으면 null)
     */
    suspend fun getEntryById(entryId: Long): JournalEntry?
    
    /**
     * 모든 일기 목록을 Limit을 적용하여 Flow로 조회합니다.
     */
    fun getJournalEntriesStream(limit: Int): Flow<List<JournalEntry>>

    /**
     * 모든 일기 목록을 Flow로 조회합니다.
     * 
     * @return 일기 목록 Flow
     */
    fun getAllEntries(): Flow<List<JournalEntry>>
    
    /**
     * 특정 날짜의 일기 목록을 조회합니다.
     * 
     * @param date 조회할 날짜
     * @return 일기 목록
     */
    suspend fun getEntriesByDate(date: LocalDate): List<JournalEntry>
    
    /**
     * 특정 기간의 일기 목록을 조회합니다.
     * 
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 일기 목록
     */
    suspend fun getEntriesByDateRange(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<JournalEntry>
    
    /**
     * 감정 분석이 완료되지 않은 일기 목록을 조회합니다.
     * 
     * @return 분석 대기 중인 일기 목록
     */
    suspend fun getEntriesWithoutAnalysis(): List<JournalEntry>

    /**
     * 감정 분석 결과를 저장합니다.
     */
    suspend fun saveEmotionAnalysis(journalId: Long, result: com.kminder.domain.model.EmotionResult?, status: AnalysisStatus)

    /**
     * 감정 분석 상태를 업데이트합니다.
     */
    suspend fun updateAnalysisStatus(journalId: Long, status: AnalysisStatus)
    
    /**
     * 특정 기간의 감정 통계를 조회합니다.
     * 
     * @param period 조회 기간 (일/주/월)
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 감정 통계 목록
     */
    suspend fun getEmotionStatistics(
        period: ChartPeriod,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<EmotionStatistics>
}
