package com.kminder.minder.ui.component.chart

import android.graphics.Paint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
import com.kminder.minder.data.mock.MockData
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

/**
 * Minimalist Design Network Chart (Prototype)
 * - Concept: Dot & Line, Faint Shadow, Monochrome
 */
@Composable
fun SimpleNetworkChart(
    analysis: EmotionAnalysis,
    modifier: Modifier = Modifier,
    onNodeClick: (String) -> Unit = {}
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // Animation state
    var animationPlayed by remember { mutableStateOf(false) }
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(analysis) {
        animationPlayed = true
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = EaseOutBack)
        )
    }

    val animValue = animatable.value

    // Layout Constants (Minimalist)
    val centerNodeRadius = 12.dp // 작고 심플한 점
    val orbitDistance = 140.dp // 넓은 배치
    val primaryNodeRadius = 8.dp
    val secondaryNodeRadius = 6.dp
    val keywordDistance = 40.dp
    
    // Style Constants
    val lineColor = Color.Black.copy(alpha = 0.8f)
    val dotColor = Color.Black
    val shadowColor = android.graphics.Color.argb(40, 0, 0, 0) // Faint Shadow
    val shadowRadius = 15f
    val shadowDy = 10f

    // Data Preparation
    val primaryEmotions = mutableListOf<EmotionType>()
    val orbitEmotions = mutableListOf<EmotionType>()
    val emotionIntensityMap = analysis.toMap()

    // Filter Logic (Simple: Score > 0)
    emotionIntensityMap.forEach { (type, score) ->
        if (score > 0f) {
            orbitEmotions.add(type)
            if (score >= 0.5f) { // 임의의 기준
                primaryEmotions.add(type)
            }
        }
    }

    Box(modifier = modifier.background(Color.White)) { // White Background
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        // Click detection logic (omitted for prototype simplicity)
                    }
                }
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val center = Offset(centerX, centerY)

            // --- 1. Nodes Layout Calculation ---
            val emotionPositions = mutableMapOf<EmotionType, Offset>()
            val sectorAngle = 360f / orbitEmotions.size.coerceAtLeast(1)

            orbitEmotions.forEachIndexed { index, emotion ->
                val angleRad = Math.toRadians((sectorAngle * index - 90).toDouble())
                val orbitR = orbitDistance.toPx() * animValue
                val x = centerX + (orbitR * cos(angleRad)).toFloat()
                val y = centerY + (orbitR * sin(angleRad)).toFloat()
                emotionPositions[emotion] = Offset(x, y)
            }

            // --- 2. Draw Lines (Connections) ---
            // Center to Emotion
            orbitEmotions.forEach { emotion ->
                val endPos = emotionPositions[emotion] ?: return@forEach
                drawLine(
                    color = lineColor,
                    start = center,
                    end = endPos,
                    strokeWidth = 1.dp.toPx() * animValue
                )
            }



            // Emotion to Keywords
            analysis.keywords.forEach { keyword ->
                val parentPos = emotionPositions[keyword.emotion] ?: return@forEach
                // Simple outward layout for keywords
                 // For prototype, just placing them randomly around parent for visual check
                // In real implementation, use the Greedy algorithm here too
                // Simplified: Just draw a line to a fixed offset for now
            }

            // --- 3. Draw Nodes (Native Paint for Shadow) ---
            val paint = Paint().apply {
                color = android.graphics.Color.BLACK
                isAntiAlias = true
                setShadowLayer(shadowRadius, 0f, shadowDy, shadowColor)
            }

            drawIntoCanvas { canvas ->
                // Center Node
                val cRadius = centerNodeRadius.toPx() * animValue
                if (cRadius > 0) {
                    canvas.nativeCanvas.drawCircle(centerX, centerY, cRadius, paint)
                }

                // Emotion Nodes
                orbitEmotions.forEach { emotion ->
                    val pos = emotionPositions[emotion] ?: return@forEach
                    val isPrimary = emotion in primaryEmotions
                    val eRadius = (if (isPrimary) primaryNodeRadius else secondaryNodeRadius).toPx() * animValue
                    
                    if (eRadius > 0) {
                        canvas.nativeCanvas.drawCircle(pos.x, pos.y, eRadius, paint)
                    }
                }
            }
            
            // --- 4. Labels (Simple Text) ---
             val labelStyle = TextStyle(
                 fontSize = 12.sp,
                 fontWeight = FontWeight.Normal,
                 color = Color.Black
             )
             
             // Center Label
             val centerText = textMeasurer.measure("감정", labelStyle)
             // drawText(centerText, topLeft = Offset(centerX - centerText.size.width/2, centerY + centerNodeRadius.toPx() + 10))

             // Emotion Labels
             orbitEmotions.forEach { emotion ->
                 val pos = emotionPositions[emotion] ?: return@forEach
                 val name = emotion.name // Simplified
                 val textLayout = textMeasurer.measure(name, labelStyle)
                 
                 // Text with background? No, minimalist
                 drawText(
                     textLayout, 
                     topLeft = Offset(pos.x - textLayout.size.width/2, pos.y + 15.dp.toPx()) 
                 )
             }
        }
    }
}

@Preview
@Composable
fun PreviewSimpleNetworkChart() {
    val mockEntry = MockData.mockJournalEntries[6]
    mockEntry.emotionAnalysis?.let {
        SimpleNetworkChart(analysis = it, modifier = Modifier.fillMaxSize())
    }
}
