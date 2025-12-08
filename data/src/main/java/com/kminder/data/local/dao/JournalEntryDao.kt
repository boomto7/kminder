package com.kminder.data.local.dao

import androidx.room.*
import com.kminder.data.local.entity.JournalEntryEntity
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
     * 일기를 업데이트합니다.
     */
    @Update
    suspend fun update(entry: JournalEntryEntity)
    
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
     * ID로 일기를 조회합니다.
     */
    @Query("SELECT * FROM journal_entries WHERE id = :entryId")
    suspend fun getById(entryId: Long): JournalEntryEntity?
    
    /**
     * 모든 일기를 조회합니다 (최신순).
     */
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<JournalEntryEntity>>
    
    /**
     * 특정 날짜의 일기를 조회합니다.
     * @param dateStart 날짜 시작 (YYYY-MM-DD 00:00:00)
     * @param dateEnd 날짜 종료 (YYYY-MM-DD 23:59:59)
     */
    @Query("SELECT * FROM journal_entries WHERE createdAt >= :dateStart AND createdAt <= :dateEnd ORDER BY createdAt DESC")
    suspend fun getByDate(dateStart: String, dateEnd: String): List<JournalEntryEntity>
    
    /**
     * 특정 기간의 일기를 조회합니다.
     */
    @Query("SELECT * FROM journal_entries WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    suspend fun getByDateRange(startDate: String, endDate: String): List<JournalEntryEntity>
    
    /**
     * 감정 분석이 완료되지 않은 일기를 조회합니다.
     */
    @Query("SELECT * FROM journal_entries WHERE anger IS NULL ORDER BY createdAt DESC")
    suspend fun getEntriesWithoutAnalysis(): List<JournalEntryEntity>
    
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
