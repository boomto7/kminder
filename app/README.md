# App 모듈 구조

## 📦 패키지 구조

```
app/
└── src/main/java/com/kminder/minder/
    ├── MinderApplication.kt           # Hilt Application
    ├── MainActivity.kt                # 메인 Activity
    │
    ├── navigation/                    # Navigation
    │   ├── Screen.kt                 # 화면 라우트 정의
    │   └── MinderNavGraph.kt         # Navigation Graph
    │
    ├── ui/
    │   ├── screen/                   # 화면별 UI
    │   │   ├── home/
    │   │   │   ├── HomeViewModel.kt
    │   │   │   └── HomeScreen.kt
    │   │   ├── write/
    │   │   │   └── WriteEntryScreen.kt (Placeholder)
    │   │   ├── list/
    │   │   │   └── EntryListScreen.kt (Placeholder)
    │   │   ├── detail/
    │   │   │   └── EntryDetailScreen.kt (Placeholder)
    │   │   └── statistics/
    │   │       └── StatisticsScreen.kt (Placeholder)
    │   │
    │   └── theme/                    # 테마 및 디자인
    │       ├── Color.kt              # Coffee/Latte 색상 팔레트
    │       ├── Theme.kt              # Material3 테마
    │       └── Type.kt               # Typography
    │
    └── AndroidManifest.xml
```

## 🎯 구현된 컴포넌트

### 1. Application & Activity

#### MinderApplication
- `@HiltAndroidApp` 어노테이션
- Hilt DI 진입점

#### MainActivity
- `@AndroidEntryPoint` 어노테이션
- Navigation 설정
- MinderTheme 적용

### 2. Navigation

#### Screen (Sealed Class)
화면 라우트 정의:
- `Home` - 홈/대시보드
- `WriteEntry` - 일기 작성
- `EntryList` - 일기 목록
- `EntryDetail` - 일기 상세 (파라미터: entryId)
- `Statistics` - 통계/차트

#### MinderNavGraph
- NavHost 구성
- 5개 화면 라우팅
- 화면 간 데이터 전달

### 3. UI Screens

#### ✅ HomeScreen (완전 구현)
**기능:**
- 최근 일기 5개 표시
- 전체 보기/통계 버튼
- FAB (새 일기 작성)
- Loading/Empty/Success 상태 처리

**ViewModel:**
- `GetAllJournalEntriesUseCase` 사용
- Flow로 실시간 데이터 수신
- UI 상태 관리

#### ⏳ WriteEntryScreen (Placeholder)
- 기본 Scaffold 구조
- 추후 구현 필요

#### ⏳ EntryListScreen (Placeholder)
- 기본 Scaffold 구조
- 추후 구현 필요

#### ⏳ EntryDetailScreen (Placeholder)
- 기본 Scaffold 구조
- entryId 파라미터 수신
- 추후 구현 필요

#### ⏳ StatisticsScreen (Placeholder)
- 기본 Scaffold 구조
- 추후 구현 필요

### 4. 테마 & 디자인

#### Color.kt - Coffee/Latte 색상 팔레트

**Primary Colors:**
- `CoffeeBrown` - #6F4E37
- `CoffeeBrownLight` - #8B6F47
- `CoffeeBrownDark` - #4A3325

**Secondary Colors:**
- `LatteCream` - #F5E6D3
- `LatteBeige` - #E8D5C4
- `LatteTan` - #D4B5A0

**Accent Colors:**
- `EspressoDark` - #3E2723
- `CreamWhite` - #FFFBF5
- `MochaLight` - #BCAA99

