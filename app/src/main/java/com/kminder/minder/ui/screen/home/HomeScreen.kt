package com.kminder.minder.ui.screen.home

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.minder.R
import com.kminder.minder.ui.component.MinderGlassCard
import com.kminder.minder.ui.theme.MinderBackground
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 홈/대시보드 화면
 * 최근 감정 상태를 배경 애니메이션으로 표현합니다.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // 기존 네비게이션 콜백은 유지하되 사용하지 않음 (요청에 따라 UI 초기화)
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val moodColor by viewModel.moodColor.collectAsState()
    
    // 부드러운 색상 전환 애니메이션
    val animatedColor by animateColorAsState(
        targetValue = moodColor,
        animationSpec = tween(durationMillis = 1500, easing = LinearEasing),
        label = "moodColor"
    )

    // 퍼지는 효과 애니메이션 (Splash-like)
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val radiusScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground)
    ) {
        // 배경 감정 확산 효과
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 오른쪽 상단 기준점
            val origin = Offset(width * 0.9f, height * 0.1f)

            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    color = animatedColor.toArgb()
                    // 매우 강한 블러로 부드럽게 퍼지는 느낌
                    maskFilter = BlurMaskFilter(width * 0.5f, BlurMaskFilter.Blur.NORMAL)
                    alpha = 180 // 약간 투명하게
                }

                // 원형 그라디언트 느낌으로 그리기
                // 1. 핵심 코어 (진한 부분)
                drawCircle(
                    origin.x,
                    origin.y,
                    width * 0.6f * radiusScale,
                    paint
                )

                // 2. 외곽 퍼짐 (더 연하게, 화면 안쪽으로)
                paint.alpha = 100
                drawCircle(
                    origin.x - 100f,
                    origin.y + 100f,
                    width * 0.9f * radiusScale,
                    paint
                )
            }
        }

        // 컨텐츠 레이어
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            containerColor = Color.Transparent, // 배경 애니메이션이 보이도록 투명 처리
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToWrite,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.home_new_diary_desc)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                // 상단: 날짜 및 타이틀
                Column(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "오늘의 마음 날씨",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LocalDate.now().format(DateTimeFormatter.ofPattern("M월 d일 EEEE")),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 중앙: 감정 분석 카드
                if (uiState is HomeUiState.Success) {
                    val analysis = (uiState as HomeUiState.Success).analysis
                    if (analysis != null) {
                        // 그라디언트 결정 (복합 감정우선, 없으면 기본값)
                        val cardGradient = analysis.complexEmotionType?.let {
                            com.kminder.minder.ui.theme.EmotionColorTheme.getComplexGradient(it)
                        } ?: analysis.detailedEmotionType?.let {
                            com.kminder.minder.ui.theme.EmotionColorTheme.getSingleEmotionGradient(it)
                        } 
                        
                        MinderGlassCard(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            gradient = cardGradient ?: androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFF59D).copy(alpha = 0.8f),
                                    Color(0xFFC5E1A5).copy(alpha = 0.8f)
                                )
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // 1. 복합 감정 타이틀
                                Text(
                                    text = "지금 당신의 상태",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = analysis.complexEmotionString,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // 2. 키워드 칩
                                FlowRow(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    analysis.keywords.forEach { keyword ->
                                        SuggestionChip(
                                            onClick = { },
                                            label = { Text("#$keyword") },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.surface.copy(
                                                    alpha = 0.5f
                                                )
                                            ),
                                            border = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // 3. 요약 및 조언
                                Text(
                                    text = analysis.summary,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
                                )

                                Divider(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .fillMaxWidth(0.5f),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )

                                Text(
                                    text = analysis.suggestedAction,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else if (uiState is HomeUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    // Empty State
                    Text(
                        text = "작성된 일기가 없습니다.\n오늘 하루를 기록해보세요.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 하단: 메뉴 버튼
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = onNavigateToList) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "목록")
                    }
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(imageVector = Icons.Default.ShowChart, contentDescription = "통계")
                    }
                }
            }
        }
    }
}
