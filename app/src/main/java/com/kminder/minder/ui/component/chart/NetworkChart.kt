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
                durationMillis = 1000,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
    }

    // Design Constants (Neo-Brutalism)
    val borderStrokeWidth = with(density) { 2.dp.toPx() }
    val shadowOffset = with(density) { 4.dp.toPx() }
    val blackColor = Color.Black

    // Sizes (Radius)
    val centerNodeRadius = 50.dp
    val primaryNodeRadius = 35.dp
    val secondaryNodeRadius = 25.dp // Keyword-only emotions
    val keywordNodeRadius = 18.dp

    // 감정 색상 정의
    fun getEmotionColor(emotion: EmotionType): Color {
        return when (emotion) {
            EmotionType.ANGER -> Color(0xFFE57373)
            EmotionType.ANTICIPATION -> Color(0xFFFFB74D)
            EmotionType.JOY -> Color(0xFFFFD54F) 
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

        // 1. Data Preparation
        val primaryEmotions = listOfNotNull(result.primaryEmotion, result.secondaryEmotion)
        
        // Group keywords by emotion
        val keywordMap = analysis.keywords.groupBy { it.emotion }
        
        // All related emotions (Primary + Secondary + Emotions from keywords)
        val allRelatedEmotions = (primaryEmotions + keywordMap.keys).distinct()
        
        // Exclude Primary/Secondary to find "Keyword-only" emotions
        val keywordOnlyEmotions = allRelatedEmotions - primaryEmotions.toSet()
        
        // Final sorted list of emotions to display in orbit
        // To maintain consistent layout: fixed order or sorted by type ordinal? 
        // Let's sort to keep Primary/Secondary close if possible, or just standard order.
        // Or put Primary/Secondary first.
        val orbitEmotions = primaryEmotions + keywordOnlyEmotions

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val dx = tapOffset.x - centerX
                        val dy = tapOffset.y - centerY
                        val cr = with(density) { centerNodeRadius.toPx() }
                        if (dx * dx + dy * dy <= cr * cr) {
                            onNodeClick(result)
                        }
                    }
                }
        ) {
             val animValue = progress.value
             
             // Base distance for Orbit 1 (Emotions)
             // Reduced from 0.3f to 0.25f to make space for outer keywords
             val orbitDistance = minOf(width, height) * 0.25f * animValue
             
             // Calculate positions for Orbit Emotions
             val emotionPositions = mutableMapOf<EmotionType, Offset>()
             val emotionAngles = mutableMapOf<EmotionType, Double>()
             
             val totalEmotions = orbitEmotions.size
             val angleStep = (2 * Math.PI) / totalEmotions.coerceAtLeast(1)
             // Start from top (-PI/2)
             var currentAngle = -Math.PI / 2
             
             orbitEmotions.forEach { emotion ->
                 val x = centerX + orbitDistance * cos(currentAngle).toFloat()
                 val y = centerY + orbitDistance * sin(currentAngle).toFloat()
                 emotionPositions[emotion] = Offset(x, y)
                 emotionAngles[emotion] = currentAngle
                 
                 currentAngle += angleStep
             }
             
             // --- Draw Lines (Edges) Phase 1: Center to Emotions ---
             orbitEmotions.forEach { emotion ->
                 val pos = emotionPositions[emotion] ?: return@forEach
                 val isPrimary = emotion in primaryEmotions
                 
                 // Line style: Primary is thick, Others thin/dashed? Just thin for now.
                 val intensity = analysis.getEmotionIntensity(emotion)
                 val strokeWidth = if (isPrimary) {
                     (3.dp.toPx() * intensity + 2.dp.toPx()) 
                 } else {
                     1.5.dp.toPx()
                 }
                 
                 drawLine(
                    color = blackColor,
                    start = Offset(centerX, centerY),
                    end = pos,
                    strokeWidth = strokeWidth * animValue,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                 )
             }
             
             // --- Draw Keywords & Lines Phase 2: Emotions to Keywords (Greedy Placement) ---
             
             // 1. Define Occupied Zones (Center + Orbit Nodes)
             // We use a simple Rect for collision detection
             val placedRects = mutableListOf<androidx.compose.ui.geometry.Rect>()
             
             // Add Center Node Rect
             val crVal = with(density) { centerNodeRadius.toPx() } * animValue
             placedRects.add(
                 androidx.compose.ui.geometry.Rect(
                     centerX - crVal, centerY - crVal,
                     centerX + crVal, centerY + crVal
                 )
             )
             
             // Add Emotion Nodes Rects
             orbitEmotions.forEach { emotion ->
                 val pos = emotionPositions[emotion] ?: return@forEach
                 val isPrimary = emotion in primaryEmotions
                 val rDp = if (isPrimary) primaryNodeRadius else secondaryNodeRadius
                 val rVal = with(density) { rDp.toPx() } * animValue
                 placedRects.add(
                     androidx.compose.ui.geometry.Rect(
                         pos.x - rVal, pos.y - rVal,
                         pos.x + rVal, pos.y + rVal
                     )
                 )
             }

             // 2. Place Keywords
             orbitEmotions.forEach { emotion ->
                 val emotionPos = emotionPositions[emotion] ?: return@forEach
                 val baseAngle = emotionAngles[emotion] ?: 0.0
                 val keywords = keywordMap[emotion] ?: emptyList()
                 
                 if (keywords.isNotEmpty()) {
                     // Basic Fan-out logic for INITIAL angles
                     val sectorAngle = Math.toRadians(100.0) // Wider sector
                     val startKAngle = baseAngle - sectorAngle / 2
                     val kAngleStep = if (keywords.size > 1) sectorAngle / (keywords.size - 1) else 0.0
                     
                     keywords.forEachIndexed { index, keyword ->
                         val kAngle = if (keywords.size == 1) baseAngle else startKAngle + (kAngleStep * index)
                         
                         // Measure Text for Size Calculation
                         val kText = textMeasurer.measure(
                             text = keyword.word,
                             style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = blackColor)
                         )
                         val textWidth = kText.size.width.toFloat()
                         val textHeight = kText.size.height.toFloat()
                         val hPadding = with(density) { 10.dp.toPx() } * animValue // Slightly tighter padding
                         val vPadding = with(density) { 5.dp.toPx() } * animValue
                         val pillWidth = textWidth + hPadding * 2
                         val pillHeight = textHeight + vPadding * 2
                         
                         // Greedy Search for Position
                         // Start close and push out
                         var currentDistDp = 40.dp // Start closer (User wants "as close as possible")
                         var foundPos: Offset? = null
                         var finalPillRect: androidx.compose.ui.geometry.Rect? = null
                         
                         val maxDistDp = 150.dp // Limit iteration
                         val distStepDp = 5.dp
                         
                         while (currentDistDp <= maxDistDp) {
                             val currentDistPx = with(density) { currentDistDp.toPx() } * animValue
                             
                             // Proposed Position
                             val kx = emotionPos.x + currentDistPx * cos(kAngle).toFloat()
                             val ky = emotionPos.y + currentDistPx * sin(kAngle).toFloat()
                             
                             // Proposed Rect (with some buffer for spacing)
                             val buffer = with(density) { 4.dp.toPx() } // Minimum gap between nodes
                             val proposedRect = androidx.compose.ui.geometry.Rect(
                                 kx - pillWidth / 2 - buffer, ky - pillHeight / 2 - buffer,
                                 kx + pillWidth / 2 + buffer, ky + pillHeight / 2 + buffer
                             )
                             
                             // Check Collision
                             var hasCollision = false
                             for (placed in placedRects) {
                                 if (placed.overlaps(proposedRect)) {
                                     hasCollision = true
                                     break
                                 }
                             }
                             
                             if (!hasCollision) {
                                 foundPos = Offset(kx, ky)
                                 finalPillRect = proposedRect // Store the rect (including buffer) to reserve space
                                 break
                             }
                             
                             currentDistDp += distStepDp
                         }
                         
                         // Fallback if limit reached (just place at max)
                         if (foundPos == null) {
                             val currentDistPx = with(density) { maxDistDp.toPx() } * animValue
                             foundPos = Offset(
                                 emotionPos.x + currentDistPx * cos(kAngle).toFloat(),
                                 emotionPos.y + currentDistPx * sin(kAngle).toFloat()
                             )
                             // Still add to placedRects to try to avoid others overlapping this fallback
                             finalPillRect = androidx.compose.ui.geometry.Rect(
                                 foundPos!!.x - pillWidth/2, foundPos.y - pillHeight/2,
                                 foundPos.x + pillWidth/2, foundPos.y + pillHeight/2
                             )
                         }
                         
                         // Mark valid position as occupied
                         finalPillRect?.let { placedRects.add(it) }
                         
                         val kPos = foundPos!!
                         
                         // --- Draw ---
                         // Line: Emotion -> Keyword
                         drawLine(
                             color = blackColor,
                             start = emotionPos,
                             end = kPos,
                             strokeWidth = 1.dp.toPx() * animValue
                         )
                         
                         if (pillHeight > 2f) {
                             val cornerRadius = androidx.compose.ui.geometry.CornerRadius(pillHeight / 2, pillHeight / 2)
                             val pillTopLeft = Offset(kPos.x - pillWidth / 2, kPos.y - pillHeight / 2)
                             
                             // Shadow
                             drawRoundRect(
                                 color = blackColor,
                                 topLeft = Offset(pillTopLeft.x + shadowOffset, pillTopLeft.y + shadowOffset),
                                 size = Size(pillWidth, pillHeight),
                                 cornerRadius = cornerRadius
                             )
                             // Body
                             drawRoundRect(
                                 color = Color.White,
                                 topLeft = pillTopLeft,
                                 size = Size(pillWidth, pillHeight),
                                 cornerRadius = cornerRadius
                             )
                             // Border
                             drawRoundRect(
                                 color = blackColor,
                                 topLeft = pillTopLeft,
                                 size = Size(pillWidth, pillHeight),
                                 cornerRadius = cornerRadius,
                                 style = Stroke(width = 1.dp.toPx())
                             )
                             
                             // Center text
                             drawText(
                                 textLayoutResult = kText,
                                 topLeft = Offset(kPos.x - textWidth / 2, kPos.y - textHeight / 2)
                             )
                         }
                     }
                 }
             }

             // --- Draw Emotion Nodes (Level 2 & 3) ---
             orbitEmotions.forEach { emotion ->
                 val pos = emotionPositions[emotion] ?: return@forEach
                 val isPrimary = emotion in primaryEmotions
                 
                 val radiusDp = if (isPrimary) primaryNodeRadius else secondaryNodeRadius
                 val radius = with(density) { radiusDp.toPx() } * animValue
                 
                 if (radius > 1f) {
                     // Color logic: Primary gets real color, Level 3 gets White
                     val nodeColor = if (isPrimary) getEmotionColor(emotion) else Color.White
                     
                     // Shadow (Common)
                     drawCircle(
                         color = blackColor, 
                         radius = radius, 
                         center = Offset(pos.x + shadowOffset, pos.y + shadowOffset)
                     )
                     
                     // Body (Common Circle)
                     drawCircle(
                         color = nodeColor, 
                         radius = radius, 
                         center = pos
                     )
                     
                     // Border (Common)
                     drawCircle(
                         color = blackColor, 
                         radius = radius, 
                         center = pos, 
                         style = Stroke(width = borderStrokeWidth)
                     )
                     
                     // Label
                     val labelStyle = if (isPrimary) {
                         TextStyle(fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = blackColor)
                     } else {
                         TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = blackColor)
                     }
                     
                     val name = emotionNames[emotion] ?: emotion.name
                     val textLayout = textMeasurer.measure(text = name, style = labelStyle)
                     
                     // Center Text inside the Node
                     drawText(
                         textLayoutResult = textLayout,
                         topLeft = Offset(pos.x - textLayout.size.width / 2, pos.y - textLayout.size.height / 2)
                     )
                 }
             }
             
            // --- Draw Center Node (Level 1) ---
            val cr = with(density) { centerNodeRadius.toPx() } 
            // Scale animation slightly elastic
            val currentCenterRadius = cr * androidx.compose.animation.core.EaseOutBack.transform(animValue)
            
            val centerColor = getEmotionColor(result.primaryEmotion)
            
            // Hard Shadow
            drawCircle(
                color = blackColor,
                radius = currentCenterRadius,
                center = Offset(centerX + shadowOffset, centerY + shadowOffset)
            )

            // Main Circle
            drawCircle(color = Color.White, radius = currentCenterRadius, center = Offset(centerX, centerY))
            drawCircle(color = centerColor.copy(alpha = 0.3f), radius = currentCenterRadius, center = Offset(centerX, centerY)) // Tint
            
            // Border
            drawCircle(
                color = blackColor, 
                radius = currentCenterRadius, 
                center = Offset(centerX, centerY), 
                style = Stroke(width = borderStrokeWidth * 1.5f)
            )
             
             // Label
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
    // Shows Mock Entry 26 (Single Emotion: Anger)
    val mockEntry = MockData.mockJournalEntries[24]
    mockEntry.emotionAnalysis?.let {
        NetworkChart(analysis = it, modifier = Modifier.fillMaxSize()) 
    }
}