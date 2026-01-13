package com.kminder.minder.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.journal.GetAllJournalEntriesUseCase
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
/**
 * 일기 목록 ViewModel
 */
@HiltViewModel
class EntryListViewModel @Inject constructor(
    private val getAllJournalEntriesUseCase: GetAllJournalEntriesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EntryListUiState())
    val uiState: StateFlow<EntryListUiState> = _uiState.asStateFlow()

    // Pagination State
    private var currentPage = 0
    private val pageSize = 10
    
    // DB에서 가져온 전체 엔트리 캐시
    private var _cachedAllEntries = listOf<JournalEntry>()
    // 현재 페이지에 보여지는 엔트리 (누적)
    private val _displayEntries = mutableListOf<JournalEntry>()
    
    private var isLastPage = false
    
    init {
        // DB 변경사항 실시간 감지
        viewModelScope.launch {
            getAllJournalEntriesUseCase().collect { entries ->
                // 최신순 정렬
                _cachedAllEntries = entries.sortedByDescending { it.createdAt }
                
                // 데이터 갱신 시 초기 페이지 다시 로드
                loadEntries()
            }
        }
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
     * 당겨서 새로고침 (DB는 자동 갱신되지만 UI 리프레시 효과)
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            resetPagination() // UI 목록 초기화
            delay(1000)
            loadNextPage()
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    private fun resetPagination() {
        currentPage = 0
        _displayEntries.clear()
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
        // 전체 데이터 캐시 사용
        val allData = _cachedAllEntries
        
        if (currentPage * pageSize >= allData.size) {
            isLastPage = true
            return
        }

        val start = currentPage * pageSize
        val end = minOf(start + pageSize, allData.size)
        
        val newEntries = allData.subList(start, end)
        _displayEntries.addAll(newEntries)
        
        if (end >= allData.size) {
            isLastPage = true
        } else {
            currentPage++
        }

        _uiState.value = _uiState.value.copy(entries = _displayEntries.toList())
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
