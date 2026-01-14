package com.kminder.minder.ui.component.chart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
import com.kminder.minder.util.EmotionColorUtil.getEmotionColor

/**
 * 감정 분포 도넛 차트 (Neo-Brutalism Style)
 */
@Composable
fun EmotionDonutChart(
    analysis: EmotionAnalysis,
    modifier: Modifier = Modifier
) {
    val totalScore = analysis.run {
        joy + trust + fear + surprise + sadness + disgust + anger + anticipation
    }
    
    // 각 감정별 비중 계산
    val emotionSegments = remember(analysis) {
        if (totalScore == 0f) return@remember emptyList()
        
        val list = mutableListOf<Pair<EmotionType, Float>>()
        list.add(EmotionType.JOY to analysis.joy)
        list.add(EmotionType.TRUST to analysis.trust)
        list.add(EmotionType.FEAR to analysis.fear)
        list.add(EmotionType.SURPRISE to analysis.surprise)
        list.add(EmotionType.SADNESS to analysis.sadness)
        list.add(EmotionType.DISGUST to analysis.disgust)
        list.add(EmotionType.ANGER to analysis.anger)
        list.add(EmotionType.ANTICIPATION to analysis.anticipation)
        
        // 내림차순 정렬하여 큰 감정부터 그리기
        list.filter { it.second > 0f }
            .sortedByDescending { it.second }
    }
    
    // Animation
    val progress = remember { Animatable(0f) }
    LaunchedEffect(analysis) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(1000))
    }
    
    Box(modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val strokeWidth = 40.dp.toPx()
            val blackBorderWidth = 3.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            var startAngle = -90f
            
            if (emotionSegments.isEmpty()) {
                // 데이터 없음: 회색 링
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    radius = radius,
                    style = Stroke(width = strokeWidth)
                )
            } else {
                emotionSegments.forEach { (type, score) ->
                    val sweepAngle = (score / totalScore) * 360f * progress.value
                    val color = getEmotionColor(type)
                    
                    // 1. Draw Colored Arc
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    
                    // 2. Draw Black Border (Neo-Brutalism)
                    // Segment Separator
                    // Draw arc border? No, just separators or outline?
                    // Let's draw outer and inner circle borders later.
                    // Here we can draw a black line at the start?
                    
                    startAngle += sweepAngle
                }
                
                // 3. Draw Inner and Outer Borders (Black)
                drawCircle(
                    color = Color.Black,
                    radius = radius + strokeWidth / 2,
                    style = Stroke(width = blackBorderWidth)
                )
                drawCircle(
                    color = Color.Black,
                    radius = radius - strokeWidth / 2,
                    style = Stroke(width = blackBorderWidth)
                )
                
                // 4. Draw Segment Separators
                var separatorAngle = -90f
                emotionSegments.forEach { (_, score) ->
                    val sweep = (score / totalScore) * 360f * progress.value
                    // Don't draw separator if full circle
                    if (sweep < 360f && sweep > 1f) {
                        // Calculate separator line
                        // This is tricky with simple canvas calls if we want exact cut
                        // But since it's a Donut, just drawing a black line from inner to outer radius at angle
                        
                        // Pass for now to keep it clean, usually Donut segments are distinct enough or we add white/black gap
                        // Let's add specific logic later if needed.
                        separatorAngle += sweep
                    }
                }
            }
        }
    }
}
