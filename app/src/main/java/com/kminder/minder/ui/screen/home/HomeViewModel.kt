package com.kminder.minder.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.IntegratedAnalysis
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.analysis.AnalyzeIntegratedEmotionUseCase
import com.kminder.domain.usecase.journal.GetDynamicJournalEntriesUseCase
import com.kminder.minder.data.mock.MockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeViewModel @Inject constructor(
    private val getDynamicJournalEntriesUseCase: GetDynamicJournalEntriesUseCase,
    private val analyzeIntegratedEmotionUseCase: AnalyzeIntegratedEmotionUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // Grouping Option (Daily, Weekly, Monthly)
    private val _groupingOption = MutableStateFlow(FeedGroupingOption.DAILY)
    val groupingOption: StateFlow<FeedGroupingOption> = _groupingOption.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _isLastPage = MutableStateFlow(false)
    val isLastPage: StateFlow<Boolean> = _isLastPage.asStateFlow()

    // Dynamic Limit (starts at 20)
    private val _limit = MutableStateFlow(20)
    private val LIMIT_STEP = 20
    
    // 로드된 모든 엔트리 (Grouping을 위해 캐시)
    private var _cachedAllEntries = listOf<JournalEntry>()

    init {
        viewModelScope.launch {
            // Limit 변경 시 마다 DB 재구독 (flatMapLatest)
            // DB 변경 시에도 자동 방출 (Room Flow)
            _limit.flatMapLatest { limit ->
                getDynamicJournalEntriesUseCase(limit)
            }.collect { entries ->
                _cachedAllEntries = entries
                
                // 데이터가 비어있으면 Empty
                // 하지만 Refresh 중이 아니고 데이터가 없으면 Empty 처리
                if (entries.isEmpty() && !_isRefreshing.value) {
                     _uiState.value = HomeUiState.Empty
                     _isLastPage.value = true // 데이터가 없으니 마지막 페이지
                } else {
                    // 데이터가 있으면 Success
                    updateUiState()
                    
                    // 만약 요청한 limit보다 적게 왔다면 더 이상 데이터가 없는 것 (Last Page)
                    _isLastPage.value = entries.size < _limit.value
                }
                
                // 로딩 상태 해제
                _isRefreshing.value = false
                _isLoadingMore.value = false
            }
        }
    }
    
    fun setGroupingOption(option: FeedGroupingOption) {
        _groupingOption.value = option
        updateUiState()
    }

    private fun updateUiState() {
        if (_cachedAllEntries.isNotEmpty()) {
            val grouped = groupEntries(_cachedAllEntries, _groupingOption.value)
            _uiState.value = HomeUiState.Success(groupedFeed = grouped)
        }
    }

    fun refresh() {
        if (_isRefreshing.value) return
        _isRefreshing.value = true
        // Refresh 시 Limit 초기화 -> 자동으로 Flow 재시작 -> DB 조회
        // 딜레이 제거
        _limit.value = LIMIT_STEP
    }

    fun loadMore() {
        if (_isLoadingMore.value || _uiState.value !is HomeUiState.Success || _isLastPage.value) return
        _isLoadingMore.value = true
        // Limit 증가 -> Flow 재시작 -> DB 조회 (전체 데이터 + 추가분)
        _limit.value += LIMIT_STEP
    }

    private fun groupEntries(entries: List<JournalEntry>, option: FeedGroupingOption): Map<String, List<JournalEntry>> {
        return when (option) {
            FeedGroupingOption.DAILY -> {
                val formatter = DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREA)
                entries.groupBy { it.createdAt.format(formatter) }
            }
            FeedGroupingOption.WEEKLY -> {
                val formatter = DateTimeFormatter.ofPattern("M월 W주차", Locale.KOREA)
                entries.groupBy { it.createdAt.format(formatter) }
            }
            FeedGroupingOption.MONTHLY -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREA)
                entries.groupBy { it.createdAt.format(formatter) }
            }
        }
    }
}

enum class FeedGroupingOption {
    DAILY, WEEKLY, MONTHLY
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(
        val groupedFeed: Map<String, List<JournalEntry>>
    ) : HomeUiState
}
