package com.kminder.minder.ui.screen.statistics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun WeeklyEmotionChart(
    statistics: List<EmotionStatistics>
) {
    val context = LocalContext.current
    val stringProvider = remember(context) { AndroidEmotionStringProvider(context) }
    
    // 1. Calculate Week Label
    val weekLabelFormat = stringResource(R.string.format_month_week_label)
    val weekLabel = remember(statistics, weekLabelFormat) {
        if (statistics.isNotEmpty()) {
            val firstDate = statistics.first().date
            val weekFields = WeekFields.of(Locale.getDefault())
            val weekOfMonth = firstDate.get(weekFields.weekOfMonth())
            String.format(weekLabelFormat, firstDate.monthValue, weekOfMonth)
        } else ""
    }

    val datePattern = stringResource(R.string.format_date_day_pattern)
    val timePattern = stringResource(R.string.format_time_pattern)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Top Header
        if (weekLabel.isNotEmpty()) {
            Text(
                text = weekLabel,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        
        statistics.forEachIndexed { index, dayStat ->
            val isLast = index == statistics.lastIndex
            
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
                                .padding(top = 10.dp) // Start from center of dot approx
                                .background(Color.Black)
                        )
                    }
                    
                    // Dot (Day Node)
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp) // Align with Text baseline
                            .size(12.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.Black)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Content
                Column(modifier = Modifier.padding(bottom = 32.dp).weight(1f)) {
                    // Date Header
                    Text(
                        text = dayStat.date.format(DateTimeFormatter.ofPattern(datePattern, Locale.getDefault())),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    if (dayStat.entries.isEmpty()) {
                         Text(
                            text = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.statistics_no_emotions_recorded),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        dayStat.entries.forEach { entry ->
                             val result = entry.emotionResult
                             val emotionColor = if (result != null) 
                                EmotionColorUtil.getEmotionResultColor(result) else Color.White
                             
                             // Compact Card
                             NeoShadowBox(
                                containerColor = emotionColor,
                                borderWidth = 2.dp,
                                offset = 2.dp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                     if (result != null) {
                                         val iconRes = EmotionUiUtil.getEmotionImageResId(context, result)
                                         if (iconRes != null) {
                                             Image(
                                                painter = painterResource(id = iconRes),
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                         }
                                         Text(
                                            text = EmotionUiUtil.getLabel(result, stringProvider),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                     } else {
                                          Text(
                                            text = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.statistics_analysis_pending),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                     }
                                     
                                     Spacer(modifier = Modifier.weight(1f))
                                     
                                     Text(
                                        text = entry.createdAt.format(DateTimeFormatter.ofPattern(timePattern, Locale.getDefault())),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.DarkGray
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

@Preview(showBackground = true)
@Composable
fun WeeklyEmotionChartPreview() {
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
        ),
         EmotionStatistics(
            date = LocalDate.now(),
            entries = emptyList()
        )
    )
    
    MinderTheme {
        WeeklyEmotionChart(statistics = dummyStats)
    }
}
