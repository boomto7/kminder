package com.kminder.minder.ui.screen.detail

import android.text.style.TtsSpan
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.AnalysisStatus
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.provider.LanguageProvider
import com.kminder.domain.usecase.analysis.SaveAnalysisResultUseCase
import com.kminder.domain.usecase.emotion.AnalyzeEmotionUseCase
import com.kminder.domain.usecase.journal.DeleteJournalEntryUseCase
import com.kminder.domain.usecase.journal.GetJournalEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EntryDetailViewModel @Inject constructor(
    private val getJournalEntryUseCase: GetJournalEntryUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase,
    private val saveAnalysisResultUseCase: SaveAnalysisResultUseCase,
    private val analyzeEmotionUseCase: AnalyzeEmotionUseCase,
    private val languageProvider: LanguageProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<EntryDetailUiState>(EntryDetailUiState.Loading)
    val uiState: StateFlow<EntryDetailUiState> = _uiState.asStateFlow()

    fun loadEntry(entryId: Long) {
        viewModelScope.launch {
            _uiState.value = EntryDetailUiState.Loading
            // 실제 유즈케이스 사용
            val entry = getJournalEntryUseCase(entryId)

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
            val currentState = _uiState.value
            if (currentState is EntryDetailUiState.Success) {
                val entry = currentState.entry

                // 0. 로컬 상태만 변경하여 UI에 로딩 표시 (DB 업데이트 X)
                _uiState.value = currentState.copy(isAnalyzing = true)
                
                Timber.d("Starting analysis for entry $entryId")

                // 1. 분석 수행
                val analysisResult = analyzeEmotionUseCase(
                    text = entry.content, 
                    language = languageProvider.getLanguageCode()
                )

                val analysis = analysisResult.getOrNull()
                
                if (analysis != null) {
                     // 2. 통합 감정 분석 결과로 저장 (SaveAnalysisResultUseCase 내부에서 변환됨)
                     saveAnalysisResultUseCase(
                         journalId = entryId,
                         analysis = analysis,
                         status = AnalysisStatus.COMPLETED
                     )
                } else {
                    // 실패 시 status 업데이트
                    saveAnalysisResultUseCase(
                         journalId = entryId,
                         analysis = null,
                         status = AnalysisStatus.FAILED
                    )
                }

                // 3. 데이터 갱신 (이때 새로운 Entry가 로드되면서 isAnalyzing은 초기화됨)
                loadEntry(entryId)
            }
        }
    }
}

sealed interface EntryDetailUiState {
    data object Loading : EntryDetailUiState
    data object Error : EntryDetailUiState
    data class Success(
        val entry: JournalEntry,
        val isAnalyzing: Boolean = false
    ) : EntryDetailUiState
}
