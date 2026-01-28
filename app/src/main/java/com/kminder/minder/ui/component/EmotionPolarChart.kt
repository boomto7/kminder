package com.kminder.minder.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.minder.R
import com.kminder.minder.ui.theme.EmotionAnger
import com.kminder.minder.ui.theme.EmotionAnticipation
import com.kminder.minder.ui.theme.EmotionDisgust
import com.kminder.minder.ui.theme.EmotionFear
import com.kminder.minder.ui.theme.EmotionJoy
import com.kminder.minder.ui.theme.EmotionSadness
import com.kminder.minder.ui.theme.EmotionSurprise
import com.kminder.minder.ui.theme.EmotionTrust
import com.kminder.minder.ui.theme.MinderTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 감정의 강도를 Polar Area Chart (Rose Chart) 형태로 시각화하는 컴포넌트
 * 각 쐐기(slice)의 반지름이 해당 감정의 강도를 나타냅니다.
 */
@Composable
fun EmotionPolarChart(
    emotions: EmotionAnalysis,
    modifier: Modifier = Modifier,
    animationDurationMillis: Int = 1000
) {
    // 순서: Plutchik Wheel Order
    val emotionList = listOf(
        Triple(emotions.joy, EmotionJoy, stringResource(R.string.emotion_joy)),
        Triple(emotions.trust, EmotionTrust, stringResource(R.string.emotion_trust)),
        Triple(emotions.fear, EmotionFear, stringResource(R.string.emotion_fear)),
        Triple(emotions.surprise, EmotionSurprise, stringResource(R.string.emotion_surprise)),
        Triple(emotions.sadness, EmotionSadness, stringResource(R.string.emotion_sadness)),
        Triple(emotions.disgust, EmotionDisgust, stringResource(R.string.emotion_disgust)),
        Triple(emotions.anger, EmotionAnger, stringResource(R.string.emotion_anger)),
        Triple(emotions.anticipation, EmotionAnticipation, stringResource(R.string.emotion_anticipation))
    )

    val gapDp = 6.dp // Gap increased
    
    val textMeasurer = rememberTextMeasurer()
    val textColor = androidx.compose.ui.graphics.Color.Black
    val boldTextStyle = TextStyle(
        fontSize = 11.sp, // Slightly larger font
        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
        color = textColor
    )
    
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = minOf(centerX, centerY)
        val innerRadius = maxRadius * 0.2f // 20% inner hole (Donut style)
        
        val sliceCount = emotionList.size
        // 360 degrees divided by count, minus gap degrees
        // Calculating gap in degrees based on inner circumference roughly
        // Circumference at innerRadius = 2 * PI * innerRadius
        // Gap width / Circumference * 360
        val gapDegrees = (gapDp.toPx() / (2 * PI * innerRadius)) * 360f
        val availableSweep = 360f / sliceCount
        val sweepAngle = availableSweep - gapDegrees.toFloat().coerceAtLeast(1f)
        val startAngleOffset = gapDegrees.toFloat() / 2f
        
        val cornerRadius = 8.dp.toPx() // Rounded corners

        // Offscreen Layer (Optional, kept if needed for complex blending, but standard drawing is fine here)
        // drawContext.canvas.saveLayer(...) // Removing for simpler solid drawing unless needed

        // 1. Draw Slices
        emotionList.forEachIndexed { index, (intensity, color, category) ->
            // Center angle for this slice
            val centerAngle = -90f + (index * availableSweep)
            val startAngle = centerAngle - (sweepAngle / 2f)

            // 0. Background Wedge
            // Extend to maxRadius (full intensity)
            val bgThickness = maxRadius - innerRadius
            val bgCornerRadius = minOf(cornerRadius, bgThickness / 2f)
            
            val bgPath = androidx.compose.ui.graphics.Path().apply {
                addRoundedAnnularSector(
                    center = Offset(centerX, centerY),
                    innerRadius = innerRadius,
                    outerRadius = maxRadius,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    cornerRadius = bgCornerRadius
                )
            }
            // Light gray or faint color of the emotion
            // User requested Gray background ("기존의 회색을 유지").
            drawPath(path = bgPath, color = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.1f))

            if (intensity > 0.01f) {
                // Calculate outer radius based on intensity, strictly between inner and max
                // Map intensity 0..1 to inner..max
                val outerRadius = innerRadius + (maxRadius - innerRadius) * intensity.coerceIn(0.1f, 1f)
                
                // Adjust corner radius to not exceed thickness/2
                val thickness = outerRadius - innerRadius
                val adjustedCornerRadius = minOf(cornerRadius, thickness / 2f)

                val dataPath = androidx.compose.ui.graphics.Path().apply {
                    addRoundedAnnularSector(
                        center = Offset(centerX, centerY),
                        innerRadius = innerRadius,
                        outerRadius = outerRadius,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        cornerRadius = adjustedCornerRadius
                    )
                }
                
                // 1. Fill (Solid Color)
                drawPath(path = dataPath, color = color)
                
                // 2. Border (Bold Black Stroke)
                drawPath(path = dataPath, color = androidx.compose.ui.graphics.Color.Black, style = Stroke(width = 2.dp.toPx()))
            }

            // Category Label Position
            // Place label slightly outside the max radius or at a fixed functional radius
            val labelRadius = maxRadius * 0.85f 
            val angleRadians = Math.toRadians(centerAngle.toDouble())
            
            val textX = centerX + labelRadius * cos(angleRadians).toFloat()
            val textY = centerY + labelRadius * sin(angleRadians).toFloat()
            
            val textLayoutResult = textMeasurer.measure(
                text = category,
                style = boldTextStyle
            )

            drawText(
                textMeasurer = textMeasurer,
                text = category,
                topLeft = Offset(
                    x = textX - textLayoutResult.size.width / 2f,
                    y = textY - textLayoutResult.size.height / 2f
                ),
                style = boldTextStyle
            )
        }
    }
}

