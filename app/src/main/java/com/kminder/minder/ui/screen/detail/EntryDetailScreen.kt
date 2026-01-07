package com.kminder.minder.ui.screen.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.JournalEntry
import com.kminder.minder.R
import com.kminder.minder.data.mock.MockData
import com.kminder.minder.ui.component.chart.NetworkChart
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
            title = { Text(text = "일기 삭제") },
            text = { Text(text = "정말로 이 일기를 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteEntry(entryId) {
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                }) {
                    Text("삭제", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
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
                            Text(text = "일기를 불러올 수 없습니다.")
                        }
                    }

                    is EntryDetailUiState.Success -> {
                        @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
                        CompositionLocalProvider(
                            LocalOverscrollFactory provides null
                        ) {
                            DetailContent(
                                entry = state.entry,
                                onRetryAnalysis = { viewModel.retryAnalysis(entryId) }
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
    onRetryAnalysis: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val emotionResult = entry.emotionResult
    val cardColor = emotionResult?.let { EmotionColorUtil.getEmotionResultColor(it) } ?: Color.White

    val context = LocalContext.current
    val stringProvider = remember { AndroidEmotionStringProvider(context) }

    // [New] State for showing detailed radar chart popup
    var selectedEmotionResult by remember { mutableStateOf<EmotionResult?>(null) }

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
            text = "Journal Content",
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

        // 2. Emotion Analysis (Chart inside Card Style)
        val result = entry.emotionResult
        if (entry.hasEmotionAnalysis() && result != null) {
            Text(
                text = "Emotion Analysis",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f) // Adjusted to 4:3 ratio to reduce vertical empty space
            ) {
                // Shadow
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 4.dp, y = 4.dp)
                        .background(Color.Black, RoundedCornerShape(16.dp))
                )

                // Content (Chart Container with Card Style)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(cardColor, RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    NetworkChart(
                        result = result,
                        onNodeClick = { nodeResult ->
                            selectedEmotionResult = nodeResult
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
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
                        text = "감정 분석 결과를 불러오지 못했습니다.",
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
                        Text("분석 다시 시도", color = Color.White)
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
                        text = "감정을 분석하고 있습니다...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }

    // Detail Pop-up (Radar Chart)
    if (selectedEmotionResult != null) {
        val result = selectedEmotionResult!!
        androidx.compose.ui.window.Dialog(onDismissRequest = { selectedEmotionResult = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Detail Analysis",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = EmotionUiUtil.getLabel(result, stringProvider),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Radar Chart
                    // Note: EmotionResult 모델 개선 단계에서 source 추가됨 (compile check required)
                    // 현재 Mock 등에서 source가 올바르게 들어오는지 확인 필요.
                    // 만약 source 필드가 아직 릴리즈되지 않은 라이브러리/모듈 문제라면 컴파일 에러가 날 수 있음.
                    // 앞선 step에서 source 필드 추가 완료했으므로 안전함.
                    com.kminder.minder.ui.component.chart.RadarChart(
                        analysis = result.source,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { selectedEmotionResult = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("닫기", color = Color.White)
                    }
                }
            }
        }
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

    val mockEntry = MockData.mockJournalEntries[1]
    MaterialTheme {
        DetailContent(entry = mockEntry)
    }
}
