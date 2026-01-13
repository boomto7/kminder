# NetworkChart 고도화 및 프로토타입 보고서 (통합)

## 1. Minimalist NetworkChart (Prototype)
- **Concept**: "Less is More". White 배경 + Black Dot/Line.
- **특징**: 색상을 배제하여 정보의 구조(Structure)와 관계(Relation)에 집중.

## 2. Constellation Chart (Prototype) [Optimized + Scale Fix]
- **Design Philosophy**: **Curved Connections** & **Axis Coloring** & **Performance**.
- **Evolution**: 심미성, 기능성, 성능 최적화에 이어 **레이아웃 안정성**을 확보했습니다.

### Key Features
1.  **Structure & Layout**:
    -   **Scale Adjustment**: 차트의 반경 계산 로직을 수정하여(`maxRadius` 축소), 3차 감정(Tertiary) 링이 패딩 영역을 침범하지 않고 전체 화면 내에 안정적으로 배치되도록 했습니다.
    -   **Bug Fix (Optimism Node)**: Angle Wrap-around 문제를 해결하여 모든 감정 노드 연결이 정상적으로 그려집니다.

2.  **Emphasis (Foreground)**:
    -   **Active Emotion**: 색상 점 + 굵은 라벨.
    -   **Axis Coloring**: 활성 감정의 근간이 되는 축 전체가 해당 감정 색상으로 빛납니다.

3.  **Internationalization (I18n)**:
    -   **Support**: 한국어 (기본 `values`) / English (`values-en`).
    -   **Coverage**: 앱 내 모든 문자열 (감정 이름, 설명, UI 텍스트 등)의 완벽한 영문 번역 리소스 추가 완료.

4.  **Optimization (Performance)**:
    -   **Caching**: `BoxWithConstraints`와 `remember`를 활용해 좌표 계산을 캐싱, 렌더링 성능을 극대화했습니다.

### 확인 방법
- **영역 확인**: 차트의 가장 바깥쪽 요소(Tertiary Label 등)가 흰색 박스 영역을 벗어나거나 잘리지 않는지 확인.
- **UI 반응성**: 스크롤 및 애니메이션 시 부드러운 동작 확인.

### 5. Selective Highlighting (Visual Refresh)
- **Goal**: "선택과 집중". 사용자의 최종 감정을 명확히 드러내기 위해 비활성 요소를 과감히 덜어냈습니다.
- **New Rules**:
    - **Grayscale Default**: 모든 기본 구조, 점, 선은 무채색(LightGray/Black)이 기본입니다.
    - **Active Emphasis**: 오직 감정 점수가 존재하는 "최종 감정"과 그 구성 요소만 색상으로 빛납니다.
    - **Trace Logc (경로 추적)**:
        - **Basic**: Center -> Basic Node.
        - **Primary**: Center -> Basic Nodes (Mid) -> Primary Node.
        - **Sec/Tert**: Center -> Basic Nodes (Full) -> Connection -> Final Node.

### 6. Intensity Scale Inversion (User Request)
- **Change**: 기존 (Inner=Strong, Outer=Weak) -> **(Inner=Weak, Outer=Strong)**.
- **Reason**: 일반적인 레이더 차트의 직관성과 일치시키기 위함.
- **Effect**: 강한 감정일수록 차트의 가장자리(Outer)에 배치되며, 그래프가 바깥으로 뻗어나가는 형태가 됩니다.

### 7. Final Visual Polish (Step-by-Step Methodology)
- **Goal**: 사용자가 정의한 5단계 그리기 로직을 엄격하게 구현했습니다.
- **Process**:
    1.  **Coordinate Calculation**: 모든 좌표(Basic, Primary, Sec/Tert Axes & Rings) 사전 계산.
    2.  **Background Glow**: **최종 감정(Final Emotion)** 뒤에 은은하게 퍼지는 색상 배경(Glow) 추가.
    3.  **Dot Differentiation**:
        -   **Final Dot**: 크고, 색상이 채워져 있으며, 하얀 눈(Eye)이 있는 강조 스타일.
        -   **Origin Dot (Basic)**: 크기 변화 없이, 하얀 내부 + 색상 테두리(Border) 스타일.
    4.  **Trace Logic (Line Coloring)**:
        -   **1차(Primary)**: Center -> Basic(Mid) -> Dyad Dot.
        -   **2/3차(Sec/Tert)**: Center -> Basic(Outer) -> Dyad Dot.
    5.  **Labels**:
        -   모든 라벨(All Labels)을 흐리게(Gray) 표시하여 위치 정보 제공.
        -   **최종 감정 라벨**만 진하고(Black/Bold) 크게 강조.

