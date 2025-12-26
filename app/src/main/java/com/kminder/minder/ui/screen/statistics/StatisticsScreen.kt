package com.kminder.minder.ui.screen.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.minder.R
import com.kminder.minder.ui.component.chart.NetworkChart

/**
 * 통계/차트 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.statistics_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.keywords.isEmpty()) {
                Text("아직 분석된 감정 키워드가 없습니다.")
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "감정 관계망 (Last 30 Days)",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    val aggregatedAnalysis = androidx.compose.runtime.remember(uiState.keywords) {
                        val emotionMap = uiState.keywords.groupBy { it.emotion }
                            .mapValues { (_, k) -> k.sumOf { it.score.toDouble() }.toFloat().coerceAtMost(1.0f) }

                        com.kminder.domain.model.EmotionAnalysis(
                            joy = emotionMap[com.kminder.domain.model.EmotionType.JOY] ?: 0f,
                            trust = emotionMap[com.kminder.domain.model.EmotionType.TRUST] ?: 0f,
                            fear = emotionMap[com.kminder.domain.model.EmotionType.FEAR] ?: 0f,
                            surprise = emotionMap[com.kminder.domain.model.EmotionType.SURPRISE] ?: 0f,
                            sadness = emotionMap[com.kminder.domain.model.EmotionType.SADNESS] ?: 0f,
                            disgust = emotionMap[com.kminder.domain.model.EmotionType.DISGUST] ?: 0f,
                            anger = emotionMap[com.kminder.domain.model.EmotionType.ANGER] ?: 0f,
                            anticipation = emotionMap[com.kminder.domain.model.EmotionType.ANTICIPATION] ?: 0f,
                            keywords = uiState.keywords
                        )
                    }

                    NetworkChart(
                        analysis = aggregatedAnalysis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        }
    }
}
