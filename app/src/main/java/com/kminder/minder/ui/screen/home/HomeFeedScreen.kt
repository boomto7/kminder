package com.kminder.minder.ui.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kminder.domain.model.ChartPeriod
import com.kminder.domain.model.EntryType
import com.kminder.domain.model.JournalEntry
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.component.RetroLoadingIndicator
import com.kminder.minder.ui.component.OutlinedDivider
import com.kminder.minder.ui.component.MinderEmptyView
import com.kminder.minder.ui.provider.AndroidEmotionStringProvider
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.ui.theme.MinderTheme
import com.kminder.minder.util.EmotionColorUtil
import com.kminder.minder.util.EmotionUiUtil
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun HomeFeedScreen(
    onNavigateToWrite: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: (ChartPeriod?, Long?) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()

    Timber.e("isRefreshing : $isRefreshing in ui")

    val listState = rememberLazyListState()
    // Show FAB when scrolled past the first item
    val showFab by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    val isLastPage by viewModel.isLastPage.collectAsState()
    val groupingOption by viewModel.groupingOption.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HomeDrawerContent(
                onNavigateToList = onNavigateToList,
                onNavigateToStatistics = { onNavigateToStatistics(null, null) }
            )
        }
    ) {
        Scaffold(
            topBar = {
                HomeFeedTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    selectedOption = groupingOption,
                    onOptionSelected = viewModel::setGroupingOption
                )
            },
            floatingActionButton = {
                if (showFab) {
                    RetroFAB(
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                        icon = Icons.Default.ArrowUpward,
                        contentDescription = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.home_cd_scroll_to_top)
                    )
                }
            },
            containerColor = MinderBackground
        ) { paddingValues ->
            HomeFeedContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                onEntryClick = onNavigateToDetail,
                onWriteClick = onNavigateToWrite,
                isRefreshing = isRefreshing,
                isLoadingMore = isLoadingMore,
                isLastPage = isLastPage,
                onRefresh = viewModel::refresh,
                onLoadMore = viewModel::loadMore,
                listState = listState,
                onNavigateToStatistics = onNavigateToStatistics
            )
        }
    }
}

