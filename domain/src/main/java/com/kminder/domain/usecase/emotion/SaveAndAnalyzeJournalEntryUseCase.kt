package com.kminder.domain.usecase.emotion

import com.kminder.domain.model.JournalEntry
import com.kminder.domain.repository.EmotionAnalysisRepository
import com.kminder.domain.repository.JournalRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 일기를 저장하고 감정 분석을 수행하는 UseCase
 */
class SaveAndAnalyzeJournalEntryUseCase @Inject constructor(
    private val journalRepository: JournalRepository,
    private val emotionAnalysisRepository: EmotionAnalysisRepository
) {
    /**
     * 일기를 저장하고 감정 분석을 수행합니다.
     * 
     * @param entry 저장할 일기 항목
     * @return 감정 분석이 완료된 일기 항목
     */
    suspend operator fun invoke(entry: JournalEntry): Result<JournalEntry> {
        return try {
            // 1. 일기 저장
            val entryId = journalRepository.insertEntry(entry)
            
            // 2. 감정 분석 수행
            val analysisResult = emotionAnalysisRepository.analyzeEmotion(entry.content)
            
            // 3. 분석 결과를 일기에 업데이트
            val updatedEntry = entry.copy(
                id = entryId,
                emotionAnalysis = analysisResult.getOrNull(),
                updatedAt = LocalDateTime.now()
            )
            
            journalRepository.updateEntry(updatedEntry)
            
            Result.success(updatedEntry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
