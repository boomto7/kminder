# Gemini API 404 오류 해결 가이드

## ❌ 발생한 오류

```
models/gemini-xxx is not found for API version v1beta
```

이 오류는 다음과 같은 이유로 발생할 수 있습니다:

## 🔍 원인 및 해결 방법

### 1. API 키 권한 문제 (가장 가능성 높음)

#### 확인 방법:
1. [Google AI Studio](https://makersuite.google.com/app/apikey) 접속
2. API 키 확인
3. **"Get API key"** 버튼이 보이면 새로 생성 필요

#### 해결 방법:
```
1. Google AI Studio에서 새 API 키 생성
2. local.properties 업데이트:
   GEMINI_API_KEY=새로운_API_키
3. Gradle Sync 실행
4. 테스트 재실행
```

### 2. API 키 지역 제한

일부 지역에서는 Gemini API 사용이 제한될 수 있습니다.

#### 확인 방법:
- [Gemini API 지원 국가](https://ai.google.dev/available_regions) 확인
- 한국은 지원됩니다 ✅

### 3. 모델 이름 문제

현재 코드에서 시도한 모델들:
- ❌ `gemini-1.5-flash` → 404 오류
- ❌ `gemini-1.5-flash-latest` → 404 오류
- ⏳ `gemini-pro` → 현재 설정

#### 대체 방법:

**GeminiApiClient.kt 수정**:
```kotlin
// 방법 1: gemini-pro (기본)
modelName = "gemini-pro"

// 방법 2: 버전 명시
modelName = "models/gemini-pro"

// 방법 3: 1.5 버전 (API 키에 따라)
modelName = "gemini-1.5-pro"
```

### 4. SDK 버전 문제

현재 SDK 버전: `0.9.0`

#### 최신 버전으로 업데이트:

**gradle/libs.versions.toml**:
```toml
generativeai = "0.9.0"  # 현재
# 또는
generativeai = "0.9.1"  # 최신 (있다면)
```

## 🛠️ 권장 해결 순서

### Step 1: API 키 재생성

1. **Google AI Studio 접속**
   ```
   https://makersuite.google.com/app/apikey
   ```

2. **새 API 키 생성**
   - "Create API key" 클릭
   - 프로젝트 선택 또는 새로 생성
   - API 키 복사

3. **local.properties 업데이트**
   ```properties
   GEMINI_API_KEY=새로_생성한_API_키
   ```

4. **Gradle Sync**
   - Android Studio에서 Sync Project with Gradle Files

### Step 2: 간단한 테스트

API 키가 작동하는지 확인:

```kotlin
// 테스트 코드
val model = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = "your_api_key"
)

val response = model.generateContent("Hello")
println(response.text)
```

### Step 3: 모델 이름 변경 시도

만약 `gemini-pro`도 안 되면:

```kotlin
// 1. 전체 경로 사용
modelName = "models/gemini-pro"

// 2. 다른 모델 시도
modelName = "gemini-1.0-pro"

// 3. 최신 모델
modelName = "gemini-1.5-pro-latest"
```

## 🔄 대안: REST API 직접 사용

SDK가 계속 문제가 있다면 REST API를 직접 호출:

```kotlin
// Retrofit을 사용한 직접 호출
interface GeminiApiService {
    @POST("v1/models/gemini-pro:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}
```

## 📝 현재 상태 확인

### 확인할 사항:

1. ✅ API 키가 local.properties에 있는가?
2. ✅ API 키가 유효한가? (Google AI Studio에서 확인)
3. ✅ 인터넷 연결이 되어 있는가?
4. ✅ 방화벽이 API 호출을 차단하지 않는가?

### 테스트 명령어:

```bash
# API 키 확인 (마스킹됨)
cat local.properties | grep GEMINI_API_KEY

# Gradle Sync
./gradlew --refresh-dependencies

# 테스트 실행
./gradlew :data:testDebugUnitTest --tests "*.GeminiApiTest"
```

## 💡 임시 해결책

테스트를 위해 임시로 Mock 데이터 사용:

```kotlin
// GeminiApiClient.kt에 추가
suspend fun analyzeEmotionMock(text: String): Result<EmotionAnalysis> {
    // 임시 Mock 데이터
    return Result.success(
        EmotionAnalysis(
            anger = 0.05f,
            anticipation = 0.85f,
            joy = 0.70f,
            trust = 0.30f,
            fear = 0.20f,
            sadness = 0.05f,
            disgust = 0.00f,
            surprise = 0.40f
        )
    )
}
```

## 🆘 추가 도움

### Google AI Studio 문서:
- [Gemini API 시작하기](https://ai.google.dev/tutorials/get_started_android)
- [사용 가능한 모델 목록](https://ai.google.dev/models/gemini)

### 문제가 계속되면:
1. API 키를 완전히 새로 생성
2. 다른 Google 계정으로 시도
3. VPN 사용 (지역 제한 가능성)

---

**가장 먼저 API 키를 재생성해보세요!** 대부분의 404 오류는 API 키 권한 문제입니다.
