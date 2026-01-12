package com.kminder.minder.ui.screen.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.clickable
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.minder.ui.component.chart.ConstellationChart
import com.kminder.domain.model.JournalEntry
import com.kminder.minder.R
import com.kminder.minder.data.mock.MockData
import com.kminder.minder.ui.component.EmotionPolarChart
import com.kminder.minder.ui.component.chart.NetworkChart
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.screen.home.OutlinedDivider
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.util.EmotionColorUtil
import com.kminder.minder.util.EmotionUiUtil
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 일기 상세 화면
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntryDetailScreen(
    entryId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToAnalysisDetail: (Long) -> Unit,
    viewModel: EntryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load data on first launch
    LaunchedEffect(entryId) {
        viewModel.loadEntry(entryId)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.entry_detail_delete_dialog_title)) },
            text = { Text(text = stringResource(R.string.entry_detail_delete_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEntry(entryId) {
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                }) {
                    Text(stringResource(R.string.common_delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Header for consistent alignment (matching EntryListScreen)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                RetroIconButton(
                    onClick = onNavigateBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_back)
                )

                // Delete Button
                RetroIconButton(
                    onClick = { showDeleteDialog = true },
                    icon = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }

            // Divider matching List screen style (optional but good for consistency)
            OutlinedDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(4.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )

            // Content
            Box(modifier = Modifier.weight(1f)) {
                when (val state = uiState) {
                    is EntryDetailUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.Black)
                        }
                    }

                    is EntryDetailUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.entry_detail_load_error))
                        }
                    }

                    is EntryDetailUiState.Success -> {
                        @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
                        CompositionLocalProvider(
                            LocalOverscrollFactory provides null
                        ) {
                            DetailContent(
                                entry = state.entry,
                                onRetryAnalysis = { viewModel.retryAnalysis(entryId) },
                                onNavigateToAnalysisDetail = { onNavigateToAnalysisDetail(entryId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailContent(
    entry: JournalEntry,
    onRetryAnalysis: () -> Unit = {},
    onNavigateToAnalysisDetail: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val emotionResult = entry.emotionResult
    val cardColor = emotionResult?.let { EmotionColorUtil.getEmotionResultColor(it) } ?: Color.White

    val context = LocalContext.current
    val stringProvider = remember { AndroidEmotionStringProvider(context) }
    val density = LocalDensity.current.density

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Date Header
        val dateStr =
            entry.createdAt.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREA))
        val timeStr = entry.createdAt.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA))

        Text(
            text = dateStr,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Text(
            text = timeStr,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 1. Journal Content
        Text(
            text = stringResource(R.string.entry_detail_section_journal),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 120.dp) // Adjusted min height for better initial view
                .background(Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp)) // Neo-Brutalism border
                .padding(24.dp)
        ) {
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Emotion Analysis
        val result = entry.emotionResult
        if (result != null) {
            Text(
                text = stringResource(R.string.analysis_report_card_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 2-1. Emotion Report Card
            NeoShadowBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                containerColor = cardColor
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Title (Emotion Label) with Image
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val imageResId = EmotionUiUtil.getEmotionImageResId(context, result)
                        if (imageResId != null) {
                            coil.compose.AsyncImage(
                                model = imageResId,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = EmotionUiUtil.getLabel(result, stringProvider),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text(
                        text = EmotionUiUtil.getDescription(result, stringProvider),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Advice
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.Black.copy(alpha = 0.05f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.common_tip_prefix),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = EmotionUiUtil.getAdvice(result, stringProvider),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2-2. Word Cloud Analysis (NetworkChart)
            Text(
                text = stringResource(R.string.analysis_detail_chart_word_cloud), // "Word Cloud Analysis"
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            NeoShadowBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f),
                shape = RoundedCornerShape(24.dp)
            ) {
                // Use remember to prevent unnecessary recomposition
                val networkChart = remember(result) {
                    movableContentOf {
                        NetworkChart(
                            result = result,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                }
                networkChart()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2-3. Emotion Analysis Chart (Flippable: Polar <-> Constellation)
            Text(
                text = stringResource(R.string.analysis_detail_chart_emotion_analysis), // "Emotion Analysis" or similar
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Flippable Chart Container
            var isPolarView by remember { mutableStateOf(true) }
            val rotation by androidx.compose.animation.core.animateFloatAsState(
                targetValue = if (isPolarView) 0f else 180f,
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 600),
                label = "ChartFlip"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { isPolarView = !isPolarView }
            ) {
                NeoShadowBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        },
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Content wrapper to handle back-face visibility logic
                        if (rotation <= 90f) {
                            // Front: PolarChart
                            EmotionPolarChart(
                                emotions = result.source,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            )
                        } else {
                            // Back: ConstellationChart
                            // Rotate content back 180 degrees so it's not mirrored
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer { rotationY = 180f }
                            ) {
                                ConstellationChart(
                                    result = result,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Derivation Tip
            Text(
                text = "* "+EmotionUiUtil.getDerivationExplanation(result, stringProvider),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

        } else {
            // Analysis not available
            val status = entry.status
            if (status == com.kminder.domain.model.AnalysisStatus.FAILED || status == com.kminder.domain.model.AnalysisStatus.NONE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.entry_detail_analysis_failed),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRetryAnalysis,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text(stringResource(R.string.entry_detail_analysis_retry), color = Color.White)
                    }
                }
            } else if (status == com.kminder.domain.model.AnalysisStatus.PENDING) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.Black,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.entry_detail_analysis_analyzing),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFFe5e4c8)
@Composable
fun EntryDetailPreview() {
//    val mockEntry = com.kminder.domain.model.JournalEntry(
//        id = 1,
//        content = "오늘은 정말 기분 좋은 날이었다. 친구들과 만나서 즐거운 시간을 보냈다. 날씨도 좋고 모든 게 완벽했다.",
//        entryType = com.kminder.domain.model.EntryType.FREE_WRITING,
//        createdAt = java.time.LocalDateTime.now(),
//        updatedAt = java.time.LocalDateTime.now(),
//        emotionAnalysis = com.kminder.domain.model.EmotionAnalysis(
//            anger = 0.0f,
//            anticipation = 0.0f,
//            joy = 0.9f,
//            trust = 0.5f,
//            fear = 0.0f,
//            sadness = 0.0f,
//            disgust = 0.0f,
//            surprise = 0.2f,
//            keywords = listOf(
//                com.kminder.domain.model.EmotionKeyword("행복", com.kminder.domain.model.EmotionType.JOY, 0.9f),
//                com.kminder.domain.model.EmotionKeyword("친구", com.kminder.domain.model.EmotionType.TRUST, 0.8f),
//                com.kminder.domain.model.EmotionKeyword("날씨", com.kminder.domain.model.EmotionType.JOY, 0.7f)
//            )
//        )
//    )

    val mockEntry = MockData.mockJournalEntries[2]
    MaterialTheme {
        DetailContent(entry = mockEntry)
    }
}
