package com.kminder.minder.ui.screen.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.EntryType
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.usecase.emotion.SaveAndAnalyzeJournalEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WriteUiState(
    val content: String = "",
    val isSaving: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isSuccess: Boolean = false,
    val savedEntryId: Long? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val saveAndAnalyzeUseCase: SaveAndAnalyzeJournalEntryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

    fun updateContent(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun saveAndAnalyze() {
        val currentContent = _uiState.value.content
        if (currentContent.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, isAnalyzing = true, errorMessage = null) }

            val newEntry = JournalEntry(
                content = currentContent,
                entryType = EntryType.FREE_WRITING,
                createdAt = java.time.LocalDateTime.now(),
                updatedAt = java.time.LocalDateTime.now()
            )

            val result = saveAndAnalyzeUseCase(newEntry)

            if (result.isSuccess) {
                val savedEntry = result.getOrNull()
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        isAnalyzing = false, 
                        isSuccess = true,
                        savedEntryId = savedEntry?.id
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isSaving = false, 
                        isAnalyzing = false, 
                        errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                    ) 
                }
            }
        }
    }
    
    fun resetState() {
        _uiState.update { it.copy(isSuccess = false, savedEntryId = null, errorMessage = null) }
    }
}
