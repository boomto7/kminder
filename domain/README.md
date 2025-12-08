# Domain 모듈 구조

## 📦 패키지 구조

```
domain/
└── src/main/java/com/kminder/domain/
    ├── model/                          # 도메인 모델 (Entity)
    │   ├── ChartPeriod.kt             # 차트 조회 기간 (일/주/월)
    │   ├── EmotionAnalysis.kt         # 감정 분석 결과
    │   ├── EmotionStatistics.kt       # 감정 통계 데이터
    │   ├── EmotionType.kt             # Plutchik 8가지 감정
    │   ├── EntryType.kt               # 일기 작성 모드
    │   └── JournalEntry.kt            # 일기 항목
    │
    ├── repository/                     # Repository Interface
    │   ├── EmotionAnalysisRepository.kt  # 감정 분석 Repository
    │   ├── JournalRepository.kt          # 일기 Repository
    │   └── QuestionRepository.kt         # 질문 Repository
    │
    └── usecase/                        # UseCase (비즈니스 로직)
        ├── emotion/
        │   ├── AnalyzeEmotionUseCase.kt           # 감정 분석
        │   └── SaveAndAnalyzeJournalEntryUseCase.kt  # 일기 저장 + 분석
        │
        ├── journal/
        │   ├── CreateJournalEntryUseCase.kt       # 일기 생성
        │   ├── DeleteJournalEntryUseCase.kt       # 일기 삭제
        │   ├── GetAllJournalEntriesUseCase.kt     # 전체 일기 조회
        │   ├── GetJournalEntriesByDateUseCase.kt  # 날짜별 일기 조회
        │   ├── GetJournalEntryUseCase.kt          # 단일 일기 조회
        │   └── UpdateJournalEntryUseCase.kt       # 일기 수정
        │
        ├── question/
        │   └── GetRandomQuestionUseCase.kt        # 무작위 질문 조회
        │
        └── statistics/
            └── GetEmotionStatisticsUseCase.kt     # 감정 통계 조회
```

## 🎯 주요 컴포넌트

### 1. Model (도메인 모델)

#### JournalEntry
- 일기 항목의 핵심 엔티티
- 내용, 작성 모드, 날짜, 감정 분석 결과 포함
- 문답 모드일 경우 질문도 포함

#### EmotionAnalysis
- Gemini API로부터 받은 감정 분석 결과
- Plutchik의 8가지 원형 감정 강도 (0.0 ~ 1.0)
- 유틸리티 메서드:
  - `getEmotionIntensity()`: 특정 감정 강도 조회
  - `getDominantEmotion()`: 가장 강한 감정 반환
  - `toMap()`: 모든 감정을 Map으로 변환

#### EmotionType (Enum)
- Plutchik의 8가지 원형 감정:
  - ANGER (분노)
  - ANTICIPATION (기대)
  - JOY (기쁨)
  - TRUST (신뢰)
  - FEAR (두려움)
  - SADNESS (슬픔)
  - DISGUST (혐오)
  - SURPRISE (놀람)

#### EntryType (Enum)
- FREE_WRITING: 자유 작성 모드
- QNA: 문답 모드

#### ChartPeriod (Enum)
- DAY: 일별 통계
- WEEK: 주별 통계
- MONTH: 월별 통계

#### EmotionStatistics
- 특정 기간의 감정 통계 데이터
- 날짜, 평균 감정 분석 결과, 일기 개수 포함

### 2. Repository Interface

#### JournalRepository
일기 데이터 접근을 위한 인터페이스:
- CRUD 작업 (생성, 조회, 수정, 삭제)
- 날짜별/기간별 조회
- 감정 통계 조회
- Flow를 통한 실시간 데이터 스트림

#### EmotionAnalysisRepository
감정 분석을 위한 인터페이스:
- `analyzeEmotion()`: Gemini API를 통한 감정 분석
- 한글/영어 언어 지원

#### QuestionRepository
문답 모드를 위한 질문 관리:
- `getRandomQuestion()`: 무작위 질문 조회
- `getAllQuestions()`: 전체 질문 목록 조회

### 3. UseCase (비즈니스 로직)

#### Journal UseCase
- **CreateJournalEntryUseCase**: 새 일기 생성
- **UpdateJournalEntryUseCase**: 일기 수정
- **DeleteJournalEntryUseCase**: 일기 삭제
- **GetJournalEntryUseCase**: 단일 일기 조회
- **GetAllJournalEntriesUseCase**: 전체 일기 목록 조회 (Flow)
- **GetJournalEntriesByDateUseCase**: 특정 날짜의 일기 조회

#### Emotion UseCase
- **AnalyzeEmotionUseCase**: 텍스트 감정 분석
- **SaveAndAnalyzeJournalEntryUseCase**: 일기 저장 + 자동 감정 분석

#### Statistics UseCase
- **GetEmotionStatisticsUseCase**: 기간별 감정 통계 조회

#### Question UseCase
- **GetRandomQuestionUseCase**: 문답 모드용 무작위 질문 조회

## 🔄 데이터 흐름

### 일기 작성 및 분석 플로우
```
1. 사용자 입력 (UI)
   ↓
2. SaveAndAnalyzeJournalEntryUseCase
   ↓
3. JournalRepository.insertEntry() → 일기 저장
   ↓
4. EmotionAnalysisRepository.analyzeEmotion() → Gemini API 호출
   ↓
5. JournalRepository.updateEntry() → 분석 결과 업데이트
   ↓
6. 완료된 일기 항목 반환
```

### 통계 조회 플로우
```
1. 기간 선택 (UI)
   ↓
2. GetEmotionStatisticsUseCase
   ↓
3. JournalRepository.getEmotionStatistics()
   ↓
4. 감정 통계 데이터 반환
   ↓
5. 차트 렌더링 (UI)
```

## 📋 의존성

- **Kotlin Coroutines**: 비동기 처리
- **Java Time API**: 날짜/시간 처리
- **순수 Kotlin**: Android 의존성 없음

## ✅ 특징

1. **순수 Kotlin 모듈**: Android 프레임워크에 의존하지 않음
2. **단일 책임 원칙**: 각 UseCase는 하나의 비즈니스 로직만 담당
3. **의존성 역전**: Repository는 인터페이스로만 정의, 구현은 Data 모듈에서
4. **테스트 용이성**: 순수 Kotlin으로 단위 테스트 작성 용이
5. **Flow 지원**: 실시간 데이터 스트림을 위한 Kotlin Flow 사용

## 🚀 다음 단계

Domain 모듈 구현이 완료되었습니다. 이제 Data 모듈에서:
1. Repository 구현체 작성
2. Room Database 설정
3. Gemini API 클라이언트 구현
4. Hilt DI 모듈 설정

을 진행할 수 있습니다.
