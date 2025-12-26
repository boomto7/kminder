# 로직 분리 및 오프라인 우선 분석 리팩토링

## 문제 정의
현재 구현은 `JournalEntry`와 `EmotionAnalysis`가 하나의 테이블에 결합되어 있습니다. 사용자 요청 사항:
1.  `EmotionAnalysis`를 별도 테이블로 분리 (Journal과 1:1 관계).
2.  일기를 **먼저** 저장한 후 분석을 시도.
3.  분석 상태(예: 네트워크 실패)를 추적하기 위해 `JournalEntry`에 상태 필드 추가.
4.  실패하거나 처리되지 않은 항목에 대해 재분석 허용.

## 사용자 리뷰 필요
> [!IMPORTANT]
> **데이터베이스 스키마 변경**: 이 작업은 `journal_entries`에서 감정 컬럼을 제거하고 새로운 `emotion_analyses` 테이블을 생성하는 작업을 포함합니다. 개발 중 데이터를 지우거나 재설치해야 하는 파괴적인 변경입니다.

## 제안된 변경 사항

### Domain Layer
#### [수정] [JournalEntry.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/domain/src/main/java/com/kminder/domain/model/JournalEntry.kt)
- `val status: AnalysisStatus` 필드 추가.
- 생성자에서 `emotionAnalysis` 제거? 아니요, UI 표시를 위해 nullable `EmotionAnalysis?`로 유지합니다. Repository에서 테이블 조인을 통해 채워질 것입니다.

#### [신규] [AnalysisStatus.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/domain/src/main/java/com/kminder/domain/model/AnalysisStatus.kt)
- Enum 클래스: `PENDING`, `COMPLETED`, `FAILED`, `NONE`.

#### [신규] [SaveAnalysisResultUseCase.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/domain/src/main/java/com/kminder/domain/usecase/analysis/SaveAnalysisResultUseCase.kt)
- 특정 일기 ID에 대한 분석 결과를 저장하는 함수.

### Data Layer
#### [수정] [JournalEntryEntity.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/data/src/main/java/com/kminder/data/local/entity/JournalEntryEntity.kt)
- **제거**: `anger`, `anticipation`, `joy`, `trust`, `fear`, `sadness`, `disgust`, `surprise`, `keywords`.
- **추가**: `status: String` (`AnalysisStatus`에서 매핑).

#### [신규] [EmotionAnalysisEntity.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/data/src/main/java/com/kminder/data/local/entity/EmotionAnalysisEntity.kt)
- 필드: `id` (PK), `journalId` (FK), `anger`, `anticipation`... `surprise`, `keywords`.
- 키워드를 위해 `StringListConverter` 사용.

#### [신규] [JournalWithAnalysis.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/data/src/main/java/com/kminder/data/local/model/JournalWithAnalysis.kt)
- Room Relation 클래스 (`@Embedded` journal + `@Relation` analysis).

#### [수정] [JournalEntryDao.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/data/src/main/java/com/kminder/data/local/dao/JournalEntryDao.kt)
- 쿼리 메서드가 `JournalWithAnalysis`를 반환하도록 업데이트.
- `EmotionAnalysisEntity` 삽입 메서드 추가.
- `JournalEntryEntity` 상태 업데이트 메서드 추가.

#### [수정] [MinderDatabase.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/data/src/main/java/com/kminder/data/local/database/MinderDatabase.kt)
- `EmotionAnalysisEntity` 추가.
- 버전 올림.

#### [수정] [JournalRepositoryImpl.kt](file:///Users/hongchang-gi/Documents/work_space/antigravity/minder/data/src/main/java/com/kminder/data/repository/JournalRepositoryImpl.kt)
- 저장 로직 구현: 일기 먼저 저장 (PENDING).
- 조회 로직 구현: `JournalWithAnalysis`를 `JournalEntry`로 매핑.

## 검증 계획
### 자동화 테스트
- `GeminiApiAppInstrumentedTest`: 분석 API가 여전히 작동하는지 확인.
- (신규) `DatabaseTest`: 1:1 관계 및 트랜잭션 로직 검증 (선택 사항).

### 수동 검증
1.  **작성 화면**: (시뮬레이션) 일기 저장 -> DB 상태 확인 -> 분석 -> DB 상태 및 분석 테이블 확인.
2.  **홈 화면**: 분석된 항목과 그렇지 않은 항목이 정상적으로 처리되는지 확인.
