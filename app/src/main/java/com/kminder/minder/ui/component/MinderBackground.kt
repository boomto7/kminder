package com.kminder.minder.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.core.graphics.toColorInt
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.ui.theme.MinderLavender
import com.kminder.minder.ui.theme.MinderMint
import com.kminder.minder.ui.theme.MinderPeach
import com.kminder.minder.ui.theme.MinderSkyBlue

/**
 * Minder 앱의 공통 애니메이션 배경
 * - 부드럽게 움직이는 파스텔 원들과 블러 효과로 프리미엄한 분위기를 연출합니다.
 */
@Composable
fun MinderBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // === 메시 그라디언트 애니메이션 (밝고 화사한 배경) ===
    val infiniteTransition = rememberInfiniteTransition(label = "mesh_gradient")

    // 원 1 (좌상단 -> 우하단)
    val circle1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle1"
    )

    // 원 2 (우상단 -> 좌하단)
    val circle2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle2"
    )

    // 원 3 (중앙 일렁임)
    val circle3Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle3"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MinderBackground) // 기본 배경색
    ) {
        // 배경 그라디언트 캔버스
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height

            // BlurMaskFilter를 적용하기 위해 Native Canvas 사용
            drawContext.canvas.nativeCanvas.apply {
                // 블러 설정 (Radius를 매우 크게 설정하여 부드럽게)
                val blurRadius = width * 0.4f
                val paint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    maskFilter = android.graphics.BlurMaskFilter(blurRadius, android.graphics.BlurMaskFilter.Blur.NORMAL)
                }

                // 1. 좌상단: 피치/오렌지
                paint.color = MinderPeach.value.toLong().toColorInt()
                paint.alpha = (255 * 0.6).toInt() // Alpha 0.6
                drawCircle(
                    width * 0.2f + (width * 0.3f * circle1Offset),
                    height * 0.2f + (height * 0.2f * circle1Offset),
                    width * 0.6f, // 실제 원 크기는 줄이고 블러로 퍼뜨림
                    paint
                )

                // 2. 우하단: 스카이블루
                paint.color = MinderSkyBlue.value.toLong().toColorInt()
                paint.alpha = (255 * 0.6).toInt()
                drawCircle(
                    width * 0.8f - (width * 0.2f * circle2Offset),
                    height * 0.8f - (height * 0.3f * circle2Offset),
                    width * 0.7f,
                    paint
                )

                // 3. 중앙: 라벤더
                paint.color = MinderLavender.value.toLong().toColorInt()
                paint.alpha = (255 * 0.7).toInt()
                drawCircle(
                    center.x,
                    center.y,
                    width * 0.5f * circle3Scale,
                    paint
                )

                // 4. 우상단: 민트
                paint.color = MinderMint.value.toLong().toColorInt()
                paint.alpha = (255 * 0.5).toInt()
                drawCircle(
                    width,
                    0f,
                    width * 0.5f,
                    paint
                )
            }
        }
        
        // 실제 컨텐츠
        content()
    }
}