/**
 * Adds a rounded annular sector (donut slice) to the path.
 * 
 * @param center The center of the circle.
 * @param innerRadius The radius of the inner arc.
 * @param outerRadius The radius of the outer arc.
 * @param startAngle The starting angle in degrees.
 * @param sweepAngle The sweep angle in degrees.
 * @param cornerRadius The radius of the rounded corners.
 */
fun androidx.compose.ui.graphics.Path.addRoundedAnnularSector(
    center: Offset,
    innerRadius: Float,
    outerRadius: Float,
    startAngle: Float,
    sweepAngle: Float,
    cornerRadius: Float
) {
    // Determine actual corners.
    // We need 4 corners: InnerStart, OuterStart, OuterEnd, InnerEnd.
    // But we use arcTo for the main edges.
    
    // Convert angles to radians
    val startRad = Math.toRadians(startAngle.toDouble())
    val endRad = Math.toRadians((startAngle + sweepAngle).toDouble())
    
    // We use a simplified strategy utilizing `arcTo` and `quadraticTo(x1, y1, x2, y2)simple lines.
    // Since calculating exact tangent points for rounded corners on a polar sector is complex,
    // we will essentially trace the shape:
    // 1. Inner End -> Inner Start (CCW)? No, usually paths are one direction. Let's go Clockwise (CW) or CCW.
    // Compose arcTo sweeps CW if sweepAngle is positive.
    
    // Structure:
    // 1. Move to Inner Start (adjusted for corner)
    // 2. Line to Outer Start (adjusted)
    // 3. Arc Outer (adjusted)
    // 4. Line to Inner End (adjusted)
    // 5. Arc Inner (adjusted) - wait, inner arc is reverse?
    
    // Much easier to think in terms of 4 arcs (2 main, 2 corners) and 2 lines? 
    // Or 4 rounded corners connected by lines/arcs.
    
    // Let's use a simpler approximation that looks good visually.
    // Inner/Outer arcs are main.
    // Side lines are radial.
    
    // Helper to get point
    fun getPoint(radius: Float, angleDeg: Float): Offset {
        val rad = Math.toRadians(angleDeg.toDouble())
        return Offset(
            center.x + radius * cos(rad).toFloat(),
            center.y + radius * sin(rad).toFloat()
        )
    }

    // angular adjustment for corners at given radius
    // s = r * theta -> theta = s / r
    fun angleShift(radius: Float) = Math.toDegrees((cornerRadius / radius).toDouble()).toFloat()
    
    val outerAngleShift = angleShift(outerRadius)
    val innerAngleShift = angleShift(innerRadius)
    
    // Ensure sweep angle is large enough to check fit
    if (sweepAngle < maxOf(outerAngleShift, innerAngleShift) * 2) {
        // Fallback to simple sector if too small
        // Just draw simple block
        val p1 = getPoint(innerRadius, startAngle)
        val p2 = getPoint(outerRadius, startAngle)
        val p3 = getPoint(outerRadius, startAngle + sweepAngle)
        val p4 = getPoint(innerRadius, startAngle + sweepAngle)
        moveTo(p1.x, p1.y)
        lineTo(p2.x, p2.y)
        arcTo(androidx.compose.ui.geometry.Rect(center, outerRadius), startAngle, sweepAngle, false)
        lineTo(p4.x, p4.y)
        arcTo(androidx.compose.ui.geometry.Rect(center, innerRadius), startAngle + sweepAngle, -sweepAngle, false)
        close()
        return
    }

    // 1. Inner Start Corner
    // Start slightly after startAngle on Inner Radius
    val innerStartAngle = startAngle + innerAngleShift
    val innerStart = getPoint(innerRadius, innerStartAngle)
    
    // 2. Outer Start Corner
    val outerStartAngle = startAngle + outerAngleShift
    val outerStart = getPoint(outerRadius, outerStartAngle)
    
    // 3. Outer End Corner
    val outerEndAngle = startAngle + sweepAngle - outerAngleShift
    // val outerEnd = getPoint(outerRadius, outerEndAngle) // Not directly needed for arcTo
    
    // 4. Inner End Corner
    val innerEndAngle = startAngle + sweepAngle - innerAngleShift
    val innerEnd = getPoint(innerRadius, innerEndAngle)

    // Start drawing
    
    // Move to Inner Start (after corner)
    moveTo(innerStart.x, innerStart.y)
    
    // Line to Outer Start (before corner)?
    // Actually, we want to draw the rounded corner first?
    // Let's start at Inner Start Main Arc end point (closest to startAngle side)
    // No, let's start at Inner-Start Corner, go radial out.
    
    // Start at Inner Start Point (on ring)
    // Draw Corner: Curve from Inner Ring to Left Line
    // Left Line: Radial line from Inner to Outer
    // Outer Start Corner: Curve from Left Line to Outer Ring
    // Outer Ring: Arc
    // ...
    
    // Let's refine vertices for radial lines
    // Left Line segment: from (Inner+Corner, Start) to (Outer-Corner, Start)
    // But 'Start' angle is fixed.
    
    // Corner centers?
    // Standard approach: Inset the shape by radius r.
    // The "Skeleton" shape is the sector from inner+r to outer-r, angle start+ang to end-ang.
    // Then stroke with width 2r? No.
    
    // Direct construction using quadTo for corners is robust enough for UI.
    
    // --- Inner Start CORNER ---
    // Anchor: (innerRadius, startAngle)
    // Start point of corner: (innerRadius, startAngle + shift) -> innerStart
    // Control point: (innerRadius, startAngle) ? No, that's inside the hole.
    // Tangent intersection is at (innerRadius, startAngle) and (innerRadius, startAngle).
    // Actually intersection of Inner Arc tangent and Radial Line.
    // Using (innerRadius, startAngle) as control point works for small arcs.
    
    // Wait, simpler:
    // 1. Arc (Inner): from InnerEndAngle down to InnerStartAngle (Reverse direction)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(center, innerRadius),
        startAngleDegrees = innerEndAngle,
        sweepAngleDegrees = -(innerEndAngle - innerStartAngle), // Negative sweep
        forceMoveTo = true // Start here
    )
    
    // 2. Corner (Inner -> Start Line)
    // From current (InnerStart) to a point on the Start Radial Line.
    // Target on radial line: radius = innerRadius + cornerRadius
    val startLineP1 = getPoint(innerRadius + cornerRadius, startAngle)
    val innerCornerControl = getPoint(innerRadius, startAngle) // Not quite right but close
    // Better control point for "Inner Start" corner: 
    // It's the intersection of the tangent at InnerStart and the Radial Line.
    // Tangent at InnerStart is roughly perpendicular to radius.
    quadraticTo(innerCornerControl.x, innerCornerControl.y, startLineP1.x, startLineP1.y)
    
    // 3. Line (Start Line)
    // Go up to outer radius - cornerRadius
    val startLineP2 = getPoint(outerRadius - cornerRadius, startAngle)
    lineTo(startLineP2.x, startLineP2.y)
    
    // 4. Corner (Start Line -> Outer)
    // Control point: (outerRadius, startAngle)
    // Target: (outerRadius, startAngle + shift) -> outerStart
    val outerCornerControl = getPoint(outerRadius, startAngle)
    quadraticTo(outerCornerControl.x, outerCornerControl.y, outerStart.x, outerStart.y)
    
    // 5. Arc (Outer)
    arcTo(
        rect = androidx.compose.ui.geometry.Rect(center, outerRadius),
        startAngleDegrees = outerStartAngle,
        sweepAngleDegrees = outerEndAngle - outerStartAngle,
        forceMoveTo = false
    )
    
    // 6. Corner (Outer -> End Line)
    // Control: (outerRadius, endAngle)
    // Target: (outerRadius - cornerRadius, endAngle)
    val outerEndControl = getPoint(outerRadius, startAngle + sweepAngle)
    val endLineP1 = getPoint(outerRadius - cornerRadius, startAngle + sweepAngle)
    quadraticTo(outerEndControl.x, outerEndControl.y, endLineP1.x, endLineP1.y)
    
    // 7. Line (End Line)
    val endLineP2 = getPoint(innerRadius + cornerRadius, startAngle + sweepAngle)
    lineTo(endLineP2.x, endLineP2.y)
    
    // 8. Corner (End Line -> Inner)
    // Control: (innerRadius, endAngle)
    // Target: innerEnd (where we started roughly)
    val innerEndControl = getPoint(innerRadius, startAngle + sweepAngle)
    // We need to connect to the start of the Inner Arc (which was the first MoveTo / ArcTo start)
    // The very first point was `innerEnd` (actually the start of the reversed arc).
    // So we just curve to that.
    quadraticTo(innerEndControl.x, innerEndControl.y, innerEnd.x, innerEnd.y)
    
    close()
}

@Preview(showBackground = true)
@Composable
fun PreviewEmotionPolarChart() {
    val mockAnalysis = EmotionAnalysis(
        joy = 0.8f,
        trust = 0.5f,
        fear = 0.2f,
        surprise = 0.4f,
        sadness = 0.3f,
        disgust = 0.1f,
        anger = 0.6f,
        anticipation = 0.7f
    )

    MinderTheme() {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
            EmotionPolarChart(emotions = mockAnalysis)
        }
    }
}
