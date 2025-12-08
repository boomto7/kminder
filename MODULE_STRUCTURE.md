# Minder 프로젝트 모듈 구조

## 📦 모듈 구성

```
minder/
├── app/                    # 프레젠테이션 계층 (UI, ViewModel)
│   └── build.gradle.kts    # Compose, Hilt, Navigation 등
├── data/                   # 데이터 계층 (Repository, DB, API)
│   └── build.gradle.kts    # Room, Retrofit, Gemini API 등
└── domain/                 # 도메인 계층 (비즈니스 로직, UseCase)
    └── build.gradle.kts    # 순수 Kotlin, Coroutines만 사용
```

## 🏗️ Clean Architecture 레이어

### 1. Domain 모듈 (`:domain`)
- **타입**: 순수 Kotlin 모듈 (Android 의존성 없음)
- **역할**: 비즈니스 로직, UseCase, Entity, Repository Interface
- **의존성**:
  - Kotlin Coroutines (Core)
  - JUnit (테스트)

### 2. Data 모듈 (`:data`)
- **타입**: Android Library
- **역할**: Repository 구현, 데이터 소스 (Local DB, Remote API)
- **의존성**:
  - Domain 모듈
  - Room (로컬 데이터베이스)
  - Retrofit & OkHttp (네트워크 통신)
  - Gemini API (감정 분석)
  - Hilt (의존성 주입)
  - Coroutines

### 3. App 모듈 (`:app`)
- **타입**: Android Application
- **역할**: UI (Jetpack Compose), ViewModel, Navigation
- **의존성**:
  - Domain 모듈
  - Data 모듈
  - Jetpack Compose
  - Navigation Compose
  - Hilt (의존성 주입)
  - ViewModel

## 📋 의존성 방향

```
app → data → domain
```

- `app`은 `data`와 `domain`에 의존
- `data`는 `domain`에만 의존
- `domain`은 어떤 모듈에도 의존하지 않음 (순수 Kotlin)

## 🔧 주요 기술 스택

### 의존성 관리
- **Version Catalog** (`gradle/libs.versions.toml`)

### 의존성 주입
- **Hilt** (Google Dagger 기반)

### 데이터베이스
- **Room** (로컬 데이터 저장)

### 네트워크
- **Retrofit** (REST API 클라이언트)
- **OkHttp** (HTTP 클라이언트)

### AI/ML
- **Gemini API** (감정 분석)

### UI
- **Jetpack Compose** (선언적 UI)
- **Material3** (디자인 시스템)
- **Navigation Compose** (화면 전환)

### 비동기 처리
- **Kotlin Coroutines** (비동기 프로그래밍)

## 🚀 다음 단계

이제 각 모듈에서 다음 작업을 진행할 수 있습니다:

### Domain 모듈
- [ ] Entity 클래스 정의
- [ ] Repository Interface 정의
- [ ] UseCase 클래스 구현

### Data 모듈
- [ ] Room Database 설정
- [ ] DAO 인터페이스 정의
- [ ] Gemini API 클라이언트 구현
- [ ] Repository 구현체 작성
- [ ] Hilt Module 설정

### App 모듈
- [ ] Navigation Graph 구성
- [ ] UI Screen 구현 (Compose)
- [ ] ViewModel 구현
- [ ] Hilt Application 클래스 생성

## 📝 참고사항

- Android Studio에서 프로젝트를 열면 자동으로 Gradle Sync가 실행됩니다.
- 모든 모듈은 Java 11을 타겟으로 설정되어 있습니다.
- KSP (Kotlin Symbol Processing)를 사용하여 Hilt와 Room의 코드 생성을 처리합니다.
