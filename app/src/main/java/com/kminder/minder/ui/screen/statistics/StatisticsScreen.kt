package com.kminder.minder.ui.screen.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.kminder.minder.ui.component.MinderEmptyView
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionKeyword
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.EmotionStatistics
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.EntryType
import com.kminder.domain.model.JournalEntry
import com.kminder.minder.R
import com.kminder.minder.ui.component.BlockingLoadingOverlay
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.component.OutlinedDivider
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.minder.util.EmotionColorUtil
import com.kminder.minder.util.EmotionUiUtil
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 통계/차트 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToMindBlossom: (ChartPeriod, LocalDate) -> Unit,
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
                        Text(stringResource(R.string.common_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(R.string.common_cancel))
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
        onMindBlossomClick = {
            onNavigateToMindBlossom(uiState.selectedPeriod, uiState.anchorDate)
        },
        onNavigateToWrite = onNavigateToWrite
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
    onMindBlossomClick: () -> Unit,
    onNavigateToWrite: () -> Unit
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
                                MinderEmptyView(
                                    message = stringResource(R.string.statistics_no_data_message),
                                    actionLabel = stringResource(R.string.common_go_to_write),
                                    onActionClick = onNavigateToWrite,
                                    modifier = Modifier.height(300.dp)
                                )
                            } else {
                                // 2. 요약 및 통합 분석 버튼
                                SummarySection(
                                    uiState = uiState,
                                    onMindBlossomClick = onMindBlossomClick
                                )

                                // 3. 타임라인 차트
                                ChartSection(title = "감정 타임라인") {
                                    when (uiState.selectedPeriod) {
                                        ChartPeriod.DAILY -> DailyEmotionList(statistics = uiState.statistics)
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
                    message = stringResource(R.string.common_loading_data)
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
                    ChartPeriod.DAILY -> stringResource(R.string.period_daily)
                    ChartPeriod.WEEKLY -> stringResource(R.string.period_weekly)
                    ChartPeriod.MONTHLY -> stringResource(R.string.period_monthly)
                    ChartPeriod.YEARLY -> stringResource(R.string.period_yearly)
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
    onMindBlossomClick: () -> Unit
) {
    val context = LocalContext.current
    val stringProvider = remember(context) { AndroidEmotionStringProvider(context) }
    
    val totalAnalysis = uiState.totalEmotionAnalysis
    
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        
        // 1. Header Row: Total Entries + Integrated Analysis Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Match ChartControlSection spacing
        ) {
            // Total Entries (Compact Left - Border Style)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                    .padding(vertical = 10.dp), // Match PeriodTab vertical padding
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.statistics_total_entries_format, uiState.totalEntryCount),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }

            // Integrated Analysis (Compact Right - Neo Button Style)
            NeoShadowBox(
                modifier = Modifier.weight(1f).clickable(onClick = onMindBlossomClick),
                containerColor = Color.White,
                shape = RoundedCornerShape(8.dp),
                offset = 4.dp
            ) {
                 Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp), // Match PeriodTab
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.statistics_mind_blossom),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. Top 5 Emotions List
        if (totalAnalysis.top5ComplexEmotions.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.statistics_top_emotions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    totalAnalysis.top5ComplexEmotions.forEachIndexed { index, (type, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .background(if(index == 0) MaterialTheme.colorScheme.primary else Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if(index == 0) MaterialTheme.colorScheme.onPrimary else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            // Emotion Icon & Name
                            val iconRes = EmotionUiUtil.getEmotionImageResId(context, type)
                            if (iconRes != null) {
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            
                            Text(
                                text = stringProvider.getComplexEmotionTitle(type),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            // Count Bar (Visual representation)
                            val maxCount = totalAnalysis.top5ComplexEmotions.first().second.toFloat()
                            val widthFraction = (count / maxCount) * 0.4f // Max 40% width
                            
                            Box(
                                modifier = Modifier
                                    .height(8.dp)
                                    .fillMaxWidth(widthFraction)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmotionColorUtil.getComplexEmotionColor(type).copy(alpha = 0.5f))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = stringResource(R.string.statistics_count_format, count),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )
                        }
                        if (index < totalAnalysis.top5ComplexEmotions.lastIndex) {
                             HorizontalDivider(color = Color.LightGray.copy(alpha=0.3f), thickness = 1.dp)
                        }
                    }
                }
            }
        }

        // 3. Insights Row (Compact & Equal Height)
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Most Frequent Basic Emotion
            totalAnalysis.mostFrequentBasicEmotion?.let { (emotion, _) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight() // Restore fillMaxHeight to match sibling
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmotionColorUtil.getEmotionColor(emotion))
                        .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween // Distribute space
                    ) {
                         Text(
                            text = stringResource(R.string.statistics_most_frequent_emotion),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                             val iconRes = EmotionUiUtil.getEmotionImageResId(context, emotion)
                             if (iconRes != null) {
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                             }
                             Text(
                                text = stringProvider.getEmotionName(emotion),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Highest Scored Emotion
            totalAnalysis.highestScoredEmotion?.let { (type, score) ->
                 Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight() // Restore fillMaxHeight
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween // Distribute space
                    ) {
                         Text(
                            text = stringResource(R.string.statistics_highest_scored_emotion),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                         Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.statistics_score_format, (score * 100).toInt()),
                                style = MaterialTheme.typography.titleMedium, // Reduced to TitleMedium to match Left
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringProvider.getComplexEmotionTitle(type),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        


// ... inside SummarySection ...
        // 4. Time Analysis
        totalAnalysis.mostFrequentTimeRange?.let { timeRange ->
             val dominantInTime = totalAnalysis.dominantEmotionByTime[timeRange]
             Box(
                 modifier = Modifier
                     .fillMaxWidth()
                     .clip(RoundedCornerShape(16.dp))
                     .background(Color(0xFFF0F0F0)) // Keep original light gray
                     .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
             ) {
                 Row(
                     modifier = Modifier.padding(16.dp).fillMaxWidth(),
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     Box(
                         modifier = Modifier
                             .size(40.dp)
                             .clip(androidx.compose.foundation.shape.CircleShape)
                             .background(Color.White),
                         contentAlignment = Alignment.Center
                     ) {
                         Icon(
                             imageVector = Icons.Default.DateRange, 
                             contentDescription = null, 
                             tint = Color.Black
                         )
                     }
                     Spacer(modifier = Modifier.width(16.dp))
                     Column {
                         Text(
                             text = stringResource(R.string.statistics_emotional_time),
                             style = MaterialTheme.typography.labelSmall,
                             color = Color.Gray
                         )
                         val emotionName = dominantInTime?.let { stringProvider.getComplexEmotionTitle(it) } ?: stringResource(R.string.statistics_diverse_emotions)
                         Text(
                             text = "$timeRange",
                             style = MaterialTheme.typography.titleSmall,
                             fontWeight = FontWeight.Bold
                         )
                         Text(
                             text = stringResource(R.string.statistics_dominant_emotion_in_time_format, emotionName),
                             style = MaterialTheme.typography.bodyMedium
                         )
                     }
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
        Box(modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)

        ) {
            content()
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
fun DailyEmotionList(
    statistics: List<EmotionStatistics>
) {
    val context = LocalContext.current
    val stringProvider = remember(context) { AndroidEmotionStringProvider(context) }
    val datePattern = stringResource(R.string.format_date_day_pattern)
    val timePattern = stringResource(R.string.format_time_pattern)

    Column(modifier = Modifier.fillMaxWidth()) {
        statistics.forEach { dayStat ->
            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                // Header (Date)
                Text(
                    text = dayStat.date.format(DateTimeFormatter.ofPattern(datePattern, Locale.getDefault())),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (dayStat.entries.isEmpty()) {
                    Text(
                         text = stringResource(R.string.statistics_no_emotions_recorded),
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
                             context = context,
                             timePattern = timePattern
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
    context: android.content.Context,
    timePattern: String
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
                text = entry.createdAt.format(DateTimeFormatter.ofPattern(timePattern, Locale.getDefault())),
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
                            text = if(result != null) EmotionUiUtil.getLabel(result, stringProvider) else stringResource(R.string.statistics_analysis_pending),
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
    val dummyAnalysis = EmotionAnalysis(
        joy = 0.5f,
        trust = 0.3f,
        fear = 0.1f,
        surprise = 0.1f,
        anger = 0.2f
    )

    val dummyTotalAnalysis = TotalEmotionAnalysis(
        top5ComplexEmotions = listOf(
            ComplexEmotionType.JOY to 12,
            ComplexEmotionType.LOVE to 8,
            ComplexEmotionType.ANTICIPATION to 5,
            ComplexEmotionType.OPTIMISM to 3,
            ComplexEmotionType.SERENITY to 2
        ),
        mostFrequentBasicEmotion = com.kminder.domain.model.EmotionType.JOY to 20,
        highestScoredEmotion = com.kminder.domain.model.ComplexEmotionType.ECSTASY to 0.95f,
        mostFrequentTimeRange = "14:00 - 16:00",
        dominantEmotionByTime = mapOf(
            "08:00 - 10:00" to ComplexEmotionType.ANTICIPATION,
            "12:00 - 14:00" to ComplexEmotionType.JOY,
            "18:00 - 20:00" to ComplexEmotionType.SERENITY
        )
    )

    val dummyStats = listOf(
        EmotionStatistics(
            date = java.time.LocalDate.now().minusDays(1),
            entries = emptyList() // Simplified for preview
        ),
        EmotionStatistics(
            date = java.time.LocalDate.now(),
            entries = listOf(
                 JournalEntry(
                     id = 1,
                     content = "Preview Content",
                     entryType = EntryType.FREE_WRITING,
                     createdAt = LocalDateTime.now(),
                     updatedAt = LocalDateTime.now(),
                     emotionResult = EmotionResult(
                         primaryEmotion = EmotionType.JOY,
                         secondaryEmotion = null,
                         score = 0.8f,
                         category = ComplexEmotionType.Category.SINGLE_EMOTION,
                         source = dummyAnalysis
                     )
                 )
            )
        )
    )

    val dummyState = StatisticsUiState(
        isLoading = false,
        selectedPeriod = ChartPeriod.WEEKLY,
        anchorDate = LocalDate.now(),
        statistics = dummyStats,
        keywords = listOf(
            EmotionKeyword("행복", EmotionType.JOY, 0.8f),
            EmotionKeyword("즐거움", com.kminder.domain.model.EmotionType.JOY, 0.6f)
        ),
//        aggregatedAnalysis = dummyAnalysis,
        totalEmotionAnalysis = dummyTotalAnalysis,
        totalEntryCount = 0
    )

    MinderTheme {
        StatisticsContent(
            uiState = dummyState,
            onNavigateBack = {},
            onPeriodSelect = {},
            onPrevClick = {},
            onNextClick = {},
            onDateClick = {},
            onMindBlossomClick = {},
            onNavigateToWrite = {},
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

    AlertDialog(
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

