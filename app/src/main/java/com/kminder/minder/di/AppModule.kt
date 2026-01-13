package com.kminder.minder.di

import com.kminder.domain.provider.EmotionStringProvider
import com.kminder.domain.provider.LanguageProvider
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.ui.provider.SystemLanguageProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindEmotionStringProvider(
        impl: AndroidEmotionStringProvider
    ): EmotionStringProvider

    @Binds
    @Singleton
    abstract fun bindLanguageProvider(
        impl: SystemLanguageProvider
    ): LanguageProvider

}
