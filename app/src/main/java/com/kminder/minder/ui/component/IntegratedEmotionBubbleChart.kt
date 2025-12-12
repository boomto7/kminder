package com.kminder.minder.ui.component

import android.graphics.PointF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionType
import com.kminder.minder.ui.theme.*
import com.kminder.minder.util.EmotionColorUtil
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 통합 감정 버블 차트 (Flower Cluster Style)
 *
 * - 변경사항:
 *   1. 바람에 휘날리는 효과 (Wind swaying)
 *   2. 터치/드래그 시 구심점 이동 (Drag pull intensity clamped)
 */
@Composable
fun IntegratedEmotionBubbleChart(
    emotionCounts: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val scope = rememberCoroutineScope()
    
    val bubbleNodes = remember(emotionCounts) {
        createAllEmotionNodes(emotionCounts)
    }

    // Entrance Animation
    val progress = remember { Animatable(0f) }
    LaunchedEffect(emotionCounts) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )
    }

    // --- 1. Wind Effect (Idle Animation) ---
    val infiniteTransition = rememberInfiniteTransition(label = "wind")
    // X축 흔들림: 조금 느리고 넓게
    val windX by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(3000), // 3초 주기
            repeatMode = RepeatMode.Reverse
        ), label = "windX"
    )
    // Y축 흔들림: 조금 빠르고 좁게 (리듬감 차이)
    val windY by infiniteTransition.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(2300), 
            repeatMode = RepeatMode.Reverse
        ), label = "windY"
    )
    
    // --- 2. Drag/Touch Interaction State ---
    // 구심점의 이동 오프셋 (드래그에 의해 변경됨)
    val dragOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    // 시각적 터치 포인트 (버블 확대를 위함)
    var touchPoint by remember { mutableStateOf<Offset?>(null) }

    // 드래그 최대 허용 거리 (픽셀 단위, 바람 효과와 비슷하게 미세한 움직임만 허용)
    val maxDragDistance = 15.dp

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 400.dp)
            .fillMaxHeight()
            .pointerInput(Unit) {
                // 통합 제스처: 터치/드래그로 구심점 이동 & 버블 확대
                val maxDistPx = maxDragDistance.toPx()
                
                awaitPointerEventScope {
                    // 구심점을 상단 35% 지점으로 이동 (하단 공간 확보)
                    val center = Offset(size.width / 2f, size.height * 0.35f) 
                    
                    while (true) {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        touchPoint = down.position
                        
                        // 터치 시작 시 구심점 당기기 (Pull)
                        val pullVector = (down.position - center)
                        val clampedPull = clampVector(pullVector, maxDistPx)
                        scope.launch { 
                            dragOffset.animateTo(clampedPull, spring()) 
                        }
                        
                        // 드래그 루프
                        do {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull()
                            if (change != null && change.pressed) {
                                touchPoint = change.position
                                // 드래그 위치로 구심점 업데이트 (Snap for responsiveness)
                                val dragVector = (change.position - center)
                                val clampedDrag = clampVector(dragVector, maxDistPx)
                                scope.launch { dragOffset.snapTo(clampedDrag) }
                            } else {
                                touchPoint = null
                            }
                        } while (change != null && change.pressed)
                        
                        // 터치 종료: 원위치 복귀
                        touchPoint = null
                        scope.launch { 
                            dragOffset.animateTo(Offset.Zero, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) 
                        }
                    }
                }
            }
            .padding(16.dp)
    ) {
        val centerX = size.width / 2
        // 구심점을 상단 35% 지점으로 이동
        val centerY = size.height * 0.35f
        
        // 최종 구심점 = 화면 중앙 + 바람(Idle) + 드래그(Interaction)
        val activeCenterX = centerX + windX + dragOffset.value.x
        val activeCenterY = centerY + windY + dragOffset.value.y
        
        // --- 3. Draw Stem (꽃줄기) ---
        // 줄기 끝이 움직이는 구심점을 따라가도록 수정
        val stemPath = Path().apply {
            moveTo(0f, size.height) // 좌측 하단 고정
            
            // 컨트롤 포인트도 구심점 이동에 따라 살짝 움직여주면 더 자연스러움
            // dragOffset의 절반 정도만 반영
            val swayX = (windX + dragOffset.value.x) * 0.5f
            val swayY = (windY + dragOffset.value.y) * 0.5f
            
            cubicTo(
                size.width * 0.15f + swayX * 0.2f, size.height * 0.7f, 
                centerX * 0.5f + swayX, centerY * 1.2f + swayY,        
                activeCenterX, activeCenterY // End at Active Center
            )
        }

        drawPath(
            path = stemPath,
            color = Color(0xFF81C784).copy(alpha = 0.4f),
            style = Stroke(width = 4.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        
        // --- 4. Bubble Layout ---
        val sortedNodes = bubbleNodes.sortedByDescending { it.count }
        val maxCount = sortedNodes.maxOfOrNull { it.count }?.takeIf { it > 0 } ?: 1
        
        val baseRadius = 6.dp.toPx()
        val activeScale = 32.dp.toPx()
        
        sortedNodes.forEachIndexed { index, node ->
            // Size
            val rawRadius = if (node.count > 0) {
                baseRadius + (activeScale * sqrt(node.count.toFloat() / maxCount))
            } else {
                baseRadius
            }
            
            // Position (Spiral around Active Center)
            val spreadFactorStart = 35.dp.toPx()
            val spreadFactorEnd = 12.dp.toPx()
            val indexRatio = index.toFloat() / sortedNodes.size
            val currentSpread = spreadFactorStart * (1f - indexRatio) + spreadFactorEnd * indexRatio
            
            val angle = index * 137.508f * (PI / 180f)
            val dist = currentSpread * sqrt(index.toFloat())
            
            // Relative to Active Center
            val nodeX = activeCenterX + dist * cos(angle).toFloat()
            val nodeY = activeCenterY + dist * sin(angle).toFloat()
            
            // Interaction Scaling (based on touch point relative to node)
            var scale = 1f
            val tPoint = touchPoint
            if (tPoint != null) {
                val d = sqrt((nodeX - tPoint.x).pow(2) + (nodeY - tPoint.y).pow(2))
                val effectRadius = 150.dp.toPx()
                if (d < effectRadius) {
                    val influence = (1f - d / effectRadius).coerceIn(0f, 1f)
                    scale = 1f + (0.8f * influence)
                }
            }
            
            val finalRadius = rawRadius * scale * progress.value

            // Draw
            val color = if (node.count > 0) node.color else Color.LightGray.copy(alpha = 0.4f)
            val alpha = if (node.count > 0) 0.9f else 0.2f 
            
            // Active Glow
            if (node.count > 0) {
                 drawCircle(
                    color = color.copy(alpha = 0.3f),
                    radius = finalRadius * 1.3f,
                    center = Offset(nodeX, nodeY)
                )
            }

            // Body
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = finalRadius,
                center = Offset(nodeX, nodeY)
            )
            
            // Label
            if (node.count > 0 && finalRadius > 10.dp.toPx() && progress.value > 0.8f) {
                val label = if (node.name.length > 2) node.name.take(2) else node.name
                val measure = textMeasurer.measure(
                    text = label,
                    style = TextStyle(fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                )
                drawText(
                    textLayoutResult = measure,
                    topLeft = Offset(nodeX - measure.size.width/2, nodeY - measure.size.height/2)
                )
            }
        }
    }
}

// Helper to clamp vector magnitude
private fun clampVector(vector: Offset, maxLength: Float): Offset {
    val length = vector.getDistance()
    return if (length > maxLength) {
        vector * (maxLength / length)
    } else {
        vector
    }
}

// -----------------------------------------------------------------------------
// Data Structures & Helpers
// -----------------------------------------------------------------------------

data class BubbleNode(
    val id: String,
    val name: String,
    val color: Color,
    val count: Int
)

private fun createAllEmotionNodes(counts: Map<String, Int>): List<BubbleNode> {
    val nodes = mutableListOf<BubbleNode>()
    
    // 1. Basic 8 Emotions
    EmotionType.entries.forEach { type ->
        val koreanName = EmotionColorUtil.getKoreanName(type)
        nodes.add(BubbleNode(
            id = type.name, 
            name = koreanName,
            color = EmotionColorUtil.getEmotionColor(type),
            count = counts[koreanName] ?: counts[type.name] ?: 0
        ))
    }
    
    // 2. Complex 24 Emotions
    ComplexEmotionType.entries.forEach { complex ->
        val color1 = EmotionColorUtil.getEmotionColor(complex.composition.first)
        val color2 = EmotionColorUtil.getEmotionColor(complex.composition.second)
        val blended = EmotionColorUtil.blendColors(color1, color2, 0.5f)
        val simpleName = complex.title.substringBefore(" (")
        
        nodes.add(BubbleNode(
            id = complex.name,
            name = simpleName,
            color = blended,
            count = counts[simpleName] ?: counts[complex.name] ?: 0
        ))
    }
    
    // 3. Conflict 4 Emotions
    val conflicts = listOf(
        Pair(EmotionType.JOY, EmotionType.SADNESS),
        Pair(EmotionType.TRUST, EmotionType.DISGUST),
        Pair(EmotionType.FEAR, EmotionType.ANGER),
        Pair(EmotionType.SURPRISE, EmotionType.ANTICIPATION)
    )
    
    conflicts.forEach { (t1, t2) ->
        val name1 = EmotionColorUtil.getKoreanName(t1)
        val name2 = EmotionColorUtil.getKoreanName(t2)
        val conflictName = "${name1}/${name2}"
        val keyName = "${name1}과(와) ${name2}의 충돌" 
        
        val color = EmotionColorUtil.blendColors(EmotionColorUtil.getEmotionColor(t1), EmotionColorUtil.getEmotionColor(t2), 0.5f)
        val count = counts[conflictName] ?: counts[keyName] ?: 0
        
        nodes.add(BubbleNode(
            id = "CONFLICT_${t1.name}_${t2.name}",
            name = "충돌",
            color = color,
            count = count
        ))
    }
    return nodes
}

@Preview(showBackground = true)
@Composable
fun PreviewIntegratedEmotionChart() {
    val mockCounts = mapOf(
        // Basic Emotions
        "기쁨" to 3,
        "신뢰" to 2,
        "기대" to 2,
        "분노" to 1,
        "슬픔" to 1,
        "두려움" to 1,
        
        // Complex Emotions (Dyads)
        "사랑" to 30,      // 기쁨+신뢰
        "낙관" to 25,      // 기대+기쁨
        "공격성" to 20,    // 분노+기대
        "자부심" to 18,    // 분노+기쁨
        "호기심" to 15,    // 신뢰+놀람
        "수용" to 12,      // 신뢰+두려움
        "불안" to 10,      // 기대+두려움
        "죄책감" to 8,     // 기쁨+두려움
        "냉소" to 7,       // 혐오+기대
        "후회" to 5,       // 슬픔+혐오
        
        // Conflicts
        "기쁨/슬픔" to 6,
        "신뢰/혐오" to 4
    )
    
    MinderTheme {
        Box(modifier = Modifier
            .padding(16.dp)) {
            IntegratedEmotionBubbleChart(emotionCounts = mockCounts)
        }
    }
}
