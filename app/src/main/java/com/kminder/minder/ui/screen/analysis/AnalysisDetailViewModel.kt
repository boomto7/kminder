package com.kminder.minder.ui.screen.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.journal.GetJournalEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalysisDetailViewModel @Inject constructor(
    private val getJournalEntryUseCase: GetJournalEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnalysisDetailUiState>(AnalysisDetailUiState.Loading)
    val uiState: StateFlow<AnalysisDetailUiState> = _uiState.asStateFlow()

    fun loadEntry(entryId: Long) {
        viewModelScope.launch {
            _uiState.value = AnalysisDetailUiState.Loading
            
            // 실제 유즈케이스 호출
            // val entry = getJournalEntryUseCase(entryId)

            // Mock Data 사용 (EntryDetailViewModel과 동일한 방식 유지)
            val mockEntries = com.kminder.minder.data.mock.MockData.mockJournalEntries
            val entry = mockEntries.find { it.id == entryId } ?: mockEntries.firstOrNull()

            if (entry != null) {
                _uiState.value = AnalysisDetailUiState.Success(entry)
            } else {
                _uiState.value = AnalysisDetailUiState.Error
            }
        }
    }
}

sealed interface AnalysisDetailUiState {
    data object Loading : AnalysisDetailUiState
    data object Error : AnalysisDetailUiState
    data class Success(val entry: JournalEntry) : AnalysisDetailUiState
}
