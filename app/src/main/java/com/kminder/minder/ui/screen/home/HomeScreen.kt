package com.kminder.minder.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.IntegratedAnalysis
import com.kminder.minder.R
import com.kminder.minder.ui.theme.EmotionEmpty
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.minder.util.CustomDateUtil
import com.kminder.minder.util.EmotionImageUtil
import com.kminder.minder.ui.component.NeoShadowBox
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToGuide: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val moodColor by viewModel.moodColor.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        moodColor = moodColor,
        onNavigateToWrite = onNavigateToWrite,
        onNavigateToList = onNavigateToList,
        onNavigateToStatistics = onNavigateToStatistics,
        onNavigateToGuide = onNavigateToGuide
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    moodColor: Color,
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToGuide: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. 상단 영역: 날짜와 시간 (화면의 약 45% 차지)
            HeaderSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.40f) // 요청: 상단에 반정도 많은 부분 차지
            )

            // 구분선 (Divider) - 마진 없이 바로 구분선 배치
//            androidx.compose.material3.HorizontalDivider(
//                modifier = Modifier.fillMaxWidth(),
//                thickness = 1.dp,
//                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
//            )

            OutlinedDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .height(4.dp)
                ,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // 2. 하단 영역 (좌: 감정분석, 우: 네비게이션)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.50f) // 나머지 55%
                    .padding(8.dp)
            ) {
                // 2-1. 좌측: 감정 분석 영역
                // Retro Card Style: Border + Hard Shadow (Logic implemented via offsets & borders)
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                ) {
                    val cardShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    
                    NeoShadowBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 6.dp, end = 6.dp), // Margin for shadow visibility
                        shape = cardShape,
                        containerColor = moodColor
                    ) {
                        Box(modifier = Modifier.padding(20.dp)) {
                            if (uiState is HomeUiState.Success) {
                                val analysis = uiState.analysis
                                if (analysis != null) {
                                    EmotionAnalysisContent(analysis)
                                } else {
                                    Text(stringResource(R.string.common_no_data), modifier = Modifier.align(Alignment.Center))
                                }
                            } else if (uiState is HomeUiState.Loading) {
                                Text(stringResource(R.string.common_loading), modifier = Modifier.align(Alignment.Center))
                            } else {
                                Text(stringResource(R.string.common_no_data), modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                }

                Spacer(Modifier.width(6.dp))
                // 2-2. 우측: 네비게이션 메뉴 영역
                NavigationMenuSection(
                    modifier = Modifier
                        .width(64.dp)
//                        .weight(0.3f)
                        .fillMaxHeight(),
                    moodColor = moodColor,
                    onNavigateToWrite = onNavigateToWrite,
                    onNavigateToList = onNavigateToList,
                    onNavigateToStatistics = onNavigateToStatistics,
                    onNavigateToGuide = onNavigateToGuide
                )
            }
        }
    }
}

@Composable
fun HeaderSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Preview 안전성을 위해 try-catch 또는 하드코딩
        val now = try {
            LocalTime.now()
        } catch (e: Exception) {
            LocalTime.of(5, 48)
        }
        val hourText = now.format(DateTimeFormatter.ofPattern("hh"))
        val minuteText = now.format(DateTimeFormatter.ofPattern("mm"))

        val currentLocale = Locale.getDefault()
        val dateText = CustomDateUtil.getCurrentDate(currentLocale)

        // IntrinsicSize.Max를 사용하여 내부 컨텐츠(시간/날짜 중 더 긴 쪽)의 너비에 맞춤
        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 시간 표시 (Row로 분리하여 콜론(:) 스타일 별도 적용)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
//                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // 시 (Hour)
                OutlinedTimeText(text = hourText, fontSize = 80.sp, strokeWidth = 12f)

                // 콜론 (:) - 숫자 사이즈의 1/3 (약 30sp), 하단 정렬 요청
                OutlinedTimeText(
                    text = ":",
                    fontSize = 30.sp,
                    strokeWidth = 6f,
                    modifier = Modifier
                        .align(Alignment.Bottom) // Row 내에서 하단 정렬
                        .padding(horizontal = 6.dp)
                        .padding(bottom = 15.dp)
                        // 숫자의 베이스라인과 어울리도록 미세 높이 조정
                )

                // 분 (Minute)
                OutlinedTimeText(text = minuteText, fontSize = 80.sp, strokeWidth = 12f)
            }

            // 시간/날짜 사이 외곽선 모양 구분선
            OutlinedDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(Modifier.height(9.dp))

            Text(
                text = dateText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun OutlinedDivider(
    modifier: Modifier = Modifier,
    color: Color,
    strokeWidth: Dp = 1.dp,
    cornerRadius: Dp = 8.dp
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()
        val cornerRadiusPx = cornerRadius.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(x = 0f, y = 0f), // Sharp corners or small radius
            style = Stroke(width = strokeWidthPx)
        )
    }
}

@Composable
fun OutlinedTimeText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    strokeWidth: Float = 8f,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // 1. 외곽선 (Stroke)
        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold, // Boldest font
                fontSize = fontSize,
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = androidx.compose.ui.graphics.StrokeJoin.Miter // Sharp joints
                )
            ),
            color = Color.Black // Always black border for Retro style
        )
        // 2. 내부 채우기 (Fill)
        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold, // Boldest font
                fontSize = fontSize
            ),
            color = MinderBackground // Fill Color
        )
    }
}

