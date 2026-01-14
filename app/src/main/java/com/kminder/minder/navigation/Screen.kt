package com.kminder.minder.navigation

/**
 * 앱 내 화면 라우트
 */
sealed class Screen(val route: String) {
    /**
     * 스플래시 화면
     */
    data object Splash : Screen("splash")
    
    /**
     * 홈/대시보드 화면
     */
    data object Home : Screen("home")
    
    /**
     * 일기 작성 화면
     */
    data object WriteEntry : Screen("write_entry?entryId={entryId}") {
        fun createRoute(entryId: Long? = null): String {
            return if (entryId != null) "write_entry?entryId=$entryId" else "write_entry"
        }
    }
    
    /**
     * 일기 목록 화면
     */
    data object EntryList : Screen("entry_list")
    
    /**
     * 일기 상세 화면
     */
    data object EntryDetail : Screen("entry_detail/{entryId}") {
        fun createRoute(entryId: Long) = "entry_detail/$entryId"
    }
    
    /**
     * 통계/차트 화면
     */
    data object Statistics : Screen("statistics")

    /**
     * 감정 가이드 화면
     */
    data object EmotionGuide : Screen("emotion_guide")

    /**
     * 감정 분석 상세 화면
     */
    data object AnalysisDetail : Screen("analysis_detail/{entryId}") {
        fun createRoute(entryId: Long) = "analysis_detail/$entryId"
    }
}
