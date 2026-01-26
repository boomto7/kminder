package com.kminder.minder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kminder.minder.ui.screen.splash.SplashScreenV2
import com.kminder.minder.ui.screen.home.HomeFeedScreen
import com.kminder.minder.ui.screen.write.WriteEntryScreen
import com.kminder.minder.ui.screen.list.EntryListScreen
import com.kminder.minder.ui.screen.detail.EntryDetailScreen
import com.kminder.minder.ui.screen.statistics.StatisticsScreen
import com.kminder.minder.ui.screen.guide.EmotionGuideScreen
import com.kminder.minder.ui.screen.analysis.AnalysisDetailScreen

/**
 * 앱의 Navigation Graph
 */
@Composable
fun MinderNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 스플래시 화면
        composable(Screen.Splash.route) {
            SplashScreenV2(
                onNavigateToHome = {
                    //todo 임시적으로 화면을 이동하지 못하게 한다.
                    navController.navigate(Screen.Home.route) {
                        // 스플래시 화면을 백스택에서 제거
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 홈 화면
        composable(Screen.Home.route) {
            HomeFeedScreen(
                onNavigateToWrite = {
                    navController.navigate(Screen.WriteEntry.route)
                },
                onNavigateToList = {
                    navController.navigate(Screen.EntryList.route)
                },
                onNavigateToStatistics = { period, dateMillis ->
                    if (period != null && dateMillis != null) {
                        navController.navigate(Screen.Statistics.createRoute(period.name, dateMillis))
                    } else {
                        navController.navigate(Screen.Statistics.createRoute())
                    }
                },
                onNavigateToDetail = { entryId ->
                    navController.navigate(Screen.EntryDetail.createRoute(entryId))
                }
            )
        }

        // 감정 가이드 화면
        composable(Screen.EmotionGuide.route) {
            EmotionGuideScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 일기 작성 화면
        composable(
            route = Screen.WriteEntry.route,
            arguments = listOf(
                navArgument("entryId") {
                    type = NavType.LongType
                    defaultValue = 0L // 0L implies new entry
                }
            )
        ) {
            WriteEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEntrySaved = { entryId ->
                    navController.navigate(Screen.EntryDetail.createRoute(entryId)) {
                        popUpTo(Screen.WriteEntry.route) { inclusive = true }
                    }
                }
            )
        }

        // 일기 목록 화면
        composable(Screen.EntryList.route) {
            EntryListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEntryClick = { entryId ->
                    navController.navigate(Screen.EntryDetail.createRoute(entryId))
                },
                onNavigateToWrite = {},
            )
        }

        // 일기 상세 화면
        composable(
            route = Screen.EntryDetail.route,
            arguments = listOf(
                navArgument("entryId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            EntryDetailScreen(
                entryId = entryId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAnalysisDetail = { id ->
                    navController.navigate(Screen.AnalysisDetail.createRoute(id))
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.WriteEntry.createRoute(id))
                }
            )
        }

        // 감정 분석 상세 화면
        composable(
            route = Screen.AnalysisDetail.route,
            arguments = listOf(
                navArgument("entryId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            AnalysisDetailScreen(
                entryId = entryId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 통계 화면
        composable(
            route = Screen.Statistics.route,
            arguments = listOf(
                navArgument("period") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("dateMillis") {
                    type = NavType.LongType
                    defaultValue = -1L // Use -1L to indicate no value if nullable doesn't work well with primitives
                }
            )
        ) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToIntegratedAnalysis = { period, date ->
                    val dateMillis = date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                    navController.navigate(Screen.IntegratedAnalysis.createRoute(period.name, dateMillis))
                }
            )
        }

        // 통합 감정 분석 화면
        composable(
            route = Screen.IntegratedAnalysis.route,
            arguments = listOf(
                navArgument("period") { type = NavType.StringType },
                navArgument("dateMillis") { type = NavType.LongType }
            )
        ) {
            com.kminder.minder.ui.screen.statistics.IntegratedAnalysisScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
