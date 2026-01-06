package com.kminder.minder.ui.component.chart

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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

/**
 * Constellation Chart (Prototype)
 * - Concept: 8-Direction Radial Coordinates
 * - Anchors: Fixed category dots at max radius.
 * - Data: Dots placed on axes based on intensity, connected to their anchors.
 */
@Composable
fun ConstellationChart(
    analysis: EmotionAnalysis,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // Animation - Disabled explicitly by user request
    // var animationPlayed by remember { mutableStateOf(false) }
    // val animatable = remember { Animatable(0f) }
    // LaunchedEffect...
    
    val animValue = 1f

    // Constants
    val axisColor = Color.LightGray.copy(alpha = 0.5f)
    val anchorColor = Color.Gray
    val anchorRadius = 4.dp
    val dataDotRadius = 6.dp
    
    // 8 Basic Emotions Order (Plutchik Wheel Order for symmetry)
    val orderedEmotions = listOf(
        EmotionType.JOY, EmotionType.TRUST, EmotionType.FEAR, EmotionType.SURPRISE,
        EmotionType.SADNESS, EmotionType.DISGUST, EmotionType.ANGER, EmotionType.ANTICIPATION
    )
    // Note: Enum names case might need matching. EmotionType is usually UPPERCASE.
    // Let's check EmotionType definition if needed, but assuming standard UPPERCASE from previous contexts.
    // Actually, looking at MockData, it uses EmotionType.JOY, EmotionType.TRUST etc.

    // Fixing generic list based on standard EmotionType enum assumptions (usually upper case)
    val axesEmotions = listOf(
        EmotionType.JOY, 
        EmotionType.TRUST, 
        EmotionType.FEAR, 
        EmotionType.SURPRISE,
        EmotionType.SADNESS, 
        EmotionType.DISGUST, 
        EmotionType.ANGER, 
        EmotionType.ANTICIPATION
    )

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

    Box(modifier = modifier.background(Color.White).padding(16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val center = Offset(centerX, centerY)
            
            // Maximum radius for anchors (leaving space for labels)
            val maxRadius = (minOf(size.width, size.height) / 2) * 0.75f // Reduce slightly to fit outer labels
            
            val intensityMap = analysis.toMap()
            
            // Pre-calculate which Basic Anchors are highlighted (Connected to an active Dyad or are active themselves)
            val highlightedBasicIndices = mutableSetOf<Int>()
            for (i in 0 until 8) {
                // Check Basic
                if ((intensityMap[axesEmotions[i]] ?: 0f) > 0f) highlightedBasicIndices.add(i)
                
                // Check Primary (i and i+1)
                val next = (i + 1) % 8
                val s1 = intensityMap[axesEmotions[i]] ?: 0f
                val s2 = intensityMap[axesEmotions[next]] ?: 0f
                if ((s1 + s2) / 2 > 0f) { highlightedBasicIndices.add(i); highlightedBasicIndices.add(next) }
                
                // Check Secondary (i connects to i-1 and i+1)
                // Angle i connects to prev(i-1) and next(i+1)
                val prevIdx = (i + 7) % 8
                val nextIdx = (i + 1) % 8
                val ss1 = intensityMap[axesEmotions[prevIdx]] ?: 0f
                val ss2 = intensityMap[axesEmotions[nextIdx]] ?: 0f
                if ((ss1 + ss2) / 2 > 0f) { highlightedBasicIndices.add(prevIdx); highlightedBasicIndices.add(nextIdx) }
                
                // Check Tertiary (i connects to i-1 and i+2)
                val c1Idx = (i + 7) % 8
                val c2Idx = (i + 2) % 8
                val ts1 = intensityMap[axesEmotions[c1Idx]] ?: 0f
                val ts2 = intensityMap[axesEmotions[c2Idx]] ?: 0f
                if ((ts1 + ts2) / 2 > 0f) { highlightedBasicIndices.add(c1Idx); highlightedBasicIndices.add(c2Idx) }
            }
            
            // 1. Draw Map Structure (Axes & 3-Level Dots)
            val angleStep = (2 * Math.PI) / 8
            var currentAngle = -Math.PI / 2 // Start from Top
            
            val anchorPositions = mutableMapOf<EmotionType, Offset>()
            
            // Radii for Intensity Levels
            val strongRadius = maxRadius * 0.35f
            val basicRadius = maxRadius * 0.65f
            val weakRadius = maxRadius * 0.95f
            
            // Black Structure
            val structColor = Color.Black
            
            axesEmotions.forEachIndexed { index, emotion ->
                val cosA = cos(currentAngle).toFloat()
                val sinA = sin(currentAngle).toFloat()
                
                // Positions
                val strongPos = Offset(centerX + strongRadius * cosA, centerY + strongRadius * sinA)
                val basicPos = Offset(centerX + basicRadius * cosA, centerY + basicRadius * sinA)
                val weakPos = Offset(centerX + weakRadius * cosA, centerY + weakRadius * sinA)
                
                // Store Outer (Weak) as the main anchor for connections
                anchorPositions[emotion] = weakPos
                
                // Draw Axis Line (Center to Outer)
                // Draw Axis Line (Center to Outer) - Thinner
                drawLine(
                    color = structColor,
                    start = center,
                    end = weakPos,
                    strokeWidth = 0.5.dp.toPx()
                )
                
                // Draw 3 Structural Dots - Smaller
                // If data exists at this index, strictly speaking the dot is covered.
                // However, user specifically asked to "not draw background dot" for "final emotion related".
                // The "Weak" dot is the Anchor.
                
                drawCircle(color = structColor, radius = 1.5.dp.toPx(), center = strongPos)
                drawCircle(color = structColor, radius = 1.5.dp.toPx(), center = basicPos)
                if (index !in highlightedBasicIndices) {
                     drawCircle(color = structColor, radius = 1.5.dp.toPx(), center = weakPos)
                }
                
                // Draw Label (AT BASIC RING)
                // Suppress if this emotion is active (will be drawn larger later)
                if ((intensityMap[emotion] ?: 0f) == 0f) {
                    val labelStyle = TextStyle(fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    val textLayout = textMeasurer.measure(emotion.name, labelStyle)
                // Offset slightly from basic radius
                val labelDist = basicRadius + 14.dp.toPx()
                val lx = centerX + (labelDist * cosA)
                val ly = centerY + (labelDist * sinA)
                
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(lx - textLayout.size.width/2, ly - textLayout.size.height/2)
                    )
                }
                
                currentAngle += angleStep
            }
            
            // val intensityMap = analysis.toMap() (Moved to top)

            // 1.5 Draw Primary Dyads (Structure & Connections)
            val dyadNames = listOf(
                "Love", "Submission", "Awe", "Disapproval",
                "Remorse", "Contempt", "Aggressiveness", "Optimism"
            )
            
            val dyadRadius = basicRadius 
            
            for (i in 0 until 8) {
                val currentIdx = i
                val nextIdx = (i + 1) % 8
                
                // Mid Angle
                val angleStart = -Math.PI / 2 + (angleStep * currentIdx)
                val dAngle = angleStart + (angleStep / 2)
                
                val dx = centerX + dyadRadius * cos(dAngle).toFloat()
                val dy = centerY + dyadRadius * sin(dAngle).toFloat()
                val dyadPos = Offset(dx, dy)
                
                // Pre-calculate Score
                 val s1 = intensityMap[axesEmotions[currentIdx]] ?: 0f
                 val s2 = intensityMap[axesEmotions[nextIdx]] ?: 0f
                 val dScore = (s1 + s2) / 2
                 
                // Draw Structure (Black) - Conditional
                if (dScore == 0f) {
                    drawCircle(
                        color = structColor, 
                        radius = 2.dp.toPx(), 
                        center = dyadPos
                    )
                    
                    // Label
                    val labelStyle = TextStyle(fontSize = 9.sp, color = Color.Black)
                    val textLayout = textMeasurer.measure(dyadNames[i], labelStyle)
                    drawText(
                        textLayout,
                        topLeft = Offset(dx - textLayout.size.width/2, dy + 6.dp.toPx())
                    )
                }
                
                // Connect to Adjacent Basic Dots (Structure)
                // Use drawArc for "Curved/Elliptical" connection along the ring
                // Start Angle: prevAngle (in degrees)
                // Sweep: dAngle - prevAngle? No, basic is at prevAngle. dyad is at dAngle.
                // We draw FROM Dyad TO Prev (or vice versa).
                // drawArc takes startAngle (degrees) and sweepAngle (degrees).
                // 0 degrees is 3 o'clock. 
                // angles in code are radians, need to convert to degrees.
                
                val prevAngle = -Math.PI / 2 + (angleStep * currentIdx)
                val nextAngle2 = -Math.PI / 2 + (angleStep * nextIdx)
                
                // DATA_VIS_REQUIREMENT: pPos and nPos are needed for Neighbor Highlight circles later
                val pPos = Offset(centerX + basicRadius * cos(prevAngle).toFloat(), centerY + basicRadius * sin(prevAngle).toFloat()) 
                val nPos = Offset(centerX + basicRadius * cos(nextAngle2).toFloat(), centerY + basicRadius * sin(nextAngle2).toFloat())
                
                // Convert to degrees
                fun toDeg(rad: Double): Float = Math.toDegrees(rad).toFloat()
                
                // Arc 1: Prev -> Dyad
                // Start: prevAngle. End: dAngle.
                // Sweep: dAngle - prevAngle.
                drawArc(
                    color = structColor,
                    startAngle = toDeg(prevAngle),
                    sweepAngle = toDeg(dAngle - prevAngle),
                    useCenter = false,
                    topLeft = Offset(centerX - basicRadius, centerY - basicRadius),
                    size = androidx.compose.ui.geometry.Size(basicRadius * 2, basicRadius * 2),
                    style = Stroke(width = 0.5.dp.toPx()) // Thin 0.5dp
                )
                
                // Arc 2: Dyad -> Next
                // Start: dAngle. End: nextAngle2.
                drawArc(
                    color = structColor,
                    startAngle = toDeg(dAngle),
                    sweepAngle = toDeg(nextAngle2 - dAngle),
                    useCenter = false,
                    topLeft = Offset(centerX - basicRadius, centerY - basicRadius),
                    size = androidx.compose.ui.geometry.Size(basicRadius * 2, basicRadius * 2),
                    style = Stroke(width = 0.5.dp.toPx()) // Thin 0.5dp
                )
                
                // Data Vis (Primary)
                 if (dScore > 0f) {
                     val c1 = getEmotionColor(axesEmotions[currentIdx]); val c2 = getEmotionColor(axesEmotions[nextIdx])
                     val bColor = Color((c1.red+c2.red)/2f, (c1.green+c2.green)/2f, (c1.blue+c2.blue)/2f, 1f)
                     
                     // Highlight Dot
                     drawCircle(color = bColor.copy(alpha=0.9f), radius = dataDotRadius.toPx()*0.7f, center = dyadPos)
                     // Highlight Lines - Removed by user request (Only Dots)
                     // drawLine(color = bColor.copy(alpha=0.8f), start = dyadPos, end = pPos, strokeWidth = 2.dp.toPx())
                     // drawLine(color = bColor.copy(alpha=0.8f), start = dyadPos, end = nPos, strokeWidth = 2.dp.toPx())
                     // Highlight Neighbor Nodes (Larger)
                     drawCircle(color = bColor.copy(alpha=0.6f), radius = 4.5.dp.toPx(), center = pPos)
                     drawCircle(color = bColor.copy(alpha=0.6f), radius = 4.5.dp.toPx(), center = nPos)
                     
                     // Highlight Label (Emphasized)
                     val activeLabelStyle = TextStyle(fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                     val activeTextLayout = textMeasurer.measure(dyadNames[i], activeLabelStyle)
                     drawText(activeTextLayout, topLeft = Offset(dx - activeTextLayout.size.width/2, dy + 6.dp.toPx()))
                 }
            }
            
            // 1.8 Draw Secondary Dyads (Structure & Extended Connections)
            // Sitting on the axis extension (visual behind), connected to neighbors.
             val secDyadNames = listOf(
                "Fatalism", // on Joy axis
                "Guilt",    // on Trust axis
                "Curiosity",// on Fear axis
                "Despair",  // on Surprise axis
                "Unbelief", // on Sadness axis
                "Envy",     // on Disgust axis
                "Cynicism", // on Anger axis
                "Pride"     // on Anticipation axis
            )
            
            val secRadius = maxRadius * 1.25f
            
            for (i in 0 until 8) {
                val currentAngle = -Math.PI / 2 + (angleStep * i)
                val sx = centerX + secRadius * cos(currentAngle).toFloat()
                val sy = centerY + secRadius * sin(currentAngle).toFloat()
                val secPos = Offset(sx, sy)
                
                val prevIdx = (i + 7) % 8
                val nextIdx = (i + 1) % 8
                val prevPos = anchorPositions[axesEmotions[prevIdx]]!!
                val nextPos = anchorPositions[axesEmotions[nextIdx]]!!
                
                val s1 = intensityMap[axesEmotions[prevIdx]] ?: 0f
                val s2 = intensityMap[axesEmotions[nextIdx]] ?: 0f
                val secScore = (s1 + s2) / 2
                
                // Draw Structure Dot/Label (Conditional)
                if (secScore == 0f) {
                    drawCircle(color = structColor, radius = 2.dp.toPx(), center = secPos)
                    
                    // Label
                    val secLabelStyle = TextStyle(fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    val textLayout = textMeasurer.measure(secDyadNames[i], secLabelStyle)
                    drawText(textLayout, topLeft = Offset(sx - textLayout.size.width/2, sy + 8.dp.toPx()))
                }
                
                // Connections (Always)
                // Connect to Constituent Emotions (Curved)
                 fun drawCurvedLine(start: Offset, end: Offset, color: Color, width: Float) {
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        // Simple Control Point: Midpoint pushed outward
                        val midX = (start.x + end.x) / 2
                        val midY = (start.y + end.y) / 2
                        // Push out? Or just use midpoint? 
                        // If we use simple Quad to midpoint, it's straight.
                        // We need a control point that creates an "ellipse" segment.
                        // Let's use the center to determine "outward".
                        val vecX = midX - centerX
                        val vecY = midY - centerY
                        // Scale slightly (e.g., 1.1x from center)
                        val cpX = centerX + vecX * 1.1f
                        val cpY = centerY + vecY * 1.1f
                        
                        quadraticTo(cpX, cpY, end.x, end.y)
                    }
                    drawPath(path, color, style = Stroke(width))
                }
                
                val strokeW = 0.5.dp.toPx()
                drawCurvedLine(secPos, prevPos, structColor, strokeW)
                drawCurvedLine(secPos, nextPos, structColor, strokeW)
                
                // Data Vis (Secondary)
                if (secScore > 0f) {
                     val c1 = getEmotionColor(axesEmotions[prevIdx]); val c2 = getEmotionColor(axesEmotions[nextIdx])
                     val bColor = Color((c1.red+c2.red)/2f, (c1.green+c2.green)/2f, (c1.blue+c2.blue)/2f, 1f)
                     
                     // Highlight Dot
                     drawCircle(color = bColor.copy(alpha=0.9f), radius = dataDotRadius.toPx()*0.7f, center = secPos)
                     // Highlight Lines - Removed
                     // drawLine(color = bColor.copy(alpha=0.8f), start = secPos, end = prevPos, strokeWidth = 2.dp.toPx())
                     // drawLine(color = bColor.copy(alpha=0.8f), start = secPos, end = nextPos, strokeWidth = 2.dp.toPx())
                     // Highlight Neighbor Nodes (Larger)
                     drawCircle(color = bColor.copy(alpha=0.6f), radius = 4.5.dp.toPx(), center = prevPos)
                     drawCircle(color = bColor.copy(alpha=0.6f), radius = 4.5.dp.toPx(), center = nextPos)
                     
                     // Highlight Label (Emphasized)
                     val activeLabelStyle = TextStyle(fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                     val activeTextLayout = textMeasurer.measure(secDyadNames[i], activeLabelStyle)
                     drawText(activeTextLayout, topLeft = Offset(sx - activeTextLayout.size.width/2, sy + 8.dp.toPx()))
                }
            }
            
            // 1.9 Draw Tertiary Dyads (Far Outer Ring)
            // Tertiary: 3 steps apart. Sits between axes (like Primary).
            // Slot i (between i and i+1): Combines (i-1) and (i+2).
            val tertDyadNames = listOf(
                "Anxiety",       // 0: Anticipation(7) + Fear(2)
                "Delight",       // 1: Joy(0) + Surprise(3)
                "Sentimentality",// 2: Trust(1) + Sadness(4)
                "Shame",         // 3: Fear(2) + Disgust(5)
                "Outrage",       // 4: Surprise(3) + Anger(6)
                "Pessimism",     // 5: Sadness(4) + Anticipation(7)
                "Morbidity",     // 6: Disgust(5) + Joy(0)
                "Dominance"      // 7: Anger(6) + Trust(1)
            )
            
            val tertRadius = maxRadius * 1.55f
            
            for (i in 0 until 8) {
                // Angle is mid-point between i and i+1
                val angleStart = -Math.PI / 2 + (angleStep * i)
                val tAngle = angleStart + (angleStep / 2)
                
                val tx = centerX + tertRadius * cos(tAngle).toFloat()
                val ty = centerY + tertRadius * sin(tAngle).toFloat()
                val tertPos = Offset(tx, ty)
                
                val c1Idx = (i + 7) % 8 // i-1
                val c2Idx = (i + 2) % 8 // i+2
                
                val pos1 = anchorPositions[axesEmotions[c1Idx]]!!
                val pos2 = anchorPositions[axesEmotions[c2Idx]]!!
                
                val s1 = intensityMap[axesEmotions[c1Idx]] ?: 0f
                val s2 = intensityMap[axesEmotions[c2Idx]] ?: 0f
                val tScore = (s1 + s2) / 2
                
                // Draw Structure Dot (Conditional)
                if (tScore == 0f) {
                    drawCircle(color = structColor, radius = 2.dp.toPx(), center = tertPos)
                    
                    val tertLabelStyle = TextStyle(fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                    val textLayout = textMeasurer.measure(tertDyadNames[i], tertLabelStyle)
                    drawText(textLayout, topLeft = Offset(tx - textLayout.size.width/2, ty + 8.dp.toPx()))
                }
                
                // Connections (Always)
                // Use same drawCurvedLine logic (duplicated for now or assuming helper function scope?)
                // Helper defined inside 'Canvas' scope would be accessible, but I defined it inside Secondary Loop scope.
                // I need to redefine or move it.
                // I'll redefine inline for safety.
                
                val strokeW = 0.5.dp.toPx()
                
                val path1 = Path().apply {
                    moveTo(tertPos.x, tertPos.y)
                    val midX = (tertPos.x + pos1.x) / 2
                    val midY = (tertPos.y + pos1.y) / 2
                    val vecX = midX - centerX
                    val vecY = midY - centerY
                    val cpX = centerX + vecX * 1.1f
                    val cpY = centerY + vecY * 1.1f
                    quadraticTo(cpX, cpY, pos1.x, pos1.y)
                }
                drawPath(path1, structColor, style = Stroke(strokeW))
                
                val path2 = Path().apply {
                    moveTo(tertPos.x, tertPos.y)
                    val midX = (tertPos.x + pos2.x) / 2
                    val midY = (tertPos.y + pos2.y) / 2
                    val vecX = midX - centerX
                    val vecY = midY - centerY
                    val cpX = centerX + vecX * 1.1f
                    val cpY = centerY + vecY * 1.1f
                    quadraticTo(cpX, cpY, pos2.x, pos2.y)
                }
                drawPath(path2, structColor, style = Stroke(strokeW))
                
                // Data Vis (Tertiary)
                if (tScore > 0f) {
                     val color1 = getEmotionColor(axesEmotions[c1Idx])
                     val color2 = getEmotionColor(axesEmotions[c2Idx])
                     val bColor = Color((color1.red+color2.red)/2f, (color1.green+color2.green)/2f, (color1.blue+color2.blue)/2f, 1f)
                     
                     // Highlight Dot
                     drawCircle(color = bColor.copy(alpha=0.9f), radius = dataDotRadius.toPx()*0.7f, center = tertPos)
                     // Highlight Lines - Removed
                     // drawLine(color = bColor.copy(alpha=0.8f), start = tertPos, end = pos1, strokeWidth = 2.dp.toPx())
                     // drawLine(color = bColor.copy(alpha=0.8f), start = tertPos, end = pos2, strokeWidth = 2.dp.toPx())
                     // Highlight Neighbors (Larger)
                     drawCircle(color = bColor.copy(alpha=0.6f), radius = 4.5.dp.toPx(), center = pos1)
                     drawCircle(color = bColor.copy(alpha=0.6f), radius = 4.5.dp.toPx(), center = pos2)
                     
                     // Highlight Label (Emphasized)
                     val activeLabelStyle = TextStyle(fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                     val activeTextLayout = textMeasurer.measure(tertDyadNames[i], activeLabelStyle)
                     drawText(activeTextLayout, topLeft = Offset(tx - activeTextLayout.size.width/2, ty + 8.dp.toPx()))
                }
            }
            
            // 2. Draw Data Dots
            var dAngle = -Math.PI / 2
            axesEmotions.forEach { emotion ->
                val score = intensityMap[emotion] ?: 0f
                val cosA = cos(dAngle).toFloat()
                val sinA = sin(dAngle).toFloat()
                
                if (score > 0f) {
                    val targetRadius = if (score >= 0.6f) strongRadius else if (score >= 0.3f) basicRadius else weakRadius
                    val r = targetRadius * animValue
                    
                    val dx = centerX + r * cosA
                    val dy = centerY + r * sinA
                    val dataPos = Offset(dx, dy)
                    val color = getEmotionColor(emotion)
                    
                    drawCircle(color = color, radius = dataDotRadius.toPx(), center = dataPos)
                    // Highlight Lines - Removed
                    // drawLine(color = color.copy(alpha=0.8f), start = center, end = dataPos, strokeWidth = 2.dp.toPx())
                    
                    // Highlight Anchor (Connected Node - Larger)
                    val anchorPos = anchorPositions[emotion]!!
                    drawCircle(color = color.copy(alpha=0.6f), radius = 5.dp.toPx(), center = anchorPos)
                    
                    // Highlight Label (Emphasized)
                    val activeLabelStyle = TextStyle(fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                    val activeTextLayout = textMeasurer.measure(emotion.name, activeLabelStyle)
                    val labelDist = basicRadius + 14.dp.toPx() 
                    val lx = centerX + (labelDist * cosA)
                    val ly = centerY + (labelDist * sinA)
                    drawText(activeTextLayout, topLeft = Offset(lx - activeTextLayout.size.width/2, ly - activeTextLayout.size.height/2))
                }
                dAngle += angleStep
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConstellationChart() {
    val mockEntry = MockData.mockJournalEntries[6]
    mockEntry.emotionAnalysis?.let {
        ConstellationChart(analysis = it, modifier = Modifier.fillMaxSize())
    }
}
