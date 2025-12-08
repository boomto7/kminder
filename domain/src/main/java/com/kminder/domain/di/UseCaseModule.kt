package com.kminder.domain.di

import com.kminder.domain.repository.EmotionAnalysisRepository
import com.kminder.domain.repository.JournalRepository
import com.kminder.domain.repository.QuestionRepository
import com.kminder.domain.usecase.emotion.AnalyzeEmotionUseCase
import com.kminder.domain.usecase.emotion.SaveAndAnalyzeJournalEntryUseCase
import com.kminder.domain.usecase.journal.*
import com.kminder.domain.usecase.question.GetRandomQuestionUseCase
import com.kminder.domain.usecase.statistics.GetEmotionStatisticsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Domain UseCase 관련 Hilt 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    // Journal UseCases
    
    @Provides
    @Singleton
    fun provideCreateJournalEntryUseCase(
        repository: JournalRepository
    ): CreateJournalEntryUseCase = CreateJournalEntryUseCase(repository)
    
    @Provides
    @Singleton
    fun provideUpdateJournalEntryUseCase(
        repository: JournalRepository
    ): UpdateJournalEntryUseCase = UpdateJournalEntryUseCase(repository)
    
    @Provides
    @Singleton
    fun provideDeleteJournalEntryUseCase(
        repository: JournalRepository
    ): DeleteJournalEntryUseCase = DeleteJournalEntryUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetJournalEntryUseCase(
        repository: JournalRepository
    ): GetJournalEntryUseCase = GetJournalEntryUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetAllJournalEntriesUseCase(
        repository: JournalRepository
    ): GetAllJournalEntriesUseCase = GetAllJournalEntriesUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetJournalEntriesByDateUseCase(
        repository: JournalRepository
    ): GetJournalEntriesByDateUseCase = GetJournalEntriesByDateUseCase(repository)
    
    // Emotion UseCases
    
    @Provides
    @Singleton
    fun provideAnalyzeEmotionUseCase(
        repository: EmotionAnalysisRepository
    ): AnalyzeEmotionUseCase = AnalyzeEmotionUseCase(repository)
    
    @Provides
    @Singleton
    fun provideSaveAndAnalyzeJournalEntryUseCase(
        journalRepository: JournalRepository,
        emotionAnalysisRepository: EmotionAnalysisRepository
    ): SaveAndAnalyzeJournalEntryUseCase = SaveAndAnalyzeJournalEntryUseCase(
        journalRepository,
        emotionAnalysisRepository
    )
    
    // Statistics UseCases
    
    @Provides
    @Singleton
    fun provideGetEmotionStatisticsUseCase(
        repository: JournalRepository
    ): GetEmotionStatisticsUseCase = GetEmotionStatisticsUseCase(repository)
    
    // Question UseCases
    
    @Provides
    @Singleton
    fun provideGetRandomQuestionUseCase(
        repository: QuestionRepository
    ): GetRandomQuestionUseCase = GetRandomQuestionUseCase(repository)
}
