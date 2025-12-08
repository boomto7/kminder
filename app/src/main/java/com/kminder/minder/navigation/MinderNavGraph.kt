package com.kminder.minder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kminder.minder.ui.screen.home.HomeScreen
import com.kminder.minder.ui.screen.write.WriteEntryScreen
import com.kminder.minder.ui.screen.list.EntryListScreen
import com.kminder.minder.ui.screen.detail.EntryDetailScreen
import com.kminder.minder.ui.screen.statistics.StatisticsScreen

/**
 * 앱의 Navigation Graph
 */
@Composable
fun MinderNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
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
                }
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
