package com.kminder.minder.ui.screen.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.minder.R
import com.kminder.minder.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 스플래시 화면 V2 (Stateful)
 */
@Composable
fun SplashScreenV2(
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isDataReady by viewModel.isCompleted.collectAsState()
    
    SplashScreenV2Content(
        isDataReady = isDataReady,
        onNavigateToHome = onNavigateToHome
    )
}

/**
 * 스플래시 화면 컨텐츠 (Stateless / UI Logic)
 */
@Composable
fun SplashScreenV2Content(
    isDataReady: Boolean,
    onNavigateToHome: () -> Unit
) {
    // 애니메이션 상태
    var startAnimation by remember { mutableStateOf(false) }
    // 최소 애니메이션 시간 보장 상태
    var minTimePassed by remember { mutableStateOf(false) }

    // 메인 컨텐츠 등장 애니메이션 (Scale + Fade)
    val contentScale by animateFloatAsState(
        targetValue = if (startAnimation) 1.2f else 0.8f, // 튀어오르는 느낌 (0.8 -> 1.2 -> 1.0)
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "content_scale"
    )

    // 애니메이션 시퀀스 및 네비게이션 트리거
    LaunchedEffect(Unit) {
        delay(100) // 살짝 대기
        startAnimation = true
        delay(2500) // 로고 보여주는 최소 시간
        minTimePassed = true
    }
    
    // 두 조건이 모두 만족되면 이동
    LaunchedEffect(minTimePassed, isDataReady) {
        if (minTimePassed && isDataReady) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground), // 앱 기본 배경색 (파스텔톤)
        contentAlignment = Alignment.Center
    ) {
        // --- 배경 장식 요소 (Floating Bubbles) ---
        // 애니메이션: 둥둥 떠다니는 효과
        val infiniteTransition = rememberInfiniteTransition(label = "floating")
        val floatY by infiniteTransition.animateFloat(
            initialValue = -10f, targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "floatY"
        )

        // 장식 1 (좌상단)
        DecorBubble(
            color = EmotionJoy,
            size = 80.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 100.dp, start = 40.dp)
                .offset(y = floatY.dp)
        )

        // 장식 2 (우하단)
        DecorBubble(
            color = EmotionTrust,
            size = 120.dp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 120.dp, end = 20.dp)
                .offset(y = -floatY.dp) // 반대 방향 움직임
        )

        // 장식 3 (중앙 하단 미니)
        DecorBubble(
            color = EmotionSurprise,
            size = 40.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 200.dp, end = 100.dp)
                .offset(y = (floatY * 0.5f).dp)
        )


        // --- 메인 로고 컨테이너 (Hard Shadow + Border) ---
        // 그림자 (Shadow Layer) - 뒤쪽에 위치
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(contentScale)
                .offset(x = 6.dp, y = 6.dp) // Hard Shadow Offset
                .background(Color.Black, shape = RoundedCornerShape(32.dp))
        )

        // 본체 (Main Layer) - 앞쪽에 위치
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(contentScale)
                .background(Color.White, shape = RoundedCornerShape(32.dp))
                .border(3.dp, Color.Black, shape = RoundedCornerShape(32.dp)) // Bold Outline
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 앱 이름
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 56.sp,
                        letterSpacing = (-1).sp
                    ),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 슬로건 뱃지 (Chip Style)
                Surface(
                    color = EmotionJoy, // 포인트 컬러
                    shape = CircleShape, // 알약 모양
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.Black),
                    shadowElevation = 0.dp // Elevation 대신 Hard Shadow 사용하므로 0
                ) {
                    Text(
                        text = stringResource(R.string.splash_slogan),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * 배경 장식용 버블 (Outline + Pastel Color)
 */
@Composable
fun DecorBubble(
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape)
            .border(2.dp, Color.Black, CircleShape)
    )
}

@Preview
@Composable
fun SplashScreenV2Preview() {
    SplashScreenV2Content(
        isDataReady = true,
        onNavigateToHome = {}
    )
}
