package com.kminder.data.di

import com.kminder.data.repository.EmotionAnalysisRepositoryImpl
import com.kminder.data.repository.JournalRepositoryImpl
import com.kminder.data.repository.QuestionRepositoryImpl
import com.kminder.domain.repository.EmotionAnalysisRepository
import com.kminder.domain.repository.JournalRepository
import com.kminder.domain.repository.QuestionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository 관련 Hilt 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * JournalRepository 바인딩
     */
    @Binds
    @Singleton
    abstract fun bindJournalRepository(
        journalRepositoryImpl: JournalRepositoryImpl
    ): JournalRepository
    
    /**
     * EmotionAnalysisRepository 바인딩
     */
    @Binds
    @Singleton
    abstract fun bindEmotionAnalysisRepository(
        emotionAnalysisRepositoryImpl: EmotionAnalysisRepositoryImpl
    ): EmotionAnalysisRepository
    
    /**
     * QuestionRepository 바인딩
     */
    @Binds
    @Singleton
    abstract fun bindQuestionRepository(
        questionRepositoryImpl: QuestionRepositoryImpl
    ): QuestionRepository
}
