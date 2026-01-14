package com.kminder.minder.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.analysis.AnalyzeIntegratedEmotionUseCase
import com.kminder.domain.usecase.journal.GetDynamicJournalEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import timber.log.Timber
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

    // Force refresh trigger
    private val _refreshTrigger = MutableStateFlow(0)

    // 로드된 모든 엔트리 (Grouping을 위해 캐시)
    private var _cachedAllEntries = listOf<JournalEntry>()

    init {
        viewModelScope.launch {
            // Limit 변경 또는 Refresh Trigger 시 마다 DB 재구독 (flatMapLatest)
            // DB 변경 시에도 자동 방출 (Room Flow)
            combine(_limit, _refreshTrigger) { limit, _ -> limit }
                .flatMapLatest { limit ->
                    getDynamicJournalEntriesUseCase(limit)
                        .catch { e ->
                            Timber.e(e, "Error loading journal entries")
                            // 에러 발생 시 빈 리스트 방출하여 collect 블록 실행 유도 (플래그 리셋 목적)
                            // 실제 프로덕션에서는 에러 상태 처리가 필요할 수 있음
                            emit(emptyList())
                        }
                }.collect { entries ->
                _cachedAllEntries = entries
                
                // 데이터가 비어있으면 Empty
                // Refresh 여부와 관계없이 데이터가 비어있으면 Empty 상태로 전환해야 함
                if (entries.isEmpty()) {
                     _uiState.value = HomeUiState.Empty
                     _isLastPage.value = true // 데이터가 없으니 마지막 페이지
                } else {
                    // 데이터가 있으면 Success
                    updateUiState()
                    
                    // 만약 요청한 limit보다 적게 왔다면 더 이상 데이터가 없는 것 (Last Page)
                    _isLastPage.value = entries.size < _limit.value
                }
                
                // 로딩 상태 해제
                if (_isRefreshing.value) {
                    // 로컬 DB 로드가 너무 빨라 로딩 UI가 보이지 않는 현상 방지을 위해 최소 시간 보장
                    delay(300)
                }
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
        if (_isRefreshing.value || _isLoadingMore.value) return
        _isRefreshing.value = true
        
        // 중요: Limit가 이미 초기값(20)인 경우 StateFlow는 변경을 감지하지 않음.
        // 따라서 Limit 값을 확인하여 필요한 경우에만 Trigger를 갱신함.
        if (_limit.value != LIMIT_STEP) {
            _limit.value = LIMIT_STEP
        } else {
            _refreshTrigger.value += 1
        }
    }

    fun loadMore() {
        if (_isLoadingMore.value || _uiState.value !is HomeUiState.Success || _isLastPage.value) return
        _isLoadingMore.value = true
        // Limit 증가 -> Flow 재시작 -> DB 조회 (전체 데이터 + 추가분)
        _limit.value += LIMIT_STEP
    }

    private fun groupEntries(
        entries: List<JournalEntry>,
        option: FeedGroupingOption
    ): Map<String, List<JournalEntry>> {
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
