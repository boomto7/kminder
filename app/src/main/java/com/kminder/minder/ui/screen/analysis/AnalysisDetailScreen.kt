package com.kminder.minder.ui.screen.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.minder.R

import com.kminder.minder.ui.provider.AndroidEmotionStringProvider

import com.kminder.minder.ui.screen.list.RetroIconButton
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.LocalOverscrollFactory
import com.kminder.minder.ui.component.EmotionPolarChart
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.component.chart.ConstellationChart
import com.kminder.minder.ui.theme.MinderBackground
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
                            CompositionLocalProvider(
                                LocalOverscrollFactory provides null
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val context = LocalContext.current
                                    val stringProvider = remember { AndroidEmotionStringProvider(context) }
                                    
                                    // Title (Emotion Label)
                                    Text(
                                        text = EmotionUiUtil.getLabel(result, stringProvider),
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = Color.Black
                                    )
                                    
                                    Spacer(modifier = Modifier.height(32.dp))
                                    
                                    // 1. Emotion Polar Chart (Neo-Brutalism Wrapped)
                                    NeoShadowBox(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                        shape = RoundedCornerShape(24.dp)
                                    ) {
                                        EmotionPolarChart(
                                            emotions = result.source,
                                            modifier = Modifier.fillMaxSize().padding(16.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(32.dp))
                                    
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RetroIconButton(
            onClick = onNavigateBack,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_back)
        )
        Text(
            text = stringResource(R.string.analysis_detail_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        // Balance spacer (Size matching RetroIconButton default size)
        Spacer(modifier = Modifier.size(50.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PreviewAnalysisDetailScreen() {
    val mockEntry = com.kminder.minder.data.mock.MockData.mockJournalEntries.first()
    com.kminder.minder.ui.theme.MinderTheme {
        AnalysisDetailContent(
            uiState = AnalysisDetailUiState.Success(mockEntry),
            onNavigateBack = {}
        )
    }
}
