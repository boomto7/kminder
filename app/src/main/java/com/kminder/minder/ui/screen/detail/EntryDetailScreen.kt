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
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.domain.model.AnalysisStatus
import com.kminder.minder.ui.component.chart.ConstellationChart
import com.kminder.domain.model.JournalEntry
import com.kminder.minder.R
import com.kminder.minder.data.mock.MockData
import com.kminder.minder.ui.component.EmotionPolarChart
import com.kminder.minder.ui.component.chart.NetworkChart
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.component.BlockingLoadingOverlay
import com.kminder.minder.ui.component.OutlinedDivider
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.util.EmotionColorUtil
import com.kminder.minder.util.EmotionUiUtil
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.ui.screen.write.WriteEntryContent
import com.kminder.minder.ui.screen.write.WriteUiState
import com.kminder.minder.ui.theme.MinderTheme
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
    onNavigateToEdit: (Long) -> Unit,
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

    EntryDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onDeleteClick = { showDeleteDialog = true },
        onRetryAnalysis = { viewModel.retryAnalysis(entryId) },
        onNavigateToAnalysisDetail = { onNavigateToAnalysisDetail(entryId) },
        onEditClick = { onNavigateToEdit(entryId) }
    )
}

@Composable
fun EntryDetailContent(
    uiState: EntryDetailUiState,
    onNavigateBack: () -> Unit,
    onDeleteClick: () -> Unit,
    onRetryAnalysis: () -> Unit,
    onNavigateToAnalysisDetail: () -> Unit,
    onEditClick: () -> Unit
) {
    val isAnalyzing = (uiState as? EntryDetailUiState.Success)?.isAnalyzing == true
    val entry = (uiState as? EntryDetailUiState.Success)?.entry
    
    // Edit possible if not analyzing and status is not COMPLETED
    val isEditable = !isAnalyzing && entry != null && entry.status != AnalysisStatus.COMPLETED

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground)
    ) {
        Scaffold(
            containerColor = MinderBackground,
            topBar = {
                EntryDetailTopBar(
                    onBackClick = onNavigateBack,
                    onDeleteClick = onDeleteClick,
                    onEditClick = onEditClick,
                    isEditable = isEditable
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Divider matching List screen style
                    OutlinedDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .height(4.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        when (uiState) {
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
                                    DetailBody(
                                        entry = uiState.entry,
                                        isAnalyzing = uiState.isAnalyzing,
                                        onRetryAnalysis = onRetryAnalysis,
                                        onNavigateToAnalysisDetail = onNavigateToAnalysisDetail
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        BlockingLoadingOverlay(
            isVisible = isAnalyzing,
            message = stringResource(R.string.entry_detail_analysis_analyzing)
        )
    }
}

@Composable
fun EntryDetailTopBar(
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    isEditable: Boolean
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button & Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            RetroIconButton(
                onClick = onBackClick,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.common_back)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = stringResource(R.string.entry_detail_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        Row {
            // Edit Button (Visible only if editable)
            if (isEditable) {
                RetroIconButton(
                    onClick = onEditClick,
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Delete Button
            RetroIconButton(
                onClick = onDeleteClick,
                icon = Icons.Default.Delete,
                contentDescription = "Delete"
            )
        }
    }
}

@Composable
fun DetailBody(
    entry: JournalEntry,
    isAnalyzing: Boolean = false,
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
            if ((status == AnalysisStatus.FAILED || status == AnalysisStatus.NONE || status == AnalysisStatus.PENDING) && !isAnalyzing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (status == AnalysisStatus.FAILED) {
                        Text(
                            text = stringResource(R.string.entry_detail_analysis_failed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    Button(
                        onClick = onRetryAnalysis,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text(
                            text = stringResource(
                                if (status == AnalysisStatus.FAILED) R.string.entry_detail_analysis_retry 
                                else R.string.entry_detail_analysis_start
                            ), 
                            color = Color.White
                        )
                    }
                }
            } else if (isAnalyzing) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

@Preview(showBackground = true)
@Composable
fun WriteEntryScreenPreview() {
    val mockEntry = MockData.mockJournalEntries[52]
    MinderTheme {
        EntryDetailContent(
            uiState = EntryDetailUiState.Success(mockEntry),
            onNavigateBack = {},
            onDeleteClick = {  },
            onRetryAnalysis = {  },
            onNavigateToAnalysisDetail = {},
            onEditClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WriteEntryScreenLoadingPreview() {
    MinderTheme {
        val mockEntry = MockData.mockJournalEntries[21]
        MinderTheme {
            EntryDetailContent(
                uiState = EntryDetailUiState.Success(mockEntry),
                onNavigateBack = {},
                onDeleteClick = {  },
                onRetryAnalysis = {  },
                onNavigateToAnalysisDetail = {},
                onEditClick = {}
            )
        }
    }
}