### 8. Visual Consistency (Legacy -> Neo-Brutalism Update)
- **Concept**: 기존 `NetworkChart`의 디자인 언어(Bold, Shadow, High Contrast)를 `ConstellationChart`에도 적용하여 앱 전체의 일관성을 확보했습니다.
- **Changes**:
    -   **Web Lines**: 0.5dp Gray -> **1.dp Solid Gray Web**. 명확한 구조감을 부여.
    -   **Active Traces**: 1.5dp -> **4.dp Active Line**. 선택된 감정 경로를 압도적으로 강조.
    -   **Active Traces**: 1.5dp -> **4.dp Active Line**. 선택된 감정 경로를 압도적으로 강조.
    -   **Dots**:
        -   **Final**: Large Radius (16dp) + **Black Border** + **Solid Shadow**. (유일한 Neo-Brutalism 강조)
        -   **Active Ingredients**: **Basic Standard Style** (White Fill + Color Border). 최종 감정과의 명확한 구분을 위해 심플하게 유지.
    -   **Typography**: 모든 라벨의 가독성을 위해 Weight 상향 (Regular -> **Bold/ExtraBold**) 및 Black Color 적용.
    -   **Typography**: 모든 라벨의 가독성을 위해 Weight 상향 (Regular -> **Bold/ExtraBold**) 및 Black Color 적용.

## 9. 감정 데이터 모델 고도화 (Refactoring)
- **목표**: 원본 감정 분석 데이터(`EmotionAnalysis`)를 사용자 친화적인 가공 결과(`EmotionResult`)로 전환하여 데이터 흐름을 최적화하고 UI 표시 효율을 높였습니다.

### 주요 변경 사항
1.  **도메인 레이어 (Domain)**:
    -   `EmotionResult` 데이터 클래스 신설: 라벨, 설명, 주/부 감정, 점수, 카테고리 등을 포함.
    -   `JournalEntry` 수정: `emotionAnalysis` 필드를 `emotionResult`로 교체.
    -   `PlutchikEmotionCalculator`: 분석 로직이 `EmotionResult`를 직접 반환하도록 고도화.
2.  **데이터 레이어 (Data)**:
    -   `EmotionAnalysisEntity`: 가공된 결과(라벨, 설명 등)를 DB에 직접 저장하여 조회 성능 개선.
    -   매퍼(Mapper) 업데이트: `EmotionResult`와 Entity 간의 양방향 변환 로직 구현.
    -   리포지토리: `saveEmotionAnalysis`가 가공된 결과를 저장하도록 수정.
3.  **유스케이스 (UseCase)**:
    -   `SaveAndAnalyzeJournalEntryUseCase`: **분석 -> 가공 -> 저장**으로 이어지는 신규 데이터 파이프라인 구축.
    -   통합 분석 유스케이스들(통계, 통합 분석)이 새로운 모델을 참조하도록 전면 수정.
4.  **프리젠테이션 레이어 (Presentation)**:
    -   `MockData`: 모든 테스트 데이터가 `EmotionResult`를 포함하도록 업데이트.
    -   `JournalCard` (목록): `emotionResult`의 라벨과 가공된 배경색(블렌딩 포함)을 즉시 적용.
    -   `DetailContent` (상세): 요약 정보는 가공된 결과를, 차트는 원본 데이터(`source`)를 사용하도록 이원화하여 정확도와 사용자 경험 동시 확보.

### 기술적 성과
- **성능 최적화**: 매번 화면 진입 시 발생하던 감정 계산 로직을 데이터 쓰기 시점으로 옮겨 UI 렌더링 부하를 줄였습니다.
- **데이터 정합성**: `EmotionResult` 내에 `source` 필드를 유지함으로써, 가공된 결과와 원본 데이터 사이의 연결 고리를 명확히 했습니다.
- **UI 일관성**: `EmotionColorUtil`을 통한 색상 결정 로직을 중앙 집중화하여 목록과 상세 화면의 시각적 경험을 통일했습니다.

## 10. 감정 분석 문자열 의존성 제거 (String Dependency Refactoring)

