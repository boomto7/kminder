package com.kminder.minder.ui.component.chart

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionType
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.minder.util.EmotionColorUtil
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 8각형 Radar Chart (Net Chart, Spider Chart)
 * EmotionAnalysis 데이터를 시각화합니다.
 */
@Composable
fun RadarChart(
    analysis: EmotionAnalysis,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Black,
    labelColor: Color = Color.Black
) {
    val plutchikOrder = remember {
        listOf(
            EmotionType.JOY,          // 기쁨
            EmotionType.TRUST,        // 신뢰
            EmotionType.FEAR,         // 두려움
            EmotionType.SURPRISE,     // 놀람
            EmotionType.SADNESS,      // 슬픔
            EmotionType.DISGUST,      // 혐오
            EmotionType.ANGER,        // 분노
            EmotionType.ANTICIPATION  // 기대
        )
    }

    // 각 감정의 이름 가져오기 (Context 필요)
    val context = LocalContext.current
    // 주의: 실제 앱에서는 EmotionStringProvider를 주입받아 사용하는 것이 좋으나,
    // Composable Preview 및 간단한 사용을 위해 임시로 하드코딩하거나 유틸을 사용할 수 있음.
    // 여기서는 간단한 매핑을 사용합니다.
    val emotionLabels = remember {
        // TODO: 다국어 지원 시 String Resource 사용 권장
        mapOf(
            EmotionType.JOY to "기쁨",
            EmotionType.TRUST to "신뢰",
            EmotionType.FEAR to "두려움",
            EmotionType.SURPRISE to "놀람",
            EmotionType.SADNESS to "슬픔",
            EmotionType.DISGUST to "혐오",
            EmotionType.ANGER to "분노",
            EmotionType.ANTICIPATION to "기대"
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // 정사각형 비율 유지
            .padding(24.dp), // 라벨 공간 확보
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 * 0.8f // 라벨 공간 고려하여 80%만 사용
            val angleStep = (2 * PI / 8).toFloat()

            // 1. Grid (Background) - 5단계 (0.2, 0.4, ... 1.0)
            for (i in 1..5) {
                val gridRadius = radius * (i / 5f)
                val gridPath = Path()
                
                for (j in 0 until 8) {
                    val angle = (j * angleStep) - (PI / 2).toFloat() // 12시 방향부터 시작
                    val x = center.x + gridRadius * cos(angle)
                    val y = center.y + gridRadius * sin(angle)
                    
                    if (j == 0) gridPath.moveTo(x, y)
                    else gridPath.lineTo(x, y)
                }
                gridPath.close()

                drawPath(
                    path = gridPath,
                    color = lineColor.copy(alpha = 0.1f + (i * 0.05f)), // 바깥쪽일수록 약간 진하게
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // 2. Axes (Center to Vertices)
            for (j in 0 until 8) {
                val angle = (j * angleStep) - (PI / 2).toFloat()
                val endX = center.x + radius * cos(angle)
                val endY = center.y + radius * sin(angle)
                
                drawLine(
                    color = lineColor.copy(alpha = 0.2f),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 3. Data Polygon
            val dataPath = Path()
            val intensities = analysis.toMap()
            
            // 데이터의 평균적인 색상을 계산하거나 Dominant Emotion 색상 사용
            val dominantEmotion = intensities.maxByOrNull { it.value }?.key ?: EmotionType.JOY
            val polygonColor = EmotionColorUtil.getEmotionColor(dominantEmotion)

            plutchikOrder.forEachIndexed { index, type ->
                val intensity = intensities[type] ?: 0f
                // 최소 가시성을 위해 0.05f 정도는 보장해주는 것이 좋음 (옵션)
                val adjustedIntensity = intensity.coerceAtLeast(0.05f)
                
                val angle = (index * angleStep) - (PI / 2).toFloat()
                val dist = radius * adjustedIntensity
                
                val x = center.x + dist * cos(angle)
                val y = center.y + dist * sin(angle)
                
                if (index == 0) dataPath.moveTo(x, y)
                else dataPath.lineTo(x, y)
            }
            dataPath.close()

            // Fill
            drawPath(
                path = dataPath,
                color = polygonColor.copy(alpha = 0.5f)
            )
            // Stroke
            drawPath(
                path = dataPath,
                color = lineColor, // 디자인 시스템: 검은색 외곽선
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. Labels
            drawIntoCanvas { canvas ->
                val textPaint = Paint().apply {
                    color = labelColor.toArgb()
                    textSize = 12.dp.toPx()
                    textAlign = Paint.Align.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                }

                for (j in 0 until 8) {
                    val angle = (j * angleStep) - (PI / 2).toFloat()
                    // 라벨은 반지름보다 조금 더 바깥에 위치
                    val labelRadius = radius + 20.dp.toPx() 
                    
                    val x = center.x + labelRadius * cos(angle)
                    // Y 좌표 보정 (텍스트 중심점)
                    val y = center.y + labelRadius * sin(angle) + (textPaint.textSize / 3) 

                    val emotionType = plutchikOrder[j]
                    val label = emotionLabels[emotionType] ?: ""
                    
                    canvas.nativeCanvas.drawText(label, x, y, textPaint)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRadarChart() {
    val mockAnalysis = EmotionAnalysis(
        joy = 0.8f,
        trust = 0.6f,
        fear = 0.2f,
        surprise = 0.4f,
        sadness = 0.1f,
        disgust = 0.0f,
        anger = 0.1f,
        anticipation = 0.5f,
        keywords = emptyList()
    )
    
    MinderTheme {
        RadarChart(analysis = mockAnalysis)
    }
}
