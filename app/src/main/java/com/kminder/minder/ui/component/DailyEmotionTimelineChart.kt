package com.kminder.minder.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kminder.domain.logic.PlutchikEmotionCalculator
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.minder.util.EmotionColorUtil

/**
 * 일일 감정 분포 및 추이 차트 (Russell's Circumplex Model 기반)
 */
@Composable
fun DailyEmotionDistributionChart(
    hourlyData: Map<Int, PlutchikEmotionCalculator.EmotionResult>,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(hourlyData) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val textMeasurer = rememberTextMeasurer()
    val axisLabelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(24.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f

        // 좌표 매핑
        fun mapX(valence: Float): Float {
            val normalized = (valence + 3f) / 6f
            return normalized * width
        }

        fun mapY(intensity: Float): Float {
            val normalized = intensity / 3f
            return height - (normalized * height)
        }

        // --- 1. 배경 없이 최소한의 가이드 텍스트만 표시 (사분면 의미) ---
        val labelStyle = TextStyle(fontSize = 11.sp, color = axisLabelColor, fontWeight = FontWeight.Normal)
        
        drawText(textMeasurer, "Stress", Offset(0f, 0f), labelStyle)
        val vitalitySize = textMeasurer.measure("Energy", labelStyle).size
        drawText(textMeasurer, "Energy", Offset(width - vitalitySize.width, 0f), labelStyle)
        
        val depressionSize = textMeasurer.measure("Down", labelStyle).size
        drawText(textMeasurer, "Down", Offset(0f, height - depressionSize.height), labelStyle)
        
        val calmSize = textMeasurer.measure("Calm", labelStyle).size
        drawText(textMeasurer, "Calm", Offset(width - calmSize.width, height - calmSize.height), labelStyle)

        if (hourlyData.isEmpty()) return@Canvas

        // --- 2. 데이터 포인트 준비 ---
        val sortedPoints = hourlyData.entries.sortedBy { it.key }
        
        val pointCoords = sortedPoints.map { (hour, result) ->
            val properties = getEmotionProperties(result, hour)
            
            val x = mapX(properties.valence)
            val y = mapY(properties.intensity)
            
            val primaryColor = EmotionColorUtil.getEmotionColor(result.primaryEmotion)
            val finalColor = if (result.secondaryEmotion != null) {
                EmotionColorUtil.blendColors(primaryColor, EmotionColorUtil.getEmotionColor(result.secondaryEmotion!!), 0.5f)
            } else {
                primaryColor
            }

            ChartPoint(
                x = x, 
                y = y, 
                result = result, 
                hour = hour,
                radiusBase = properties.totalIntensity, 
                color = finalColor
            )
        }

        // --- 3. 추이 선 (Trend Line) 그리기 (Optional: 흐릿하게 연결) ---
        val path = Path()
        if (pointCoords.isNotEmpty()) {
            val start = pointCoords.first()
            path.moveTo(start.x, start.y)
            
            for (i in 1 until pointCoords.size) {
                val p = pointCoords[i]
                path.lineTo(p.x, p.y)
            }
            
            drawPath(
                path = path,
                color = axisLabelColor.copy(alpha = 0.2f),
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                )
            )
        }

        // --- 4. 버블 그리기 (Glassy / Ethereal Style) ---
        pointCoords.forEachIndexed { index, point ->
            val pointProgress = (progress.value * pointCoords.size - index).coerceIn(0f, 1f)
            
            if (pointProgress > 0) {
                val radius = (15.dp.toPx() + (point.radiusBase * 10.dp.toPx())) * pointProgress
                
                // 1) Soft Glow Shadow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            point.color.copy(alpha = 0.4f),
                            point.color.copy(alpha = 0.0f)
                        ),
                        center = Offset(point.x, point.y),
                        radius = radius * 1.8f
                    ),
                    center = Offset(point.x, point.y),
                    radius = radius * 1.8f
                )

                // 2) Main Bubble Body
                drawCircle(
                    color = point.color.copy(alpha = 0.7f),
                    radius = radius,
                    center = Offset(point.x, point.y)
                )

                // 3) Top Gradient Overlay
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color.Transparent
                        ),
                        startY = point.y - radius,
                        endY = point.y + radius
                    ),
                    radius = radius,
                    center = Offset(point.x, point.y)
                )

                // 텍스트 라벨
                if (pointProgress > 0.8f) {
                    val timeStr = "${point.hour}h"
                    val labelResult = textMeasurer.measure(
                        text = timeStr,
                        style = TextStyle(
                            fontSize = 12.sp, 
                            color = Color.White, 
                            fontWeight = FontWeight.Bold,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = Color.Black.copy(alpha=0.3f), blurRadius = 2f
                            )
                        )
                    )
                    drawText(
                        textLayoutResult = labelResult,
                        topLeft = Offset(
                            point.x - labelResult.size.width / 2,
                            point.y - labelResult.size.height / 2
                        )
                    )
                    
                    val emoStr = point.result.label
                    val emoResult = textMeasurer.measure(
                        text = emoStr,
                        style = TextStyle(fontSize = 11.sp, color = textColor, fontWeight = FontWeight.Medium)
                    )
                    drawText(
                        textLayoutResult = emoResult,
                        topLeft = Offset(
                            point.x - emoResult.size.width / 2,
                            point.y + radius + 6.dp.toPx()
                        )
                    )
                }
            }
        }
    }
}