다국어 지원 및 아키텍처 개선을 위해 `EmotionResult` 및 `EmotionAnalysisEntity`에 포함되어 있던 문자열(`label`, `description`) 필드를 제거하였습니다.

### 변경 사항
- **Domain Layer**:
    - `EmotionResult` 데이터 클래스에서 `label`, `description` 제거.
    - `PlutchikEmotionCalculator`에서 문자열 생성 로직 및 `EmotionStringProvider` 의존성 제거.
    - `AnalyzeIntegratedEmotionUseCase`에서 필요한 문자열을 로컬에서 생성하도록 변경.
- **Data Layer**:
    - `EmotionAnalysisEntity`에서 `label`, `description` 컬럼 제거.
    - `EmotionResult` Mapper(`toEntity`, `toDomain`) 업데이트.
- **Presentation Layer**:
    - `EmotionUiUtil` 유틸리티 클래스 생성: `EmotionResult`와 `EmotionStringProvider`를 받아 Label/Description을 생성하는 로직 중앙화.
    - `EntryListScreen`, `EntryDetailScreen`: `EmotionUiUtil.getLabel()`을 사용하여 동적으로 라벨 표시.
    - **Color Logic**: `EmotionColorUtil`을 업데이트하여, 단순/복합 감정에 관계없이 2차 감정이 존재할 경우 색상을 블렌딩하여 '최종 감정'의 색상을 표현하도록 로직 개선.
    - Charts (`ConstellationChart`, `RelationChart`, `DailyEmotionDistributionChart`): `EmotionUiUtil`을 사용하여 차트 내 라벨 표시.
- **Test/Mock**:
    - `MockData`의 `EmotionResult` 생성 로직 업데이트.

### 4. 감정 관계망 차트 (Network Chart) 개선
- **Word Cloud Chart**: 사용자의 요청에 따라 더욱 정교한 워드 클라우드를 구현했습니다.
    - **Visual Style**: 모든 텍스트는 **검은색(Black)**으로 통일하여 가독성을 높였습니다.
    - **Layout Algorithm**: Greedy Spiral Algorithm을 사용하며, **0도, 90도, -90도 랜덤 회전**을 적용하여 역동적인 구름 형태를 연출했습니다.
    - **4-Level Hierarchy**:
        1.  **Final Emotion**: 압도적인 크기 (40sp), 중앙 배치.
        2.  **Primary/Secondary**: 큰 크기 (28sp), 주요 감정.
        3.  **Other Emotions**: 중간 크기 (22sp), 키워드에서 추출된 기타 감정.
        4.  **Keywords**: 작은 크기 (14~18sp), 점수에 비례.

### 5. Detail Screen Layout Refinement
- **Consolidated UI**: 텍스트 분석 카드와 차트를 분리하지 않고, 하나의 카드 형태로 통합했습니다.
    - **Chart in Card**: 워드 클라우드 차트를 카드 내부에 배치하여 정보 집중도를 높임.
    - **Style**: 카드의 그림자, 테두리, 배경색 스타일을 차트에 적용하여 디자인 일관성 유지.
