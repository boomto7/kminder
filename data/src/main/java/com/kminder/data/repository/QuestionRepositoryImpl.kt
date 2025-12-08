package com.kminder.data.repository

import com.kminder.domain.repository.QuestionRepository
import javax.inject.Inject
import kotlin.random.Random

/**
 * QuestionRepository 구현체
 */
class QuestionRepositoryImpl @Inject constructor() : QuestionRepository {
    
    /**
     * 미리 정의된 질문 목록
     */
    private val questions = listOf(
        "오늘 가장 기억에 남는 순간은 무엇인가요?",
        "지금 이 순간 어떤 감정을 느끼고 있나요?",
        "오늘 감사했던 일이 있나요?",
        "최근에 당신을 웃게 만든 것은 무엇인가요?",
        "지금 가장 걱정되는 것은 무엇인가요?",
        "오늘 누군가에게 친절을 베풀었나요?",
        "최근에 새롭게 배운 것이 있나요?",
        "지금 가장 하고 싶은 일은 무엇인가요?",
        "오늘 당신에게 힘이 되어준 사람이나 것은 무엇인가요?",
        "최근에 성취한 작은 일이라도 있나요?",
        "지금 이 순간 필요한 것은 무엇인가요?",
        "오늘 하루 중 가장 평화로웠던 순간은 언제인가요?",
        "최근에 당신을 놀라게 한 일이 있나요?",
        "지금 가장 기대되는 것은 무엇인가요?",
        "오늘 스스로에게 해주고 싶은 말이 있나요?",
        "최근에 당신의 마음을 움직인 것은 무엇인가요?",
        "지금 이 순간 당신의 기분을 색깔로 표현한다면?",
        "오늘 가장 힘들었던 순간은 무엇이었나요?",
        "최근에 당신을 행복하게 만든 작은 일은 무엇인가요?",
        "지금 가장 듣고 싶은 말은 무엇인가요?",
        "오늘 하루를 한 문장으로 표현한다면?",
        "최근에 당신이 내린 좋은 결정이 있나요?",
        "지금 이 순간 가장 소중한 것은 무엇인가요?",
        "오늘 당신을 지치게 만든 것은 무엇인가요?",
        "최근에 당신에게 영감을 준 것은 무엇인가요?",
        "지금 가장 편안함을 느끼는 순간은 언제인가요?",
        "오늘 스스로를 칭찬해주고 싶은 부분이 있나요?",
        "최근에 당신의 생각을 바꾼 경험이 있나요?",
        "지금 이 순간 당신에게 가장 중요한 가치는 무엇인가요?",
        "오늘 하루 동안 느낀 다양한 감정들을 나열해본다면?"
    )
    
    override suspend fun getRandomQuestion(): String {
        val randomIndex = Random.nextInt(questions.size)
        return questions[randomIndex]
    }
    
    override suspend fun getAllQuestions(): List<String> {
        return questions
    }
}
