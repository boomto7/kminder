# Data 모듈 구조

## 📦 패키지 구조

```
data/
└── src/main/java/com/kminder/data/
    ├── di/                             # Dependency Injection
    │   ├── DatabaseModule.kt          # Room Database DI
    │   ├── NetworkModule.kt           # Gemini API DI
    │   └── RepositoryModule.kt        # Repository 바인딩
    │
    ├── local/                          # 로컬 데이터 소스
    │   ├── dao/
    │   │   └── JournalEntryDao.kt     # Room DAO
    │   ├── database/
    │   │   └── MinderDatabase.kt      # Room Database
    │   └── entity/
    │       └── JournalEntryEntity.kt  # Room Entity + 매퍼
    │
    ├── remote/                         # 원격 데이터 소스
    │   ├── api/
    │   │   └── GeminiApiClient.kt     # Gemini API 클라이언트
    │   ├── model/
    │   │   └── EmotionAnalysisResponse.kt  # API 응답 모델
    │   └── prompt/
    │       └── EmotionAnalysisPrompt.kt    # 프롬프트 템플릿
    │
    └── repository/                     # Repository 구현
        ├── EmotionAnalysisRepositoryImpl.kt
        ├── JournalRepositoryImpl.kt
        └── QuestionRepositoryImpl.kt
```

## 🎯 주요 컴포넌트

### 1. Local Data Source (Room Database)

#### JournalEntryEntity
- Room Database용 일기 Entity
- Domain 모델과 분리하여 데이터베이스 스키마 독립성 유지
- 변환 함수:
  - `JournalEntry.toEntity()`: Domain → Entity
  - `JournalEntryEntity.toDomain()`: Entity → Domain

#### JournalEntryDao
Room DAO 인터페이스:
- **Insert/Update/Delete**: 기본 CRUD 작업
- **Query 메서드**:
  - `getAll()`: 전체 일기 조회 (Flow)
  - `getById()`: ID로 조회
  - `getByDate()`: 특정 날짜 조회
  - `getByDateRange()`: 기간별 조회
  - `getEntriesWithoutAnalysis()`: 분석 대기 중인 일기

#### MinderDatabase
- Room Database 클래스
- 버전: 1
- Entity: JournalEntryEntity

### 2. Remote Data Source (Gemini API)

#### GeminiApiClient
Gemini API 호출 클라이언트:
- **모델**: gemini-1.5-flash
- **기능**:
  - `analyzeEmotion()`: 텍스트 감정 분석
  - JSON 응답 파싱 및 에러 처리
  - 마크다운 코드 블록에서 JSON 추출

#### EmotionAnalysisPrompt
프롬프트 템플릿:
- **한국어 프롬프트**: Plutchik 8가지 감정 분석
- **영어 프롬프트**: 영어 텍스트 분석
- JSON 스키마 정의 포함

#### EmotionAnalysisResponse
Gemini API 응답 모델:
- Gson을 사용한 JSON 파싱
- 8가지 감정 강도 (0.0 ~ 1.0)

### 3. Repository 구현

#### JournalRepositoryImpl
일기 데이터 관리:
- Room DAO를 사용한 CRUD 작업
- **통계 계산 로직**:
  - `groupByDay()`: 일별 그룹화
  - `groupByWeek()`: 주별 그룹화 (월요일 시작)
  - `groupByMonth()`: 월별 그룹화
  - `calculateAverageEmotion()`: 평균 감정 계산

#### EmotionAnalysisRepositoryImpl
감정 분석:
- Gemini API 클라이언트 사용
- 언어별 프롬프트 자동 선택

#### QuestionRepositoryImpl
질문 관리:
- **30개의 감정 유도 질문** 포함
- 무작위 질문 제공

### 4. Dependency Injection (Hilt)

#### DatabaseModule
- `MinderDatabase` 제공
- `JournalEntryDao` 제공
- Singleton 스코프

#### NetworkModule
- `GeminiApiClient` 제공
- API 키 관리
- ⚠️ **TODO**: BuildConfig에서 API 키 읽어오기

#### RepositoryModule
- Repository 인터페이스 ↔ 구현체 바인딩
- Abstract 모듈 사용

## 🔄 데이터 흐름

### 일기 저장 플로우
```
UI Layer
  ↓
UseCase (SaveAndAnalyzeJournalEntryUseCase)
  ↓
JournalRepositoryImpl
  ↓
1. JournalEntryDao.insert() → Room DB 저장
  ↓
2. EmotionAnalysisRepositoryImpl.analyzeEmotion()
  ↓
3. GeminiApiClient.analyzeEmotion() → Gemini API 호출
  ↓
4. JSON 파싱 → EmotionAnalysis
  ↓
5. JournalEntryDao.update() → 분석 결과 업데이트
```

### 통계 조회 플로우
```
UI Layer
  ↓
GetEmotionStatisticsUseCase
  ↓
JournalRepositoryImpl.getEmotionStatistics()
  ↓
1. JournalEntryDao.getByDateRange() → 기간별 일기 조회
  ↓
2. groupByDay/Week/Month() → 기간별 그룹화
  ↓
3. calculateAverageEmotion() → 평균 감정 계산
  ↓
4. EmotionStatistics 리스트 반환
```

## 📋 데이터베이스 스키마

### journal_entries 테이블
```sql
CREATE TABLE journal_entries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    content TEXT NOT NULL,
    entryType TEXT NOT NULL,
    question TEXT,
    createdAt TEXT NOT NULL,
    updatedAt TEXT NOT NULL,
    anger REAL,
    anticipation REAL,
    joy REAL,
    trust REAL,
    fear REAL,
    sadness REAL,
    disgust REAL,
    surprise REAL
)
```

## 🔑 API 키 설정

Gemini API를 사용하려면 API 키가 필요합니다:

1. **API 키 발급**: [Google AI Studio](https://makersuite.google.com/app/apikey)에서 발급
2. **설정 방법** (추후 구현 필요):
   ```kotlin
   // local.properties에 추가
   GEMINI_API_KEY=your_api_key_here
   
   // build.gradle.kts에서 읽기
   buildConfigField("String", "GEMINI_API_KEY", "\"${properties["GEMINI_API_KEY"]}\"")
   ```

## ✅ 구현 완료 항목

- ✅ Room Database 설정
- ✅ DAO 인터페이스 정의
- ✅ Entity 및 매퍼 함수
- ✅ Gemini API 클라이언트
- ✅ 프롬프트 템플릿 (한글/영어)
- ✅ Repository 구현체 (3개)
- ✅ Hilt DI 모듈 (3개)
- ✅ 통계 계산 로직
- ✅ 30개 질문 데이터

## 🚀 다음 단계

Data 모듈 구현이 완료되었습니다. 이제 **App 모듈**에서:

1. **Hilt Application 클래스** 생성
2. **ViewModel** 구현
3. **UI Screen** (Jetpack Compose)
4. **Navigation** 설정
5. **테마 및 디자인 시스템**

을 진행할 수 있습니다.

## ⚠️ 주의사항

1. **API 키 보안**: Gemini API 키는 반드시 `local.properties`에서 관리
2. **데이터베이스 마이그레이션**: 스키마 변경 시 Migration 전략 필요
3. **에러 처리**: API 호출 실패 시 적절한 에러 처리 필요
4. **테스트**: Repository 및 DAO 단위 테스트 작성 권장