- **Aspect Ratio Tuning**: 1:1 비율에서 오는 과도한 여백을 줄이기 위해 **1.6:1 (약 16:10) 비율**로 조정하여 텍스트 구름이 더 꽉 차 보이도록 개선했습니다.
- **Auto-Scale to Fit**: 가로로 긴 비율(Wide Ratio) 변경 시 텍스트가 잘리지 않도록, 전체 배치 영역을 계산하여 화면에 딱 맞게 **자동 축소/확대(Scale)** 하는 로직을 추가했습니다.
- **Detail Button Styling**: "View Detailed Analysis" 버튼에 **하드 그림자(Hard Shadow)**와 **굵은 테두리(Bold Border)**를 적용하여 카드 UI와의 디자인 일관성을 확보했습니다.
- **UI Localization**: `EntryDetailScreen` 및 `AnalysisDetailScreen`의 모든 UI 텍스트를 `strings.xml`로 추출하여 **한국어 및 영어 다국어 지원**을 적용했습니다.
- **NeoShadowBox Component**: 반복되는 하드 그림자(Hard Shadow) UI 코드를 **`NeoShadowBox` 공용 컴포넌트**로 리팩토링하여 `EntryDetail`, `List`, `Home`, `Guide` 화면에 일괄 적용하고 코드 중복을 제거했습니다.
- **Refactor Analysis Detail**: `AnalysisDetailScreen`의 ViewModel을 분리(`AnalysisDetailViewModel`)하고, 헤더 UI를 `EntryDetailScreen`과 동일한 구조(Back Button + Divider)로 통일하여 디자인 일관성을 강화했습니다.
- **Analysis Content Update**: `AnalysisDetailScreen`의 컨텐츠를 개편하여 기존 RadarChart 대신 **`EmotionPolarChart` (감정 분포)**와 **`ConstellationChart` (감정 별자리)**를 추가했습니다. 또한 불필요한 **오버스크롤 효과를 제거**하여 깔끔한 UX를 제공합니다.
- **Preview Support**: `AnalysisDetailScreen`의 UI를 쉽게 확인할 수 있도록 `AnalysisDetailContent`를 분리하고 **Preview Composable**을 추가했습니다.
- **EmotionPolarChart Design**: `EmotionPolarChart`의 애니메이션을 제거하여 즉각적인 반응성을 확보하고, **Neo-Brutalism** 스타일(Solid Color + Bold Border)을 적용하여 디자인 일관성을 높였습니다.
- **EmotionPolarChart Layout**: 차트 중앙에 빈 공간을 추가(Donut Style)하고, 모든 모서리에 라운딩 처리를 적용하여 더욱 부드럽고 세련된 시각적 경험을 제공합니다. 또한 섹션 간 간격을 확대하여 각 감정의 독립성을 강조했습니다.
- **Chart Background**: 데이터가 없는 영역에서도 전체적인 차트 구조(8가지 감정)를 인지할 수 있도록, 각 감정 영역에 **회색(Gray) 배경**을 흐릿하게 추가하여 시각적 안정감을 더했습니다.
- **Fix Chart Overflow**: `NeoShadowBox` 내부의 차트 배경이 둥근 모서리를 침범하는 문제를 `modifier.clip(shape)`을 적용하여 해결했습니다.
- **Header Standardization**: `AnalysisDetailScreen`의 헤더를 `EntryDetailScreen`과 동일한 구조(Back Button + Divider)로 통일하고, 차트 타이틀에 다국어(영어) 지원을 추가했습니다.

