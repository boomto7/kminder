package com.kminder.domain.usecase.journal

import com.kminder.domain.model.AnalysisStatus
import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.JournalRepository
import javax.inject.Inject

/**
 * 일기를 분석 없이 저장만 하는 UseCase.
 * 저장 시 상태를 [AnalysisStatus.PENDING]으로 설정합니다.
 */
class SaveJournalEntryUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * 일기를 저장합니다.
     *
     * @param entry 저장할 일기 항목
     * @return 저장된 일기의 ID
     */
    suspend operator fun invoke(entry: JournalEntry): Result<Long> {
        return try {
            val pendingEntry = entry.copy(
                status = AnalysisStatus.PENDING
            )
            val id = journalRepository.insertEntry(pendingEntry)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
