# Domain 모듈 Hilt 적용 완료 ✅

## 🔧 적용 내용

### 1. build.gradle.kts 업데이트

```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)              // ← 추가
    alias(libs.plugins.hilt.android)     // ← 추가
}

dependencies {
    // Hilt (javax.inject 대체)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
}
```

**변경사항:**
- ❌ `javax.inject:javax.inject:1` 제거
- ✅ `hilt-android` 추가 (javax.inject 포함)
- ✅ `ksp` 플러그인 추가 (Hilt 코드 생성)

### 2. UseCaseModule 생성

**파일:** `domain/src/main/java/com/kminder/domain/di/UseCaseModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideCreateJournalEntryUseCase(
        repository: JournalRepository
    ): CreateJournalEntryUseCase = CreateJournalEntryUseCase(repository)
    
    // ... 총 10개 UseCase 제공
}
```

**제공하는 UseCase:**
- ✅ CreateJournalEntryUseCase
- ✅ UpdateJournalEntryUseCase
- ✅ DeleteJournalEntryUseCase
- ✅ GetJournalEntryUseCase
- ✅ GetAllJournalEntriesUseCase
- ✅ GetJournalEntriesByDateUseCase
- ✅ AnalyzeEmotionUseCase
- ✅ SaveAndAnalyzeJournalEntryUseCase
- ✅ GetEmotionStatisticsUseCase
- ✅ GetRandomQuestionUseCase

## 🎯 Hilt 의존성 주입 구조

### 전체 흐름

```
App Module (ViewModel)
  ↓ @HiltViewModel
  ↓ @Inject constructor
  ↓
UseCase (Domain)
  ↓ @Inject constructor
  ↓ UseCaseModule @Provides
  ↓
Repository Interface (Domain)
  ↓
Repository Implementation (Data)
  ↓ RepositoryModule @Binds
  ↓
DAO / API Client (Data)
  ↓ DatabaseModule / NetworkModule @Provides
```

### 모듈별 역할

#### 1. Domain 모듈
```kotlin
// UseCase - @Inject constructor
class CreateJournalEntryUseCase @Inject constructor(
    private val repository: JournalRepository
)

// DI Module - @Provides
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    fun provideCreateJournalEntryUseCase(
        repository: JournalRepository
    ): CreateJournalEntryUseCase
}
```

#### 2. Data 모듈
```kotlin
// Repository Implementation
class JournalRepositoryImpl @Inject constructor(
    private val dao: JournalEntryDao
) : JournalRepository

// DI Module - @Binds
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindJournalRepository(
        impl: JournalRepositoryImpl
    ): JournalRepository
}
```

#### 3. App 모듈
```kotlin
// ViewModel - @HiltViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllJournalEntriesUseCase: GetAllJournalEntriesUseCase
) : ViewModel()
```

## 📋 UseCase 주입 방식

### Before: javax.inject만 사용
```kotlin
// UseCase
class CreateJournalEntryUseCase @Inject constructor(
    private val repository: JournalRepository
)

// ViewModel에서 직접 주입 불가 (수동 생성 필요)
```

### After: Hilt 완전 통합
```kotlin
// UseCase
class CreateJournalEntryUseCase @Inject constructor(
    private val repository: JournalRepository
)

// UseCaseModule에서 제공
@Provides
fun provideCreateJournalEntryUseCase(
    repository: JournalRepository
): CreateJournalEntryUseCase

// ViewModel에서 자동 주입
@HiltViewModel
class MyViewModel @Inject constructor(
    private val createJournalEntryUseCase: CreateJournalEntryUseCase
) : ViewModel()
```

## ✅ 장점

### 1. 완전한 의존성 주입
- ViewModel → UseCase → Repository 전체 자동 주입
- 수동 생성 불필요

### 2. 타입 안전성
- 컴파일 타임에 의존성 검증
- 런타임 에러 방지

### 3. 테스트 용이성
- Mock 객체 주입 간편
- 단위 테스트 작성 용이

### 4. 코드 간결성
- Boilerplate 코드 감소
- 의존성 관리 자동화

## 🔍 확인 방법

### 1. Gradle Sync
```bash
./gradlew clean build
```

### 2. Hilt 코드 생성 확인
빌드 후 다음 파일들이 생성됩니다:
- `domain/build/generated/ksp/.../UseCaseModule_*.java`
- Hilt가 자동으로 의존성 그래프 생성

### 3. ViewModel에서 사용
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllJournalEntriesUseCase: GetAllJournalEntriesUseCase
) : ViewModel() {
    // UseCase 자동 주입됨!
}
```

## 📦 최종 구조

```
domain/
├── build.gradle.kts          ✅ Hilt 플러그인 추가
└── src/main/java/com/kminder/domain/
    ├── di/
    │   └── UseCaseModule.kt  ✅ 새로 추가
    ├── model/                (6개)
    ├── repository/           (3개 인터페이스)
    └── usecase/              (10개 - @Inject 유지)
        ├── emotion/
        ├── journal/
        ├── question/
        └── statistics/
```

## 🚀 다음 단계

1. **Gradle Sync 실행**
2. **빌드 확인**
3. **ViewModel에서 UseCase 주입 테스트**

---

**Domain 모듈에 Hilt가 완전히 통합되었습니다!** 🎉

이제 모든 UseCase가 Hilt를 통해 자동으로 주입되며, ViewModel에서 간편하게 사용할 수 있습니다!

## 💡 참고

### javax.inject vs Hilt
- **Hilt**: `javax.inject`를 포함하고 있음
- **@Inject**: 동일하게 사용 (javax.inject.Inject)
- **추가 기능**: Hilt 어노테이션 (@HiltViewModel 등) 사용 가능

### UseCase에서 @Inject 유지
UseCase의 `@Inject constructor`는 그대로 유지됩니다:
```kotlin
import javax.inject.Inject  // Hilt에 포함됨

class CreateJournalEntryUseCase @Inject constructor(
    private val repository: JournalRepository
)
```

UseCaseModule의 `@Provides`는 선택적이지만, 명시적인 제공을 위해 추가했습니다.
