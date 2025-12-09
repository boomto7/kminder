package com.kminder.minder.data.remote.api

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kminder.data.BuildConfig
import com.kminder.data.remote.api.GeminiApiClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Gemini API (Firebase Vertex AI) 실제 연동 테스트 (App 모듈)
 * 
 * 실행 방법:
 * 1. Android 기기 또는 에뮬레이터 연결
 * 2. 이 파일의 옆에 있는 실행 버튼(▶️) 클릭
 * 또는 터미널: ./gradlew :app:connectedAndroidTest
 */
import com.google.firebase.FirebaseApp
import androidx.test.platform.app.InstrumentationRegistry

@RunWith(AndroidJUnit4::class)
class GeminiApiAppInstrumentedTest {

    @Test
    fun testRealEmotionAnalysisWithFirebase() = runBlocking {
        // 테스트 환경에서 Firebase 초기화 보장
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        // API Key (app 모듈의 BuildConfig 사용)
        // 만약 키가 없다면 data 모듈의 BuildConfig를 import해서 써야 할 수도 있음.
        // 일단 app의 BuildConfig를 먼저 시도.
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            // App 모듈에 키가 없으면 하드코딩된 더미키 (Firebase는 json 사용하므로 통과될 수도)
            "dummy_key"
        }
        
        val apiClient = GeminiApiClient(apiKey)
        
        val testText = "오늘 드디어 그 발표를 끝냈다! 결과가 정말 기대돼서 잠이 안 올 것 같다. 빨리 내일이 왔으면 좋겠어!"
        
        Log.d("GeminiTest", "🚀 테스트 시작 (App Module): $testText")

        // When
        val result = apiClient.analyzeEmotion(testText)

        // 실패 시 에러 내용을 로그로 출력
        result.onFailure { exception ->
            Log.e("GeminiTest", "❌ 분석 실패! 원인: ${exception.message}", exception)
        }

        // Then
        assertTrue("분석 결과는 성공해야 합니다. 원인: ${result.exceptionOrNull()?.message}", result.isSuccess)
        
        val analysis = result.getOrThrow()
        Log.d("GeminiTest", "✅ 분석 성공!")
        Log.d("GeminiTest", "주요 감정: ${analysis.getDominantEmotion()}")
        Log.d("GeminiTest", "기쁨(Joy): ${analysis.joy}")
        Log.d("GeminiTest", "기대(Anticipation): ${analysis.anticipation}")
        
        // 검증
        assertTrue(analysis.joy > 0.3f || analysis.anticipation > 0.3f)
    }
}
