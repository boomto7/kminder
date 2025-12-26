package com.kminder.minder.ui.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.journal.DeleteJournalEntryUseCase
import com.kminder.domain.usecase.journal.GetJournalEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryDetailViewModel @Inject constructor(
    private val getJournalEntryUseCase: GetJournalEntryUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase,
    private val saveAnalysisResultUseCase: com.kminder.domain.usecase.analysis.SaveAnalysisResultUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EntryDetailUiState>(EntryDetailUiState.Loading)
    val uiState: StateFlow<EntryDetailUiState> = _uiState.asStateFlow()

    fun loadEntry(entryId: Long) {
        viewModelScope.launch {
            _uiState.value = EntryDetailUiState.Loading
            // 임시 목업 데이터 사용 (UI 테스트용)
            // val entry = getJournalEntryUseCase(entryId)
            
            // MockData에서 찾거나 없으면 첫 번째 데이터 사용
            val mockEntries = com.kminder.minder.data.mock.MockData.mockJournalEntries
            val entry = mockEntries.find { it.id == entryId } ?: mockEntries.firstOrNull()

            if (entry != null) {
                _uiState.value = EntryDetailUiState.Success(entry)
            } else {
                _uiState.value = EntryDetailUiState.Error
            }
        }
    }

    fun deleteEntry(entryId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            deleteJournalEntryUseCase(entryId)
            onDeleted()
        }
    }

    fun retryAnalysis(entryId: Long) {
        viewModelScope.launch {
            // 분석 상태를 PENDING으로 변경하여 재분석 요청 (실제 로직은 Worker 등이 처리한다고 가정)
            // 여기서는 UI 반영을 위해 status만 업데이트
            saveAnalysisResultUseCase(entryId, null, com.kminder.domain.model.AnalysisStatus.PENDING)
            
            // 데이터 갱신
            loadEntry(entryId)
        }
    }
}

sealed interface EntryDetailUiState {
    data object Loading : EntryDetailUiState
    data object Error : EntryDetailUiState
    data class Success(val entry: JournalEntry) : EntryDetailUiState
}
