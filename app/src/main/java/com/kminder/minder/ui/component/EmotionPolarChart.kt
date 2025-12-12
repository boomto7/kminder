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

    val progress = remember { Animatable(0f) }

    LaunchedEffect(emotions) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDurationMillis, easing = FastOutSlowInEasing)
        )
    }

    val gapDp = 2.dp

    val textMeasurer = rememberTextMeasurer()
    val textColor = MaterialTheme.colorScheme.onPrimary
    
    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = minOf(centerX, centerY)
        
        val sliceCount = emotionList.size
        val sliceAngle = 360f / sliceCount
        val cornerRadius = 12.dp.toPx()

        // Offscreen Layer 생성 (BlendMode.Clear 적용을 위해)
        drawContext.canvas.saveLayer(
            androidx.compose.ui.geometry.Rect(0f, 0f, size.width, size.height),
            androidx.compose.ui.graphics.Paint()
        )

        // 1. 모든 슬라이스 (배경 + 데이터) 그리기
        emotionList.forEachIndexed { index, (intensity, color, category) ->
            val startAngle = -90f + (index * sliceAngle)

            // 배경
            val bgPath = androidx.compose.ui.graphics.Path().apply {
                addRoundedWedge(
                    center = Offset(centerX, centerY),
                    radius = maxRadius,
                    startAngle = startAngle,
                    sweepAngle = sliceAngle,
                    cornerRadius = cornerRadius
                )
            }
            drawPath(path = bgPath, color = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.1f))

            // 데이터
            val animatedIntensity = intensity * progress.value
            if (animatedIntensity > 0.01f) {
                val dataRadius = maxRadius * animatedIntensity.coerceIn(0.1f, 1f)
                val adjustedCornerRadius = minOf(cornerRadius, dataRadius / 2)

                val dataPath = androidx.compose.ui.graphics.Path().apply {
                    addRoundedWedge(
                        center = Offset(centerX, centerY),
                        radius = dataRadius,
                        startAngle = startAngle,
                        sweepAngle = sliceAngle,
                        cornerRadius = adjustedCornerRadius
                    )
                }
                drawPath(path = dataPath, color = color.copy(alpha = 0.8f))
            }

            // category
            val textRadius = maxRadius * 0.7f
            // 텍스트의 중심 각도 (시작점 + 절반 각도)
            val angleDegrees = startAngle + sliceAngle / 2f
            // Compose의 삼각함수는 라디안(Radian) 값을 받기 때문에 변환 필요
            val angleRadians = angleDegrees * PI.toFloat() / 180f
            // 극좌표계를 직교 좌표계(X, Y)로 변환
            val textX = center.x + textRadius * cos(angleRadians)
            val textY = center.y + textRadius * sin(angleRadians)
            val textLayoutResult = textMeasurer.measure(
                text = category,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                )
            )

            drawText(
                textMeasurer = textMeasurer,
                text = category,
                topLeft = Offset(
                    x = textX - textLayoutResult.size.width / 2f,
                    y = textY - textLayoutResult.size.height / 2f
                ),
                style = TextStyle(
                    fontSize = 8.sp,
                    color = textColor
                )
            )

        }

        // 2. 갭/간격 마스킹 (BlendMode.Clear)
        // 슬라이스 경계마다 선을 그려서 지움 (Constant Gap 효과)
        val gapPx = gapDp.toPx()
        for (i in 0 until sliceCount) {
            val angleDeg = -90f + (i * sliceAngle)
            val angleRad = Math.toRadians(angleDeg.toDouble())
            
            // 중심에서 외부로 뻗는 선
            // 넉넉하게 maxRadius보다 길게 그림
            val lineEnd = Offset(
                centerX + (maxRadius * 1.5f) * cos(angleRad).toFloat(),
                centerY + (maxRadius * 1.5f) * sin(angleRad).toFloat()
            )
            
            drawLine(
                color = androidx.compose.ui.graphics.Color.Black, // 색상은 상관없음 (Clear)
                start = Offset(centerX, centerY),
                end = lineEnd,
                strokeWidth = gapPx,
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
            )
        }

        drawContext.canvas.restore()
    }
}

