package com.kminder.minder.data.mock

import com.kminder.domain.logic.PlutchikEmotionDetailCalculator
import com.kminder.domain.model.*
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * 임시 목업 데이터
 * 플루치크 감정 모델의 모든 카테고리(단일 24종 + 복합 28종 = 총 52종)를 테스트하기 위한 데이터셋입니다.
 * 각 데이터는 UI 풍성함을 위해 5~10개의 키워드를 포함합니다.
 */
object MockData {
    private val calculator = PlutchikEmotionDetailCalculator()

    private fun createEmotion(
        joy: Float = 0f, trust: Float = 0f, fear: Float = 0f, surprise: Float = 0f,
        sadness: Float = 0f, disgust: Float = 0f, anger: Float = 0f, anticipation: Float = 0f,
        keywords: List<EmotionKeyword> = emptyList()
    ) = EmotionAnalysis(
        joy = joy, trust = trust, fear = fear, surprise = surprise,
        sadness = sadness, disgust = disgust, anger = anger, anticipation = anticipation,
        keywords = keywords
    )

    private fun createResult(analysis: EmotionAnalysis): EmotionResult {
        return calculator.classify(analysis)
    }

    // 각 감정별 풍부한 키워드 풀
    private val keywordPool = mapOf(
        EmotionType.JOY to listOf("성취감", "환희", "즐거운 대화", "따뜻한 햇살", "맛있는 식사", "친구의 미소", "소소한 행복", "기쁜 소식", "뿌듯한 마음", "활기찬 아침"),
        EmotionType.TRUST to listOf("든든한 동료", "포근한 지지", "믿음직한 태도", "정직한 고백", "깊은 신뢰", "안전한 장소", "헌신적인 태도", "편안한 유대감", "따뜻한 배려", "확고한 신념"),
        EmotionType.FEAR to listOf("어두운 밤", "낯선 시선", "불안한 예감", "차가운 분위기", "위태로운 상황", "긴박한 순간", "떨리는 목소리", "압도적인 공포", "식은땀", "숨막히는 긴장"),
        EmotionType.SURPRISE to listOf("예상치 못한 선물", "갑작스러운 방문", "놀라운 소식", "신기한 광경", "당혹스러운 전개", "기막힌 우연", "충격적인 반전", "멍해진 기분", "눈이 커지는 순간", "숨겨진 진실"),
        EmotionType.SADNESS to listOf("깊은 상실감", "텅 빈 가슴", "울적한 기분", "아픈 추억", "외로운 새벽", "흐린 날씨", "서글픈 눈물", "가슴 저린 이별", "무거운 침묵", "안타까운 마음"),
        EmotionType.DISGUST to listOf("불쾌한 냄새", "거북한 표정", "역겨운 상황", "혐오스러운 태도", "지저분한 환경", "무례한 말투", "비도덕적인 행동", "꺼림칙한 기분", "추악한 진실", "눈살 찌푸려지는 광경"),
        EmotionType.ANGER to listOf("치밀어오르는 화", "부당한 대우", "날카로운 고성", "폭발할 듯한 열기", "억울한 심정", "격렬한 저항", "차오르는 분노", "강한 질책", "거친 숨소리", "참을 수 없는 짜증"),
        EmotionType.ANTICIPATION to listOf("다가올 여행", "설레는 첫 만남", "두근거리는 계획", "희망찬 비전", "새로운 도전", "찬란한 미래", "기다림의 즐거움", "벅차오르는 열정", "원대한 꿈", "내일의 기대")
    )

