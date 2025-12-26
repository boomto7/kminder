package com.kminder.minder.ui.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kminder.domain.logic.PlutchikEmotionCalculator
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
import com.kminder.minder.data.mock.MockData
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import kotlin.math.cos
import kotlin.math.sin

/**
 * 감정 결과 관계망 차트 (Result Network Chart)
 *
 * - 중심 노드: 최종 분석된 감정 (EmotionResult)
 * - 주변 노드: 원인이 되는 구성 감정 (Primary, Secondary)
 * - 사용자의 요청에 따라 키워드는 제외하고 감정선 위주로 시각화합니다.
 */

// 오버로딩 수정 필요: StringProvider를 넘겨주도록 변경

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun NetworkChart(
    result: PlutchikEmotionCalculator.EmotionResult,
    analysis: EmotionAnalysis,
    emotionNames: Map<EmotionType, String>,
    onNodeClick: (PlutchikEmotionCalculator.EmotionResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val density = androidx.compose.ui.platform.LocalDensity.current

    // Animation State
    val progress = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(result) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 800,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
    }

    // Design Constants (Neo-Brutalism)
    val borderStrokeWidth = with(density) { 2.dp.toPx() }
    val shadowOffset = with(density) { 4.dp.toPx() }
    val blackColor = Color.Black

    // 감정 색상 정의
    fun getEmotionColor(emotion: EmotionType): Color {
        return when (emotion) {
            EmotionType.ANGER -> Color(0xFFE57373)
            EmotionType.ANTICIPATION -> Color(0xFFFFB74D)
            EmotionType.JOY -> Color(0xFFFFD54F) // Latte-like Yellow
            EmotionType.TRUST -> Color(0xFFAED581)
            EmotionType.FEAR -> Color(0xFF4DB6AC)
            EmotionType.SADNESS -> Color(0xFF64B5F6)
            EmotionType.DISGUST -> Color(0xFF90A4AE)
            EmotionType.SURPRISE -> Color(0xFFBA68C8)
        }
    }

    BoxWithConstraints(modifier = modifier.padding(16.dp)) {
        val width = with(density) { maxWidth.toPx() }
        val height = with(density) { maxHeight.toPx() }
        val centerX = width / 2
        val centerY = height / 2

        val componentEmotions = listOfNotNull(result.primaryEmotion, result.secondaryEmotion)
        
        // 중앙 노드 반지름 (dp -> px)
        val centerRadiusDp = 50.dp
        val centerRadius = with(density) { centerRadiusDp.toPx() }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val dx = tapOffset.x - centerX
                        val dy = tapOffset.y - centerY
                        if (dx * dx + dy * dy <= centerRadius * centerRadius) {
                            onNodeClick(result)
                        }
                    }
                }
        ) {
             // 1. 노드 위치 계산
            val maxDistance = minOf(width, height) * 0.35f
            // 애니메이션 적용된 거리 (Pop out effect)
            val currentDistance = maxDistance * progress.value
            
            val nodePositions = mutableMapOf<EmotionType, Offset>()
            
            if (componentEmotions.size == 1) {
                // 단일
                nodePositions[componentEmotions[0]] = Offset(centerX, centerY - currentDistance)
            } else {
                // 다중 (2개)
                val baseAngle = -Math.PI / 2 // 12시
                val spread = Math.PI / 3 // 60도
                
                componentEmotions.forEachIndexed { index, emotion ->
                    val angle = if (index == 0) baseAngle - spread/2 else baseAngle + spread/2
                    val x = centerX + currentDistance * cos(angle).toFloat()
                    val y = centerY + currentDistance * sin(angle).toFloat()
                    nodePositions[emotion] = Offset(x, y)
                }
            }
            
            // 2. Edges (Shadow -> Line)
            componentEmotions.forEach { emotion ->
                val pos = nodePositions[emotion] ?: return@forEach
                val score = analysis.getEmotionIntensity(emotion)
                val strokeWidth = (2.dp.toPx() * score + 2.dp.toPx()) // 조금 더 두껍게

                // Line Shadow? (Optional in Neo-Brutalism, usually lines are just solid black)
                // Here we just draw thick black lines with Rounded Cap
                drawLine(
                    color = blackColor,
                    start = Offset(centerX, centerY),
                    end = pos,
                    strokeWidth = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
            
            // 3. Component Nodes
             componentEmotions.forEach { emotion ->
                val pos = nodePositions[emotion] ?: return@forEach
                val score = analysis.getEmotionIntensity(emotion)
                
                // 애니메이션 적용된 반지름
                val targetRadius = (30.dp.toPx() * score).coerceAtLeast(18.dp.toPx())
                val nodeRadius = targetRadius * progress.value // Scale animation
                
                if (nodeRadius > 1f) {
                    val color = getEmotionColor(emotion)

                    // 3.1 Hard Shadow
                    drawCircle(
                        color = blackColor,
                        radius = nodeRadius,
                        center = Offset(pos.x + shadowOffset, pos.y + shadowOffset)
                    )

                    // 3.2 Main Circle
                    drawCircle(color = color, radius = nodeRadius, center = pos)
                    
                    // 3.3 Border
                    drawCircle(
                        color = blackColor, 
                        radius = nodeRadius, 
                        center = pos, 
                        style = Stroke(width = borderStrokeWidth)
                    )
                    
                    // 3.4 Label (Visible only when animation is sufficient)
                    if (progress.value > 0.8f) {
                        val name = emotionNames[emotion] ?: emotion.name
                        val textLayout = textMeasurer.measure(
                            text = name,
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = blackColor)
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(pos.x - textLayout.size.width / 2, pos.y + nodeRadius + 8.dp.toPx())
                        )
                    }
                }
            }
            
            // 4. Center Node (Result)
            // Center Scale Animation
            val currentCenterRadius = centerRadius * androidx.compose.animation.core.EaseOutBack.transform(progress.value)
            
            val centerColor = getEmotionColor(result.primaryEmotion)
            
            // 4.1 Hard Shadow
            drawCircle(
                color = blackColor,
                radius = currentCenterRadius,
                center = Offset(centerX + shadowOffset, centerY + shadowOffset)
            )

            // 4.2 Main Circle
            drawCircle(color = Color.White, radius = currentCenterRadius, center = Offset(centerX, centerY))
            drawCircle(color = centerColor.copy(alpha = 0.3f), radius = currentCenterRadius, center = Offset(centerX, centerY)) // Tint
            
            // 4.3 Border
            drawCircle(
                color = blackColor, 
                radius = currentCenterRadius, 
                center = Offset(centerX, centerY), 
                style = Stroke(width = borderStrokeWidth * 1.5f) // 중심 노드는 더 굵게
            )
             
            // 4.4 Label
             val resultText = textMeasurer.measure(
                 text = result.label,
                 style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = blackColor)
             )
             drawText(
                 textLayoutResult = resultText,
                 topLeft = Offset(centerX - resultText.size.width / 2, centerY - resultText.size.height / 2)
             )
        }
    }
}

@Composable
fun NetworkChart(
    analysis: EmotionAnalysis,
    modifier: Modifier = Modifier,
    onNodeClick: (PlutchikEmotionCalculator.EmotionResult) -> Unit = {} 
) {
    val context = LocalContext.current
    val stringProvider = remember(context) { AndroidEmotionStringProvider(context) }
    
    val result = remember(analysis) {
        PlutchikEmotionCalculator.analyzeDominantEmotionCombination(
            analysis = analysis,
            stringProvider = stringProvider
        )
    }
    
    val emotionNames = remember(analysis) {
        EmotionType.values().associateWith { stringProvider.getEmotionName(it) }
    }

    NetworkChart(
        result = result,
        analysis = analysis,
        emotionNames = emotionNames,
        onNodeClick = onNodeClick,
        modifier = modifier
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun PreviewNetworkChart() {
    val mockEntry = MockData.mockJournalEntries[0]
    MockData.mockJournalEntries[0].emotionAnalysis?.let { 
        NetworkChart(analysis = it, modifier = Modifier.fillMaxSize()) 
    }
}