@Composable
fun HomeFeedTopBar(
    onMenuClick: () -> Unit,
    selectedOption: FeedGroupingOption,
    onOptionSelected: (FeedGroupingOption) -> Unit
) {
    Column(modifier = Modifier.statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Menu Button
            RetroIconButton(
                onClick = onMenuClick,
                icon = Icons.Default.Menu,
                contentDescription = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.home_cd_menu)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Center: App Title
            Text(
                text = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.app_name),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        // Grouping Selector
        FeedGroupingSelector(
            selectedOption = selectedOption,
            onOptionSelected = onOptionSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Divider
        OutlinedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun WriteEntryPrompt(onClick: () -> Unit) {
    NeoShadowBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(112.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.home_prompt_mood),
//                style = MaterialTheme.typography.titleMedium,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onEntryClick: (Long) -> Unit,
    onWriteClick: () -> Unit,
    isRefreshing: Boolean,
    isLoadingMore: Boolean,
    isLastPage: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    listState: LazyListState,
    onNavigateToStatistics: (ChartPeriod?, Long?) -> Unit
) {
    val pullState = rememberPullToRefreshState()
    Timber.e("isRefreshing : $isRefreshing in content")
    PullToRefreshBox(
        state = pullState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
        indicator = {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                val fraction = pullState.distanceFraction
                // 당기기 시작했거나 로딩 중일 때만 인디케이터를 Composition 트리에 추가
                // (미리 추가해두면 내부 무한 애니메이션이 돌면서 시스템 로그 유발 가능성)
                if (fraction > 0f || isRefreshing) {
                    RetroLoadingIndicator(
                        modifier = Modifier
                            .size(42.dp)
                            .graphicsLayer {
                                val validFraction = if (fraction.isNaN()) 0f else fraction.coerceIn(0f, 1f)
                                
                                alpha = if (isRefreshing) 1f else validFraction
                                
                                val safeScale = if (isRefreshing) 1f else validFraction.coerceAtLeast(0.01f)
                                scaleX = safeScale
                                scaleY = safeScale
                            }
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is HomeUiState.Loading -> {
                    // Only show big loader if NOT refreshing (to avoid double indicators)
                    if (!isRefreshing) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            RetroLoadingIndicator()
                        }
                    }
                }

                is HomeUiState.Empty -> {
                    TimelineFeed(
                        groupedEntries = emptyMap(),
                        onEntryClick = onEntryClick,
                        onWriteClick = onWriteClick,
                        listState = listState,
                        onLoadMore = onLoadMore,
                        isLoadingMore = isLoadingMore,
                        isLastPage = isLastPage,
                        onNavigateToStatistics = onNavigateToStatistics
                    )
                }

                is HomeUiState.Success -> {
                    TimelineFeed(
                        groupedEntries = uiState.groupedFeed,
                        onEntryClick = onEntryClick,
                        onWriteClick = onWriteClick,
                        listState = listState,
                        onLoadMore = onLoadMore,
                        isLoadingMore = isLoadingMore,
                        isLastPage = isLastPage,
                        onNavigateToStatistics = onNavigateToStatistics
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimelineFeed(
    groupedEntries: Map<String, List<com.kminder.domain.model.JournalEntry>>,
    onEntryClick: (Long) -> Unit,
    onWriteClick: () -> Unit,
    listState: LazyListState,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    isLastPage: Boolean,
    onNavigateToStatistics: (ChartPeriod?, Long?) -> Unit
) {
    // Disable Overscroll Effect (Glow)
    CompositionLocalProvider(
        LocalOverscrollFactory provides null
    ) {
        // Observe list state to trigger load more
        val shouldLoadMore by remember {
            derivedStateOf {
                val layoutInfo = listState.layoutInfo
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                // Trigger when we are near the end (e.g., last 3 items)
                // distinctUntilChanged handling is implicit in derivedStateOf for the boolean result
                totalItems > 0 && lastVisibleItemIndex >= totalItems - 3 && !isLoadingMore && !isLastPage
            }
        }

        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) {
                onLoadMore()
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp) // Bottom padding for FAB or aesthetics
        ) {
            item {
                WriteEntryPrompt(onClick = onWriteClick)
            }

            if (groupedEntries.isEmpty()) {
                item {
                    MinderEmptyView(
                        message = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.home_empty_title),
                        subMessage = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.home_empty_desc),
                        actionLabel = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.common_go_to_write),
                        onActionClick = onWriteClick,
                        modifier = Modifier.padding(top = 80.dp) // Push it down a bit
                    )
                }
            } else {
                groupedEntries.forEach { (header, entries) ->
                    stickyHeader(key = header) {
                        FeedSectionHeader(
                            title = header, 
                            onViewAllClick = {
                                // Parse header to trigger navigation
                                // Note: This parsing logic relies on the format defined in HomeViewModel.kt
                                try {
                                    val now = LocalDateTime.now()
                                    val (period, date) = when {
                                        header.contains("주차") -> { // Weekly: "M월 W주차"
                                            // Parsing fuzzy week string is hard, so we use the first entry's date as approximation
                                            // or we rely on the fact that entries are sorted.
                                            // Best is to use the first entry's date.
                                            ChartPeriod.WEEKLY to entries.firstOrNull()?.createdAt?.toLocalDate()
                                        }
                                        header.contains("년") && header.contains("월") && !header.contains("일") -> { // Monthly: "yyyy년 M월"
                                            val formatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREA)
                                            // toLocalDate requires day, default to 1st
                                            val temporal = formatter.parse(header)
                                            val year = temporal.get(java.time.temporal.ChronoField.YEAR)
                                            val month = temporal.get(java.time.temporal.ChronoField.MONTH_OF_YEAR)
                                            ChartPeriod.MONTHLY to LocalDate.of(year, month, 1)
                                        }
                                        else -> { // Daily: "M월 d일 EEEE" or fallback
                                            // Try daily parser or fallback to first entry
                                            ChartPeriod.DAILY to entries.firstOrNull()?.createdAt?.toLocalDate()
                                        }
                                    }
                                    
                                    if (date != null) {
                                        val dateMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                        onNavigateToStatistics(period, dateMillis)
                                    } else {
                                        onNavigateToStatistics(null, null)
                                    }
                                } catch (e: Exception) {
                                    Timber.e(e, "Failed to parse header for navigation: $header")
                                    onNavigateToStatistics(null, null)
                                }
                            }
                        )
                    }

                    items(
                        count = entries.size,
                        key = { index -> entries[index].id },
                        contentType = { _ -> "FeedEntry" }
                    ) { index ->
                        FeedEntryItem(
                            entry = entries[index],
                            onClick = { onEntryClick(entries[index].id) }
                        )
                    }
                }
            }

            // Load More Indicator
            // Only show when actually loading
            if (isLoadingMore) {
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

@Composable
fun FeedSectionHeader(
    title: String,
    onViewAllClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MinderBackground) // Opaque background for sticky header
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )

        Text(
            text = androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.home_view_all),
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.clickable { onViewAllClick() }
        )
    }
}

@Composable
fun FeedEntryItem(
    entry: JournalEntry,
    onClick: () -> Unit
) {
    val emotionResult = remember { entry.emotionResult }
    // Thread-style simple card
    val cardColor = remember(entry.emotionResult) {
        entry.emotionResult?.let { EmotionColorUtil.getEmotionResultColor(it) } ?: Color.White
    }

    val context = LocalContext.current
    val stringProvider = remember { AndroidEmotionStringProvider(context) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            // Simple simplified view
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
                                    .background(
                                        EmotionColorUtil.getEmotionColor(emotionResult.primaryEmotion),
                                        CircleShape
                                    )
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

                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.createdAt.format(DateTimeFormatter.ofPattern("a hh:mm")),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun HomeDrawerContent(
    onNavigateToList: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MinderBackground,
        drawerContentColor = Color.Black
    ) {
        Spacer(Modifier.height(32.dp))
        NavigationDrawerItem(
            label = { Text(androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.home_menu_list)) },
            selected = false,
            onClick = onNavigateToList,
            icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = { Text(androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.home_menu_stats)) },
            selected = false,
            onClick = onNavigateToStatistics,
            icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, null) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeFeedScreenPreview() {
    val now = LocalDateTime.now()
    val mockEntries = listOf(
        JournalEntry(
            id = 1,
            content = "오늘은 기분이 정말 좋다. 맛있는 점심을 먹었다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = now.minusHours(2),
            updatedAt = now.minusHours(2)
        ),
        JournalEntry(
            id = 2,
            content = "조금 피곤하지만 보람찬 하루였다. 프로젝트가 잘 진행되고 있다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = now.minusHours(5),
            updatedAt = now.minusHours(5)
        ),
        JournalEntry(
            id = 3,
            content = "어제는 비가 와서 우울했다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = now.minusDays(1),
            updatedAt = now.minusDays(1)
        )
    )

    val grouped = mapOf(
        "오늘" to mockEntries.take(2),
        "어제" to mockEntries.takeLast(1)
    )

    MinderTheme {
        Scaffold(
            topBar = {
                HomeFeedTopBar(
                    onMenuClick = {},
                    selectedOption = FeedGroupingOption.WEEKLY,
                    onOptionSelected = {})
            },
            containerColor = MinderBackground
        ) { paddingValues ->
            HomeFeedContent(
                modifier = Modifier.padding(paddingValues),
//                uiState = HomeUiState.Success(groupedFeed = emptyMap()),
                uiState = HomeUiState.Success(groupedFeed = emptyMap()),
                onEntryClick = {},
                onWriteClick = {},
                isRefreshing = false,
                isLoadingMore = false,
                isLastPage = false,
                onRefresh = {},
                onLoadMore = {},
                listState = rememberLazyListState(),
                onNavigateToStatistics = { _, _ -> }
            )
        }
    }
}

