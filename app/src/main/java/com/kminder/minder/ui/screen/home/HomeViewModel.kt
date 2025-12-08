package com.kminder.minder.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.journal.GetAllJournalEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 홈 화면 ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllJournalEntriesUseCase: GetAllJournalEntriesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadRecentEntries()
    }
    
    /**
     * 최근 일기 목록 로드
     */
    private fun loadRecentEntries() {
        viewModelScope.launch {
            getAllJournalEntriesUseCase().collect { entries ->
                _uiState.value = if (entries.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    HomeUiState.Success(entries.take(5)) // 최근 5개만
                }
            }
        }
    }
}

/**
 * 홈 화면 UI 상태
 */
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState
    data class Success(val recentEntries: List<JournalEntry>) : HomeUiState
}
