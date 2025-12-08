package com.kminder.domain.usecase.journal

import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.JournalRepository
import javax.inject.Inject

/**
 * 특정 일기를 조회하는 UseCase
 */
class GetJournalEntryUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * ID로 일기를 조회합니다.
     * 
     * @param entryId 조회할 일기의 ID
     * @return 일기 항목 (없으면 null)
     */
    suspend operator fun invoke(entryId: Long): JournalEntry? {
        return journalRepository.getEntryById(entryId)
    }
}
