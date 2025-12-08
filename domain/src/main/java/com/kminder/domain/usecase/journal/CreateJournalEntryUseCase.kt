package com.kminder.domain.usecase.journal

import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.JournalRepository
import javax.inject.Inject

/**
 * 새로운 일기를 저장하는 UseCase
 */
class CreateJournalEntryUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * 일기를 저장합니다.
     * 
     * @param entry 저장할 일기 항목
     * @return 저장된 일기의 ID
     */
    suspend operator fun invoke(entry: JournalEntry): Long {
        return journalRepository.insertEntry(entry)
    }
}
