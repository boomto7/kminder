# Gemini API 키 설정 가이드

## 🔑 API 키 발급

1. [Google AI Studio](https://makersuite.google.com/app/apikey)에 접속
2. Google 계정으로 로그인
3. "Create API Key" 버튼 클릭
4. API 키 복사

## ⚙️ 프로젝트 설정

### 1. local.properties 파일 수정

프로젝트 루트의 `local.properties` 파일을 열고 다음 줄을 추가하세요:

```properties
GEMINI_API_KEY=여기에_발급받은_API_키_붙여넣기
```

**예시:**
```properties
sdk.dir=/Users/username/Library/Android/sdk
GEMINI_API_KEY=AIzaSyABC123def456GHI789jkl012MNO345pqr
```

### 2. Gradle Sync

Android Studio에서 Gradle Sync를 실행하세요:
- 메뉴: File → Sync Project with Gradle Files
- 또는 상단 툴바의 "Sync Now" 클릭

### 3. 빌드 및 실행

이제 앱을 빌드하고 실행할 수 있습니다!

## 🔒 보안 주의사항

### ✅ 안전한 방법 (현재 설정)
- ✅ `local.properties`에 API 키 저장
- ✅ `.gitignore`에 `local.properties` 포함
- ✅ BuildConfig를 통해 접근

### ❌ 위험한 방법 (절대 금지)
- ❌ 코드에 직접 하드코딩
- ❌ Git에 커밋
- ❌ 공개 저장소에 업로드

## 🔍 확인 방법

API 키가 제대로 설정되었는지 확인:

1. **빌드 성공 확인**
   ```bash
   ./gradlew build
   ```

2. **BuildConfig 확인**
   - 빌드 후 `data/build/generated/source/buildConfig/` 폴더에서 확인 가능
   - `BuildConfig.GEMINI_API_KEY` 값이 설정되어 있어야 함

3. **앱 실행 테스트**
   - 일기 작성 후 감정 분석이 정상 작동하는지 확인

## 🐛 문제 해결

### API 키가 비어있는 경우

**증상:**
```
BuildConfig.GEMINI_API_KEY = ""
```

**해결 방법:**
1. `local.properties` 파일에 `GEMINI_API_KEY` 추가 확인
2. Gradle Sync 재실행
3. Clean & Rebuild:
   ```bash
   ./gradlew clean build
   ```

### API 호출 실패

**증상:**
```
API call failed: Invalid API key
```

**해결 방법:**
1. API 키가 올바른지 확인
2. [Google AI Studio](https://makersuite.google.com/app/apikey)에서 API 키 상태 확인
3. 필요시 새 API 키 발급

### BuildConfig를 찾을 수 없는 경우

**증상:**
```
Unresolved reference: BuildConfig
```

**해결 방법:**
1. `data/build.gradle.kts`에 `buildFeatures { buildConfig = true }` 확인
2. Gradle Sync 재실행
3. Rebuild Project

## 📋 체크리스트

설정 완료 전 확인사항:

- [ ] Google AI Studio에서 API 키 발급
- [ ] `local.properties`에 `GEMINI_API_KEY` 추가
- [ ] Gradle Sync 실행
- [ ] 빌드 성공 확인
- [ ] `.gitignore`에 `local.properties` 포함 확인
- [ ] API 키를 Git에 커밋하지 않았는지 확인

## 🚀 다음 단계

API 키 설정이 완료되면:

1. 앱 실행
2. 일기 작성
3. 감정 분석 기능 테스트
4. 통계 화면에서 결과 확인

---

**중요:** API 키는 절대 공개 저장소에 커밋하지 마세요! 
`local.properties`는 `.gitignore`에 포함되어 있으므로 안전합니다.
