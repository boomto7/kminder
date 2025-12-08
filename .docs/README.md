# Minder 프로젝트 문서

이 폴더에는 Minder 앱 개발을 위한 기획 문서와 에이전트 설정 파일들이 포함되어 있습니다.

## 📋 기획 문서
- **PRD.md**: 제품 요구사항 정의서
- **AGENT_ROLES.md**: 에이전트 역할 정의 (기획자, 개발자, 디자이너, 테스터)
- **implementation_plan.md**: 구현 계획

## 🤖 에이전트 프롬프트
`agent_prompts/` 폴더에는 각 에이전트의 상세 시스템 프롬프트가 있습니다:
- `planner_agent.md`: 프로젝트 매니저 역할
- `developer_agent.md`: 안드로이드 개발자 역할
- `designer_agent.md`: UI/UX 디자이너 역할
- `tester_agent.md`: QA 엔지니어 역할

## 🎯 사용 방법
새로운 대화를 시작할 때 다음과 같이 요청하세요:
```
".docs 폴더의 문서들을 참조해서 Minder 프로젝트 개발을 계속 진행해줘"
```

## 🏗️ 아키텍처
- **Multi-module Clean Architecture**
  - `:domain` - 순수 Kotlin 모듈 (비즈니스 로직, UseCase)
  - `:data` - 안드로이드 라이브러리 (Repository 구현, DB, API)
  - `:app` - 프레젠테이션 계층 (UI, ViewModel)

## 📦 주요 기술 스택
- Kotlin, Jetpack Compose
- Hilt (DI)
- Room (Local DB)
- Gemini API (감정 분석)
- Version Catalog (의존성 관리)

## 🎨 디자인 테마
Coffee/Latte 색상 팔레트 기반의 차분하고 프리미엄한 디자인
