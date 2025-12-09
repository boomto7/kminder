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
    
    private val _uiState = MutableStateFlow<EntryListUiState>(EntryListUiState.Loading)
    val uiState: StateFlow<EntryListUiState> = _uiState.asStateFlow()

    // Pagination State
    private var currentPage = 0
    private val pageSize = 10
    private val allMockEntries = com.kminder.minder.data.mock.MockData.mockJournalEntries
    private val currentEntries = mutableListOf<JournalEntry>()
    private var isLastPage = false
    private var isLoadingMore = false
    
    init {
        loadEntries()
    }
    
    /**
     * 일기 목록 로드 (초기 로드)
     */
    fun loadEntries() {
        viewModelScope.launch {
            currentPage = 0
            currentEntries.clear()
            isLastPage = false
            isLoadingMore = false
            
            loadNextPage()
        }
    }

    /**
     * 추가 데이터 로드 (스크롤 시 호출)
     */
    fun loadMoreEntries() {
        if (isLoadingMore || isLastPage) return

        viewModelScope.launch {
            isLoadingMore = true
            delay(1000) 
            loadNextPage()
            isLoadingMore = false
        }
    }

    private fun loadNextPage() {
        val start = currentPage * pageSize
        val end = minOf(start + pageSize, allMockEntries.size)
        
        if (start >= allMockEntries.size) {
            isLastPage = true
            return
        }

        val newEntries = allMockEntries.subList(start, end)
        currentEntries.addAll(newEntries)
        
        if (end >= allMockEntries.size) {
            isLastPage = true
        } else {
            currentPage++
        }

        _uiState.value = if (currentEntries.isEmpty()) {
            EntryListUiState.Empty
        } else {
            EntryListUiState.Success(currentEntries.toList())
        }
    }
}

/**
 * 일기 목록 화면 UI 상태
 */
sealed interface EntryListUiState {
    data object Loading : EntryListUiState
    data object Empty : EntryListUiState
    data class Success(val entries: List<JournalEntry>) : EntryListUiState
}
