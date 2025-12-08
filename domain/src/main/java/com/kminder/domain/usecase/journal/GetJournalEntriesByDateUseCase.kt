package com.kminder.domain.usecase.journal

import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.JournalRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * 특정 날짜의 일기 목록을 조회하는 UseCase
 */
class GetJournalEntriesByDateUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * 특정 날짜의 일기 목록을 조회합니다.
     * 
     * @param date 조회할 날짜
     * @return 일기 목록
     */
    suspend operator fun invoke(date: LocalDate): List<JournalEntry> {
        return journalRepository.getEntriesByDate(date)
    }
}
