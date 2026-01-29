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
    /**
     * 통계/차트 화면
     */
    data object Statistics : Screen("statistics?period={period}&dateMillis={dateMillis}") {
        fun createRoute(period: String? = null, dateMillis: Long? = null): String {
            return if (period != null && dateMillis != null) {
                "statistics?period=$period&dateMillis=$dateMillis"
            } else {
                "statistics"
            }
        }
    }

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

    /**
     * 마인드 블러썸 화면 (통합 감정 분석 / 버블 차트)
     */
    data object MindBlossom : Screen("mind_blossom/{period}/{dateMillis}") {
        fun createRoute(period: String, dateMillis: Long) = "mind_blossom/$period/$dateMillis"
    }
}
