package com.kminder.minder.util

import com.kminder.domain.repository.JournalRepository
import com.kminder.minder.data.mock.MockData
import javax.inject.Inject

class MockDataInitializer @Inject constructor(
    private val journalRepository: JournalRepository
) {

    suspend fun initialize() {
        MockData.mockJournalEntries.forEach { entry ->
            val entryId = journalRepository.insertEntry(entry)

            // emotionResult가 있으면 별도로 저장해야 함 (테이블이 분리되어 있음)
            if (entry.emotionResult != null) {
                journalRepository.saveEmotionAnalysis(
                    journalId = entryId,
                    result = entry.emotionResult,
                    status = entry.status
                )
            }
        }
    }
}
