package com.kminder.domain.usecase.journal

import com.kminder.domain.repository.JournalRepository
import javax.inject.Inject

/**
 * 일기를 삭제하는 UseCase
 */
class DeleteJournalEntryUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * 일기를 삭제합니다.
     * 
     * @param entryId 삭제할 일기의 ID
     */
    suspend operator fun invoke(entryId: Long) {
        journalRepository.deleteEntry(entryId)
    }
}
