package com.kminder.domain.usecase.journal

import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 모든 일기 목록을 조회하는 UseCase
 */
class GetAllJournalEntriesUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * 모든 일기 목록을 Flow로 조회합니다.
     * 
     * @return 일기 목록 Flow
     */
    operator fun invoke(): Flow<List<JournalEntry>> {
        return journalRepository.getAllEntries()
    }
}
