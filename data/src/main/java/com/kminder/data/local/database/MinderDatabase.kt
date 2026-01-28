package com.kminder.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kminder.data.local.converter.EmotionKeywordListConverter
import com.kminder.data.local.converter.EmotionTypeListConverter
import com.kminder.data.local.converter.StringListConverter
import com.kminder.data.local.dao.JournalEntryDao
import com.kminder.data.local.entity.EmotionAnalysisEntity
import com.kminder.data.local.entity.JournalEntryEntity

/**
 * Minder 앱의 Room Database
 */
@Database(
    entities = [JournalEntryEntity::class, EmotionAnalysisEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class, EmotionKeywordListConverter::class, EmotionTypeListConverter::class)
abstract class MinderDatabase : RoomDatabase() {
    
    /**
     * 일기 DAO
     */
    abstract fun journalEntryDao(): JournalEntryDao
    
    companion object {
        const val DATABASE_NAME = "minder_database"
    }
}
