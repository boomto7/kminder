package com.kminder.domain.usecase.question

import com.kminder.domain.repository.QuestionRepository
import javax.inject.Inject

/**
 * 무작위 질문을 가져오는 UseCase
 */
class GetRandomQuestionUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    /**
     * 무작위 질문을 가져옵니다.
     * 
     * @return 무작위 질문
     */
    suspend operator fun invoke(): String {
        return questionRepository.getRandomQuestion()
    }
}
