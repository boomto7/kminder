package com.kminder.domain.repository

/**
 * 문답 모드를 위한 질문 Repository Interface
 */
interface QuestionRepository {
    
    /**
     * 무작위 질문을 가져옵니다.
     * 
     * @return 무작위 질문
     */
    suspend fun getRandomQuestion(): String
    
    /**
     * 모든 질문 목록을 가져옵니다.
     * 
     * @return 질문 목록
     */
    suspend fun getAllQuestions(): List<String>
}
