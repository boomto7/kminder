package com.kminder.minder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kminder.minder.ui.screen.splash.SplashScreenV2
import com.kminder.minder.ui.screen.home.HomeScreen
import com.kminder.minder.ui.screen.write.WriteEntryScreen
import com.kminder.minder.ui.screen.list.EntryListScreen
import com.kminder.minder.ui.screen.detail.EntryDetailScreen
import com.kminder.minder.ui.screen.statistics.StatisticsScreen
import com.kminder.minder.ui.screen.guide.EmotionGuideScreen

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
                    navController.navigate(Screen.Home.route) {
                        // 스플래시 화면을 백스택에서 제거
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 홈 화면
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToWrite = {
                    navController.navigate(Screen.WriteEntry.route)
                },
                onNavigateToList = {
                    navController.navigate(Screen.EntryList.route)
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToGuide = {
                    navController.navigate(Screen.EmotionGuide.route)
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
        composable(Screen.WriteEntry.route) {
            WriteEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEntrySaved = {
                    navController.popBackStack()
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
                }
            )
        }
        
        // 통계 화면
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