/**
 * 모서리가 둥근 부채꼴(Wedge) 경로를 생성하는 확장 함수
 */
fun androidx.compose.ui.graphics.Path.addRoundedWedge(
    center: Offset,
    radius: Float,
    startAngle: Float,
    sweepAngle: Float,
    cornerRadius: Float
) {
    // 1. 중심에서 시작
    moveTo(center.x, center.y)

    // 각도를 라디안으로 변환
    val startRad = Math.toRadians(startAngle.toDouble())
    val endRad = Math.toRadians((startAngle + sweepAngle).toDouble())
    val sweepRad = Math.toRadians(sweepAngle.toDouble())

    // 2. 부채꼴의 꼭지점 계산 (직선으로 갔을 때)
    // 하지만 둥근 모서리를 위해 안쪽으로 들어온 점들을 계산해야 함.
    // 간단한 근사: 반지름을 CornerRadius만큼 줄인 호를 그리고, 양 끝에서 원을 그림?
    // 더 나은 방법: 아크의 양 끝점에 원을 배치하여 연결.
    
    // 단순화된 로직: 
    // 중심 -> (radius - corner) 까지 직선
    // 끝에서 corner radius 만큼의 라운드 처리 (베지어 혹은 아크)
    // 외곽 호 그리기
    // 다시 들어오는 라운드 처리
    // 중점으로 복귀
    
    // 외곽 아크를 그리기 위한 사각형
    // cornerRadius 만큼 안쪽으로 들어간 반지름에서 아크를 그리고, 끝부분을 라운드 처리하는 방식은 복잡.
    // "Rounded Polygon" 방식:
    // 다행히 Compose Path에는 arcTo가 있음.
    
    // 여기서는 visual hack을 사용:
    // 1. 중심에서 시작
    // 2. 왼쪽 변을 따라 나가되, 끝부분(R-r)까지만 감.
    // 3. 외곽 왼쪽 모서리 둥글게 (arcTo)
    // 4. 외곽 호 (arcTo) - 원래 반지름 R 유지
    // 5. 외곽 오른쪽 모서리 둥글게 (arcTo)
    // 6. 오른쪽 변을 따라 들어옴.
    
    // 좌표 계산
    // 왼쪽 변 벡터: (cos(start), sin(start))
    // 오른쪽 변 벡터: (cos(end), sin(end))
    
    // 왼쪽 선분 끝점 (라운드 시작 전)
    val lineEndRadius = radius - cornerRadius
    
    // A: 왼쪽 직선 끝점
    val p1X = center.x + lineEndRadius * cos(startRad).toFloat()
    val p1Y = center.y + lineEndRadius * sin(startRad).toFloat()
    
    lineTo(p1X, p1Y)
    
    // B: 외곽 호를 그리기. 
    // 모서리를 둥글게 하기 위해선 quadraticBezierTo 등을 쓰거나 arc를 잘 써야 함.
    // 쉬운 방법: LineTo -> Arc(corner) -> Arc(main) -> Arc(corner) -> LineTo(Zero)
    
    // Corner Arc 1 (Left Outer)
    // Control Point는 (R, startAngle) 지점
    // Target Point는 외곽 호의 시작점.
    // 그냥 cornerRadius 반지름의 원을 그리는 것으로 근사.
    
    // 완벽한 수학적 구현보다는 "부드러운 느낌"을 위해 
    // arcToPoint 등을 활용하기보다, 
    // arcTo(rect, start, sweep)을 사용하여 3개의 호를 연결.
    
    // 1. 왼쪽 코너 아크
    // 이 아크의 중심은? (R-r) 위치에서 각도 방향으로? 
    // 복잡하므로, 단순하게:
    // 그냥 큰 호를 그리고 StrokeJoin.Round/StrokeCap.Round를 쓰는게 나을 수도 있지만 fill이 문제.
    
    // 직접 구현:
    // 외곽 호의 시작 각도와 끝 각도를 cornerRadius에 해당하는 각도만큼 줄임.
    // 호의 길이 L = r * theta -> theta = L / r
    // theta_offset = cornerRadius / radius
    val angleOffsetRad = (cornerRadius / radius).toDouble()
    val angleOffsetDeg = Math.toDegrees(angleOffsetRad).toFloat()
    
    if (sweepAngle < 2 * angleOffsetDeg) {
        // 너무 좁아서 라운드 처리가 힘들면 그냥 삼각형/직선 취급하거나 작은 아크
        // Fallback to simple arc
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                center = center, radius = radius
            ),
            startAngleDegrees = startAngle,
            sweepAngleDegrees = sweepAngle,
            forceMoveTo = false
        )
    } else {
        // 왼쪽 코너: (R-r) 지점이 아니라, 그냥 외곽 호의 양 끝을 부드럽게.
        // 이것을 path.arcTo로 하기엔 중심점 잡기가 애매함.
        // 큐빅 베지어로 코너 처리.
        
        // P_outer_start: (R, startAngle)
        // P_outer_end: (R, endAngle)
        
        // P_inner_start: (R-r, startAngle)
        // P_inner_end: (R-r, endAngle)
        
        // Path:
        // 1. Move Center
        // 2. Line to P_inner_start
        // 3. Quadratic to P_outer_start -> then to (R, start + offset) ??
        // Better: simple arcTo logic is hard.
        
        // **Best Approximation**:
        // Standard shape with `close()` and `CornerPathEffect`? Compose `Paint` supports `pathEffect`.
        // But `drawPath` takes simple params.
        
        // Manual construction:
        val r = radius
        val cr = cornerRadius
        
        // Outer arc boundaries
        val effectiveStartAngle = startAngle + angleOffsetDeg
        val effectiveSweepAngle = sweepAngle - (2 * angleOffsetDeg)
        
        // Left Corner
        // We want a curve from (R-cr, startAngle) to (R, startAngle + offset)
        // Control point can be (R, startAngle)
        val outerLeftTip = Offset(center.x + r * cos(startRad).toFloat(), center.y + r * sin(startRad).toFloat())
        val arcStart = Offset(
            center.x + r * cos(Math.toRadians((startAngle + angleOffsetDeg).toDouble())).toFloat(), 
            center.y + r * sin(Math.toRadians((startAngle + angleOffsetDeg).toDouble())).toFloat()
        )
        
        quadraticBezierTo(outerLeftTip.x, outerLeftTip.y, arcStart.x, arcStart.y)
        
        // Main Outer Arc
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(center = center, radius = r),
            startAngleDegrees = effectiveStartAngle,
            sweepAngleDegrees = effectiveSweepAngle,
            forceMoveTo = false
        )
        
        // Right Corner
        // Curve from (R, endAngle - offset) to (R-cr, endAngle)
        // Control point (R, endAngle)
        val arcEnd = Offset(
            center.x + r * cos(Math.toRadians((startAngle + sweepAngle - angleOffsetDeg).toDouble())).toFloat(),
            center.y + r * sin(Math.toRadians((startAngle + sweepAngle - angleOffsetDeg).toDouble())).toFloat()
        )
        val outerRightTip = Offset(
            center.x + r * cos(endRad).toFloat(),
            center.y + r * sin(endRad).toFloat()
        )
        val innerRight = Offset(
            center.x + (r - cr) * cos(endRad).toFloat(),
            center.y + (r - cr) * sin(endRad).toFloat()
        )
        
        // We are already at arcEnd.
        quadraticBezierTo(outerRightTip.x, outerRightTip.y, innerRight.x, innerRight.y)
    }
    
    // 3. 중심으로 복귀
    lineTo(center.x, center.y)
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
