package com.kminder.data.di

import com.kminder.data.BuildConfig
import com.kminder.data.remote.api.GeminiApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Network 관련 Hilt 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    /**
     * GeminiApiClient 제공
     * API 키는 local.properties에서 읽어옴
     */
    @Provides
    @Singleton
    fun provideGeminiApiClient(): GeminiApiClient {
        return GeminiApiClient()
    }
}
