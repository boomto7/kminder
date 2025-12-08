package com.kminder.minder.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.domain.model.JournalEntry
import java.time.format.DateTimeFormatter

/**
 * 홈/대시보드 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minder") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToWrite,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "새 일기 작성"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 상단 액션 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToList,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("전체 보기")
                }
                
                OutlinedButton(
                    onClick = onNavigateToStatistics,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("통계")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 최근 일기 섹션
            Text(
                text = "최근 일기",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // UI 상태에 따른 컨텐츠
            when (uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                is HomeUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "아직 작성된 일기가 없습니다",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "+ 버튼을 눌러 첫 일기를 작성해보세요",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                is HomeUiState.Success -> {
                    val entries = (uiState as HomeUiState.Success).recentEntries
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(entries) { entry ->
                            RecentEntryCard(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 최근 일기 카드
 */
@Composable
fun RecentEntryCard(entry: JournalEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 날짜
            Text(
                text = entry.createdAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm")),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 내용 미리보기
            Text(
                text = entry.content.take(100) + if (entry.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
            
            // 감정 분석 결과가 있으면 표시
            if (entry.hasEmotionAnalysis()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                val dominantEmotion = entry.emotionAnalysis?.getDominantEmotion()
                Text(
                    text = "주요 감정: ${getEmotionName(dominantEmotion)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * 감정 타입을 한글 이름으로 변환
 */
private fun getEmotionName(emotionType: com.kminder.domain.model.EmotionType?): String {
    return when (emotionType) {
        com.kminder.domain.model.EmotionType.ANGER -> "분노"
        com.kminder.domain.model.EmotionType.ANTICIPATION -> "기대"
        com.kminder.domain.model.EmotionType.JOY -> "기쁨"
        com.kminder.domain.model.EmotionType.TRUST -> "신뢰"
        com.kminder.domain.model.EmotionType.FEAR -> "두려움"
        com.kminder.domain.model.EmotionType.SADNESS -> "슬픔"
        com.kminder.domain.model.EmotionType.DISGUST -> "혐오"
        com.kminder.domain.model.EmotionType.SURPRISE -> "놀람"
        null -> "알 수 없음"
    }
}
