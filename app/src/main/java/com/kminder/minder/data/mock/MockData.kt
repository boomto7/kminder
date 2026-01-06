package com.kminder.minder.data.mock

import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EntryType
import com.kminder.domain.model.JournalEntry
import java.time.LocalDateTime

/**
 * 임시 목업 데이터
 * 개발 및 테스트 용도로 사용됩니다.
 */
object MockData {
    private fun createEmotion(
        joy: Float = 0f, trust: Float = 0f, fear: Float = 0f, surprise: Float = 0f,
        sadness: Float = 0f, disgust: Float = 0f, anger: Float = 0f, anticipation: Float = 0f,
        keywords: List<com.kminder.domain.model.EmotionKeyword> = emptyList()
    ) = EmotionAnalysis(
        joy = joy, trust = trust, fear = fear, surprise = surprise,
        sadness = sadness, disgust = disgust, anger = anger, anticipation = anticipation,
        keywords = keywords
    )

    val mockJournalEntries = listOf(
        // 1. 행복한 하루 (기쁨, 신뢰)
        JournalEntry(
            id = 1,
            content = "오늘 오랜만에 친구들을 만나서 정말 즐거웠다. 맛있는 것도 먹고 옛날 이야기도 하면서 시간 가는 줄 몰랐다. 친구들과 함께 있으니 마음이 편안하고 든든했다. 이런 시간이 자주 있었으면 좋겠다. 앞으로도 우리 우정이 변치 않았으면 좋겠다.오늘 오랜만에 친구들을 만나서 정말 즐거웠다. 맛있는 것도 먹고 옛날 이야기도 하면서 시간 가는 줄 몰랐다. 친구들과 함께 있으니 마음이 편안하고 든든했다. 이런 시간이 자주 있었으면 좋겠다. 앞으로도 우리 우정이 변치 않았으면 좋겠다.오늘 오랜만에 친구들을 만나서 정말 즐거웠다. 맛있는 것도 먹고 옛날 이야기도 하면서 시간 가는 줄 몰랐다. 친구들과 함께 있으니 마음이 편안하고 든든했다. 이런 시간이 자주 있었으면 좋겠다. 앞으로도 우리 우정이 변치 않았으면 좋겠다.오늘 오랜만에 친구들을 만나서 정말 즐거웠다. 맛있는 것도 먹고 옛날 이야기도 하면서 시간 가는 줄 몰랐다. 친구들과 함께 있으니 마음이 편안하고 든든했다. 이런 시간이 자주 있었으면 좋겠다. 앞으로도 우리 우정이 변치 않았으면 좋겠다.오늘 오랜만에 친구들을 만나서 정말 즐거웠다. 맛있는 것도 먹고 옛날 이야기도 하면서 시간 가는 줄 몰랐다. 친구들과 함께 있으니 마음이 편안하고 든든했다. 이런 시간이 자주 있었으면 좋겠다. 앞으로도 우리 우정이 변치 않았으면 좋겠다.오늘 오랜만에 친구들을 만나서 정말 즐거웠다. 맛있는 것도 먹고 옛날 이야기도 하면서 시간 가는 줄 몰랐다. 친구들과 함께 있으니 마음이 편안하고 든든했다. 이런 시간이 자주 있었으면 좋겠다. 앞으로도 우리 우정이 변치 않았으면 좋겠다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(1).withHour(20).withMinute(30),
            updatedAt = LocalDateTime.now().minusDays(1).withHour(20).withMinute(30),
            emotionAnalysis = createEmotion(
                joy = 0.8f, trust = 0.6f, anticipation = 0.4f, surprise = 0.2f, 
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("오랜만의 만남", com.kminder.domain.model.EmotionType.JOY, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("맛있는 음식", com.kminder.domain.model.EmotionType.JOY, 0.7f),
                    com.kminder.domain.model.EmotionKeyword("든든한 친구", com.kminder.domain.model.EmotionType.TRUST, 0.8f),
                    com.kminder.domain.model.EmotionKeyword("즐거운 수다", com.kminder.domain.model.EmotionType.JOY, 0.8f)
                )
            )
        ),
        
        // 2. 힘든 하루 (슬픔, 두려움)
        JournalEntry(
            id = 2,
            content = "회사에서 실수를 해서 상사에게 혼났다. 너무 부끄럽고 자존심이 상했다. 내가 이 일을 계속 잘할 수 있을지 걱정이 된다. 혹시 해고당하는 건 아닐까? 밤에 잠이 잘 오질 않는다. 마음이 무겁고 답답하다. 누군가 내 이야기를 들어줬으면 좋겠다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(3).withHour(22).withMinute(15),
            updatedAt = LocalDateTime.now().minusDays(3).withHour(22).withMinute(15),
            emotionAnalysis = createEmotion(
                sadness = 0.7f, fear = 0.6f, anger = 0.2f, disgust = 0.1f, 
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("업무 실수", com.kminder.domain.model.EmotionType.SADNESS, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("상사의 꾸중", com.kminder.domain.model.EmotionType.FEAR, 0.8f),
                    com.kminder.domain.model.EmotionKeyword("해고 걱정", com.kminder.domain.model.EmotionType.FEAR, 0.7f),
                    com.kminder.domain.model.EmotionKeyword("상한 자존심", com.kminder.domain.model.EmotionType.SADNESS, 0.8f)
                )
            )
        ),
        
        // 3. 화나는 상황 (분노, 혐오)
        JournalEntry(
            id = 3,
            content = "지하철에서 어떤 사람이 발을 밟고도 사과 한마디 없이 지나갔다. 정말 예의 없고 무례한 사람이다. 하루 종일 기분이 나빴다. 세상에는 왜 이렇게 자기 생각만 하는 사람들이 많을까? 생각할수록 열받는다. 아오 짜증나!",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(5).withHour(18).withMinute(45),
            updatedAt = LocalDateTime.now().minusDays(5).withHour(18).withMinute(45),
            emotionAnalysis = createEmotion(
                anger = 0.9f, disgust = 0.7f, sadness = 0.2f, 
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("무례한 사람", com.kminder.domain.model.EmotionType.ANGER, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("밟힌 발", com.kminder.domain.model.EmotionType.DISGUST, 0.7f),
                    com.kminder.domain.model.EmotionKeyword("사과 없음", com.kminder.domain.model.EmotionType.ANGER, 0.8f)
                )
            )
        ),
        
        // 4. 기대되는 내일 (기대, 놀람)
        JournalEntry(
            id = 4,
            content = "드디어 내일 여행 가는 날이다! 짐을 싸면서 설레는 마음을 감출 수가 없다. 가서 어떤 새로운 경험을 하게 될지 너무 기대된다. 갑자기 날씨가 좋다는 예보를 봐서 깜짝 놀랐다. 분명 비 온다고 했었는데! 정말 행운이다. 빨리 내일이 왔으면 좋겠다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(7).withHour(21).withMinute(0),
            updatedAt = LocalDateTime.now().minusDays(7).withHour(21).withMinute(0),
            emotionAnalysis = createEmotion(
                anticipation = 0.8f, surprise = 0.5f, joy = 0.6f, trust = 0.3f,
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("여행 준비", com.kminder.domain.model.EmotionType.ANTICIPATION, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("좋은 날씨", com.kminder.domain.model.EmotionType.SURPRISE, 0.8f),
                    com.kminder.domain.model.EmotionKeyword("새로운 경험", com.kminder.domain.model.EmotionType.ANTICIPATION, 0.7f)
                )
            )
        ),

        // 5. 비오는 날 (슬픔, 차분함)
        JournalEntry(
            id = 5,
            content = "하루 종일 비가 내린다. 창밖을 보고 있으니 괜히 우울해진다. 옛날 생각이 많이 난다. 그때 내가 다른 선택을 했더라면 어땠을까? 후회가 밀려온다. 따뜻한 차나 한 잔 마시고 싶다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(8).withHour(16).withMinute(0),
            updatedAt = LocalDateTime.now().minusDays(8).withHour(16).withMinute(0),
            emotionAnalysis = createEmotion(
                sadness = 0.6f, trust = 0.2f,
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("내리는 비", com.kminder.domain.model.EmotionType.SADNESS, 0.7f),
                    com.kminder.domain.model.EmotionKeyword("지난 후회", com.kminder.domain.model.EmotionType.SADNESS, 0.8f),
                    com.kminder.domain.model.EmotionKeyword("따뜻한 차", com.kminder.domain.model.EmotionType.TRUST, 0.4f)
                )
            )
        ),