    private fun generateKeywords(type1: EmotionType, type2: EmotionType? = null): List<EmotionKeyword> {
        val count = Random.nextInt(7, 11) // 7 to 10 inclusive
        val result = mutableListOf<EmotionKeyword>()
        
        // 첫 번째 감정에서 과반수 추출
        val pool1 = keywordPool[type1] ?: emptyList()
        pool1.shuffled().take(if (type2 == null) count else count / 2 + 1).forEach {
            val score = Random.nextDouble(0.7, 1.0).toFloat()
            result.add(EmotionKeyword(it, type1, score))
        }
        
        // 두 번째 감정이 있다면 나머지를 채움
        if (type2 != null) {
            val pool2 = keywordPool[type2] ?: emptyList()
            pool2.shuffled().take(count - result.size).forEach {
                val score = Random.nextDouble(0.5, 0.8).toFloat()
                result.add(EmotionKeyword(it, type2, score))
            }
        }
        
        return result.shuffled()
    }

    val mockJournalEntries = mutableListOf<JournalEntry>().apply {
        var currentId = 1L

        // --- 1. 단일 감정 (8종 * 3강도 = 24개) ---
        val types = listOf(
            EmotionType.JOY, EmotionType.TRUST, EmotionType.FEAR, EmotionType.SURPRISE,
            EmotionType.SADNESS, EmotionType.DISGUST, EmotionType.ANGER, EmotionType.ANTICIPATION
        )
        val intensities = listOf(
            Triple("강함", 0.9f, "정말 압도적인"),
            Triple("중간", 0.6f, "적당히"),
            Triple("약함", 0.3f, "약간의")
        )

        types.forEach { type ->
            intensities.forEach { (label, score, prefix) ->
                add(JournalEntry(
                    id = currentId++,
                    content = "$prefix ${type.name}을 느끼는 중입니다. ($label)",
                    entryType = EntryType.FREE_WRITING,
                    createdAt = LocalDateTime.now().minusHours(currentId.toLong()),
                    updatedAt = LocalDateTime.now().minusHours(currentId.toLong()),
                    emotionResult = createResult(when(type) {
                        EmotionType.JOY -> createEmotion(joy = score)
                        EmotionType.TRUST -> createEmotion(trust = score)
                        EmotionType.FEAR -> createEmotion(fear = score)
                        EmotionType.SURPRISE -> createEmotion(surprise = score)
                        EmotionType.SADNESS -> createEmotion(sadness = score)
                        EmotionType.DISGUST -> createEmotion(disgust = score)
                        EmotionType.ANGER -> createEmotion(anger = score)
                        EmotionType.ANTICIPATION -> createEmotion(anticipation = score)
                        else -> createEmotion()
                    }.copy(keywords = generateKeywords(type))),
                    status = AnalysisStatus.COMPLETED
                ))
            }
        }

        // --- 2. 1차 이중감정 (Primary Dyads - 8개) ---
        val primaryPairs = listOf(
            Pair(EmotionType.JOY, EmotionType.TRUST),         // Love
            Pair(EmotionType.TRUST, EmotionType.FEAR),        // Submission
            Pair(EmotionType.FEAR, EmotionType.SURPRISE),     // Awe
            Pair(EmotionType.SURPRISE, EmotionType.SADNESS),  // Disapproval
            Pair(EmotionType.SADNESS, EmotionType.DISGUST),   // Remorse
            Pair(EmotionType.DISGUST, EmotionType.ANGER),     // Contempt
            Pair(EmotionType.ANGER, EmotionType.ANTICIPATION),// Aggressiveness
            Pair(EmotionType.ANTICIPATION, EmotionType.JOY)   // Optimism
        )
        primaryPairs.forEach { (p, s) ->
            add(JournalEntry(
                id = currentId++,
                content = "1차 이중감정 테스트: ${p.name} + ${s.name}",
                entryType = EntryType.FREE_WRITING,
                createdAt = LocalDateTime.now().minusHours(currentId.toLong()),
                updatedAt = LocalDateTime.now().minusHours(currentId.toLong()),
                emotionResult = createResult(createEmotion().let { base ->
                    fun set(e: EmotionAnalysis, t: EmotionType, v: Float) = when(t) {
                        EmotionType.JOY -> e.copy(joy = v); EmotionType.TRUST -> e.copy(trust = v)
                        EmotionType.FEAR -> e.copy(fear = v); EmotionType.SURPRISE -> e.copy(surprise = v)
                        EmotionType.SADNESS -> e.copy(sadness = v); EmotionType.DISGUST -> e.copy(disgust = v)
                        EmotionType.ANGER -> e.copy(anger = v); EmotionType.ANTICIPATION -> e.copy(anticipation = v)
                        else -> e
                    }
                    set(set(base, p, 0.7f), s, 0.6f)
                }.copy(keywords = generateKeywords(p, s))),
                status = AnalysisStatus.COMPLETED
            ))
        }

        // --- 3. 2차 이중감정 (Secondary Dyads - 8개) ---
        val secondaryPairs = listOf(
            Pair(EmotionType.JOY, EmotionType.FEAR),          // Guilt
            Pair(EmotionType.TRUST, EmotionType.SURPRISE),    // Curiosity
            Pair(EmotionType.FEAR, EmotionType.SADNESS),       // Despair
            Pair(EmotionType.SURPRISE, EmotionType.DISGUST),   // Unbelief
            Pair(EmotionType.SADNESS, EmotionType.ANGER),      // Envy
            Pair(EmotionType.DISGUST, EmotionType.ANTICIPATION),// Cynicism
            Pair(EmotionType.ANGER, EmotionType.JOY),          // Pride
            Pair(EmotionType.ANTICIPATION, EmotionType.TRUST)  // Fatalism
        )
        secondaryPairs.forEach { (p, s) ->
            add(JournalEntry(
                id = currentId++,
                content = "2차 이중감정 테스트: ${p.name} + ${s.name}",
                entryType = EntryType.FREE_WRITING,
                createdAt = LocalDateTime.now().minusHours(currentId.toLong()),
                updatedAt = LocalDateTime.now().minusHours(currentId.toLong()),
                emotionResult = createResult(createEmotion().let { base ->
                    fun set(e: EmotionAnalysis, t: EmotionType, v: Float) = when(t) {
                        EmotionType.JOY -> e.copy(joy = v); EmotionType.TRUST -> e.copy(trust = v)
                        EmotionType.FEAR -> e.copy(fear = v); EmotionType.SURPRISE -> e.copy(surprise = v)
                        EmotionType.SADNESS -> e.copy(sadness = v); EmotionType.DISGUST -> e.copy(disgust = v)
                        EmotionType.ANGER -> e.copy(anger = v); EmotionType.ANTICIPATION -> e.copy(anticipation = v)
                        else -> e
                    }
                    set(set(base, p, 0.7f), s, 0.6f)
                }.copy(keywords = generateKeywords(p, s))),
                status = AnalysisStatus.COMPLETED
            ))
        }

        // --- 4. 3차 이중감정 (Tertiary Dyads - 8개) ---
        val tertiaryPairs = listOf(
            Pair(EmotionType.JOY, EmotionType.SURPRISE),      // Delight
            Pair(EmotionType.TRUST, EmotionType.SADNESS),      // Sentimentality
            Pair(EmotionType.FEAR, EmotionType.DISGUST),       // Shame
            Pair(EmotionType.SURPRISE, EmotionType.ANGER),     // Outrage
            Pair(EmotionType.SADNESS, EmotionType.ANTICIPATION),// Pessimism
            Pair(EmotionType.DISGUST, EmotionType.JOY),        // Morbidness
            Pair(EmotionType.ANGER, EmotionType.TRUST),        // Dominance
            Pair(EmotionType.ANTICIPATION, EmotionType.FEAR)   // Anxiety
        )
        tertiaryPairs.forEach { (p, s) ->
            add(JournalEntry(
                id = currentId++,
                content = "3차 이중감정 테스트: ${p.name} + ${s.name}",
                entryType = EntryType.FREE_WRITING,
                createdAt = LocalDateTime.now().minusHours(currentId.toLong()),
                updatedAt = LocalDateTime.now().minusHours(currentId.toLong()),
                emotionResult = createResult(createEmotion().let { base ->
                    fun set(e: EmotionAnalysis, t: EmotionType, v: Float) = when(t) {
                        EmotionType.JOY -> e.copy(joy = v); EmotionType.TRUST -> e.copy(trust = v)
                        EmotionType.FEAR -> e.copy(fear = v); EmotionType.SURPRISE -> e.copy(surprise = v)
                        EmotionType.SADNESS -> e.copy(sadness = v); EmotionType.DISGUST -> e.copy(disgust = v)
                        EmotionType.ANGER -> e.copy(anger = v); EmotionType.ANTICIPATION -> e.copy(anticipation = v)
                        else -> e
                    }
                    set(set(base, p, 0.7f), s, 0.6f)
                }.copy(keywords = generateKeywords(p, s))),
                status = AnalysisStatus.COMPLETED
            ))
        }

        // --- 5. 상반 감정 (Opposite Pairs - 4개) ---
        val oppositePairs = listOf(
            Pair(EmotionType.JOY, EmotionType.SADNESS),       // Bittersweetness
            Pair(EmotionType.TRUST, EmotionType.DISGUST),     // Ambivalence
            Pair(EmotionType.FEAR, EmotionType.ANGER),        // Frozenness
            Pair(EmotionType.SURPRISE, EmotionType.ANTICIPATION) // Confusion
        )
        oppositePairs.forEach { (p, s) ->
            add(JournalEntry(
                id = currentId++,
                content = "상반 감정 테스트: ${p.name} + ${s.name}",
                entryType = EntryType.FREE_WRITING,
                createdAt = LocalDateTime.now().minusHours(currentId.toLong()),
                updatedAt = LocalDateTime.now().minusHours(currentId.toLong()),
                emotionResult = createResult(createEmotion().let { base ->
                    fun set(e: EmotionAnalysis, t: EmotionType, v: Float) = when(t) {
                        EmotionType.JOY -> e.copy(joy = v); EmotionType.TRUST -> e.copy(trust = v)
                        EmotionType.FEAR -> e.copy(fear = v); EmotionType.SURPRISE -> e.copy(surprise = v)
                        EmotionType.SADNESS -> e.copy(sadness = v); EmotionType.DISGUST -> e.copy(disgust = v)
                        EmotionType.ANGER -> e.copy(anger = v); EmotionType.ANTICIPATION -> e.copy(anticipation = v)
                        else -> e
                    }
                    set(set(base, p, 0.7f), s, 0.6f)
                }.copy(keywords = generateKeywords(p, s))),
                status = AnalysisStatus.COMPLETED
            ))
        }

        // --- 7. 뉘앙스/변종 감정 (Nuance Variants - 26개) ---
        // Positive/Negative 등 논리적 판단에 의해 도출되는 상세 감정들
        val variantCases = listOf(
            // 1차 이중감정 변종 (8)
            Triple(Pair(EmotionType.JOY, EmotionType.TRUST), mapOf(EmotionType.FEAR to 0.6f), "집착 (Love + Fear)"),
            Triple(Pair(EmotionType.TRUST, EmotionType.FEAR), mapOf(EmotionType.DISGUST to 0.5f), "비굴함 (Submission + Disgust)"),
            Triple(Pair(EmotionType.FEAR, EmotionType.SURPRISE), mapOf(EmotionType.ANGER to 0.5f), "공황 (Awe + Anger)"),
            Triple(Pair(EmotionType.SURPRISE, EmotionType.SADNESS), mapOf(EmotionType.FEAR to 0.7f), "충격 (Disapproval + Fear)"),
            Triple(Pair(EmotionType.SADNESS, EmotionType.DISGUST), mapOf(EmotionType.ANGER to 0.6f), "자기비하 (Remorse + Anger)"),
            Triple(Pair(EmotionType.DISGUST, EmotionType.ANGER), mapOf(EmotionType.SADNESS to 0.6f), "질투_경멸 (Contempt + Sadness)"),
            Triple(Pair(EmotionType.ANGER, EmotionType.ANTICIPATION), mapOf(EmotionType.SADNESS to 0.7f), "복수심 (Aggressiveness + Sadness)"),
            Triple(Pair(EmotionType.ANTICIPATION, EmotionType.JOY), mapOf(EmotionType.TRUST to 0.9f, EmotionType.FEAR to 0.1f), "순진함 (Optimism + HighTrust/LowFear)"),

            // 2차 이중감정 변종 (8)
            Triple(Pair(EmotionType.JOY, EmotionType.FEAR), mapOf(EmotionType.DISGUST to 0.6f), "자기부정 (Guilt + Disgust)"),
            Triple(Pair(EmotionType.TRUST, EmotionType.SURPRISE), mapOf(EmotionType.DISGUST to 0.5f), "의구심 (Curiosity + Disgust)"),
            Triple(Pair(EmotionType.FEAR, EmotionType.SADNESS), mapOf(EmotionType.ANTICIPATION to 0.2f), "무기력 (Despair + LowAnticipation)"),
            Triple(Pair(EmotionType.SURPRISE, EmotionType.DISGUST), mapOf(EmotionType.ANGER to 0.6f), "혐오적 경악 (Unbelief + Anger)"),
            Triple(Pair(EmotionType.SADNESS, EmotionType.ANGER), mapOf(EmotionType.DISGUST to 0.7f), "울분 (Envy + Disgust)"),
            Triple(Pair(EmotionType.DISGUST, EmotionType.ANTICIPATION), mapOf(EmotionType.SADNESS to 0.7f), "불신 (Cynicism + Sadness)"),
            Triple(Pair(EmotionType.ANGER, EmotionType.JOY), mapOf(EmotionType.TRUST to 0.2f), "오만 (Pride + LowTrust)"),
            Triple(Pair(EmotionType.ANTICIPATION, EmotionType.TRUST), mapOf(EmotionType.JOY to 0.2f, EmotionType.SADNESS to 0.5f), "운명론 (Hope + LowJoy + Sadness)"),

            // 3차 이중감정 변종 (8)
            Triple(Pair(EmotionType.JOY, EmotionType.SURPRISE), mapOf(EmotionType.FEAR to 0.6f), "혼란스러운 기쁨 (Delight + Fear)"),
            Triple(Pair(EmotionType.TRUST, EmotionType.SADNESS), mapOf(EmotionType.DISGUST to 0.5f), "자기연민 (Sentimentality + Disgust)"),
            Triple(Pair(EmotionType.FEAR, EmotionType.DISGUST), mapOf(EmotionType.ANGER to 0.6f), "굴욕 (Shame + Anger)"),
            Triple(Pair(EmotionType.SURPRISE, EmotionType.ANGER), mapOf(EmotionType.ANTICIPATION to 0.2f), "당혹 (Outrage + LowAnticipation)"),
            Triple(Pair(EmotionType.SADNESS, EmotionType.ANTICIPATION), mapOf(EmotionType.TRUST to 0.6f), "체념 (Pessimism + Trust)"),
            Triple(Pair(EmotionType.DISGUST, EmotionType.JOY), mapOf(EmotionType.ANGER to 0.5f), "조소 (Morbidness + Anger)"),
            Triple(Pair(EmotionType.ANGER, EmotionType.TRUST), mapOf(EmotionType.DISGUST to 0.6f), "강압 (Dominance + Disgust)"),
            Triple(Pair(EmotionType.ANTICIPATION, EmotionType.FEAR), mapOf(EmotionType.SADNESS to 0.6f), "공포탄 (Anxiety + Sadness)"),

            // 반대 감정 변종 (2)
            Triple(Pair(EmotionType.JOY, EmotionType.SADNESS), mapOf(EmotionType.ANTICIPATION to 0.2f), "향수 (Bittersweetness + LowAnticipation)"),
            Triple(Pair(EmotionType.TRUST, EmotionType.DISGUST), mapOf(EmotionType.FEAR to 0.6f), "회의감 (Ambivalence + Fear)")
        )

        variantCases.forEach { (basePair, modifiers, label) ->
            add(JournalEntry(
                id = currentId++,
                content = "뉘앙스 감정 테스트: $label",
                entryType = EntryType.FREE_WRITING,
                createdAt = LocalDateTime.now().minusHours(currentId.toLong()),
                updatedAt = LocalDateTime.now().minusHours(currentId.toLong()),
                emotionResult = createResult(createEmotion().let { base ->
                    fun set(e: EmotionAnalysis, t: EmotionType, v: Float) = when(t) {
                        EmotionType.JOY -> e.copy(joy = v); EmotionType.TRUST -> e.copy(trust = v)
                        EmotionType.FEAR -> e.copy(fear = v); EmotionType.SURPRISE -> e.copy(surprise = v)
                        EmotionType.SADNESS -> e.copy(sadness = v); EmotionType.DISGUST -> e.copy(disgust = v)
                        EmotionType.ANGER -> e.copy(anger = v); EmotionType.ANTICIPATION -> e.copy(anticipation = v)
                        else -> e
                    }
                    // 기본 베이스 감정 설정 (0.7, 0.6)
                    var temp = set(set(base, basePair.first, 0.7f), basePair.second, 0.6f)
                    // Modifier 적용
                    modifiers.forEach { (t, v) -> temp = set(temp, t, v) }
                    temp
                }.copy(keywords = generateKeywords(basePair.first, basePair.second))),
                status = AnalysisStatus.COMPLETED
            ))
        }

        // --- 6. 분석되지 않은 데이터 (처리 중 또는 에러 케이스 테스트용) ---
        add(JournalEntry(
            id = currentId++,
            content = "아직 감정 분석이 완료되지 않은 일기 내용입니다. 분석 중 UI가 어떻게 보이는지 테스트하기 위한 용도입니다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusHours(currentId.toLong()),
            updatedAt = LocalDateTime.now().minusHours(currentId.toLong()),
            emotionResult = null
        ))

        // --- 7. 표현되지만 여려개의 카테고리 값을 가지고 있을경우.
        add(JournalEntry(
            id = currentId++,
            content = "max emotions , max emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsemotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsemotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotionsmax emotions",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusHours(currentId.toLong()),
            updatedAt = LocalDateTime.now().minusHours(currentId.toLong()),
            emotionResult = createResult(createEmotion().let { base ->
                fun set(e: EmotionAnalysis, t: EmotionType, v: Float) = when(t) {
                    EmotionType.JOY -> e.copy(joy = v); EmotionType.TRUST -> e.copy(trust = v)
                    EmotionType.FEAR -> e.copy(fear = v); EmotionType.SURPRISE -> e.copy(surprise = v)
                    EmotionType.SADNESS -> e.copy(sadness = v); EmotionType.DISGUST -> e.copy(disgust = v)
                    EmotionType.ANGER -> e.copy(anger = v); EmotionType.ANTICIPATION -> e.copy(anticipation = v)
                    else -> e
                }
                set(set(set(set(set(base, EmotionType.JOY, 0.7f), EmotionType.TRUST, 0.6f), EmotionType.FEAR, 0.5f), EmotionType.SURPRISE, 0.4f), EmotionType.DISGUST, 0.3f)
            }.copy(keywords = generateKeywords(EmotionType.JOY, EmotionType.TRUST))),
            status = AnalysisStatus.COMPLETED
        ))

    }.toList()
}
