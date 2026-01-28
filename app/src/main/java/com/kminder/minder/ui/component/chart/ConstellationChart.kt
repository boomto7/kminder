package com.kminder.minder.ui.component.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.EmotionType
import com.kminder.minder.data.mock.MockData
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.util.EmotionColorUtil.getEmotionColor
import com.kminder.minder.util.EmotionUiUtil
import kotlin.math.cos
import kotlin.math.sin

/**
 * Constellation Chart
 * Implemented based on User's 5-Step Methodology (Revised):
 * 1. Pre-calc Coords.
 * 2. Background Glow (Final Emotion) - Enhanced Radius (3x).
 * 3. Dots (Final=Large/Emphasis vs Origin=Std/Border).
 *    - NEW: Draw ALL dots (including inactive ones) as small gray dots.
 * 4. Lines (Trace Back: Single=Intensity, Primary=Mid, Sec/Tert=Outer).
 *    - Inactive Structure Lines (Gray Web).
 * 5. Labels (Highlight Final).
 *    - NEW: Basic Category Labels positioned at "Basic Category Center" (interpreted as Basic/Mid Radius).
 *
 * Scale: Inner(Weak) -> Outer(Strong).
 */
@Composable
fun ConstellationChart(
    result: EmotionResult,
    modifier: Modifier = Modifier
) {
    val analysis = result.source
    
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    
    val animValue = 1f 

    // Design Constants (Neo-Brutalism)
    val borderThickness = 2.dp
    val shadowOffset = 4.dp
    val webLineThickness = 1.dp
    val activeLineThickness = 4.dp
    val inactiveDotRadius = 4.dp
    val activeDotRadius = 8.dp
    val finalDotRadius = 16.dp

    // Colors
    val blackColor = Color.Black
    val webColor = Color.LightGray.copy(alpha = 0.5f) // Keep web subtle but structure solid
    val inactiveDotColor = Color.White
    
    // 8 Basic Emotions Order
    val orderedEmotions = listOf(
        EmotionType.JOY, EmotionType.TRUST, EmotionType.FEAR, EmotionType.SURPRISE,
        EmotionType.SADNESS, EmotionType.DISGUST, EmotionType.ANGER, EmotionType.ANTICIPATION
    )

    // Fixing generic list based on standard EmotionType enum assumptions
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

    BoxWithConstraints(modifier = modifier.background(Color.White).padding(16.dp)) {
        val context = LocalContext.current
        val resources = context.resources
        val stringProvider = remember { AndroidEmotionStringProvider(context) }
        
        val basicEmotionsArray = resources.getStringArray(com.kminder.minder.R.array.basic_emotions)
        val primaryDyadsArray = resources.getStringArray(com.kminder.minder.R.array.primary_dyads)
        val secondaryDyadsArray = resources.getStringArray(com.kminder.minder.R.array.secondary_dyads)
        val tertiaryDyadsArray = resources.getStringArray(com.kminder.minder.R.array.tertiary_dyads)
        
        // 1. Coordinate Calculation (Pre-calc)
        val width = constraints.maxWidth.toFloat()
        val height = constraints.maxHeight.toFloat()
        
        val layout = remember(width, height) {
            val centerX = width / 2
            val centerY = height / 2
            val center = Offset(centerX, centerY)
            
            val availableRadius = minOf(width, height) / 2
            val maxRadius = availableRadius * 0.55f 
            
            // Scale: Inner=Weak, Outer=Strong
            val strongRadius = maxRadius * 0.95f // Outer
            val basicRadius = maxRadius * 0.65f  // Mid
            val weakRadius = maxRadius * 0.35f   // Inner
            
            val radii = ChartRadii(strongRadius, basicRadius, weakRadius, maxRadius)
            
            val angleStep = (2 * Math.PI) / 8
            
            val axes = List(8) { i ->
                val angle = -Math.PI / 2 + (angleStep * i)
                val c = cos(angle).toFloat()
                val s = sin(angle).toFloat()
                
                AxisData(
                    index = i,
                    angle = angle,
                    cos = c,
                    sin = s,
                    strongPos = Offset(centerX + strongRadius * c, centerY + strongRadius * s), // Outer
                    basicPos = Offset(centerX + basicRadius * c, centerY + basicRadius * s),   // Mid
                    weakPos = Offset(centerX + weakRadius * c, centerY + weakRadius * s),      // Inner
                    // 5-2. Revised Label Position: Center of Basic Category -> Basic Radius (Mid)
                    // Note: This puts text in the middle of the web.
                    labelPos = Offset(centerX + basicRadius * c, centerY + basicRadius * s)
                )
            }
            
            val primary = List(8) { i ->
                val angleStart = -Math.PI / 2 + (angleStep * i)
                val dAngle = angleStart + (angleStep / 2)
                val dx = centerX + basicRadius * cos(dAngle).toFloat()
                val dy = centerY + basicRadius * sin(dAngle).toFloat()
                DyadData(i, dAngle, Offset(dx, dy))
            }
            
            val secRadius = maxRadius * 1.25f
            val secondary = List(8) { i ->
               val currentAngle = -Math.PI / 2 + (angleStep * i)
               val sx = centerX + secRadius * cos(currentAngle).toFloat()
               val sy = centerY + secRadius * sin(currentAngle).toFloat()
               DyadData(i, currentAngle, Offset(sx, sy))
            }
            
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
            val center = layout.center
            val centerX = center.x
            val centerY = center.y
            val radii = layout.radii
            
            val intensityMap = analysis.toMap()
            
            // --- Analysis & State Determination ---
            var finalDotPos: Offset? = null
            var finalDotColor: Color = Color.Transparent
            var finalLabel = ""
            
            val activeBasicIndices = mutableSetOf<Int>()
            val activePrimaryIndex = mutableStateOf<Int?>(null)
            val activeSecondaryIndex = mutableStateOf<Int?>(null)
            val activeTertiaryIndex = mutableStateOf<Int?>(null)
            
            val pIdx = axesEmotions.indexOf(result.primaryEmotion)
            val sIdx = if (result.secondaryEmotion != null) axesEmotions.indexOf(result.secondaryEmotion!!) else -1
            
            if (pIdx != -1) {
                val pColor = getEmotionColor(result.primaryEmotion)
                
                when (result.category) {
                    ComplexEmotionType.Category.SINGLE_EMOTION -> {
                        activeBasicIndices.add(pIdx)
                        val score = intensityMap[result.primaryEmotion] ?: 0f
//                        score >= 0.8f -> ComplexEmotionType.Intensity.STRONG
//                        score <= 0.4f -> ComplexEmotionType.Intensity.WEAK
                        val r = if (score >= 0.8f) radii.strong else if (score <= 0.4f) radii.weak else radii.basic
                        val axis = layout.axes[pIdx]
                        finalDotPos = Offset(centerX + r * axis.cos, centerY + r * axis.sin)
                        finalDotColor = pColor
                        finalLabel = EmotionUiUtil.getLabel(result, stringProvider)
                    }
                    ComplexEmotionType.Category.PRIMARY_DYAD -> {
                        if (sIdx != -1) {
                            activeBasicIndices.add(pIdx); activeBasicIndices.add(sIdx)
                            val sColor = getEmotionColor(result.secondaryEmotion!!)
                            finalDotColor = Color((pColor.red+sColor.red)/2f, (pColor.green+sColor.green)/2f, (pColor.blue+sColor.blue)/2f, 1f)
                            
                            if ((pIdx + 1) % 8 == sIdx) activePrimaryIndex.value = pIdx
                            else if ((sIdx + 1) % 8 == pIdx) activePrimaryIndex.value = sIdx
                            
                            activePrimaryIndex.value?.let { 
                                finalDotPos = layout.primaryDyads[it].pos 
                                finalLabel = EmotionUiUtil.getLabel(result, stringProvider)
                            }
                        }
                    }
                    ComplexEmotionType.Category.SECONDARY_DYAD -> {
                         if (sIdx != -1) {
                            activeBasicIndices.add(pIdx); activeBasicIndices.add(sIdx)
                            val sColor = getEmotionColor(result.secondaryEmotion!!)
                            finalDotColor = Color((pColor.red+sColor.red)/2f, (pColor.green+sColor.green)/2f, (pColor.blue+sColor.blue)/2f, 1f)
                            
                            for (k in 0 until 8) {
                                val c1 = (k + 7) % 8
                                val c2 = (k + 1) % 8
                                if ((c1 == pIdx && c2 == sIdx) || (c1 == sIdx && c2 == pIdx)) activeSecondaryIndex.value = k
                            }
                            activeSecondaryIndex.value?.let { 
                                finalDotPos = layout.secondaryDyads[it].pos
                                finalLabel = EmotionUiUtil.getLabel(result, stringProvider)
                            }
                        }
                    }
                    ComplexEmotionType.Category.TERTIARY_DYAD -> {
                         if (sIdx != -1) {
                            activeBasicIndices.add(pIdx); activeBasicIndices.add(sIdx)
                            val sColor = getEmotionColor(result.secondaryEmotion!!)
                            finalDotColor = Color((pColor.red+sColor.red)/2f, (pColor.green+sColor.green)/2f, (pColor.blue+sColor.blue)/2f, 1f)
                             
                            for (k in 0 until 8) {
                                val c1 = (k + 7) % 8
                                val c2 = (k + 2) % 8
                                if ((c1 == pIdx && c2 == sIdx) || (c1 == sIdx && c2 == pIdx)) activeTertiaryIndex.value = k
                            }
                            activeTertiaryIndex.value?.let { 
                                finalDotPos = layout.tertiaryDyads[it].pos 
                                finalLabel = EmotionUiUtil.getLabel(result, stringProvider)
                            }
                        }
                    }
                    ComplexEmotionType.Category.OPPOSITE -> {
                        if (sIdx != -1) {
                            activeBasicIndices.add(pIdx); activeBasicIndices.add(sIdx)
                            val sColor = getEmotionColor(result.secondaryEmotion!!)
                            finalDotColor = Color((pColor.red+sColor.red)/2f, (pColor.green+sColor.green)/2f, (pColor.blue+sColor.blue)/2f, 1f)
                            finalDotPos = Offset(centerX, centerY)
                            finalLabel = EmotionUiUtil.getLabel(result, stringProvider)
                        }
                    }
                    else -> {}
                }
            }
            
            val glowRadius = 240.dp.toPx()
            if (finalDotPos != null) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(finalDotColor.copy(alpha = 0.4f), Color.Transparent),
                        center = finalDotPos!!,
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = finalDotPos!!
                )
            }
            
            // A. Draw Base Structure (Axes)
            axesEmotions.forEachIndexed { index, emotion ->
                val axis = layout.axes[index]
                drawLine(webColor, center, axis.strongPos, webLineThickness.toPx())
                
                // Draw all 3 intensity reference dots
                drawCircle(webColor, 2.dp.toPx(), axis.strongPos)
                drawCircle(webColor, 2.dp.toPx(), axis.basicPos)
                drawCircle(webColor, 2.dp.toPx(), axis.weakPos)
                
                // Label at Basic Pos (Center of Category)
                if (finalLabel != basicEmotionsArray.getOrElse(index){""} && index !in activeBasicIndices) {
                    val labelStyle = TextStyle(fontSize = 10.sp, color = Color.Black.copy(alpha=0.3f), fontWeight = FontWeight.Normal)
                    val labelText = basicEmotionsArray.getOrElse(index) { emotion.name }
                    val textLayout = textMeasurer.measure(labelText, labelStyle)
                    // Draw text exactly centered on labelPos (which is Basic Pos)
                    drawText(textLayout, topLeft = Offset(axis.labelPos.x - textLayout.size.width/2, axis.labelPos.y - textLayout.size.height/2))
                }
            }
            
            // Draw Inactive Structure Lines AND DOTS (New Requirement)
            for (i in 0 until 8) {
                if (i != activePrimaryIndex.value) {
                    val dyadData = layout.primaryDyads[i]
                    val idx1 = i; val idx2 = (i + 1) % 8
                    val pos1 = layout.axes[idx1].basicPos; val pos2 = layout.axes[idx2].basicPos
                    val path = Path().apply {
                         moveTo(pos1.x, pos1.y)
                         quadraticTo((pos1.x+dyadData.pos.x)/2, (pos1.y+dyadData.pos.y)/2, dyadData.pos.x, dyadData.pos.y)
                         moveTo(pos2.x, pos2.y)
                         quadraticTo((pos2.x+dyadData.pos.x)/2, (pos2.y+dyadData.pos.y)/2, dyadData.pos.x, dyadData.pos.y)
                    }
                    drawPath(path, webColor, style=Stroke(webLineThickness.toPx()))
                    // Draw Inactive Dot (Basic Style: Filled Gray)
                    drawCircle(webColor, inactiveDotRadius.toPx(), dyadData.pos)
                }
            }
            for (i in 0 until 8) {
                if (i != activeSecondaryIndex.value) {
                    val dyadData = layout.secondaryDyads[i]
                    val idx1 = (i + 7) % 8; val idx2 = (i + 1) % 8
                    val pos1 = layout.axes[idx1].strongPos; val pos2 = layout.axes[idx2].strongPos
                    val path = Path().apply {
                         moveTo(pos1.x, pos1.y)
                         val cpX = centerX + ((dyadData.pos.x+pos1.x)/2 - centerX)*1.1f
                         val cpY = centerY + ((dyadData.pos.y+pos1.y)/2 - centerY)*1.1f
                         quadraticTo(cpX, cpY, dyadData.pos.x, dyadData.pos.y)
                         moveTo(pos2.x, pos2.y)
                         val cpX2 = centerX + ((dyadData.pos.x+pos2.x)/2 - centerX)*1.1f
                         val cpY2 = centerY + ((dyadData.pos.y+pos2.y)/2 - centerY)*1.1f
                         quadraticTo(cpX2, cpY2, dyadData.pos.x, dyadData.pos.y)
                    }
                    drawPath(path, webColor, style=Stroke(webLineThickness.toPx()))
                    // Draw Inactive Dot (Basic Style: Filled Gray)
                    drawCircle(webColor, inactiveDotRadius.toPx(), dyadData.pos)
                }
            }
            for (i in 0 until 8) {
                if (i != activeTertiaryIndex.value) {
                    val dyadData = layout.tertiaryDyads[i]
                    val idx1 = (i + 7) % 8; val idx2 = (i + 2) % 8
                    val pos1 = layout.axes[idx1].strongPos; val pos2 = layout.axes[idx2].strongPos
                    val path = Path().apply {
                         moveTo(pos1.x, pos1.y)
                         val cpX = centerX + ((dyadData.pos.x+pos1.x)/2 - centerX)*1.1f
                         val cpY = centerY + ((dyadData.pos.y+pos1.y)/2 - centerY)*1.1f
                         quadraticTo(cpX, cpY, dyadData.pos.x, dyadData.pos.y)
                         moveTo(pos2.x, pos2.y)
                         val cpX2 = centerX + ((dyadData.pos.x+pos2.x)/2 - centerX)*1.1f
                         val cpY2 = centerY + ((dyadData.pos.y+pos2.y)/2 - centerY)*1.1f
                         quadraticTo(cpX2, cpY2, dyadData.pos.x, dyadData.pos.y)
                    }
                    drawPath(path, webColor, style=Stroke(webLineThickness.toPx()))
                    // Draw Inactive Dot (Basic Style: Filled Gray)
                    drawCircle(webColor, inactiveDotRadius.toPx(), dyadData.pos)
                }
            }

            // B. Active Trace Lines
            activeBasicIndices.forEach { idx ->
                val axis = layout.axes[idx]
                val color = getEmotionColor(axesEmotions[idx])
                
                if (result.category == ComplexEmotionType.Category.SINGLE_EMOTION) {
                    if (finalDotPos != null) drawLine(color, center, finalDotPos!!, activeLineThickness.toPx())
                } else if (result.category == ComplexEmotionType.Category.PRIMARY_DYAD) {
                    drawLine(color.copy(alpha=0.6f), center, axis.basicPos, activeLineThickness.toPx() * 0.5f)
                } else {
                    drawLine(color.copy(alpha=0.6f), center, axis.strongPos, activeLineThickness.toPx() * 0.5f)
                }
            }
            
            // C. Connect Basic -> Dyad (Trace)
            // Note: Since we moved Basic Labels to BasicPos, they might be obscured by these lines if not careful.
            val primaryIdx = activePrimaryIndex.value
            if (primaryIdx != null) {
                val dyadData = layout.primaryDyads[primaryIdx]
                val idx1 = primaryIdx
                val idx2 = (primaryIdx + 1) % 8
                val color1 = getEmotionColor(axesEmotions[idx1])
                val color2 = getEmotionColor(axesEmotions[idx2])
                
                val path1 = Path().apply {
                     moveTo(layout.axes[idx1].basicPos.x, layout.axes[idx1].basicPos.y)
                     val midX = (layout.axes[idx1].basicPos.x + dyadData.pos.x)/2
                     val midY = (layout.axes[idx1].basicPos.y + dyadData.pos.y)/2
                     quadraticTo(midX, midY, dyadData.pos.x, dyadData.pos.y) 
                }
                drawPath(path1, color1, style=Stroke(activeLineThickness.toPx() * 0.6f))
                
                 val path2 = Path().apply {
                     moveTo(layout.axes[idx2].basicPos.x, layout.axes[idx2].basicPos.y)
                     val midX = (layout.axes[idx2].basicPos.x + dyadData.pos.x)/2
                     val midY = (layout.axes[idx2].basicPos.y + dyadData.pos.y)/2
                     quadraticTo(midX, midY, dyadData.pos.x, dyadData.pos.y)
                }
                drawPath(path2, color2, style=Stroke(activeLineThickness.toPx() * 0.6f))
            }
            
            val secIdx = activeSecondaryIndex.value
            if (secIdx != null) {
                val dyadData = layout.secondaryDyads[secIdx]
                val idx1 = (secIdx + 7) % 8
                val idx2 = (secIdx + 1) % 8
                val color1 = getEmotionColor(axesEmotions[idx1])
                val color2 = getEmotionColor(axesEmotions[idx2])
                
                val pos1 = layout.axes[idx1].strongPos
                val pos2 = layout.axes[idx2].strongPos
                
                fun drawCurve(start: Offset, end: Offset, color: Color) {
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        val midX = (start.x + end.x)/2
                        val midY = (start.y + end.y)/2
                        val cpX = centerX + (midX - centerX) * 1.3f
                        val cpY = centerY + (midY - centerY) * 1.3f
                        quadraticTo(cpX, cpY, end.x, end.y)
                    }
                    drawPath(path, color, style=Stroke(activeLineThickness.toPx() * 0.6f))
                }
                drawCurve(pos1, dyadData.pos, color1)
                drawCurve(pos2, dyadData.pos, color2)
            }
            
            val tertIdx = activeTertiaryIndex.value
            if (tertIdx != null) {
                 val dyadData = layout.tertiaryDyads[tertIdx]
                 val idx1 = (tertIdx + 7) % 8
                 val idx2 = (tertIdx + 2) % 8
                 val color1 = getEmotionColor(axesEmotions[idx1])
                 val color2 = getEmotionColor(axesEmotions[idx2])
                 
                 val pos1 = layout.axes[idx1].strongPos 
                 val pos2 = layout.axes[idx2].strongPos 
                 
                 fun drawCurve(start: Offset, end: Offset, color: Color) {
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        val midX = (start.x + end.x)/2
                        val midY = (start.y + end.y)/2
                        val cpX = centerX + (midX - centerX) * 1.3f
                        val cpY = centerY + (midY - centerY) * 1.3f
                        quadraticTo(cpX, cpY, end.x, end.y)
                    }
                    drawPath(path, color, style=Stroke(activeLineThickness.toPx() * 0.6f))
                }
                drawCurve(pos1, dyadData.pos, color1)
                drawCurve(pos2, dyadData.pos, color2)
            }


            // 2-B. Raw Score Visualization (User Request)
            // Show all other emotions that have intensity > 0 but are NOT part of the main classification.
             axesEmotions.forEachIndexed { idx, emotion ->
                 if (idx !in activeBasicIndices) {
                     val rawScore = intensityMap[emotion] ?: 0f
                     if (rawScore > 0f) {
                         val r = radii.strong * rawScore
                         val axis = layout.axes[idx]
                         val pos = Offset(centerX + r * axis.cos, centerY + r * axis.sin)
                         val color = getEmotionColor(emotion)
                         
                         // Draw Line
                         drawLine(color.copy(alpha=0.5f), center, pos, activeLineThickness.toPx() * 0.3f)
                         
                         // Draw Dot
                         drawCircle(color = color.copy(alpha=0.8f), radius = activeDotRadius.toPx(), center = pos)
                     }
                 }
             }

            // 3. Dots
             activeBasicIndices.forEach { idx ->
                 val isPrimaryMode = result.category == ComplexEmotionType.Category.PRIMARY_DYAD
                 val pos = if (isPrimaryMode) layout.axes[idx].basicPos 
                           else layout.axes[idx].strongPos 
                 
                 if (result.category != ComplexEmotionType.Category.SINGLE_EMOTION) {
                      val color = getEmotionColor(axesEmotions[idx])
                      
                      // Neo-Brutalism Dot: Shadow + Border + Color
                      drawCircle(color = blackColor, radius = activeDotRadius.toPx(), center = Offset(pos.x + shadowOffset.toPx(), pos.y + shadowOffset.toPx())) // Shadow
                      drawCircle(color = color, radius = activeDotRadius.toPx(), center = pos) // Body (Fill)
                      drawCircle(color = blackColor, radius = activeDotRadius.toPx(), center = pos, style = Stroke(width = borderThickness.toPx())) // Border
                 }

                 // Reuse logic for Active Basic Label
                 val labelText = basicEmotionsArray.getOrElse(idx) { "" }
                 if (labelText.isNotEmpty()) {
                    val labelStyle = TextStyle(fontSize = 12.sp, color = blackColor, fontWeight = FontWeight.Bold)
                    val textLayout = textMeasurer.measure(labelText, labelStyle)
                    drawText(textLayout, topLeft = Offset(layout.axes[idx].labelPos.x - textLayout.size.width/2, layout.axes[idx].labelPos.y - textLayout.size.height/2))
                 }
             }

            // 3-1. Final Dot (Neo-Brutalism: Large + Shadow + Border)
            if (finalDotPos != null) {
                // Shadow
                drawCircle(color = blackColor, radius = finalDotRadius.toPx(), center = Offset(finalDotPos!!.x + shadowOffset.toPx(), finalDotPos!!.y + shadowOffset.toPx()))
                // Body
                drawCircle(color = finalDotColor, radius = finalDotRadius.toPx(), center = finalDotPos!!)
                // Eye (Center)
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = finalDotPos!!)
                // Border
                drawCircle(color = blackColor, radius = finalDotRadius.toPx(), center = finalDotPos!!, style = Stroke(width = borderThickness.toPx()))

                // Label
                val activeLabelStyle = TextStyle(fontSize = 16.sp, color = blackColor, fontWeight = FontWeight.Black) // Extra Bold -> Black for Brutalism
                val activeTextLayout = textMeasurer.measure(finalLabel, activeLabelStyle)
                // Draw Final Label with offset to avoid covering the dot
                drawText(activeTextLayout, topLeft = Offset(finalDotPos!!.x - activeTextLayout.size.width/2, finalDotPos!!.y + 18.dp.toPx()))
            }

             // Labels for Dyads (All drawn, faint)
             primaryDyadsArray.forEachIndexed { i, label ->
                 if (activePrimaryIndex.value != i) {
                     val pos = layout.primaryDyads[i].pos
                     val style = TextStyle(fontSize = 8.sp, color = Color.Black.copy(alpha=0.2f))
                     val layout = textMeasurer.measure(label, style)
                     drawText(layout, topLeft = Offset(pos.x - layout.size.width/2, pos.y + 4.dp.toPx()))
                 }
             }
             secondaryDyadsArray.forEachIndexed { i, label ->
                 if (activeSecondaryIndex.value != i) {
                     val pos = layout.secondaryDyads[i].pos
                     val style = TextStyle(fontSize = 8.sp, color = Color.Black.copy(alpha=0.2f))
                     val layout = textMeasurer.measure(label, style)
                     drawText(layout, topLeft = Offset(pos.x - layout.size.width/2, pos.y + 4.dp.toPx()))
                 }
             }
             tertiaryDyadsArray.forEachIndexed { i, label ->
                 if (activeTertiaryIndex.value != i) {
                     val pos = layout.tertiaryDyads[i].pos
                     val style = TextStyle(fontSize = 8.sp, color = Color.Black.copy(alpha=0.2f))
                     val layout = textMeasurer.measure(label, style)
                     drawText(layout, topLeft = Offset(pos.x - layout.size.width/2, pos.y + 4.dp.toPx()))
                 }
             }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFe5e4c8)
@Composable
fun PreviewConstellationChart() {
    val mockEntry = MockData.mockJournalEntries[4]
    mockEntry.emotionResult?.let { result ->
        ConstellationChart(result = result, modifier = Modifier.fillMaxSize())
    }
}

// Layout Classes
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
    val index: Int,
    val angle: Double,
    val pos: Offset
)
