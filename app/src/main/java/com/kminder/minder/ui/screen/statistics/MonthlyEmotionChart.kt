package com.kminder.minder.ui.screen.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.EmotionStatistics
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.EntryType
import com.kminder.minder.R
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.minder.util.EmotionColorUtil
import com.kminder.minder.util.EmotionUiUtil
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MonthlyEmotionChart(
    statistics: List<EmotionStatistics>
) {
    val context = LocalContext.current
    val stringProvider = remember(context) { AndroidEmotionStringProvider(context) }
    
    val weekLabelFormat = androidx.compose.ui.res.stringResource(R.string.format_month_week_label)
    val monthLabelFormat = androidx.compose.ui.res.stringResource(R.string.format_month_label)

    // 1. Group by Week
    val groupedData = remember(statistics, weekLabelFormat) {
        statistics.flatMap { it.entries }
            .groupBy { entry ->
                val date = entry.createdAt.toLocalDate()
                val weekField = WeekFields.of(Locale.getDefault()).weekOfMonth()
                val week = date.get(weekField)
                 String.format(weekLabelFormat, date.monthValue, week)
            }
            .toSortedMap(compareBy { it }) // Sort by week string (simple approximation)
    }
    
    // Month Label (from first entry)
    val monthLabel = remember(statistics, monthLabelFormat) {
        if (statistics.isNotEmpty()) {
            String.format(monthLabelFormat, statistics.first().date.monthValue)
        } else ""
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (monthLabel.isNotEmpty()) {
            Text(
                text = monthLabel,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        
        if (groupedData.isEmpty()) {
             Text(
                text = stringResource(com.kminder.minder.R.string.statistics_no_emotions_recorded),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            groupedData.entries.forEachIndexed { index, (weekLabel, entries) ->
                val isLast = index == groupedData.size - 1
                
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                     // Timeline Track
                    Box(
                        modifier = Modifier.width(30.dp).fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // Line
                        if (!isLast) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .fillMaxHeight()
                                    .padding(top = 10.dp)
                                    .background(Color.Black)
                            )
                        }
                        
                        // Dot
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Content
                    Column(modifier = Modifier.padding(bottom = 32.dp).weight(1f)) {
                        Text(
                            text = weekLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        // Flexible Layout for Emotions
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            entries.forEach { entry ->
                                val result = entry.emotionResult
                                val emotionColor = if (result != null)
                                    EmotionColorUtil.getEmotionResultColor(result) else Color.White
                                    
                                if (result != null) {
                                     NeoShadowBox(
                                        containerColor = emotionColor,
                                        borderWidth = 1.dp,
                                        offset = 2.dp,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val iconRes = EmotionUiUtil.getEmotionImageResId(context, result)
                                            if (iconRes != null) {
                                                Image(
                                                    painter = painterResource(id = iconRes),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = EmotionUiUtil.getLabel(result, stringProvider),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MonthlyEmotionChartPreview() {
    val dummyStats = listOf(
        EmotionStatistics(
            date = java.time.LocalDate.now().minusDays(1),
            entries = listOf(
                com.kminder.domain.model.JournalEntry(
                     id = 1,
                     content = "Weekly Preview Content",
                     entryType = EntryType.FREE_WRITING,
                     createdAt = LocalDateTime.now(),
                     updatedAt = LocalDateTime.now(),
                     emotionResult = EmotionResult(
                         primaryEmotion = EmotionType.JOY,
                         secondaryEmotion = null,
                         score = 0.8f,
                         category = ComplexEmotionType.Category.SINGLE_EMOTION,
                         source = EmotionAnalysis(joy = 0.8f)
                     )
                 )
            )
        )
    )
    
    MinderTheme {
        MonthlyEmotionChart(statistics = dummyStats)
    }
}