private data class ChartPoint(
    val x: Float, 
    val y: Float, 
    val result: PlutchikEmotionCalculator.EmotionResult,
    val hour: Int,
    val radiusBase: Float,
    val color: Color
)

private data class EmotionProperties(
    val valence: Float,
    val intensity: Float,
    val totalIntensity: Float
)

private fun getEmotionProperties(result: PlutchikEmotionCalculator.EmotionResult, hour: Int): EmotionProperties {
    var (val1, aro1) = getBaseValues(result.primaryEmotion)
    
    if (result.secondaryEmotion != null) {
        val (val2, aro2) = getBaseValues(result.secondaryEmotion!!)
        val1 = (val1 + val2) / 2f
        aro1 = (aro1 + aro2) / 2f
    }
    
    val totalInt = result.score * 3.0f 
    val finalIntensity = (aro1 * 0.5f) + (totalInt * 0.5f)
    val finalValence = val1 * (0.5f + (result.score * 0.5f))

    return EmotionProperties(
        valence = finalValence.coerceIn(-3f, 3f),
        intensity = finalIntensity.coerceIn(0f, 3f),
        totalIntensity = totalInt
    )
}

private fun getBaseValues(type: EmotionType): Pair<Float, Float> {
    return when(type) {
        EmotionType.JOY -> 2.5f to 2.2f
        EmotionType.TRUST -> 1.5f to 0.8f
        EmotionType.FEAR -> -1.5f to 2.6f
        EmotionType.SURPRISE -> 0.5f to 2.8f
        EmotionType.SADNESS -> -2.5f to 0.5f
        EmotionType.DISGUST -> -2.2f to 1.8f
        EmotionType.ANGER -> -2.5f to 2.9f
        EmotionType.ANTICIPATION -> 1.2f to 2.0f
        else -> 0f to 0f
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDailyEmotionDistribChart() {
    val mockData = mapOf(
        10 to PlutchikEmotionCalculator.EmotionResult("경멸", "설명", EmotionType.DISGUST, EmotionType.ANGER, 0.83f, com.kminder.domain.model.ComplexEmotionType.Category.PRIMARY_DYAD, source = EmotionAnalysis()),
        14 to PlutchikEmotionCalculator.EmotionResult("낙관", "설명", EmotionType.JOY, EmotionType.ANTICIPATION, 0.5f, com.kminder.domain.model.ComplexEmotionType.Category.PRIMARY_DYAD, source = EmotionAnalysis()),
        18 to PlutchikEmotionCalculator.EmotionResult("절망", "설명", EmotionType.SADNESS, EmotionType.FEAR, 0.6f, com.kminder.domain.model.ComplexEmotionType.Category.SECONDARY_DYAD, source = EmotionAnalysis()),
        21 to PlutchikEmotionCalculator.EmotionResult("사랑", "설명", EmotionType.JOY, EmotionType.TRUST, 0.93f, com.kminder.domain.model.ComplexEmotionType.Category.PRIMARY_DYAD, source = EmotionAnalysis())
    )
    
    MinderTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DailyEmotionDistributionChart(hourlyData = mockData)
        }
    }
}
