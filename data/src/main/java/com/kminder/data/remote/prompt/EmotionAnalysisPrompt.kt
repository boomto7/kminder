package com.kminder.data.remote.prompt

/**
 * Gemini API 프롬프트 템플릿
 */
object EmotionAnalysisPrompt {
    

    /**
     * 한국어 텍스트 감정 분석 프롬프트
     */
    const val KOREAN_PROMPT = """
당신은 로버트 플루치크(Robert Plutchik)의 감정 모델에 기반하여 **한국어 텍스트**의 감정 상태를 정량적으로 분석하는 전문가입니다. 텍스트에 나타난 한국어 표현, 어조, 문맥 등을 최대한 고려하여 분석하세요.

### 분석 지침:
1. **분석 대상 감정:** Plutchik의 8가지 원형 감정('anger', 'anticipation', 'joy', 'trust', 'fear', 'sadness', 'disgust', 'surprise')만을 분석합니다.
2. **강도 점수:** 각 감정에 대해 0.0부터 1.0 사이의 소수점 값으로 강도를 부여하세요.
3. **핵심 키워드 분석:** 감정의 원인이 되는 대상을 **'수식어+명사'** 형태의 구체적인 구절로 3~5개 추출하세요. 각 키워드에 대해 연관된 감정(8가지 중 하나)과 그 영향력 점수(0.0~1.0)를 함께 명시하세요.
4. **출력 형식:** 반드시 다음 JSON 스키마를 준수합니다.

### 출력 형식 (JSON Schema):
```json
{
    "analysis": {
        "anger": [0.0 to 1.0],
        "anticipation": [0.0 to 1.0],
        "joy": [0.0 to 1.0],
        "trust": [0.0 to 1.0],
        "fear": [0.0 to 1.0],
        "sadness": [0.0 to 1.0],
        "disgust": [0.0 to 1.0],
        "surprise": [0.0 to 1.0],
        "keywords": [
            {
                "word": "구체적인 키워드 (예: 망친 시험)",
                "emotion": "SADNESS",
                "score": 0.9
            },
            ...
        ]
    }
}
```

### 분석할 텍스트:
"""
    
    /**
     * 영어 텍스트 감정 분석 프롬프트
     */
    const val ENGLISH_PROMPT = """
You are an expert in quantitatively analyzing the emotional state of **English text** based on Robert Plutchik's emotion model. Please analyze the text considering English expressions, tone, and context.

### Analysis Guidelines:
1. **Target Emotions:** Analyze only Plutchik's 8 primary emotions ('anger', 'anticipation', 'joy', 'trust', 'fear', 'sadness', 'disgust', 'surprise').
2. **Intensity Score:** Assign an intensity value between 0.0 and 1.0 for each emotion.
3. **Keyword Analysis:** Extract 3-5 specific phrases (modifier + noun) that represent the cause of emotions. For each keyword, specify the associated emotion (one of the 8 types) and its impact score (0.0 to 1.0).
4. **Output Format:** Strictly follow the JSON schema below.

### Output Format (JSON Schema):
```json
{
    "analysis": {
        "anger": [0.0 to 1.0],
        "anticipation": [0.0 to 1.0],
        "joy": [0.0 to 1.0],
        "trust": [0.0 to 1.0],
        "fear": [0.0 to 1.0],
        "sadness": [0.0 to 1.0],
        "disgust": [0.0 to 1.0],
        "surprise": [0.0 to 1.0],
        "keywords": [
            {
                "word": "specific phrase (e.g. ruined exam)",
                "emotion": "SADNESS",
                "score": 0.9
            },
            ...
        ]
    }
}
```

### Text to Analyze:
"""
    
    /**
     * 언어에 맞는 프롬프트를 반환합니다.
     */
    fun getPrompt(language: String, text: String): String {
        val basePrompt = when (language.lowercase()) {
            "ko", "korean" -> KOREAN_PROMPT
            "en", "english" -> ENGLISH_PROMPT
            else -> KOREAN_PROMPT
        }
        return "$basePrompt\n\n$text"
    }
}
