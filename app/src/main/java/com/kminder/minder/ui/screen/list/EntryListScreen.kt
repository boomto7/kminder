package com.kminder.minder.ui.screen.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kminder.domain.model.JournalEntry
import com.kminder.minder.R
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.ui.component.OutlinedDivider
import com.kminder.minder.ui.screen.home.OutlinedTimeText
import com.kminder.minder.ui.screen.home.OutlinedTimeText
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.component.RetroLoadingIndicator
import com.kminder.minder.util.EmotionColorUtil
import com.kminder.minder.util.EmotionImageUtil
import com.kminder.minder.util.EmotionUiUtil
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 일기 목록 화면
 * Retro & Glassmorphism 디자인 적용
 */
@Composable
fun EntryListScreen(
    onNavigateBack: () -> Unit,
    onEntryClick: (Long) -> Unit,
    onNavigateToWrite: () -> Unit, // FAB Action
    viewModel: EntryListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // 스크롤 끝 감지하여 추가 로드
    val endOfListReached by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty() || totalItemsNumber == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()
                (lastVisibleItem.index + 2 >= totalItemsNumber)
            }
        }
    }

    LaunchedEffect(endOfListReached) {
        if (endOfListReached) {
            viewModel.loadMoreEntries()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Header Section
            EntryListHeader(
                onNavigateBack = onNavigateBack
            )

            OutlinedDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(4.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )

            // 2. List Content
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        RetroLoadingIndicator()
                    }
                } else {
                    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
                    CompositionLocalProvider(
                        LocalOverscrollFactory provides null
                    ) {
                        val pullState = rememberPullToRefreshState()
                        PullToRefreshBox(
                            state = pullState,
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = { viewModel.refresh() },
                            indicator = {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 16.dp)
                                ) {
                                    val fraction = pullState.distanceFraction
                                    if (fraction > 0f || uiState.isRefreshing) {
                                        RetroLoadingIndicator(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .graphicsLayer {
                                                    val validFraction = if (fraction.isNaN()) 0f else fraction.coerceIn(0f, 1f)
                                                    alpha = if (uiState.isRefreshing) 1f else validFraction

                                                    val safeScale = if (uiState.isRefreshing) 1f else validFraction.coerceAtLeast(0.01f)
                                                    scaleX = safeScale
                                                    scaleY = safeScale
                                                }
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (uiState.entries.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = stringResource(R.string.home_empty_title),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Black.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(R.string.home_empty_desc),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Black.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    state = listState,
                                    contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp), // FAB space
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(uiState.entries) { entry ->
                                        JournalCard(entry = entry, onClick = { onEntryClick(entry.id) })
                                    }

                                    if (uiState.isLoadingMore) {
                                        item {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 24.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                RetroLoadingIndicator(modifier = Modifier.size(36.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. FAB (Floating Action Button) - Retro Style
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            RetroFAB(
                onClick = onNavigateToWrite,
                icon = Icons.Default.Add,
                contentDescription = stringResource(R.string.home_menu_add)
            )
        }
    }
}

@Composable
fun EntryListHeader(
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button (Retro Style)
        RetroIconButton(
            onClick = onNavigateBack,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_back)
        )

        // Title ("나의 기록") uses Outlined Text style from Home but smaller
        // Using a similar style or just bold text
        Text(
            text = "Journal", // English title looks better for Retro
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = Color.Black
        )
        
        // Dummy box for alignment balance
        Spacer(modifier = Modifier.size(50.dp))
    }
}

@Composable
fun JournalCard(
    entry: JournalEntry,
    onClick: () -> Unit
) {
    // Determine Color based on EmotionResult
    val emotionResult = entry.emotionResult
    val cardColor = emotionResult?.let { EmotionColorUtil.getEmotionResultColor(it) } ?: Color.White
    
    val context = LocalContext.current
    val stringProvider = remember { AndroidEmotionStringProvider(context) }

    NeoShadowBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Adapts to content
            .clickable(onClick = onClick),
        containerColor = cardColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
                // Top: Date header using Badge style
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val dateStr = entry.createdAt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
                    val dayStr = entry.createdAt.format(DateTimeFormatter.ofPattern("EEE", Locale.KOREA))
                    
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(50),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.Black)
                    ) {
                        Text(
                            text = "$dateStr ($dayStr)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.Black
                        )
                    }

                    // Time
                    Text(
                        text = entry.createdAt.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content Preview
                Text(
                    text = entry.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom: Emotion Info (if available)
                if (entry.hasEmotionAnalysis() && emotionResult != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emotion Name Badge
                        Surface(
                            color = Color.Black.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                           Row(
                               verticalAlignment = Alignment.CenterVertically,
                               modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp) 
                           ) {
                               Box(
                                   modifier = Modifier
                                       .size(8.dp)
                                       .background(EmotionColorUtil.getEmotionColor(emotionResult.primaryEmotion), CircleShape)
                                       .border(1.dp, Color.Black, CircleShape)
                               )
                               Spacer(modifier = Modifier.width(6.dp))
                               Text(
                                   text = EmotionUiUtil.getLabel(emotionResult, stringProvider),
                                   style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                   color = Color.Black
                               )
                           }
                        }
                    }
                }
        }
    }
}

@Composable
fun RetroIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String
) {
    NeoShadowBox(
        modifier = Modifier
            .size(50.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        offset = 3.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.Black
            )
        }
    }
}

@Composable
fun RetroFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String
) {
    NeoShadowBox(
        modifier = Modifier.size(64.dp).clickable(onClick = onClick),
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


