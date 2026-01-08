package com.kminder.minder.ui.screen.analysis

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.kminder.minder.R
import com.kminder.minder.ui.component.EmotionPolarChart
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.component.chart.ConstellationChart
import com.kminder.minder.ui.component.chart.NetworkChart
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.util.EmotionColorUtil
import com.kminder.minder.util.EmotionUiUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisDetailScreen(
    entryId: Long,
    onNavigateBack: () -> Unit,
    viewModel: AnalysisDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load data
    LaunchedEffect(entryId) {
        viewModel.loadEntry(entryId)
    }

    AnalysisDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnalysisDetailContent(
    uiState: AnalysisDetailUiState,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            AnalysisDetailHeader(onNavigateBack)

            // Content
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is AnalysisDetailUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                    }
                    is AnalysisDetailUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.analysis_detail_load_error))
                        }
                    }
                    is AnalysisDetailUiState.Success -> {
                        val entry = state.entry
                        val result = entry.emotionResult

                        
                        if (result != null) {
                            val cardColor = EmotionColorUtil.getEmotionResultColor(result)
                            CompositionLocalProvider(
                                LocalOverscrollFactory provides null
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.Start,

                                ) {
                                    val context = LocalContext.current
                                    val stringProvider = remember { AndroidEmotionStringProvider(context) }

                                    // Title (Emotion Label)
                                    Text(
                                        text = EmotionUiUtil.getLabel(result, stringProvider),
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color.Black
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = stringResource(R.string.analysis_detail_chart_title_1),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black.copy(alpha = 0.8f)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    

                                    
                                    // 2. Constellation Chart (Neo-Brutalism Wrapped)
                                    NeoShadowBox(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        ConstellationChart(
                                            result = result,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(24.dp))

                                    // 1. Emotion Polar Chart (Neo-Brutalism Wrapped)
                                    Text(
                                        text = stringResource(R.string.analysis_detail_chart_title_2),
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black.copy(alpha = 0.8f)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    NeoShadowBox(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1.8f),
                                        shape = RoundedCornerShape(24.dp),
                                        containerColor = cardColor
                                    ) {

                                        NetworkChart(
                                            result = result,
                                            modifier = Modifier.fillMaxSize().padding(16.dp)
                                        )
//                                        EmotionPolarChart(
//                                            emotions = result.source,
//                                            modifier = Modifier.fillMaxSize().padding(16.dp)
//                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.analysis_detail_no_data))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisDetailHeader(onNavigateBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        // Only back button, so Start is fine, or SpaceBetween
        ) {
            RetroIconButton(
                onClick = onNavigateBack,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.common_back)
            )

            Text(
                text = stringResource(R.string.analysis_detail_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color.Black
            )

            // Dummy box for alignment balance
            Spacer(modifier = Modifier.size(50.dp))
        }

        com.kminder.minder.ui.screen.home.OutlinedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(4.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewAnalysisDetailScreen() {
    val mockEntry = com.kminder.minder.data.mock.MockData.mockJournalEntries[53]
    com.kminder.minder.ui.theme.MinderTheme {
        AnalysisDetailContent(
            uiState = AnalysisDetailUiState.Success(mockEntry),
            onNavigateBack = {}
        )
    }
}
