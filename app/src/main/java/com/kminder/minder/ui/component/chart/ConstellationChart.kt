package com.kminder.minder.ui.component.chart

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.platform.LocalContext
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

    BoxWithConstraints(modifier = modifier.background(Color.White).padding(16.dp)) {
        val context = LocalContext.current
        val resources = context.resources
        
        // Load Localized Strings
        val basicEmotionsArray = resources.getStringArray(com.kminder.minder.R.array.basic_emotions)
        val primaryDyadsArray = resources.getStringArray(com.kminder.minder.R.array.primary_dyads)
        val secondaryDyadsArray = resources.getStringArray(com.kminder.minder.R.array.secondary_dyads)
        val tertiaryDyadsArray = resources.getStringArray(com.kminder.minder.R.array.tertiary_dyads)
        
        // Performance Optimization: Calculate Layout Once
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        
        val layout = remember(width, height) {
            val centerX = width / 2
            val centerY = height / 2
            val center = Offset(centerX, centerY)
            
            val availableRadius = minOf(width, height) / 2
            // Fix: Previous logic (0.75 * 1.55 = 1.16) exceeded bounds.
            // We want the Tertiary Ring (max * 1.55) to fit within availableRadius (with label padding).
            // Let's target tertRadius to be ~85-90% of availableRadius.
            // maxRadius = (availableRadius * 0.85) / 1.55 ≈ 0.55 * availableRadius
            val maxRadius = availableRadius * 0.55f 
            
            val strongRadius = maxRadius * 0.35f
            val basicRadius = maxRadius * 0.65f
            val weakRadius = maxRadius * 0.95f
            
            val radii = ChartRadii(strongRadius, basicRadius, weakRadius, maxRadius)
            
            val angleStep = (2 * Math.PI) / 8
            
            // Axes
            val axes = List(8) { i ->
                val angle = -Math.PI / 2 + (angleStep * i)
                val c = cos(angle).toFloat()
                val s = sin(angle).toFloat()
                
                AxisData(
                    index = i,
                    angle = angle,
                    cos = c,
                    sin = s,
                    strongPos = Offset(centerX + strongRadius * c, centerY + strongRadius * s),
                    basicPos = Offset(centerX + basicRadius * c, centerY + basicRadius * s),
                    weakPos = Offset(centerX + weakRadius * c, centerY + weakRadius * s),
                    labelPos = Offset(centerX + (basicRadius + 14.dp.value * density.density) * c, 
                                      centerY + (basicRadius + 14.dp.value * density.density) * s)
                )
            }
            
            // Primary Dyads (Radius = Basic)
            val primary = List(8) { i ->
                val angleStart = -Math.PI / 2 + (angleStep * i)
                val dAngle = angleStart + (angleStep / 2)
                val dx = centerX + basicRadius * cos(dAngle).toFloat()
                val dy = centerY + basicRadius * sin(dAngle).toFloat()
                DyadData(i, dAngle, Offset(dx, dy))
            }
            
            // Secondary (Radius = Max * 1.25)
            val secRadius = maxRadius * 1.25f
            val secondary = List(8) { i ->
               val currentAngle = -Math.PI / 2 + (angleStep * i)
               val sx = centerX + secRadius * cos(currentAngle).toFloat()
               val sy = centerY + secRadius * sin(currentAngle).toFloat()
               DyadData(i, currentAngle, Offset(sx, sy))
            }
            
            // Tertiary (Radius = Max * 1.55)
            val tertRadius = maxRadius * 1.55f
            val tertiary = List(8) { i ->
                val angleStart = -Math.PI / 2 + (angleStep * i)
                val tAngle = angleStart + (angleStep / 2)
                val tx = centerX + tertRadius * cos(tAngle).toFloat()
                val ty = centerY + tertRadius * sin(tAngle).toFloat()
                DyadData(i, tAngle, Offset(tx, ty))
            }
            
            ChartLayout(center, radii, axes, primary, secondary, tertiary)
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = layout.center.x
            val centerY = layout.center.y
            val center = layout.center
            val radii = layout.radii
            
            val intensityMap = analysis.toMap()
            
            // Pre-calculate Highlighted Basic Indices
            val highlightedBasicIndices = mutableSetOf<Int>()
            for (i in 0 until 8) {
                // Basic
                if ((intensityMap[axesEmotions[i]] ?: 0f) > 0f) highlightedBasicIndices.add(i)
                // Primary
                val next = (i + 1) % 8
                val s1 = intensityMap[axesEmotions[i]] ?: 0f
                val s2 = intensityMap[axesEmotions[next]] ?: 0f
                if ((s1 + s2) / 2 > 0f) { highlightedBasicIndices.add(i); highlightedBasicIndices.add(next) }
                // Secondary
                val prevIdx = (i + 7) % 8
                val nextIdx = (i + 1) % 8
                val ss1 = intensityMap[axesEmotions[prevIdx]] ?: 0f
                val ss2 = intensityMap[axesEmotions[nextIdx]] ?: 0f
                if ((ss1 + ss2) / 2 > 0f) { highlightedBasicIndices.add(prevIdx); highlightedBasicIndices.add(nextIdx) }
                // Tertiary
                val c1Idx = (i + 7) % 8
                val c2Idx = (i + 2) % 8
                val ts1 = intensityMap[axesEmotions[c1Idx]] ?: 0f
                val ts2 = intensityMap[axesEmotions[c2Idx]] ?: 0f
                if ((ts1 + ts2) / 2 > 0f) { highlightedBasicIndices.add(c1Idx); highlightedBasicIndices.add(c2Idx) }
            }
            
            // 1. Draw Map Structure (Axes & 3-Level Dots)
            val angleStep = (2 * Math.PI) / 8
            
            // Helper: Highlighting Axis Nodes
            fun drawAxisNodes(index: Int, color: Color) {
                val axis = layout.axes[index]
                drawCircle(color, 4.5.dp.toPx(), axis.strongPos)
                drawCircle(color, 4.5.dp.toPx(), axis.basicPos)
                drawCircle(color, 4.5.dp.toPx(), axis.weakPos)
            }
            
            // Black Structure
            val structColor = Color.Black
            
            axesEmotions.forEachIndexed { index, emotion ->
                val axis = layout.axes[index]
                
                // Draw Axis Line (Center to Outer)
                drawLine(
                    color = structColor,
                    start = center,
                    end = axis.weakPos,
                    strokeWidth = 0.5.dp.toPx()
                )
                
                // Draw 3 Structural Dots
                drawCircle(color = structColor, radius = 1.5.dp.toPx(), center = axis.strongPos)
                drawCircle(color = structColor, radius = 1.5.dp.toPx(), center = axis.basicPos)
                if (index !in highlightedBasicIndices) {
                     drawCircle(color = structColor, radius = 1.5.dp.toPx(), center = axis.weakPos)
                }
                
                // Draw Label
                if ((intensityMap[emotion] ?: 0f) == 0f) {
                    val labelStyle = TextStyle(fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    val labelText = basicEmotionsArray.getOrElse(index) { emotion.name }
                    val textLayout = textMeasurer.measure(labelText, labelStyle)
                    
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(axis.labelPos.x - textLayout.size.width/2, axis.labelPos.y - textLayout.size.height/2)
                    )
                }
            }
            
            // val intensityMap = analysis.toMap() (Moved to top)

            // 1.5 Draw Primary Dyads (Structure & Connections)
            // 1.5 Draw Primary Dyads (Structure & Connections)
            // Use localized array
            // 1.5 Draw Primary Dyads (Structure & Connections)
            // Use localized array
            val dyadNames = primaryDyadsArray.toList()
            
            val dyadRadius = radii.basic 
            
            for (i in 0 until 8) {
                val currentIdx = i
                val nextIdx = (i + 1) % 8
                val dyadData = layout.primaryDyads[i]
                val dyadPos = dyadData.pos
                val dAngle = dyadData.angle
                
                // Pre-calculate Score
                 val s1 = intensityMap[axesEmotions[currentIdx]] ?: 0f
                 val s2 = intensityMap[axesEmotions[nextIdx]] ?: 0f
                 val dScore = (s1 + s2) / 2
                 
                // Draw Structure (Black) - Conditional
                if (dScore == 0f) {
                    drawCircle(color = structColor, radius = 2.dp.toPx(), center = dyadPos)
                    
                    val labelStyle = TextStyle(fontSize = 9.sp, color = Color.Black)
                    val textLayout = textMeasurer.measure(dyadNames[i], labelStyle)
                    drawText(textLayout, topLeft = Offset(dyadPos.x - textLayout.size.width/2, dyadPos.y + 6.dp.toPx()))
                }
                
                // Connect to Adjacent Basic Dots (Structure)
                val prevAngle = layout.axes[currentIdx].angle
                
                // Convert to degrees
                fun toDeg(rad: Double): Float = Math.toDegrees(rad).toFloat()
                
                // Fixed Sweep: angleStep / 2 (always clockwise)
                val fixedSweep = toDeg((2 * Math.PI / 8) / 2)
                
                // Arc 1: Prev -> Dyad
                drawArc(
                    color = structColor,
                    startAngle = toDeg(prevAngle),
                    sweepAngle = fixedSweep,
                    useCenter = false,
                    topLeft = Offset(centerX - radii.basic, centerY - radii.basic),
                    size = androidx.compose.ui.geometry.Size(radii.basic * 2, radii.basic * 2),
                    style = Stroke(width = 0.5.dp.toPx())
                )
                
                // Arc 2: Dyad -> Next
                drawArc(
                    color = structColor,
                    startAngle = toDeg(dAngle),
                    sweepAngle = fixedSweep,
                    useCenter = false,
                    topLeft = Offset(centerX - radii.basic, centerY - radii.basic),
                    size = androidx.compose.ui.geometry.Size(radii.basic * 2, radii.basic * 2),
                    style = Stroke(width = 0.5.dp.toPx())
                )
                
                // Data Vis (Primary)
                 if (dScore > 0f) {
                     val c1 = getEmotionColor(axesEmotions[currentIdx]); val c2 = getEmotionColor(axesEmotions[nextIdx])
                     val bColor = Color((c1.red+c2.red)/2f, (c1.green+c2.green)/2f, (c1.blue+c2.blue)/2f, 1f)
                     
                     // Highlight Dot
                     drawCircle(color = bColor.copy(alpha=0.9f), radius = dataDotRadius.toPx()*0.7f, center = dyadPos)
                     // Highlight Neighbors (Larger) -> Full Axis Color
                     drawAxisNodes(currentIdx, bColor.copy(alpha=0.6f))
                     drawAxisNodes(nextIdx, bColor.copy(alpha=0.6f))
                     
                     // Highlight Label (Emphasized)
                     val activeLabelStyle = TextStyle(fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                     val activeTextLayout = textMeasurer.measure(dyadNames[i], activeLabelStyle)
                     drawText(activeTextLayout, topLeft = Offset(dyadPos.x - activeTextLayout.size.width/2, dyadPos.y + 6.dp.toPx()))
                 }
            }
            
            // 1.8 Draw Secondary Dyads (Structure & Extended Connections)
             val secDyadNames = secondaryDyadsArray.toList()
            
            for (i in 0 until 8) {
                val secData = layout.secondaryDyads[i]
                val secPos = secData.pos
                
                val prevIdx = (i + 7) % 8
                val nextIdx = (i + 1) % 8
                val prevPos = layout.axes[prevIdx].weakPos
                val nextPos = layout.axes[nextIdx].weakPos
                
                val s1 = intensityMap[axesEmotions[prevIdx]] ?: 0f
                val s2 = intensityMap[axesEmotions[nextIdx]] ?: 0f
                val secScore = (s1 + s2) / 2
                
                // Draw Structure Dot/Label (Conditional)
                if (secScore == 0f) {
                    drawCircle(color = structColor, radius = 2.dp.toPx(), center = secPos)
                    val secLabelStyle = TextStyle(fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    val textLayout = textMeasurer.measure(secDyadNames[i], secLabelStyle)
                    drawText(textLayout, topLeft = Offset(secPos.x - textLayout.size.width/2, secPos.y + 8.dp.toPx()))
                }
                
                // Connections (Always)
                 fun drawCurvedLine(start: Offset, end: Offset, color: Color, width: Float) {
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        val midX = (start.x + end.x) / 2
                        val midY = (start.y + end.y) / 2
                        val vecX = midX - centerX
                        val vecY = midY - centerY
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
                     
                     drawCircle(color = bColor.copy(alpha=0.9f), radius = dataDotRadius.toPx()*0.7f, center = secPos)
                     drawAxisNodes(prevIdx, bColor.copy(alpha=0.6f))
                     drawAxisNodes(nextIdx, bColor.copy(alpha=0.6f))
                     
                     val activeLabelStyle = TextStyle(fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                     val activeTextLayout = textMeasurer.measure(secDyadNames[i], activeLabelStyle)
                     drawText(activeTextLayout, topLeft = Offset(secPos.x - activeTextLayout.size.width/2, secPos.y + 8.dp.toPx()))
                }
            }
            
            // 1.9 Draw Tertiary Dyads
            val tertDyadNames = tertiaryDyadsArray.toList()
            
            for (i in 0 until 8) {
                val tertData = layout.tertiaryDyads[i]
                val tertPos = tertData.pos
                
                val c1Idx = (i + 7) % 8 // i-1
                val c2Idx = (i + 2) % 8 // i+2
                
                val pos1 = layout.axes[c1Idx].weakPos
                val pos2 = layout.axes[c2Idx].weakPos
                
                val s1 = intensityMap[axesEmotions[c1Idx]] ?: 0f
                val s2 = intensityMap[axesEmotions[c2Idx]] ?: 0f
                val tScore = (s1 + s2) / 2
                
                if (tScore == 0f) {
                    drawCircle(color = structColor, radius = 2.dp.toPx(), center = tertPos)
                    val tertLabelStyle = TextStyle(fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                    val textLayout = textMeasurer.measure(tertDyadNames[i], tertLabelStyle)
                    drawText(textLayout, topLeft = Offset(tertPos.x - textLayout.size.width/2, tertPos.y + 8.dp.toPx()))
                }
                
                val strokeW = 0.5.dp.toPx()
                // Use inline paths to avoid scope issues w/ helper
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
                
                if (tScore > 0f) {
                     val color1 = getEmotionColor(axesEmotions[c1Idx])
                     val color2 = getEmotionColor(axesEmotions[c2Idx])
                     val bColor = Color((color1.red+color2.red)/2f, (color1.green+color2.green)/2f, (color1.blue+color2.blue)/2f, 1f)
                     
                     drawCircle(color = bColor.copy(alpha=0.9f), radius = dataDotRadius.toPx()*0.7f, center = tertPos)
                     drawAxisNodes(c1Idx, bColor.copy(alpha=0.6f))
                     drawAxisNodes(c2Idx, bColor.copy(alpha=0.6f))
                     
                     val activeLabelStyle = TextStyle(fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                     val activeTextLayout = textMeasurer.measure(tertDyadNames[i], activeLabelStyle)
                     drawText(activeTextLayout, topLeft = Offset(tertPos.x - activeTextLayout.size.width/2, tertPos.y + 8.dp.toPx()))
                }
            }
            
            // 2. Draw Data Dots
            axesEmotions.forEachIndexed { index, emotion ->
                val score = intensityMap[emotion] ?: 0f
                val axis = layout.axes[index]
                val cosA = axis.cos
                val sinA = axis.sin
                
                if (score > 0f) {
                    val targetRadius = if (score >= 0.6f) radii.strong else if (score >= 0.3f) radii.basic else radii.weak
                    val r = targetRadius * animValue
                    
                    val dx = centerX + r * cosA
                    val dy = centerY + r * sinA
                    val dataPos = Offset(dx, dy)
                    val color = getEmotionColor(emotion)
                    
                    drawCircle(color = color, radius = dataDotRadius.toPx(), center = dataPos)
                    
                    drawAxisNodes(index, color.copy(alpha=0.6f))
                    
                    // Highlight Label (Emphasized)
                    val activeLabelStyle = TextStyle(fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                    val labelText = basicEmotionsArray.getOrElse(index) { emotion.name }
                    val activeTextLayout = textMeasurer.measure(labelText, activeLabelStyle)
                    
                    // Position label
                    val labelDist = radii.basic + 14.dp.toPx() 
                    val lx = centerX + (labelDist * cosA)
                    val ly = centerY + (labelDist * sinA)
                    drawText(activeTextLayout, topLeft = Offset(lx - activeTextLayout.size.width/2, ly - activeTextLayout.size.height/2))
                }
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

// Performance Optimization: Scaled Layout Cache
private data class ChartLayout(
    val center: Offset,
    val radii: ChartRadii,
    val axes: List<AxisData>,
    val primaryDyads: List<DyadData>,
    val secondaryDyads: List<DyadData>,
    val tertiaryDyads: List<DyadData>
)

private data class ChartRadii(
    val strong: Float,
    val basic: Float,
    val weak: Float,
    val max: Float
)

private data class AxisData(
    val index: Int,
    val angle: Double,
    val cos: Float,
    val sin: Float,
    val strongPos: Offset,
    val basicPos: Offset,
    val weakPos: Offset,
    val labelPos: Offset
)

private data class DyadData(
    val index: Int, // The starting index (i)
    val angle: Double,
    val pos: Offset
)
