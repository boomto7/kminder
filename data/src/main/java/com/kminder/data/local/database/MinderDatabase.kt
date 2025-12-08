package com.kminder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kminder.data.local.dao.JournalEntryDao
import com.kminder.data.local.entity.JournalEntryEntity

/**
 * Minder 앱의 Room Database
 */
@Database(
    entities = [JournalEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MinderDatabase : RoomDatabase() {
    
    /**
     * 일기 DAO
     */
    abstract fun journalEntryDao(): JournalEntryDao
    
    companion object {
        const val DATABASE_NAME = "minder_database"
    }
}
