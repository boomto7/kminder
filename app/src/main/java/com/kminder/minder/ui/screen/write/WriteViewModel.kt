package com.kminder.minder.ui.screen.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kminder.domain.model.EntryType
import com.kminder.domain.model.JournalEntry

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
    val errorMessage: String? = null,
    val isEditMode: Boolean = false
)

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val saveEntryUseCase: com.kminder.domain.usecase.journal.SaveJournalEntryUseCase,
    private val updateEntryUseCase: com.kminder.domain.usecase.journal.UpdateJournalEntryUseCase,
    private val getJournalEntryUseCase: com.kminder.domain.usecase.journal.GetJournalEntryUseCase,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: 0L
    private val _uiState = MutableStateFlow(WriteUiState())
    val uiState: StateFlow<WriteUiState> = _uiState.asStateFlow()

    init {
        if (entryId != 0L) {
            _uiState.update { it.copy(isEditMode = true) }
            loadEntry(entryId)
        }
    }

    private fun loadEntry(id: Long) {
        viewModelScope.launch {
            val entry = getJournalEntryUseCase(id)
            if (entry != null) {
                _uiState.update { it.copy(content = entry.content) }
            }
        }
    }

    fun updateContent(newContent: String) {
        _uiState.update { it.copy(content = newContent) }
    }

    fun saveEntry() {
        val currentContent = _uiState.value.content
        if (currentContent.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            try {
                if (entryId == 0L) {
                    // New Entry
                    val newEntry = JournalEntry(
                        content = currentContent,
                        entryType = EntryType.FREE_WRITING,
                        createdAt = java.time.LocalDateTime.now(),
                        updatedAt = java.time.LocalDateTime.now()
                    )
                    val result = saveEntryUseCase(newEntry)
                    handleResult(result)
                } else {
                    // Update Existing Entry
                    val existingEntry = getJournalEntryUseCase(entryId)
                    if (existingEntry != null) {
                        val updatedEntry = existingEntry.copy(
                            content = currentContent,
                            // updatedAt is handled by UseCase
                        )
                        val result = updateEntryUseCase(updatedEntry)
                        if (result.isSuccess) {
                            _uiState.update { 
                                it.copy(
                                    isSaving = false, 
                                    isSuccess = true,
                                    savedEntryId = entryId
                                ) 
                            }
                        } else {
                            handleError(result.exceptionOrNull())
                        }
                    } else {
                        handleError(Exception("Entry not found"))
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleResult(result: Result<Long>) {
        if (result.isSuccess) {
            val savedId = result.getOrNull()
            _uiState.update { 
                it.copy(
                    isSaving = false, 
                    isSuccess = true,
                    savedEntryId = savedId
                ) 
            }
        } else {
            handleError(result.exceptionOrNull())
        }
    }

    private fun handleError(e: Throwable?) {
        _uiState.update { 
            it.copy(
                isSaving = false, 
                errorMessage = e?.message ?: "Unknown error"
            ) 
        }
    }
    
    fun resetState() {
        _uiState.update { it.copy(isSuccess = false, savedEntryId = null, errorMessage = null) }
    }
}
