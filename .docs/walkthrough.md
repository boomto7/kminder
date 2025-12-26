# 워크스루 - 감정 기능 및 데이터 리팩토링

## 개요
모든 감정 유형에 대한 설명 문자열을 추가하고, 감정 분석을 오프라인 우선 워크플로우로 지원하기 위해 기본 데이터 아키텍처를 리팩토링했습니다.

## 변경 사항

### 1. 감정 설명
- **문자열**: `strings.xml`에 24개 단일 감정(강함/중간/약함 변형)에 대한 설명을 추가했습니다.
- **Provider**: 감정 유형을 설명 리소스에 매핑하도록 `AppEmotionStringProvider`를 업데이트했습니다.

### 2. AI 키워드 추출
- **프롬프트**: 감정의 원인을 나타내는 3-5개 키워드를 추출하도록 Gemini API 프롬프트를 업데이트했습니다.
- **모델**: `EmotionAnalysis` (Domain) 및 응답 모델 (Data)에 `keywords` 필드를 추가했습니다.
- **표시**: `AnalyzeIntegratedEmotionUseCase`가 AI 추출 키워드를 우선순위로 두도록 변경했습니다.

### 3. 데이터 아키텍처 리팩토링
- **분리**: 감정 분석 데이터를 `journal_entries`에서 새로운 `emotion_analyses` 테이블로 이동했습니다 (1:1 관계).
- **상태 추적**: 분석 진행 상황을 추적하기 위해 `AnalysisStatus` (PENDING, COMPLETED, FAILED)를 도입했습니다.
- **오프라인 지원**:
    - 일기는 즉시 저장됩니다 (`PENDING`).
    - `JournalWithAnalysis`를 사용하여 분석이 완료되기 전에도 UI에서 항목을 표시할 수 있습니다.
- **스키마**: 데이터베이스 버전을 3으로 올렸습니다.

## 검증
- **빌드**: `./gradlew assembleDebug`로 성공적으로 컴파일했습니다 (Java Runtime 확인됨).
- **데이터베이스**:
    - `JournalEntryEntity` 컬럼 확인 (status 추가됨).
    - `EmotionAnalysisEntity` 생성 및 `journalId`에 대한 외래 키 + 인덱스 확인.
    - `JournalWithAnalysis` 관계 매핑 확인.

## 스크린샷
(이번 리팩토링 단계에서는 스크린샷이 필요한 UI 변경 사항 없음)