@Composable
fun NavigationMenuSection(
    modifier: Modifier = Modifier,
    moodColor: Color = MinderBackground,
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToGuide: () -> Unit
) {
    Column(
        modifier = modifier.padding(end = 6.dp), // Right Margin
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 버튼들 (설정, 통계, 리스트) - 순서 및 배치 조정
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuButton(
                icon = Icons.Default.List,
                onClick = onNavigateToList,
                description = stringResource(R.string.home_menu_list)
            )

            MenuButton(
                icon = Icons.Default.ShowChart,
                onClick = onNavigateToStatistics,
                description = stringResource(R.string.home_menu_stats)
            )

            MenuButton(
                icon = Icons.Default.Description,
                onClick = onNavigateToGuide,
                description = stringResource(R.string.home_menu_guide)
            )

            // 유저 세팅 메뉴 추가 (기능은 아직 TBD -> 빈 람다)
            MenuButton(
                icon = Icons.Default.Settings, // Settings 아이콘 사용
                onClick = { /* TODO: Navigate to User Settings (TBD) */ },
                description = stringResource(R.string.home_menu_settings)
            )
        }

        // 하단 추가 버튼
        MenuButton(
            icon = Icons.Default.Add,
            onClick = onNavigateToWrite,
                description = stringResource(R.string.home_menu_add),
            containerColor = MaterialTheme.colorScheme.primary,
            iconColor = MaterialTheme.colorScheme.onPrimary,
//            size = 80.dp
        )
    }
}

@Composable
fun MenuButton(
    icon: ImageVector,
    onClick: () -> Unit,
    description: String,
    containerColor: Color = Color.White,
    iconColor: Color = Color.Black,
    size: Dp = 50.dp
) {
    // Retro Button Style: Hard Shadow + Border
    NeoShadowBox(
        modifier = Modifier.size(size),
        shape = CircleShape,
        offset = 3.dp,
        containerColor = containerColor
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    tint = iconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmotionAnalysisContent(analysis: IntegratedAnalysis) {
    val context = LocalContext.current
    val emotionImageResId = EmotionImageUtil.getEmotionImageRecourceId(context, analysis.complexEmotionType)

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Emotion Image (Background Layer, Bottom End)
        // 1. Emotion Image (Background Layer, Bottom End)
        if (emotionImageResId != 0) {
            NeoShadowBox(
                modifier = Modifier
                    .size(100.dp) // Larger size for background effect
                    .align(Alignment.BottomEnd) // Position at bottom right
                    .offset(x = 10.dp, y = 10.dp) // Slightly off-screen/clipped for dynamic look
                    .alpha(0.8f), // Slightly transparent so text is legible
                shape = CircleShape,
                offset = 2.dp,
                containerColor = Color.White
            ) {
                 AsyncImage(
                     model = ImageRequest.Builder(LocalContext.current)
                         .data(emotionImageResId)
                         .crossfade(true)
                         .build(),
                     placeholder = painterResource(R.drawable.ic_launcher_foreground),
                     contentDescription = null, // Decorative background
                     modifier = Modifier.padding(10.dp)
                 )
            }
        }

        // 2. Text Content (Foreground Layer)
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.home_today_emotion),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = analysis.complexEmotionString,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold, // Bolder
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
    
            // Keywords (Flexible Layout)
            if (analysis.keywords.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    analysis.keywords.forEach { keyword ->
                        // Score 기반 스타일 계산
                        val score = keyword.score.coerceIn(0f, 1f)
                        val borderWidth = (1.5f + (score * 2)).dp
                        val fontSize = (12 + (score * 4)).sp
                        val paddingVertical = (6 + (score * 4)).dp
                        val paddingHorizontal = (12 + (score * 6)).dp

                        // 배경색 투명, 테두리 (Background White)
                        Surface(
                            color = Color.White, // White background to stand out on potentially overlapping text
                            shape = CircleShape, // Fully rounded (Pill)
                            border = androidx.compose.foundation.BorderStroke(
                                width = borderWidth,
                                color = Color.Black
                            )
                        ) {
                            Text(
                                text = keyword.word, // # 제거, EmotionKeyword 객체의 word 속성 사용
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (score > 0.7f) FontWeight.Black else FontWeight.Bold,
                                    fontSize = fontSize
                                ),
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = paddingHorizontal, vertical = paddingVertical)
                            )
                        }
                    }
                }
            }
    
            Text(
                text = analysis.summary,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = Color.Black
            )
    
            Spacer(modifier = Modifier.height(16.dp))
    
            // 조언 (Suggested Action) 추가
            if (analysis.suggestedAction.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.common_tip_prefix),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = analysis.suggestedAction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFEF8EE)
@Composable
fun HomeScreenPreview() {
    val mockAnalysis = IntegratedAnalysis(
        recentEmotions = mapOf(
            EmotionType.JOY to 0.8f,
            EmotionType.TRUST to 0.6f
        ),
        complexEmotionString = "사랑",
        keywords = listOf(
            com.kminder.domain.model.EmotionKeyword("행복", EmotionType.JOY, 0.9f),
            com.kminder.domain.model.EmotionKeyword("신뢰", EmotionType.TRUST, 0.7f),
            com.kminder.domain.model.EmotionKeyword("따뜻함", EmotionType.JOY, 0.6f)
        ),
        summary = "최근 당신의 마음은 기쁨과 신뢰로 가득 차 있네요. 긍정적인 에너지가 넘치는 시기입니다.",
        suggestedAction = "사랑하는 사람들에게 감사의 마음을 표현해보세요.",
        complexEmotionType = ComplexEmotionType.LOVE,
        sourceAnalyses = emptyList() // Preview용 빈 리스트
    )

//    getEmotionColor(EmotionType.TRUST)
    MinderTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(emptyList(), mockAnalysis),
            moodColor = EmotionEmpty,
            onNavigateToWrite = {},
            onNavigateToList = {},
            onNavigateToStatistics = {},
            onNavigateToGuide = {}
        )
    }
}
