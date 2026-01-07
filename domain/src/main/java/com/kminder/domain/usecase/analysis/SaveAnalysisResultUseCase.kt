package com.kminder.domain.usecase.analysis

import com.kminder.domain.logic.PlutchikEmotionCalculator
import com.kminder.domain.model.AnalysisStatus
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.repository.JournalRepository
import javax.inject.Inject

/**
 * 감정 분석 결과를 저장하는 UseCase
 */
class SaveAnalysisResultUseCase @Inject constructor(
    private val journalRepository: JournalRepository
) {
    /**
     * 특정 일기에 대한 감정 분석 결과를 저장합니다.
     */
    suspend operator fun invoke(journalId: Long, analysis: EmotionAnalysis?, status: AnalysisStatus) {
        val emotionResult = analysis?.let {
            PlutchikEmotionCalculator.analyzeDominantEmotionCombination(it)
        }
        journalRepository.saveEmotionAnalysis(journalId, emotionResult, status)
    }
}