**Emotion Colors (Plutchik 8):**
- `EmotionAnger` - 빨강 (#E57373)
- `EmotionAnticipation` - 주황 (#FFB74D)
- `EmotionJoy` - 노랑 (#FFF176)
- `EmotionTrust` - 초록 (#81C784)
- `EmotionFear` - 파랑 (#64B5F6)
- `EmotionSadness` - 보라 (#9575CD)
- `EmotionDisgust` - 자주 (#BA68C8)
- `EmotionSurprise` - 청록 (#4DD0E1)

#### Theme.kt
- Light/Dark ColorScheme
- Coffee/Latte 테마 적용
- Material3 기반

## 🔄 데이터 흐름

### 홈 화면 플로우
```
1. HomeScreen 렌더링
   ↓
2. HomeViewModel 초기화
   ↓
3. GetAllJournalEntriesUseCase 호출
   ↓
4. JournalRepository.getAllEntries() (Flow)
   ↓
5. Room Database 실시간 조회
   ↓
6. UI 상태 업데이트 (Loading → Success/Empty)
   ↓
7. 최근 5개 일기 표시
```

### Navigation 플로우
```
HomeScreen
  ├─→ WriteEntryScreen (FAB 클릭)
  ├─→ EntryListScreen (전체 보기 버튼)
  ├─→ StatisticsScreen (통계 버튼)
  └─→ EntryDetailScreen (일기 카드 클릭, 추후 구현)
```

## 📱 화면 구성

### HomeScreen
```
┌─────────────────────────────┐
│ Minder                   ⚙️ │ TopBar
├─────────────────────────────┤
│ [전체 보기] [통계]           │ Action Buttons
├─────────────────────────────┤
│ 최근 일기                    │
│                             │
│ ┌─────────────────────────┐ │
│ │ 2024년 12월 08일 16:30  │ │
│ │ 오늘은 정말 좋은 날...   │ │ Entry Card
│ │ 주요 감정: 기쁨          │ │
│ └─────────────────────────┘ │
│                             │
│ ┌─────────────────────────┐ │
│ │ ...                     │ │
│ └─────────────────────────┘ │
│                             │
└─────────────────────────────┘
                           [+] FAB
```

## ✅ 구현 완료 항목

- ✅ Hilt Application 설정
- ✅ MainActivity Navigation 설정
- ✅ Coffee/Latte 테마 적용
- ✅ Navigation Graph 구성
- ✅ HomeScreen 완전 구현
- ✅ HomeViewModel 구현
- ✅ 4개 화면 Placeholder 생성

## ⏳ 추후 구현 필요

### WriteEntryScreen
- 자유 작성/문답 모드 선택
- 텍스트 입력 필드
- 저장 버튼
- ViewModel 구현
- 감정 분석 로딩 상태

### EntryListScreen
- 전체 일기 목록 표시
- 날짜별 그룹화
- 검색 기능
- ViewModel 구현

### EntryDetailScreen
- 일기 전체 내용 표시
- 감정 분석 결과 시각화
- 수정/삭제 기능
- ViewModel 구현

### StatisticsScreen
- 일/주/월 선택
- 감정 차트 (라인/바/파이)
- 기간별 통계
- ViewModel 구현

## 🚀 다음 단계

1. **WriteEntryScreen 구현**
   - ViewModel 생성
   - UI 구현
   - 자유 작성/문답 모드
   - 저장 로직

2. **EntryListScreen 구현**
   - ViewModel 생성
   - LazyColumn으로 목록 표시
   - 날짜별 섹션

3. **EntryDetailScreen 구현**
   - ViewModel 생성
   - 감정 분석 결과 표시
   - 수정/삭제 기능

4. **StatisticsScreen 구현**
   - ViewModel 생성
   - 차트 라이브러리 통합
   - 기간 선택 UI

5. **테스트 및 최적화**
   - 단위 테스트
   - UI 테스트
   - 성능 최적화

## 📝 참고사항

- **Hilt**: 모든 ViewModel은 `@HiltViewModel` 사용
- **Navigation**: Type-safe navigation 고려
- **State Management**: StateFlow 사용
- **UI**: Material3 컴포넌트 활용
- **테마**: Coffee/Latte 색상 일관성 유지
