package com.kminder.domain.usecase.journal

import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.JournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Limit을 기반으로 페이징된 일기 목록을 Flow로 관찰하는 UseCase
 */
class GetDynamicJournalEntriesUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    operator fun invoke(limit: Int): Flow<List<JournalEntry>> {
        return journalRepository.getJournalEntriesStream(limit)
    }
}
