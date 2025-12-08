package com.kminder.data.di

import android.content.Context
import androidx.room.Room
import com.kminder.data.local.dao.JournalEntryDao
import com.kminder.data.local.database.MinderDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database 관련 Hilt 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * MinderDatabase 인스턴스 제공
     */
    @Provides
    @Singleton
    fun provideMinderDatabase(
        @ApplicationContext context: Context
    ): MinderDatabase {
        return Room.databaseBuilder(
            context,
            MinderDatabase::class.java,
            MinderDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    /**
     * JournalEntryDao 제공
     */
    @Provides
    @Singleton
    fun provideJournalEntryDao(
        database: MinderDatabase
    ): JournalEntryDao {
        return database.journalEntryDao()
    }
}
