package com.kminder.domain.usecase.journal

import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.JournalRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 일기를 수정하는 UseCase
 */
class UpdateJournalEntryUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * 일기를 수정합니다.
     *
     * @param entry 수정할 일기 항목
     */
    suspend operator fun invoke(entry: JournalEntry): Result<Unit> {
        return try {
            val updatedEntry = entry.copy(
                updatedAt = java.time.LocalDateTime.now()
            )
            journalRepository.updateEntry(updatedEntry)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
