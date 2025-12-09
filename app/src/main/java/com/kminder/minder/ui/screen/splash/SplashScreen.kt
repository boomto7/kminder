package com.kminder.minder.ui.screen.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kminder.minder.R
import com.kminder.minder.ui.theme.*
import kotlinx.coroutines.delay
import androidx.core.graphics.toColorInt

/**
 * 스플래시 화면
 * 
 * 감정의 흐름 컨셉:
 * - 플루치크 8가지 감정 색상이 그라디언트로 흐르는 애니메이션
 * - 중앙에 "Minder" 로고가 페이드인
 * - 2초 후 자동으로 홈 화면으로 이동
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    // 애니메이션 상태
    var startAnimation by remember { mutableStateOf(false) }
    
    // 로고 페이드인
    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1500, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "content_alpha"
    )

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
    
    // 색상 팔레트 (밝은 파스텔 톤)
    val colorSkyBlue = Color(0xFFB3E5FC)    // 맑은 하늘색
    val colorLavender = Color(0xFFE1BEE7)   // 연보라
    val colorPeach = Color(0xFFFFCCBC)      // 살구색
    val colorMint = Color(0xFFB2DFDB)       // 민트색
    val colorBackground = Color(0xFFFAFAFA) // 거의 흰색에 가까운 배경

    // 2.5초 후 홈 화면으로 이동
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3000)
        onNavigateToHome()
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
            .background(colorBackground), // 기본 배경색
        contentAlignment = Alignment.Center
    ) {
        // 배경 그라디언트 캔버스
        androidx.compose.foundation.Canvas(
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
                paint.color = colorPeach.value.toLong().toColorInt()
                paint.alpha = (255 * 0.6).toInt() // Alpha 0.6
                drawCircle(
                    width * 0.2f + (width * 0.3f * circle1Offset),
                    height * 0.2f + (height * 0.2f * circle1Offset),
                    width * 0.6f, // 실제 원 크기는 줄이고 블러로 퍼뜨림
                    paint
                )

                // 2. 우하단: 스카이블루
                paint.color = colorSkyBlue.value.toLong().toColorInt()
                paint.alpha = (255 * 0.6).toInt()
                drawCircle(
                    width * 0.8f - (width * 0.2f * circle2Offset),
                    height * 0.8f - (height * 0.3f * circle2Offset),
                    width * 0.7f,
                    paint
                )

                // 3. 중앙: 라벤더
                paint.color = colorLavender.value.toLong().toColorInt()
                paint.alpha = (255 * 0.7).toInt()
                drawCircle(
                    center.x,
                    center.y,
                    width * 0.5f * circle3Scale,
                    paint
                )
                
                // 4. 우상단: 민트
                paint.color = colorMint.value.toLong().toColorInt()
                paint.alpha = (255 * 0.5).toInt()
                drawCircle(
                    width,
                    0f,
                    width * 0.5f,
                    paint
                )
            }
        }
        
        // Glassmorphism Container (반투명 블러 효과 흉내)
        Box(
            modifier = Modifier
                .offset(y = (-80).dp) // 위쪽으로 조금 이동
                .size(280.dp) // 둥근 정사각형 크기
                .alpha(contentAlpha)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp)) // 둥근 모서리
                .background(Color.White.copy(alpha = 0.4f)) // 반투명 흰색
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.6f), // 유리 테두리 느낌
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // 로고 및 텍스트 영역
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 앱 이름
                Text(
                    text = "Minder",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424242), // 진한 회색 (밝은 배경 대비)
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 슬로건
                Text(
                    text = "당신의 감정을 기록하다",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF616161), // 회색
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun SplashScreen_preview() {
    SplashScreen({})
}