### 7. Rich Emotion Data
Implemented detailed descriptions and actionable advice for all 52 emotion types (Plutchik's model). (KR/EN Support)
- **Data Structure**: `strings.xml` (default) and `values-en/strings.xml` with dynamic lookup keys (`emotion_desc_[name]`, `emotion_advice_[name]`).
- **Provider**: Updated `EmotionStringProvider` to support dynamic resource retrieval by Enum name, enabling scalability without large `when` blocks.
- **UI**: Added "Description" text below the title and a dedicated "Advice Card" at the bottom of `AnalysisDetailScreen`.

### 8. Analysis Screen Refinement
- **Content Reordering**: Revised the layout flow to "Emotion Report -> Network Chart -> Word Cloud" for better narrative structure (What -> So What -> Why).
- **Emotion Report Card**: Consolidated Emotion Label, Icon, Description, and Advice into a single, unified card component titled "Emotion Analysis Report".
    - **Visual Grouping**: Used `NeoShadowBox` to group related text information.
    - **Advice Styling**: Presented advice in a subtle, de-emphasized inner box to prioritize the main emotion diagnosis.
- **Emotion Icon Integration**:
    - **Dynamic Lookup**: Implemented `getEmotionImageResId` to dynamically load `img_emotion_*` resources.
    - **Async Loading**: Replaced standard `Image` with Coil's `AsyncImage` for optimized memory usage (downsampling) and asynchronous loading.

### 6. (Deleted) Physics Engine & Text-Based List
- 초기 Force-Directed Graph 및 과도기적 Text-Based List 구현체는 폐기되었습니다.
- 워드 클라우드가 감정의 복합성을 가장 직관적으로 보여주는 방식으로 최종 결정되었습니다.

### 9. Home Screen Redesign (Timeline Feed)
- **Goal**: Replace card-based Home with a thread-style Timeline Feed.
- **Changes**:
    - **Created `HomeFeedScreen`**: Implemented `Scaffold` with `Drawer` and Custom TopBar.
    - **Timeline UI**: Used `LazyColumn` with `stickyHeader` to group entries by date.
    - **Feed Logic**: Updated `HomeViewModel` to support grouping (Daily, Weekly, Monthly) and use `createdAt` instead of `date`.
    - **Refactoring**: Extracted `OutlinedDivider` and `OutlinedTimeText` to `HomeComponents.kt`.
    - **Cleanup**: Deleted legacy `HomeScreen.kt` after migration.
    - **Navigation**: Verified navigation from Feed Item -> Entry Detail.

### Refactoring & I18n
- **Strings**: Extracted all hardcoded `HomeFeedScreen` strings to `strings.xml` (Korean) and `values-en/strings.xml` (English).
- **Stateless UI**: Confirmed `HomeFeedContent` takes stable parameters and minimal state. verified build success.

### Entry Detail Analysis UI
- **Consolidation**: Merged `AnalysisDetailScreen` content into `EntryDetailScreen`.
- **Layout**: Implemented 3-section layout:
    1.  **Report Card**: Extracted from analysis screen.
    2.  **Word Cloud**: Implemented using `NetworkChart`.
    3.  **Interactive Chart**: Implemented `EmotionPolarChart` <-> `ConstellationChart` toggle with flip animation.
- **Performance**: Verified build success. Charts are wrapped in `movableContentOf` (cached) and `remember` to prevent recomposition.
- **Visuals**: Applied emotion color to Report Card. Background uses subtle gradient or solid color as requested (currently solid default background with colored card).

### Home Feed Design Refactoring
- **FeedEntryItem**: Removed `NeoShadowBox` shadow. Converted to a simple rounded card with colored background for cleaner list look.
- **WriteEntryPrompt**: Added `NeoShadowBox` shadow to emphasize the "Write" prompt.
- **Code Quality**: Fixed deprecation warnings for `Icons.Filled` by switching to `Icons.AutoMirrored.Filled`.

### Emotion Derivation Explanation
- **Feature**: Added a dynamic explanation tip below the Emotion Analysis Chart in `EntryDetailScreen`.
- **Logic**: Automatically generates a description based on whether the final emotion is a Single emotion, a Combination (Dyad), or a Conflict (Opposite).
- **Resources**: Added reusable string templates in `strings.xml` for derivation logic.
- **Maintenance**: Fixed `quadraticBezierTo` deprecation warnings in `EmotionPolarChart` and `ArrowBack` icon deprecations.

### Network Chart Visual Refresh (Black Text + Radial Glow)
- **Feature**: Reverted Text Color to **Black** (for maximum contrast) but kept the **Radial Gradient Glow**.
- **Style Rules**:
    - **Text Color**: **Black** (All items).
    - **Background**: **Radial Gradient Glow** fading from emotion color (low alpha) to transparent.
- **Benefit**: Achieves the best balance of readability (Black text) and aesthetic emotion expression (Colored Glow).

### 10. (Debug / Refactor) Analysis Status & Gemini Configuration
- **Issue**: Analysis progress indication caused persistence issues (stuck in loading) if the app was closed during analysis.
- **Solution**:
    - **State Management**: Moved `AnalysisStatus.PENDING` tracking from database persistence to `EntryDetailViewModel`'s **local UI State (`isAnalyzing`)**.
    - **Effect**: If the app is killed during analysis, the UI simply reverts to the previous valid state (e.g., failed or not analyzed) instead of showing an infinite loading spinner upon restart.
- **API Configuration**:
    - **Model Update**: Updated `GeminiApiClient` to use the newer **`gemini-2.5-flash`** model.
    - **Logging**: Added `Timber` logging to the API client to surface specific error codes (e.g., 404 Model Not Found, 403 Permission Denied) previously swallowed by empty catch blocks.

### 11. Blocking Loading Overlay (UX Improvement)
- **Goal**: Prevent user interaction (e.g., Back button, double clicks) during critical async operations like analysis.
- **Component**: Created `BlockingLoadingOverlay` - a reusable, full-screen, semi-transparent overlay that consumes all touch events.
- **Features**:
    - **Z-Index**: Ensures it renders on top of all other UI elements (Layer optimization).
    - **Optional Message**: Supports injecting a loading message (e.g., "Analyzing...") or showing only the spinner.
- **Integration**: Applied to `WriteEntryScreen` and `EntryDetailScreen` to lock the UI while the emotion analysis API is in progress.
