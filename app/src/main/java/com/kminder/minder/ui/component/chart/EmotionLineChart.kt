package com.kminder.minder.ui.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.kminder.domain.model.EmotionStatistics
import com.kminder.domain.model.EmotionType
import com.kminder.minder.data.mock.MockData
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.minder.util.EmotionColorUtil.getEmotionColor
import java.time.format.DateTimeFormatter

/**
 * 감정 흐름 라인 차트 (Neo-Brutalism Style)
 */
@Composable
fun EmotionLineChart(
    data: List<EmotionStatistics>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(modifier = modifier.height(200.dp), contentAlignment = Alignment.Center) {
            // Empty state handled by parent usually, but drawing nothing is fine.
        }
        return
    }

    // 주요 감정 상위 3개만 추적 (너무 복잡해지지 않도록)
    // 전체 기간에서 점수 합이 가장 높은 감정 3개 선정
    // 데이터 전처리: 각 날짜별 평균 감정 분석 결과 계산
    val processedData = remember(data) {
        data.mapNotNull { stat ->
            val validEntries = stat.entries.mapNotNull { it.emotionResult?.source }
            if (validEntries.isNotEmpty()) {
                val avgJoy = validEntries.map { it.joy }.average().toFloat()
                val avgTrust = validEntries.map { it.trust }.average().toFloat()
                val avgFear = validEntries.map { it.fear }.average().toFloat()
                val avgSurprise = validEntries.map { it.surprise }.average().toFloat()
                val avgSadness = validEntries.map { it.sadness }.average().toFloat()
                val avgDisgust = validEntries.map { it.disgust }.average().toFloat()
                val avgAnger = validEntries.map { it.anger }.average().toFloat()
                val avgAnticipation = validEntries.map { it.anticipation }.average().toFloat()
                
                stat.date to com.kminder.domain.model.EmotionAnalysis(
                    joy = avgJoy, trust = avgTrust, fear = avgFear, surprise = avgSurprise,
                    sadness = avgSadness, disgust = avgDisgust, anger = avgAnger, anticipation = avgAnticipation
                )
            } else {
                null
            }
        }
    }

    if (processedData.isEmpty()) {
         Box(modifier = modifier.height(200.dp), contentAlignment = Alignment.Center) {
            // Empty
        }
        return
    }

    // 주요 감정 상위 3개만 추적
    val targetEmotions = remember(processedData) {
        val scoreMap = mutableMapOf<EmotionType, Float>()
        processedData.forEach { (_, ana) ->
            scoreMap[EmotionType.JOY] = (scoreMap[EmotionType.JOY] ?: 0f) + ana.joy
            scoreMap[EmotionType.SADNESS] = (scoreMap[EmotionType.SADNESS] ?: 0f) + ana.sadness
            scoreMap[EmotionType.ANGER] = (scoreMap[EmotionType.ANGER] ?: 0f) + ana.anger
            scoreMap[EmotionType.FEAR] = (scoreMap[EmotionType.FEAR] ?: 0f) + ana.fear
            scoreMap[EmotionType.TRUST] = (scoreMap[EmotionType.TRUST] ?: 0f) + ana.trust
            scoreMap[EmotionType.ANTICIPATION] = (scoreMap[EmotionType.ANTICIPATION] ?: 0f) + ana.anticipation
            scoreMap[EmotionType.SURPRISE] = (scoreMap[EmotionType.SURPRISE] ?: 0f) + ana.surprise
            scoreMap[EmotionType.DISGUST] = (scoreMap[EmotionType.DISGUST] ?: 0f) + ana.disgust
        }
        scoreMap.entries.sortedByDescending { it.value }
            .take(3)
            .map { it.key }
    }
    
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val textStyle = androidx.compose.ui.text.TextStyle(
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        
        // Measure text height to reserve space
        val textLayoutResult = textMeasurer.measure("00.00", style = textStyle)
        val textHeight = textLayoutResult.size.height.toFloat()
        val bottomPadding = 8.dp.toPx()
        
        val chartHeight = size.height - textHeight - bottomPadding
        val width = size.width
        
        if (chartHeight <= 0) return@Canvas
        
        val pointCount = data.size
        
        // 1. Draw Grid Lines (Y-axis)
        // Draw 3 lines (0, 0.5, 1.0)
        val yStep = chartHeight / 2
        for (i in 0..2) {
            val y = i * yStep
            drawLine(
                color = Color.LightGray.copy(alpha=0.5f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        if (pointCount < 2) return@Canvas
        
        val stepX = width / (pointCount - 1)
        
        targetEmotions.forEach { emotion ->
            val color = getEmotionColor(emotion)
            val path = Path()
            
            processedData.forEachIndexed { index, (_, ana) ->
                val intensity = ana.getEmotionIntensity(emotion)
                val x = index * stepX
                // Y is inverted (0 at top), so (1 - intensity) * height
                val y = (1f - intensity) * chartHeight
                
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                
                // Draw Point
                drawCircle(
                    color = Color.Black, // Border
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = color, // Fill
                    radius = 3.dp.toPx(),
                    center = Offset(x, y)
                )
            }
            
            // Draw Line
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // 2. Draw X-axis Labels (First, Middle, Last)
        // Drawing every label might overlap.
        // Simple logic: First and Last.
        if (data.isNotEmpty()) {
            val formatter = DateTimeFormatter.ofPattern("MM.dd")
            val firstDate = processedData.first().first.format(formatter)
            val lastDate = processedData.last().first.format(formatter)
            
            drawText(
                textMeasurer = textMeasurer,
                text = firstDate,
                style = textStyle,
                topLeft = Offset(0f, chartHeight + bottomPadding)
            )
            val lastLayout = textMeasurer.measure(lastDate, style = textStyle)
            drawText(
                textMeasurer = textMeasurer,
                text = lastDate,
                style = textStyle,
                topLeft = Offset(width - lastLayout.size.width, chartHeight + bottomPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmotionLineChart() {
    MinderTheme() {
        EmotionLineChart(
            data = MockData.mockJournalEntries.filter { it ->
                it.emotionResult != null
            }.map { entry ->
                EmotionStatistics(
                    date = entry.createdAt.toLocalDate(),
                    entries = listOf(entry)
                )
             },
        )

    }
}