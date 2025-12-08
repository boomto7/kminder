package com.kminder.minder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Minder Application 클래스
 * Hilt를 사용하기 위한 진입점
 */
@HiltAndroidApp
class MinderApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
