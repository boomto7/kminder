package com.kminder.minder.ui.screen.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.domain.model.ChartPeriod
import com.kminder.minder.R
import com.kminder.minder.ui.component.BlockingLoadingOverlay
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.util.EmotionUiUtil
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import com.kminder.domain.model.EntryType
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.JournalEntry
import java.util.Locale
import com.kminder.minder.ui.component.OutlinedDivider
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.util.EmotionColorUtil
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kminder.domain.model.EmotionStatistics
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * 통계/차트 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIntegratedAnalysis: (ChartPeriod, LocalDate) -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        if (uiState.selectedPeriod == ChartPeriod.MONTHLY) {
            YearMonthPickerDialog(
                initialDate = uiState.anchorDate,
                onDateSelected = {
                    viewModel.setDate(it)
                    showDatePicker = false
                },
                onDismissRequest = { showDatePicker = false }
            )
        } else {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.anchorDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                selectableDates = object : SelectableDates {
                    override fun isSelectableYear(year: Int): Boolean {
                        return year <= LocalDate.now().year
                    }

                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis <= System.currentTimeMillis()
                    }
                }
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            viewModel.setDate(date)
                        }
                        showDatePicker = false
                    }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("취소")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
    
    StatisticsContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onPeriodSelect = { viewModel.setPeriod(it) },
        onPrevClick = { viewModel.moveDate(-1) },
        onNextClick = { viewModel.moveDate(1) },
        onDateClick = { showDatePicker = true },
        onIntegratedAnalysisClick = {
            onNavigateToIntegratedAnalysis(uiState.selectedPeriod, uiState.anchorDate)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StatisticsContent(
    uiState: StatisticsUiState,
    onNavigateBack: () -> Unit,
    onPeriodSelect: (ChartPeriod) -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onDateClick: () -> Unit,
    onIntegratedAnalysisClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MinderBackground)
        ) {
            Scaffold(
                topBar = {
                    StatisticsTopBar(onBackClick = onNavigateBack)
                },
                containerColor = MinderBackground
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Divider matching other screens
                    OutlinedDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .height(4.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                     if (uiState.isLoading && uiState.totalEntryCount == 0) {
                         // Initial Load
                         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                             CircularProgressIndicator(color = Color.Black)
                         }
                     } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {

                            // 1. 기간 선택 & 네비게이션
                            ChartControlSection(
                                selectedPeriod = uiState.selectedPeriod,
                                periodLabel = uiState.periodLabel,
                                onPeriodSelect = onPeriodSelect,
                                onPrevClick = onPrevClick,
                                onNextClick = onNextClick,
                                onDateClick = onDateClick
                            )

                            if (uiState.totalEntryCount == 0) {
                                 if (!uiState.isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("해당 기간에 작성된 일기가 없습니다.", color = Color.Gray)
                                    }
                                }
                            } else {
                                // 2. 요약 및 통합 분석 버튼
                                SummarySection(
                                    uiState = uiState,
                                    onIntegratedAnalysisClick = onIntegratedAnalysisClick
                                )

                                // 3. 타임라인 차트
                                ChartSection(title = "감정 타임라인") {
                                    when (uiState.selectedPeriod) {
                                        ChartPeriod.DAILY -> DailyEmotionTimelineChart(statistics = uiState.statistics)
                                        ChartPeriod.WEEKLY -> WeeklyEmotionChart(statistics = uiState.statistics)
                                        ChartPeriod.MONTHLY -> MonthlyEmotionChart(statistics = uiState.statistics)
                                        else -> MonthlyEmotionChart(statistics = uiState.statistics)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
            
            // Blocking Loading Overlay for re-fetching or actions (optional, but matching pattern)
            // If we want the loading to be blocked "Overlay" style:
            if (uiState.isLoading && uiState.totalEntryCount > 0) {
                 BlockingLoadingOverlay(
                    isVisible = true,
                    message = "데이터 불러오는 중..."
                )
            }
        }
    }
}



@Composable
fun ChartControlSection(
    selectedPeriod: ChartPeriod,
    periodLabel: String,
    onPeriodSelect: (ChartPeriod) -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onDateClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Period Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val periods = listOf(ChartPeriod.DAILY, ChartPeriod.WEEKLY, ChartPeriod.MONTHLY)
            periods.forEach { period ->
                val label = when (period) {
                    ChartPeriod.DAILY -> "일간"
                    ChartPeriod.WEEKLY -> "주간"
                    ChartPeriod.MONTHLY -> "월간"
                    ChartPeriod.YEARLY -> "연간"
                }
                PeriodTab(
                    text = label,
                    isSelected = selectedPeriod == period,
                    onClick = { onPeriodSelect(period) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Date Navigator
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPrevClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Prev")
            }
            NeoShadowBox(
                modifier = Modifier.clickable(onClick = onDateClick),
                containerColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                offset = 4.dp
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = periodLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            IconButton(onClick = onNextClick) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next")
            }
        }
    }
}

@Composable
fun PeriodTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoShadowBox(
        modifier = modifier.clickable(onClick = onClick),
        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
        shape = RoundedCornerShape(8.dp),
        offset = if (isSelected) 2.dp else 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Black
            )
        }
    }
}



