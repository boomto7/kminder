# Minder 감정 색상 정의 (총 48개)

Minder 앱에서는 기본 8가지 감정 색상을 기반으로, 세분화된 감정(강도 조절)과 복합 감정(색상 혼합)의 색상을 동적으로 계산하여 사용합니다.

## 1. 기본 감정 및 세부 감정 색상 (24개)
기본 감정 색상(Lv 2)을 기준으로 **약함(Lv 1)**은 투명도를 주어 연하게, **강함(Lv 3)**은 명도를 낮춰 진하게 표현합니다.

| 계열 | 약함 (Lv 1, Weak) <br> *(White 40% + Color 60%)* | 중간 (Lv 2, Base) | 강함 (Lv 3, Strong) <br> *(Brightness 85%)* |
|---|---|---|---|
| **기쁨 (JOY)** | #FFF9C4 (평온) | #FFF59D (기쁨) | #D8D085 (황홀) |
| **신뢰 (TRUST)** | #DCEDC9 (용인) | #C5E1A5 (신뢰) | #A7BF8C (감탄) |
| **두려움 (FEAR)** | #C9E6CA (우려) | #A5D6A7 (두려움) | #8CB68E (공포) |
| **놀람 (SURPRISE)** | #B2EBF2 (산만) | #80DEEA (놀람) | #6DBDC7 (경악) |
| **슬픔 (SADNESS)** | #BCDFFB (수심) | #90CAF9 (슬픔) | #7AABD4 (비탄) |
| **혐오 (DISGUST)** | #E1BEE7 (지루함) | #CE93D8 (혐오) | #AF7DB7 (증오) |
| **분노 (ANGER)** | #F5C2C2 (짜증) | #EF9A9A (분노) | #CB8383 (격노) |
| **기대 (ANTICIPATION)** | #FFE0B2 (관심) | #FFCC80 (기대) | #D9AD6D (경계) |

---

## 2. 복합 감정 색상 (24개)
두 가지 기본 감정의 색상을 50:50으로 혼합(Blend)하여 생성된 색상입니다.

### 1차 이중감정 (Primary Dyads)
| 감정 | 구성 | 혼합 결과 (Approx. Hex) |
|---|---|---|
| **사랑 (Love)** | 기쁨(#FFF59D) + 신뢰(#C5E1A5) | #E2EBA1 |
| **굴복 (Submission)** | 신뢰(#C5E1A5) + 두려움(#A5D6A7) | #B5DBC6 |
| **경외 (Awe)** | 두려움(#A5D6A7) + 놀람(#80DEEA) | #92DAC8 |
| **못마땅함 (Disapproval)** | 놀람(#80DEEA) + 슬픔(#90CAF9) | #88D4C1 |
| **후회 (Remorse)** | 슬픔(#90CAF9) + 혐오(#CE93D8) | #AFAEE8 |
| **경멸 (Contempt)** | 혐오(#CE93D8) + 분노(#EF9A9A) | #DF96B9 |
| **공격성 (Aggressiveness)** | 분노(#EF9A9A) + 기대(#FFCC80) | #F7B38D |
| **낙관 (Optimism)** | 기대(#FFCC80) + 기쁨(#FFF59D) | #FFE08E |

### 2차 이중감정 (Secondary Dyads)
| 감정 | 구성 | 혼합 결과 (Approx. Hex) |
|---|---|---|
| **죄책감 (Guilt)** | 기쁨(#FFF59D) + 두려움(#A5D6A7) | #D2E6A2 |
| **호기심 (Curiosity)** | 신뢰(#C5E1A5) + 놀람(#80DEEA) | #A2DFC7 |
| **절망 (Despair)** | 두려움(#A5D6A7) + 슬픔(#90CAF9) | #9BD0D0 |
| **불신 (Unbelief)** | 놀람(#80DEEA) + 혐오(#CE93D8) | #A7B8E1 |
| **부러움 (Envy)** | 슬픔(#90CAF9) + 분노(#EF9A9A) | #BFB2C9 |
| **냉소 (Cynicism)** | 혐오(#CE93D8) + 기대(#FFCC80) | #E7AFAC |
| **자부심 (Pride)** | 분노(#EF9A9A) + 기쁨(#FFF59D) | #F7C89B |
| **숙명 (Fatalism)** | 기대(#FFCC80) + 신뢰(#C5E1A5) | #E2D692 |

### 3차 이중감정 (Tertiary Dyads)
| 감정 | 구성 | 혼합 결과 (Approx. Hex) |
|---|---|---|
| **환희 (Delight)** | 기쁨(#FFF59D) + 놀람(#80DEEA) | #BFEAC3 |
| **감상 (Sentimentality)** | 신뢰(#C5E1A5) + 슬픔(#90CAF9) | #ABD5CF |
| **수치심 (Shame)** | 두려움(#A5D6A7) + 혐오(#CE93D8) | #B9B5BF |
| **격분 (Outrage)** | 놀람(#80DEEA) + 분노(#EF9A9A) | #B7BC C2 (Grayish due to complement) |
| **비관 (Pessimism)** | 슬픔(#90CAF9) + 기대(#FFCC80) | #C7CBBC |
| **병적 집착 (Morbidness)** | 혐오(#CE93D8) + 기쁨(#FFF59D) | #E6C4BA |
| **지배 (Dominance)** | 분노(#EF9A9A) + 신뢰(#C5E1A5) | #DABEA7 |
| **불안 (Anxiety)** | 기대(#FFCC80) + 두려움(#A5D6A7) | #D2D193 |

### 반대 감정의 결합 (Opposite Emotions / Conflict)
| 감정 | 구성 | 혼합 결과 (Approx. Hex) |
|---|---|---|
| **씁쓸함 (Bittersweetness)** | 기쁨(#FFF59D) + 슬픔(#90CAF9) | #C7DFCB |
| **애증 (Ambivalence)** | 신뢰(#C5E1A5) + 혐오(#CE93D8) | #C9BABE |
| **얼어붙음 (Frozenness)** | 두려움(#A5D6A7) + 분노(#EF9A9A) | #CAB8A0 |
| **혼란스러움 (Confusion)** | 놀람(#80DEEA) + 기대(#FFCC80) | #BFD5B5 |

> **참고**: 위 색상 코드는 앱 내 로직(`blendColors`, `adjustBrightness`)을 기반으로 계산된 근사치이며, 실제 렌더링 시 투명도(Alpha) 등의 영향으로 다르게 보일 수 있습니다.
