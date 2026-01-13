package com.kminder.data.local.dao

import androidx.room.*
import com.kminder.data.local.entity.EmotionAnalysisEntity
import com.kminder.data.local.entity.JournalEntryEntity
import com.kminder.data.local.model.JournalWithAnalysis
import kotlinx.coroutines.flow.Flow

/**
 * 일기 데이터 접근 객체 (DAO)
 */
@Dao
interface JournalEntryDao {
    
    /**
     * 새로운 일기를 삽입합니다.
     * @return 삽입된 일기의 ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity): Long

    /**
     * 감정 분석 결과를 삽입합니다.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: EmotionAnalysisEntity): Long
    
    /**
     * 일기를 업데이트합니다.
     */
    @Update
    suspend fun update(entry: JournalEntryEntity)

    /**
     * 일기 상태를 업데이트합니다.
     */
    @Query("UPDATE journal_entries SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
    
    /**
     * 일기를 삭제합니다.
     */
    @Delete
    suspend fun delete(entry: JournalEntryEntity)
    
    /**
     * ID로 일기를 삭제합니다.
     */
    @Query("DELETE FROM journal_entries WHERE id = :entryId")
    suspend fun deleteById(entryId: Long)
    
    /**
     * ID로 일기를 조회합니다 (감정 분석 결과 포함).
     */
    @Transaction
    @Query("SELECT * FROM journal_entries WHERE id = :entryId")
    suspend fun getById(entryId: Long): JournalWithAnalysis?
    
    @Transaction
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC LIMIT :limit")
    fun getJournalEntriesStream(limit: Int): Flow<List<JournalWithAnalysis>>

    /**
     * 모든 일기를 조회합니다 (최신순).
     */
    @Transaction
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<JournalWithAnalysis>>
    
    /**
     * 특정 날짜의 일기를 조회합니다.
     * @param dateStart 날짜 시작 (YYYY-MM-DD 00:00:00)
     * @param dateEnd 날짜 종료 (YYYY-MM-DD 23:59:59)
     */
    @Transaction
    @Query("SELECT * FROM journal_entries WHERE createdAt >= :dateStart AND createdAt <= :dateEnd ORDER BY createdAt DESC")
    suspend fun getByDate(dateStart: String, dateEnd: String): List<JournalWithAnalysis>
    
    /**
     * 특정 기간의 일기를 조회합니다.
     */
    @Transaction
    @Query("SELECT * FROM journal_entries WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    suspend fun getByDateRange(startDate: String, endDate: String): List<JournalWithAnalysis>
    
    /**
     * 감정 분석이 완료되지 않은 일기를 조회합니다.
     */
    @Transaction
    @Query("SELECT * FROM journal_entries WHERE status != 'COMPLETED' ORDER BY createdAt DESC")
    suspend fun getEntriesWithoutAnalysis(): List<JournalWithAnalysis>
    
    /**
     * 모든 일기를 삭제합니다 (테스트용).
     */
    @Query("DELETE FROM journal_entries")
    suspend fun deleteAll()
    
    /**
     * 일기 개수를 조회합니다.
     */
    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun getCount(): Int
}
