package com.kminder.domain.usecase.emotion

import com.kminder.domain.logic.PlutchikEmotionCalculator
import com.kminder.domain.provider.LanguageProvider
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
    private val emotionAnalysisRepository: EmotionAnalysisRepository,
    private val languageProvider: LanguageProvider
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
            val analysisResult = emotionAnalysisRepository.analyzeEmotion(
                text = entry.content,
                language = languageProvider.getLanguageCode()
            )
            val analysis = analysisResult.getOrNull()
            
            // 3. 심층 감정 분석 (EmotionResult 도출)
            val emotionResult = analysis?.let {
                PlutchikEmotionCalculator.analyzeDominantEmotionCombination(it)
            }
            
            // 4. 분석 결과를 일기에 업데이트
            // 4. 분석 결과 저장 (DB 분리됨)
            if (emotionResult != null) {
                journalRepository.saveEmotionAnalysis(
                    journalId = entryId,
                    result = emotionResult,
                    status = com.kminder.domain.model.AnalysisStatus.COMPLETED
                )
            } else {
                 // 분석 실패 또는 결과 없음 처리
                 // 여기서는 일단 성공으로 간주하고 업데이트만? 
                 // 만약 analysis가 null이면 status는 FAILURE가 되어야 할 수도 있음.
                 // 하지만 현재 로직에서는 null이면 그대로 진행. Status 업데이트 필요.
                 if (analysisResult.isFailure) {
                    journalRepository.updateAnalysisStatus(entryId, com.kminder.domain.model.AnalysisStatus.FAILED)
                 }
            }
            
            // 리턴할 객체 업데이트 (메모리 상)
            val updatedEntry = entry.copy(
                id = entryId,
                emotionResult = emotionResult,
                status = if (emotionResult != null) com.kminder.domain.model.AnalysisStatus.COMPLETED else com.kminder.domain.model.AnalysisStatus.FAILED,
                updatedAt = LocalDateTime.now()
            )
             
             // JournalEntry 자체의 업데이트 (status 등)
            journalRepository.updateEntry(updatedEntry)
            
            Result.success(updatedEntry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