        // 6. 새로운 취미 (흥미, 기쁨)
        JournalEntry(
            id = 6,
            content = "오늘부터 기타를 배우기 시작했다. 손가락이 아프지만 소리가 나는 게 신기하고 재밌다. 언젠가 멋진 곡을 연주할 수 있겠지? 새로운 것을 배우는 건 언제나 설레는 일이다. 열심히 연습해야겠다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(9).withHour(19).withMinute(30),
            updatedAt = LocalDateTime.now().minusDays(9).withHour(19).withMinute(30),
            emotionAnalysis = createEmotion(
                joy = 0.7f, anticipation = 0.6f, surprise = 0.3f,
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("기타 연습", com.kminder.domain.model.EmotionType.JOY, 0.8f),
                    com.kminder.domain.model.EmotionKeyword("새로운 소리", com.kminder.domain.model.EmotionType.SURPRISE, 0.6f),
                    com.kminder.domain.model.EmotionKeyword("연주 목표", com.kminder.domain.model.EmotionType.ANTICIPATION, 0.7f)
                )
            )
        ),

        // 7. 잃어버린 지갑 (공포, 당황)
        JournalEntry(
            id = 7,
            content = "지갑을 잃어버렸다. 어디서 잃어버린 건지 도무지 기억이 나지 않는다. 신분증이랑 카드 재발급 받을 생각을 하니 눈앞이 캄캄하다. 혹시 누가 주워서 나쁜 데 쓰면 어떡하지? 너무 불안하다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(10).withHour(14).withMinute(20),
            updatedAt = LocalDateTime.now().minusDays(10).withHour(14).withMinute(20),
            emotionAnalysis = createEmotion(
                fear = 0.9f, sadness = 0.4f, surprise = 0.4f,
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("분실된 지갑", com.kminder.domain.model.EmotionType.FEAR, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("카드 도용", com.kminder.domain.model.EmotionType.FEAR, 0.8f),
                    com.kminder.domain.model.EmotionKeyword("복잡한 재발급", com.kminder.domain.model.EmotionType.SADNESS, 0.7f)
                )
            )
        ),

        // 8. 깜짝 선물 (놀람, 기쁨)
        JournalEntry(
            id = 8,
            content = "집에 왔더니 택배가 와 있었다. 친구가 보낸 생일 선물이었다! 내 생일을 기억하고 있었다니 정말 감동이다. 선물도 내가 딱 갖고 싶었던 것이었다. 너무 고맙고 행복하다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(11).withHour(20).withMinute(0),
            updatedAt = LocalDateTime.now().minusDays(11).withHour(20).withMinute(0),
            emotionAnalysis = createEmotion(
                joy = 0.9f, surprise = 0.8f, trust = 0.7f,
                keywords = listOf(
                com.kminder.domain.model.EmotionKeyword("행복", com.kminder.domain.model.EmotionType.JOY, 0.9f),
                com.kminder.domain.model.EmotionKeyword("친구", com.kminder.domain.model.EmotionType.TRUST, 0.8f),
                com.kminder.domain.model.EmotionKeyword("날씨", com.kminder.domain.model.EmotionType.JOY, 0.7f),
                com.kminder.domain.model.EmotionKeyword("깜짝 선물", com.kminder.domain.model.EmotionType.SURPRISE, 0.6f) // New keyword for Level 3 verification
            )
        ) // Ends createEmotion
    ),

        // 9. 층간 소음 (분노, 혐오)
        JournalEntry(
            id = 9,
            content = "위층에서 또 쿵쿵거린다. 벌써 몇 번째인지 모르겠다. 올라가서 따질까 하다가 참았다. 집에서는 좀 편하게 쉬고 싶은데 스트레스 받아서 미치겠다. 이사 가고 싶다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(12).withHour(23).withMinute(30),
            updatedAt = LocalDateTime.now().minusDays(12).withHour(23).withMinute(30),
            emotionAnalysis = createEmotion(
                anger = 0.8f, disgust = 0.6f, anticipation = 0.2f,
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("층간 소음", com.kminder.domain.model.EmotionType.ANGER, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("반복된 쿵쿵", com.kminder.domain.model.EmotionType.DISGUST, 0.8f),
                    com.kminder.domain.model.EmotionKeyword("이사 고민", com.kminder.domain.model.EmotionType.ANTICIPATION, 0.5f)
                )
            )
        ),

        // 10. 맛집 탐방 (기쁨, 기대)
        JournalEntry(
            id = 10,
            content = "SNS에서 본 맛집에 다녀왔다. 웨이팅이 길었지만 기다린 보람이 있었다. 음식이 정말 맛있었고 분위기도 좋았다. 다음에 부모님 모시고 다시 와야겠다.",
            entryType = EntryType.FREE_WRITING,
            createdAt = LocalDateTime.now().minusDays(13).withHour(13).withMinute(0),
            updatedAt = LocalDateTime.now().minusDays(13).withHour(13).withMinute(0),
            emotionAnalysis = createEmotion(
                joy = 0.8f, anticipation = 0.5f, trust = 0.4f,
                keywords = listOf(
                    com.kminder.domain.model.EmotionKeyword("맛있는 음식", com.kminder.domain.model.EmotionType.JOY, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("좋은 분위기", com.kminder.domain.model.EmotionType.JOY, 0.7f),
                    com.kminder.domain.model.EmotionKeyword("부모님 생각", com.kminder.domain.model.EmotionType.TRUST, 0.6f)
                )
            )
        ),
        
        // 11~24: 추가 데이터 생성 (간략화된 키워드 추가)
        JournalEntry(id=11, content="운동을 다시 시작했다. 몸이 가벼워지는 느낌이 든다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(14), updatedAt=LocalDateTime.now().minusDays(14), 
            emotionAnalysis=createEmotion(joy=0.6f, anticipation=0.6f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("가벼운 몸", com.kminder.domain.model.EmotionType.JOY, 0.7f)))),
        JournalEntry(id=12, content="보고서를 마감 시간에 맞춰 제출했다. 정말 다행이다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(15), updatedAt=LocalDateTime.now().minusDays(15), 
            emotionAnalysis=createEmotion(joy=0.5f, fear=0.3f, trust=0.4f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("마감 준수", com.kminder.domain.model.EmotionType.TRUST, 0.8f)))),
        JournalEntry(id=13, content="오랜만에 영화를 봤다. 슬픈 영화였는데 펑펑 울고 나니 시원하다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(16), updatedAt=LocalDateTime.now().minusDays(16), 
            emotionAnalysis=createEmotion(sadness=0.8f, joy=0.3f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("슬픈 영화", com.kminder.domain.model.EmotionType.SADNESS, 0.9f)))),
        JournalEntry(id=14, content="길에서 귀여운 고양이를 만났다. 나를 보고 도망가지 않아서 한참 구경했다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(17), updatedAt=LocalDateTime.now().minusDays(17), 
            emotionAnalysis=createEmotion(joy=0.7f, trust=0.5f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("길고양이", com.kminder.domain.model.EmotionType.JOY, 0.8f)))),
        JournalEntry(id=15, content="약속 시간에 늦어서 친구에게 미안했다. 다음부터는 일찍 나가야지.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(18), updatedAt=LocalDateTime.now().minusDays(18), 
            emotionAnalysis=createEmotion(sadness=0.4f, fear=0.2f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("지각", com.kminder.domain.model.EmotionType.SADNESS, 0.6f)))),
        JournalEntry(id=16, content="월급이 들어왔다! 사고 싶었던 신발을 바로 샀다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(19), updatedAt=LocalDateTime.now().minusDays(19), 
            emotionAnalysis=createEmotion(joy=0.9f, anticipation=0.7f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("월급", com.kminder.domain.model.EmotionType.JOY, 1.0f)))),
        JournalEntry(id=17, content="컴퓨터가 갑자기 고장났다. 작업하던 파일이 다 날라갔을까 봐 걱정된다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(20), updatedAt=LocalDateTime.now().minusDays(20), 
            emotionAnalysis=createEmotion(fear=0.8f, surprise=0.6f, anger=0.3f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("컴퓨터 고장", com.kminder.domain.model.EmotionType.FEAR, 0.9f)))),
        JournalEntry(id=18, content="부모님께 안부 전화를 드렸다. 목소리를 들으니 반갑고 좋았다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(21), updatedAt=LocalDateTime.now().minusDays(21), 
            emotionAnalysis=createEmotion(joy=0.7f, trust=0.8f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("부모님 전화", com.kminder.domain.model.EmotionType.TRUST, 0.8f)))),
        JournalEntry(id=19, content="다이어트 중인데 밤에 치킨을 먹어버렸다. 내 자신이 한심하다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(22), updatedAt=LocalDateTime.now().minusDays(22), 
            emotionAnalysis=createEmotion(disgust=0.6f, sadness=0.5f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("야식 유혹", com.kminder.domain.model.EmotionType.DISGUST, 0.7f)))),
        JournalEntry(id=20, content="내일 중요한 발표가 있다. 잘할 수 있을지 모르겠다. 너무 떨린다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(23), updatedAt=LocalDateTime.now().minusDays(23), 
            emotionAnalysis=createEmotion(fear=0.7f, anticipation=0.5f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("발표 긴장", com.kminder.domain.model.EmotionType.FEAR, 0.8f)))),
        JournalEntry(id=21, content="날씨가 너무 덥다. 불쾌지수가 폭발한다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(24), updatedAt=LocalDateTime.now().minusDays(24), 
            emotionAnalysis=createEmotion(anger=0.5f, disgust=0.4f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("폭염", com.kminder.domain.model.EmotionType.DISGUST, 0.6f)))),
        JournalEntry(id=22, content="책을 읽다가 마음에 와닿는 구절을 발견했다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(25), updatedAt=LocalDateTime.now().minusDays(25), 
            emotionAnalysis=createEmotion(joy=0.5f, trust=0.6f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("독서", com.kminder.domain.model.EmotionType.JOY, 0.5f)))),
        JournalEntry(id=23, content="새로운 프로젝트 팀원이 되었다. 잘 해보고 싶다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(26), updatedAt=LocalDateTime.now().minusDays(26), 
            emotionAnalysis=createEmotion(anticipation=0.8f, trust=0.5f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("새 출발", com.kminder.domain.model.EmotionType.ANTICIPATION, 0.8f)))),
        JournalEntry(id=24, content="버스에서 잠들어서 종점까지 갔다 왔다. 시간이 너무 아깝다.", entryType=EntryType.FREE_WRITING, createdAt=LocalDateTime.now().minusDays(27), updatedAt=LocalDateTime.now().minusDays(27), 
            emotionAnalysis=createEmotion(sadness=0.5f, anger=0.4f, keywords=listOf(com.kminder.domain.model.EmotionKeyword("낭비된 시간", com.kminder.domain.model.EmotionType.SADNESS, 0.6f)))),
        
        // 25. 프로젝트 성공 (기쁨, 신뢰, 성취감 - 실긍감정 예시)
        JournalEntry(
            id=25, 
            content="드디어 프로젝트를 성공적으로 마쳤다! 팀원들과 함께 고생한 보람이 있다. 결과물도 너무 만족스럽고, 모두가 축하해줘서 정말 행복하다. 스스로가 자랑스럽다.", 
            entryType=EntryType.FREE_WRITING, 
            createdAt=LocalDateTime.now().minusDays(0).withHour(18).withMinute(0), 
            updatedAt=LocalDateTime.now().minusDays(0).withHour(18).withMinute(0), 
            emotionAnalysis=createEmotion(
                joy=0.9f, trust=0.8f, anticipation=0.6f, 
                keywords=listOf(
                    com.kminder.domain.model.EmotionKeyword("프로젝트 성공", com.kminder.domain.model.EmotionType.JOY, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("팀워크", com.kminder.domain.model.EmotionType.TRUST, 0.8f),
                    com.kminder.domain.model.EmotionKeyword("축하", com.kminder.domain.model.EmotionType.JOY, 0.7f),
                    com.kminder.domain.model.EmotionKeyword("성취감", com.kminder.domain.model.EmotionType.ANTICIPATION, 0.8f)
                )
            )
        ),
        
        // 26. 극심한 분노 (Single Emotion 예시: Anger 0.9 >> Disgust 0.2)
        JournalEntry(
            id=26,
            content="정말 참을 수가 없다. 어떻게 나한테 이럴 수가 있지? 머리 끝까지 화가 난다. 용서할 수 없다.",
            entryType=EntryType.FREE_WRITING,
            createdAt=LocalDateTime.now().minusDays(0).withHour(20).withMinute(0),
            updatedAt=LocalDateTime.now().minusDays(0).withHour(20).withMinute(0),
            emotionAnalysis=createEmotion(
                anger=0.9f, disgust=0.2f,
                keywords=listOf(
                    com.kminder.domain.model.EmotionKeyword("배신감", com.kminder.domain.model.EmotionType.ANGER, 0.95f),
                    com.kminder.domain.model.EmotionKeyword("분노", com.kminder.domain.model.EmotionType.ANGER, 0.9f),
                    com.kminder.domain.model.EmotionKeyword("용서 못함", com.kminder.domain.model.EmotionType.ANGER, 0.85f)
                )
            )
        )
    )
}
