package com.kminder.minder.ui.screen.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.IntegratedAnalysis
import com.kminder.minder.ui.theme.EmotionEmpty
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.minder.util.CustomDateUtil
import com.kminder.minder.util.EmotionColorUtil.getEmotionColor
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val moodColor by viewModel.moodColor.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        moodColor = moodColor,
        onNavigateToWrite = onNavigateToWrite,
        onNavigateToList = onNavigateToList,
        onNavigateToStatistics = onNavigateToStatistics
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    moodColor: Color,
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground)

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
            ) {
                // 2-1. 좌측: 감정 분석 영역
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                        .background(
                            color = moodColor,
//                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(16.dp)

                ) {
                    if (uiState is HomeUiState.Success) {
                        val analysis = uiState.analysis
                        if (analysis != null) {
                            EmotionAnalysisContent(analysis)
                        } else {
                            Text("No Data", modifier = Modifier.align(Alignment.Center))
                        }
                    } else if (uiState is HomeUiState.Loading) {
                        Text("Loading...", modifier = Modifier.align(Alignment.Center))
                    } else {
                        Text("No Data", modifier = Modifier.align(Alignment.Center))
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
                    onNavigateToStatistics = onNavigateToStatistics
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
                OutlinedTimeText(text = hourText, fontSize = 80.sp)

                // 콜론 (:) - 숫자 사이즈의 1/3 (약 30sp), 하단 정렬 요청
                OutlinedTimeText(
                    text = ":",
                    fontSize = 30.sp,
                    strokeWidth = 4f,
                    modifier = Modifier
                        .align(Alignment.Bottom) // Row 내에서 하단 정렬
                        .padding(horizontal = 3.dp)
                        .padding(bottom = 15.dp)
                        // 숫자의 베이스라인과 어울리도록 미세 높이 조정
                )

                // 분 (Minute)
                OutlinedTimeText(text = minuteText, fontSize = 80.sp)
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
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(x = cornerRadiusPx, y = cornerRadiusPx),
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
                fontWeight = FontWeight.Thin,
                fontSize = fontSize,
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                )
            ),
            color = MaterialTheme.colorScheme.onPrimary
        )
        // 2. 내부 채우기 (Fill)
        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Thin,
                fontSize = fontSize
            ),
            color = MinderBackground
        )
    }
}

@Composable
fun NavigationMenuSection(
    modifier: Modifier = Modifier,
    moodColor: Color = MinderBackground,
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    Column(
        modifier = modifier.background(color = moodColor),
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
                description = "List"
            )

            MenuButton(
                icon = Icons.Default.ShowChart,
                onClick = onNavigateToStatistics,
                description = "Statistics"
            )

            // 유저 세팅 메뉴 추가 (기능은 아직 TBD -> 빈 람다)
            MenuButton(
                icon = Icons.Default.Settings, // Settings 아이콘 사용
                onClick = { /* TODO: Navigate to User Settings (TBD) */ },
                description = "Settings"
            )
        }

        // 하단 추가 버튼
        MenuButton(
            icon = Icons.Default.Add,
            onClick = onNavigateToWrite,
            description = "Add New",
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
    iconColor: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 50.dp
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = containerColor,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmotionAnalysisContent(analysis: IntegratedAnalysis) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "오늘의 감정",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = analysis.complexEmotionString,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
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
                    // 배경색 투명, 테두리(Border) 추가
                    Surface(
                        color = Color.Transparent, // 배경색 없음
                        shape = RoundedCornerShape(20.dp), // 둥근 칩 형태
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) // 은은한 테두리
                        )
                    ) {
                        Text(
                            text = keyword, // # 제거
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        Text(
            text = analysis.summary,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 조언 (Suggested Action) 추가
        if (analysis.suggestedAction.isNotEmpty()) {
            Text(
                text = "💡 Tip",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = analysis.suggestedAction,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
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
        keywords = listOf("행복", "신뢰", "따뜻함따뜻함"),
        summary = "최근 당신의 마음은 기쁨과 신뢰로 가득 차 있네요. 긍정적인 에너지가 넘치는 시기입니다.",
        suggestedAction = "사랑하는 사람들에게 감사의 마음을 표현해보세요.",
        complexEmotionType = ComplexEmotionType.LOVE
    )

//    getEmotionColor(EmotionType.TRUST)
    MinderTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(emptyList(), mockAnalysis),
            moodColor = EmotionEmpty,
            onNavigateToWrite = {},
            onNavigateToList = {},
            onNavigateToStatistics = {}
        )
    }
}
