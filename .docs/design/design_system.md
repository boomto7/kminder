# Minder Design System (Retro / Soft Neo-Brutalism)

분석된 레퍼런스(`design_temp1.jpg`)를 기반으로 한 디자인 시스템 정의입니다.
기존의 파스텔톤 감성 컬러(@[.docs/data/emotion_colors.md])와 결합하여, 부드러우면서도 명확한 시각적 스타일을 구축합니다.

## 1. Design Concept
- **Keywords**: Retro, Playful, Soft Neo-Brutalism, Pop
- **Core Philosophy**: "만화 같은 부드러움과 단단함의 조화"
  - 파스텔톤의 편안한 색감
  - 선명한 외곽선으로 주는 명확한 구분감 (Sticker-like)
  - 부드러운 라운딩으로 친근함 강조

## 2. Component Style Guide

### A. Borders (외곽선)
모든 컴포넌트(카드, 버튼, 입력창 등)는 선명한 외곽선을 가집니다.
- **Stroke Width**: `2.dp` (기본), 강조 시 `3.dp`
- **Stroke Color**: `Color.Black` (또는 `Color(0xFF1C1410)` - 아주 짙은 갈색/검정)
- **Style**: Solid Line (실선)

### B. Corner Radius (라운드)
날카로운 모서리를 배제하고 둥근 형태를 유지합니다.
- **Large Container (Screen, Modal)**: `32.dp`
- **Card / Content Box**: `20.dp` ~ `24.dp`
- **Button / Chip**: `CircleShape` (완전한 타원형) 또는 `16.dp`

### C. Shadows (그림자)
일반적인 Blur 그림자 대신, 외곽선과 동일한 색상의 **Hard Shadow**를 사용하여 입체감을 줍니다.
- **Type**: Solid Drop Shadow (No Blur)
- **Offset**: `x = 4.dp`, `y = 4.dp` (카드 크기에 따라 조절)
- **Color**: `Color.Black` (투명도 없음)
- **Effect**: 컴포넌트가 화면에서 붕 떠있는 듯한 3D Pop 효과

### D. Typography
- **Headings**: **Outfit** (ExtraBold / Bold) - 영문/숫자 강조용
- **Body**: **Noto Sans KR** (Medium / Regular) - 한글 본문용
- **Contrast**: 텍스트 색상은 배경색 대비 명확한 `TextPrimary` 사용

## 3. UI Element Examples

### Cards (Emotion Bubble, Info Card)
- **Background**: 파스텔톤 (e.g., `LatteCream`, `EmotionJoy`)
- **Border**: 2dp Black Border
- **Shadow**: 4dp Offset Hard Shadow
- **Shape**: RoundedCorner(24.dp)

### Buttons
- **Primary**:
  - Background: `CoffeeBrown` or `MinderBlue`
  - Text: `White`
  - Border: 2dp Black
  - Shadow: 2dp Offset Hard Shadow
- **Secondary / Chip**:
  - Background: `White` or Transparent
  - Border: 1dp ~ 2dp Black
  - Text: `Black`

---
*이 문서는 디자인 구현 시 지속적으로 참고하며 업데이트됩니다.*
