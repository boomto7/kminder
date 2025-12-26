package com.kminder.minder.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.JournalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 일기 목록 ViewModel
 */
@HiltViewModel
class EntryListViewModel @Inject constructor(
    // private val getAllJournalEntriesUseCase: GetAllJournalEntriesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EntryListUiState())
    val uiState: StateFlow<EntryListUiState> = _uiState.asStateFlow()

    // Pagination State
    private var currentPage = 0
    private val pageSize = 10
    private val allMockEntries = com.kminder.minder.data.mock.MockData.mockJournalEntries
    private val currentEntries = mutableListOf<JournalEntry>()
    private var isLastPage = false
    
    init {
        loadEntries()
    }
    
    /**
     * 일기 목록 로드 (초기 로드)
     */
    fun loadEntries() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            resetPagination()
            loadNextPage()
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    /**
     * 당겨서 새로고침
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            resetPagination()
            // Simulate network delay for refresh feel
            delay(1000)
            loadNextPage()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    private fun resetPagination() {
        currentPage = 0
        currentEntries.clear()
        isLastPage = false
    }

    /**
     * 추가 데이터 로드 (스크롤 시 호출)
     */
    fun loadMoreEntries() {
        if (_uiState.value.isLoadingMore || isLastPage) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            delay(1000) 
            loadNextPage()
            _uiState.value = _uiState.value.copy(isLoadingMore = false)
        }
    }

    private fun loadNextPage() {
        // 이미 마지막 페이지라면 로드 중단 (하지만 loadMoreEntries에서 체크하므로 여기선 방어 코드)
        if (currentPage * pageSize >= allMockEntries.size) {
            isLastPage = true
            return
        }

        val start = currentPage * pageSize
        val end = minOf(start + pageSize, allMockEntries.size)
        
        val newEntries = allMockEntries.subList(start, end)
        currentEntries.addAll(newEntries)
        
        if (end >= allMockEntries.size) {
            isLastPage = true
        } else {
            currentPage++
        }

        _uiState.value = _uiState.value.copy(entries = currentEntries.toList())
    }
}

/**
 * 일기 목록 화면 UI 상태
 */
data class EntryListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val entries: List<JournalEntry> = emptyList()
)