@Composable
fun SummarySection(
    uiState: StatisticsUiState,
    onIntegratedAnalysisClick: () -> Unit
) {
    val context = LocalContext.current
    val stringProvider = remember(context) { AndroidEmotionStringProvider(context) }
    
    val dominantEmotion = uiState.aggregatedAnalysis.getDominantEmotion()
    val dominantColor = EmotionColorUtil.getEmotionColor(dominantEmotion)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp), // Height fixed for consistent layout
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column: Small Info Cards
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Total Entries Card (Small)
            NeoShadowBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                containerColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("기록된 일기", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text(
                        text = "${uiState.totalEntryCount}개",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // 2. Dominant Emotion Card (Small & Colored)
            NeoShadowBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                containerColor = dominantColor
            ) {
                 Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("주요 감정", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val iconRes = EmotionUiUtil.getEmotionImageResId(context, dominantEmotion)
                        if (iconRes != 0 && iconRes != null) {
                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = stringProvider.getEmotionName(dominantEmotion),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Right Column: Integrated Analysis Button (Large)
        NeoShadowBox(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable(onClick = onIntegratedAnalysisClick),
            containerColor = Color(0xFFE0E0E0) // Light Gray or Distinct Color
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icon or Graphic
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Using a generic chart icon or emotion icon
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, // Placeholder
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "통합 분석\n보러가기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ChartSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        NeoShadowBox {
            Box(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
fun StatisticsTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RetroIconButton(
            onClick = onBackClick,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_back)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = stringResource(R.string.statistics_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
    }
}


@Composable
fun DailyEmotionTimelineChart(
    statistics: List<EmotionStatistics>
) {
    val context = LocalContext.current
    val stringProvider = remember(context) { AndroidEmotionStringProvider(context) }

    Column(modifier = Modifier.fillMaxWidth()) {
        statistics.forEach { dayStat ->
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                // Header (Date)
                Text(
                    text = dayStat.date.format(DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREA)),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (dayStat.entries.isEmpty()) {
                    Text(
                         text = "기록된 감정이 없습니다.",
                         style = MaterialTheme.typography.bodyMedium,
                         color = Color.Gray,
                         modifier = Modifier.padding(start = 8.dp)
                    )
                } else {
                    dayStat.entries.forEachIndexed { index, entry ->
                         TimelineItem(
                             entry = entry, 
                             isLast = index == dayStat.entries.lastIndex,
                             stringProvider = stringProvider,
                             context = context
                         )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineItem(
    entry: JournalEntry, 
    isLast: Boolean,
    stringProvider: AndroidEmotionStringProvider,
    context: android.content.Context
) {
    val result = entry.emotionResult
    val emotionColor = if(result != null) EmotionColorUtil.getEmotionResultColor(result) else Color.Gray

    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(30.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(Color.Black)
            )
            
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(Color.Black)
            )
            
            if (!isLast) {
                 Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(Color.Black)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.padding(bottom = 24.dp).weight(1f)) {
             Text(
                text = entry.createdAt.format(DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            NeoShadowBox(
                containerColor = if(result != null) emotionColor else Color.White,
                borderWidth = 2.dp
            ) {
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (result != null) {
                         val iconRes = EmotionUiUtil.getEmotionImageResId(context, result)
                         if (iconRes != null) {
                             Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                         }
                    }
                    
                    Column {
                         Text(
                            text = if(result != null) EmotionUiUtil.getLabel(result, stringProvider) else "분석 대기 중",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = entry.content.take(30) + if(entry.content.length > 30) "..." else "",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    // Dummy Data for Preview
    val dummyAnalysis = com.kminder.domain.model.EmotionAnalysis(
        joy = 0.5f,
        trust = 0.3f,
        fear = 0.1f,
        surprise = 0.1f,
        anger = 0.2f
    )
    val dummyStats = listOf(
        com.kminder.domain.model.EmotionStatistics(
            date = java.time.LocalDate.now().minusDays(1),
            entries = emptyList() // Simplified for preview
        ),
        com.kminder.domain.model.EmotionStatistics(
            date = java.time.LocalDate.now(),
            entries = listOf(
                 com.kminder.domain.model.JournalEntry(
                     id = 1,
                     content = "Preview Content",
                     entryType = com.kminder.domain.model.EntryType.FREE_WRITING,
                     createdAt = java.time.LocalDateTime.now(),
                     updatedAt = java.time.LocalDateTime.now(),
                     emotionResult = com.kminder.domain.model.EmotionResult(
                         primaryEmotion = com.kminder.domain.model.EmotionType.JOY,
                         secondaryEmotion = null,
                         score = 0.8f,
                         category = com.kminder.domain.model.ComplexEmotionType.Category.SINGLE_EMOTION,
                         source = dummyAnalysis
                     )
                 )
            )
        )
    )

    val dummyState = StatisticsUiState(
        isLoading = false,
        selectedPeriod = ChartPeriod.WEEKLY,
        anchorDate = java.time.LocalDate.now(),
        statistics = dummyStats,
        keywords = listOf(
            com.kminder.domain.model.EmotionKeyword("행복", com.kminder.domain.model.EmotionType.JOY, 0.8f),
            com.kminder.domain.model.EmotionKeyword("즐거움", com.kminder.domain.model.EmotionType.JOY, 0.6f)
        ),
        aggregatedAnalysis = dummyAnalysis,
        totalEntryCount = 5
    )

    MinderTheme {
        StatisticsContent(
            uiState = dummyState,
            onNavigateBack = {},
            onPeriodSelect = {},
            onPrevClick = {},
            onNextClick = {},
            onDateClick = {},
            onIntegratedAnalysisClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearMonthPickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(initialDate.year) }
    var selectedMonth by remember { mutableStateOf(initialDate.monthValue) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("월 선택") },
        text = {
            Column {
                // Year Selector (Simple Row)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Prev Year")
                    }
                    Text(
                        text = "$selectedYear",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { if (selectedYear < LocalDate.now().year) selectedYear++ }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next Year")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Month Grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 4
                ) {
                    (1..12).forEach { month ->
                        val isSelected = month == selectedMonth
                        val isFuture = selectedYear == LocalDate.now().year && month > LocalDate.now().monthValue
                        
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(48.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable(enabled = !isFuture) { selectedMonth = month },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$month",
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (isFuture) Color.LightGray else Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(LocalDate.of(selectedYear, selectedMonth, 1))
            }) {
                Text("확인")
            }
        },
        dismissButton = {
             TextButton(onClick = onDismissRequest) {
                Text("취소")
            }
        }
    )
}