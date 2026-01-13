- [x] Implement Pagination Logic in HomeViewModel (20 items per page)
- [x] Verify Pagination (Load More works correctly)
- [x] Optimize Feed Updates (Reuse data on grouping change, Add Stable Keys)
- [x] Implement Feed Grouping Selector UI (Daily/Weekly/Monthly)
- [x] Refactor Load More to use listState observation
- [x] Implement Navigation from Home Feed to Entry Detail

# 홈 화면 타임라인 피드 개편 (Home Timeline Redesign)
- [ ] **레이아웃 구조 잡기 (Layout Structure)** <!-- id: 50 -->
    - [ ] `HomeFeedScreen` Composable 생성.
    - [ ] `ModalNavigationDrawer` (Menu) 적용.
    - [ ] "오늘의 기분..." 글쓰기 바 (Write Prompt) 구현.
- [ ] **데이터 그룹화 로직 (Data Grouping)** <!-- id: 51 -->
    - [ ] Date Grouping Util (Daily/Weekly/Monthly) 구현.
    - [ ] `ViewModel`에서 데이터 그룹화 로직 연결.
- [ ] **타임라인 UI 구현 (Timeline UI)** <!-- id: 52 -->
    - [ ] `LazyColumn` + `stickyHeader` 적용.
    - [ ] **Section Header**: 날짜/기간 텍스트 + "모아보기(View All)" 버튼.
    - [ ] **Entry Item**: 스레드 스타일 리스트 아이템 디자인.
- [ ] **네비게이션 및 연결 (Navigation)** <!-- id: 53 -->
    - [ ] 기존 `HomeScreen`을 `HomeFeedScreen`으로 교체.
    - [x] 글쓰기 바 클릭 시 `WriteEntryScreen` 이동.

# 일기 작성 화면 (Write Entry Screen)
- [x] **언어 로직 구현 (Language Logic)**
    - [x] `LanguageProvider` (Domain) 및 `SystemLanguageProvider` (App) 구현.
    - [x] DI 바인딩 (`AppModule`).
    - [x] `SaveAndAnalyzeJournalEntryUseCase`에 언어 파라미터 적용.
- [x] **리소스 리팩토링 (Resource Refactoring)**
    - [x] English Default (`values/strings.xml`), Korean Alternative (`values-ko/strings.xml`) 적용.
- [x] **ViewModel 구현 (WriteViewModel)**
    - [x] `SaveAndAnalyzeJournalEntryUseCase` 주입.
    - [x] UI State (Text, Loading, Error, Success) 관리.
- [x] **UI 구현 (WriteEntryScreen)**
    - [x] **Input Field**: 화면 채우는 깔끔한 텍스트 입력창 (Neo-Brutalism typography).
    - [x] **Confirm Button**: "감정 분석하기" 버튼 (Neo Button Style).
    - [x] **Loading State**: 분석 중 로딩 표시 (Shared RetroIndicator).
- [x] **리소스 추가**
    - [x] Strings (Hint, Button, Error).

- [x] **Data Initialization (Mock Data)**
    - [x] `MockDataInitializer` 구현 (Util).
    - [x] `SplashViewModel` 구현 및 초기화 로직 추가.
    - [x] `SplashScreenV2` 및 Navigation 연동.

# 유지보수 및 리팩토링 (Maintenance)
- [x] **감정 분석 상태 관리 (Analysis Status)**
    - [x] `EntryDetailViewModel`: PENDING 상태 DB 저장 제거, 로컬 State(`isAnalyzing`)로 전환.
    - [x] `EntryDetailScreen`: 로컬 State 기반 프로그레스 바 표시.
    - [x] `BlockingLoadingOverlay`: 상세 화면(`EntryDetailScreen`)에도 적용하여 재분석 시 터치 차단.
- [x] **API 설정 및 에러 핸들링**
    - [x] Gemini Model Version Update (`gemini-2.5-flash`).
    - [x] `GeminiApiClient`: Timber Error Logging 추가.
