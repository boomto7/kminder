# 구현 계획 - Minder

## 목표 설명
Gemini API를 사용하여 사용자의 감정을 추적하고 분석하는 "Minder" 안드로이드 애플리케이션을 개발합니다. 이 계획은 정의된 에이전트들이 수행할 기술적 실행 단계를 설명합니다.

## 사용자 검토 필요 사항
> [!IMPORTANT]
> **Gemini API 키**: 분석 기능을 사용하려면 Gemini에 액세스할 수 있는 유효한 Google Cloud API 키가 필요합니다.
> **개인정보 보호**: 사용자 데이터는 로컬에 저장되지만, 텍스트는 분석을 위해 Google 서버로 전송됩니다. 이 점을 사용자에게 고지해야 합니다.

## 변경 제안

### 1단계: 프로젝트 설정 (개발자 에이전트)
#### [수정] 멀티 모듈 구조 설정
- `:domain` 모듈 생성 (Java/Kotlin Library).
- `:data` 모듈 생성 (Android Library).
- `:app` 모듈 의존성 설정 (`implementation(project(":domain"))`, `implementation(project(":data"))`).
- Hilt 의존성 주입 설정 (각 모듈별 설정).
- **[신규] Version Catalog 설정**: `gradle/libs.versions.toml` 생성 및 모든 `build.gradle.kts` 마이그레이션.

### 2단계: 데이터 및 도메인 계층 (개발자 에이전트)
#### [신규] 도메인 모듈 (`:domain`)
- `Entry` 모델 (Data Class).
- `EntryRepository` 인터페이스.
- `AnalyzeEmotionUseCase`, `GetEntriesUseCase`.

#### [신규] 데이터 모듈 (`:data`)
- 로컬 데이터베이스 (Room): `EntryEntity`, `AppDatabase`, DAO.
- 네트워크 (Retrofit): `GeminiService`.
- 리포지토리 구현: `EntryRepositoryImpl`.
- 매퍼: Entity <-> Domain Model 변환기.

### 3단계: 프레젠테이션 계층 (디자이너 에이전트)
#### [신규] 디자인 시스템
- `Color.kt`, `Type.kt`, `Theme.kt`: "Minder"만의 미학 정의 (차분함, 프리미엄).

#### [신규] 화면 (Screens)
- `HomeScreen`: 요약 정보가 있는 대시보드.
- `WriteScreen`: 자유 작성 및 문답 입력 화면.
- `HistoryScreen`: 기록 목록 화면.
- `DetailScreen`: 기록 내용 및 분석 결과 보기.
- `ChartScreen`: 차트 라이브러리(Vico 또는 MPAndroidChart 등)를 활용한 시각화.

### 4단계: 검증 (테스터 에이전트)
- Room DAO 및 Repository에 대한 단위 테스트.
- 내비게이션 흐름에 대한 UI 테스트.
- Gemini API 응답 수동 검증.

## 검증 계획
### 자동화 테스트
- 로직 검증을 위해 `./gradlew testDebugUnitTest` 실행.
- UI 테스트를 위해 `./gradlew connectedAndroidTest` 실행.

### 수동 검증
- **시나리오 1**: 슬픈 일기를 작성 -> "슬픔(Sadness)"이 감지되는지 확인.
- **시나리오 2**: 주간 차트 확인 -> 새 기록이 그래프에 반영되는지 확인.
