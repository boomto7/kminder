package com.kminder.minder.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionType
import com.kminder.minder.R
import com.kminder.minder.ui.component.IntegratedEmotionBubbleChart
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.util.EmotionColorUtil
import com.kminder.minder.util.EmotionUiUtil

@Composable
fun MindBlossomScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val stringProvider = remember(context) { com.kminder.minder.ui.provider.AndroidEmotionStringProvider(context) }

    // 데이터 집계 (카운트 계산)
    val emotionCounts = remember(uiState.statistics, stringProvider) {
        val counts = mutableMapOf<String, Int>()
        val conflictFormat = context.getString(R.string.analysis_conflict_format)

        val allEntries = uiState.statistics.flatMap { it.entries }
            .filter { it.hasEmotionAnalysis() && it.emotionResult != null }

        allEntries.forEach { entry ->
            val result = entry.emotionResult!!
            val rawLabel = EmotionUiUtil.getLabel(result, stringProvider)
            
            // "사랑 (기쁨+신뢰)" -> "사랑" (Remove composition info for chart clarity)
            val simpleLabel = rawLabel.substringBefore(" (")
            counts[simpleLabel] = (counts[simpleLabel] ?: 0) + 1
        }
        counts
    }

    Scaffold(
        topBar = {
             Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RetroIconButton(
                    onClick = onNavigateBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_back)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = stringResource(R.string.statistics_mind_blossom),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        },
        containerColor = MinderBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MinderBackground)
        ) {
            if (uiState.totalEntryCount == 0) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     Text(stringResource(R.string.analysis_no_data_available), color = Color.Gray)
                 }
            } else {
                 // Chart Container including logic
                 Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                     IntegratedEmotionBubbleChart(
                         emotionCounts = emotionCounts,
                         modifier = Modifier.fillMaxSize()
                     )
                 }
            }
        }
    }
}
