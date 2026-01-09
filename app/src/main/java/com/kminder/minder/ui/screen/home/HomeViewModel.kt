package com.kminder.minder.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.EmotionType
import com.kminder.domain.model.IntegratedAnalysis
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.analysis.AnalyzeIntegratedEmotionUseCase
import com.kminder.domain.usecase.journal.GetAllJournalEntriesUseCase
import com.kminder.minder.data.mock.MockData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllJournalEntriesUseCase: GetAllJournalEntriesUseCase,
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

    private var currentPage = 0
    private val PAGE_SIZE = 20
    private val _allLoadedEntries = mutableListOf<JournalEntry>()

    init {
        viewModelScope.launch {
            loadFeed(reset = true)
        }
    }
    
    fun setGroupingOption(option: FeedGroupingOption) {
        _groupingOption.value = option
        
        // Optimize: Don't reload from scratch. Just re-group existing entries.
        if (_allLoadedEntries.isNotEmpty()) {
            val grouped = groupEntries(_allLoadedEntries, option)
            _uiState.value = HomeUiState.Success(groupedFeed = grouped)
        } else {
            // New load if empty
            viewModelScope.launch {
                loadFeed(reset = true)
            }
        }
    }

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            kotlinx.coroutines.delay(1500) // Simulate network delay
            loadFeed(reset = true)
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        if (_isLoadingMore.value || _uiState.value !is HomeUiState.Success || _isLastPage.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            kotlinx.coroutines.delay(1000) // Simulate network delay
            loadFeed(reset = false)
            _isLoadingMore.value = false
        }
    }

    private suspend fun loadFeed(reset: Boolean) {
        if (reset) {
            currentPage = 0
            _allLoadedEntries.clear()
            _isLastPage.value = false
            // Only show big loader if NOT refreshing (screen is empty or first load)
            if (!_isRefreshing.value) {
                _uiState.value = HomeUiState.Loading
            }
        } else {
            currentPage++
        }
        
        // Simulate Pagination from MockData
        val allMockData = MockData.mockJournalEntries
        val startIndex = currentPage * PAGE_SIZE
        val endIndex = (startIndex + PAGE_SIZE).coerceAtMost(allMockData.size)
        
        val newEntries = if (startIndex < allMockData.size) {
            allMockData.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        // Check if last page
        if (newEntries.size < PAGE_SIZE) {
            _isLastPage.value = true
        }

        if (reset && newEntries.isEmpty()) {
            _uiState.value = HomeUiState.Empty
        } else {
            _allLoadedEntries.addAll(newEntries)
            
            // Grouping Logic on ALL loaded entries
            val grouped = groupEntries(_allLoadedEntries, _groupingOption.value)
            
            _uiState.value = HomeUiState.Success(
                groupedFeed = grouped
            )
        }
    }

    private fun groupEntries(entries: List<JournalEntry>, option: FeedGroupingOption): Map<String, List<JournalEntry>> {
        val sorted = entries.sortedByDescending { it.createdAt }
        return when (option) {
            FeedGroupingOption.DAILY -> {
                val formatter = DateTimeFormatter.ofPattern("M월 d일 EEEE", Locale.KOREA)
                sorted.groupBy { it.createdAt.format(formatter) }
            }
            FeedGroupingOption.WEEKLY -> {
                // Simple Weekly Grouping (Week of Year)
                // Note: For production, a more robust Calendar-based logic is preferred.
                val formatter = DateTimeFormatter.ofPattern("M월 W주차", Locale.KOREA)
                sorted.groupBy { it.createdAt.format(formatter) }
            }
            FeedGroupingOption.MONTHLY -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREA)
                sorted.groupBy { it.createdAt.format(formatter) }
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
