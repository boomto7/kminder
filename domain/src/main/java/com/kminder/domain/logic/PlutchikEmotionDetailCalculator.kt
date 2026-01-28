package com.kminder.domain.logic

import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionAnalysis
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.model.EmotionType
import kotlin.math.sqrt

import javax.inject.Inject

class PlutchikEmotionDetailCalculator @Inject constructor() {

    sealed class EmotionCondition {
        abstract fun isSatisfied(data: EmotionAnalysis): Boolean
        abstract fun toKey(): String

        data class Single(
            val type: EmotionType,   // String 대신 Enum 사용
            val threshold: Float,
            val isHigher: Boolean
        ) : EmotionCondition() {
            override fun isSatisfied(data: EmotionAnalysis): Boolean {
                // EmotionAnalysis의 메서드를 직접 호출
                val value = data.getEmotionIntensity(type)
                return if (isHigher) value >= threshold else value <= threshold
            }

            override fun toKey(): String =
                "${if (isHigher) "HIGH" else "LOW"}_${type.name}"
        }

        data class OrGroup(
            val conditions: List<EmotionCondition>
        ) : EmotionCondition() {
            override fun isSatisfied(data: EmotionAnalysis): Boolean =
                conditions.any { it.isSatisfied(data) }

            override fun toKey(): String =
                "OR(${conditions.joinToString("_") { it.toKey() }})"
        }
    }

    data class LabelModifier(val conditions: List<EmotionCondition>, val targetLabel: String) {
        fun buildLogicKey(): String = conditions.joinToString("_") { it.toKey() }
    }

    // 수정 규칙 정의 (예: HOPE 성향일 때 특정 조건이 맞으면 FATALISM으로 확정)
    data class EmotionNuanceRule(
        val baseType: ComplexEmotionType,      // 기준이 되는 감정 (예: HOPE)
        val conditions: List<EmotionCondition>, // 판별 조건 (AND/OR)
        val targetType: ComplexEmotionType      // 최종 변환될 감정 (예: FATALISM)
    )

    /**
     * 감정의 기본 조합(baseType)이 특정 조건(conditions)을 만족할 때
     * 최종적인 세부 감정(targetType)으로 변환되는 규칙 리스트 (26개 전체)
     */
    val nuanceRules = listOf(
        // --- 1차 이중감정 변종 ---
        EmotionNuanceRule(
            ComplexEmotionType.LOVE,
            listOf(EmotionCondition.Single(EmotionType.FEAR, 0.5f, true)),
            ComplexEmotionType.POSSESSIVENESS
        ),
        EmotionNuanceRule(
            ComplexEmotionType.SUBMISSION,
            listOf(EmotionCondition.Single(EmotionType.DISGUST, 0.4f, true)),
            ComplexEmotionType.SERVILITY
        ),
        EmotionNuanceRule(
            ComplexEmotionType.AWE,
            listOf(EmotionCondition.Single(EmotionType.ANGER, 0.4f, true)),
            ComplexEmotionType.ALARM
        ),
        EmotionNuanceRule(
            ComplexEmotionType.DISAPPROVAL,
            listOf(EmotionCondition.Single(EmotionType.FEAR, 0.6f, true)),
            ComplexEmotionType.SHOCK
        ),
        EmotionNuanceRule(
            ComplexEmotionType.REMORSE,
            listOf(EmotionCondition.Single(EmotionType.ANGER, 0.5f, true)),
            ComplexEmotionType.SELF_LOATHING
        ),
        EmotionNuanceRule(
            ComplexEmotionType.CONTEMPT,
            listOf(
                EmotionCondition.OrGroup(
                    listOf(
                        EmotionCondition.Single(
                            EmotionType.SADNESS,
                            0.5f,
                            true
                        ), EmotionCondition.Single(EmotionType.FEAR, 0.5f, true)
                    )
                )
            ),
            ComplexEmotionType.ENVY_CONTEMPT
        ),
        EmotionNuanceRule(
            ComplexEmotionType.AGGRESSIVENESS,
            listOf(EmotionCondition.Single(EmotionType.SADNESS, 0.6f, true)),
            ComplexEmotionType.VENGEANCE
        ),
        EmotionNuanceRule(
            ComplexEmotionType.OPTIMISM,
            listOf(
                EmotionCondition.Single(EmotionType.TRUST, 0.8f, true),
                EmotionCondition.Single(EmotionType.FEAR, 0.2f, false)
            ),
            ComplexEmotionType.NAIVETY
        ),

        // --- 2차 이중감정 변종 ---
        EmotionNuanceRule(
            ComplexEmotionType.GUILT,
            listOf(EmotionCondition.Single(EmotionType.DISGUST, 0.5f, true)),
            ComplexEmotionType.SELF_DENIAL
        ),
        EmotionNuanceRule(
            ComplexEmotionType.CURIOSITY,
            listOf(EmotionCondition.Single(EmotionType.DISGUST, 0.4f, true)),
            ComplexEmotionType.AMBIVALENCE_CURIOSITY
        ),
        EmotionNuanceRule(
            ComplexEmotionType.DESPAIR,
            listOf(EmotionCondition.Single(EmotionType.ANTICIPATION, 0.3f, false)),
            ComplexEmotionType.HELPLESSNESS
        ),
        EmotionNuanceRule(
            ComplexEmotionType.UNBELIEF,
            listOf(EmotionCondition.Single(EmotionType.ANGER, 0.5f, true)),
            ComplexEmotionType.ABHORRENCE
        ),
        EmotionNuanceRule(
            ComplexEmotionType.ENVY,
            listOf(EmotionCondition.Single(EmotionType.DISGUST, 0.6f, true)),
            ComplexEmotionType.RESENTMENT
        ),
        EmotionNuanceRule(
            ComplexEmotionType.CYNICISM,
            listOf(EmotionCondition.Single(EmotionType.SADNESS, 0.6f, true)),
            ComplexEmotionType.DISTRUST
        ),
        EmotionNuanceRule(
            ComplexEmotionType.PRIDE,
            listOf(EmotionCondition.Single(EmotionType.TRUST, 0.3f, false)),
            ComplexEmotionType.HUBRIS
        ),
        EmotionNuanceRule(
            ComplexEmotionType.HOPE,
            listOf(
                EmotionCondition.Single(EmotionType.JOY, 0.35f, false),
                EmotionCondition.OrGroup(
                    listOf(
                        EmotionCondition.Single(
                            EmotionType.SADNESS,
                            0.4f,
                            true
                        ), EmotionCondition.Single(EmotionType.FEAR, 0.4f, true)
                    )
                )
            ),
            ComplexEmotionType.FATALISM
        ),

        // --- 3차 이중감정 변종 ---
        EmotionNuanceRule(
            ComplexEmotionType.DELIGHT,
            listOf(EmotionCondition.Single(EmotionType.FEAR, 0.5f, true)),
            ComplexEmotionType.CONFUSED_JOY
        ),
        EmotionNuanceRule(
            ComplexEmotionType.SENTIMENTALITY,
            listOf(EmotionCondition.Single(EmotionType.DISGUST, 0.4f, true)),
            ComplexEmotionType.SELF_PITY
        ),
        EmotionNuanceRule(
            ComplexEmotionType.SHAME,
            listOf(EmotionCondition.Single(EmotionType.ANGER, 0.5f, true)),
            ComplexEmotionType.HUMILIATION
        ),
        EmotionNuanceRule(
            ComplexEmotionType.OUTRAGE,
            listOf(EmotionCondition.Single(EmotionType.ANTICIPATION, 0.3f, false)),
            ComplexEmotionType.DISORIENTATION
        ),
        EmotionNuanceRule(
            ComplexEmotionType.PESSIMISM,
            listOf(EmotionCondition.Single(EmotionType.TRUST, 0.5f, true)),
            ComplexEmotionType.RESIGNATION
        ),
        EmotionNuanceRule(
            ComplexEmotionType.MORBIDNESS,
            listOf(EmotionCondition.Single(EmotionType.ANGER, 0.4f, true)),
            ComplexEmotionType.DERISION
        ),
        EmotionNuanceRule(
            ComplexEmotionType.DOMINANCE,
            listOf(EmotionCondition.Single(EmotionType.DISGUST, 0.5f, true)),
            ComplexEmotionType.TYRANNY
        ),
        EmotionNuanceRule(
            ComplexEmotionType.ANXIETY,
            listOf(EmotionCondition.Single(EmotionType.SADNESS, 0.5f, true)),
            ComplexEmotionType.DREAD
        ),

        // --- 반대 축 변종 ---
        EmotionNuanceRule(
            ComplexEmotionType.BITTERSWEETNESS,
            listOf(EmotionCondition.Single(EmotionType.ANTICIPATION, 0.3f, false)),
            ComplexEmotionType.NOSTALGIA
        ),
        EmotionNuanceRule(
            ComplexEmotionType.AMBIVALENCE,
            listOf(EmotionCondition.Single(EmotionType.FEAR, 0.5f, true)),
            ComplexEmotionType.MISTRUST
        )
    )

    private fun comb(a: Float, b: Float): Float = sqrt((a * b).toDouble()).toFloat()

//    enum class SingleEmotionLevel(val label: String) {
//        // Joy
//        SERENITY("평온"), JOY("기쁨"), ECSTASY("황홀경"),
//
//        // Trust
//        ACCEPTANCE("수용"), TRUST("신뢰"), ADMIRATION("찬양"),
//
//        // Fear
//        APPREHENSION("우려"), FEAR("두려움"), TERROR("공포"),
//
//        // Surprise
//        DISTRACTION("다소 놀람"), SURPRISE("놀람"), AMAZEMENT("경악"),
//
//        // Sadness
//        PENSIVENESS("우울"), SADNESS("슬픔"), GRIEF("비탄"),
//
//        // Disgust
//        BOREDOM("지루함"), DISGUST("혐오"), LOATHING("증오"),
//
//        // Anger
//        ANNOYANCE("짜증"), ANGER("분노"), RAGE("격분"),
//
//        // Anticipation
//        INTEREST("관심"), ANTICIPATION("기대"), VIGILANCE("경계"),
//
//        // Default
//        UNCLASSIFIED("미분류")
//    }

//    enum class Dyad(val key: String, val primaryLabel: String, val secondaryLabel: String? = null) {
//        // 1차 혼합 (Primary Dyads)
//        LOVE_OBSESSION("사랑/집착", "사랑(Love)", "집착(Possessiveness)"),
//        SUBMISSION_SERVILITY("굴복/비굴", "굴복(Submission)", "비굴함(Servility)"),
//        AWE_ALARM("경외/공황", "경외(Awe)", "공황(Alarm)"),
//        DISAPPROVAL_SHOCK("불신/충격", "불신(Disapproval)", "충격(Shock)"),
//        REMORSE_SELF_LOATHING("후회/비하", "후회(Remorse)", "자기비하(Self-Loathing)"),
//        CONTEMPT_ENVY("경멸/질투", "경멸(Contempt)", "질투(Envy)"),
//        AGGRESSIVENESS_VENGEANCE("공격/복수", "공격성(Aggressiveness)", "복수심(Vengeance)"),
//        OPTIMISM_NAIVETY("낙관/순진", "낙관(Optimism)", "순진함(Naivety)"),
//
//        // 2차 혼합 (Secondary Dyads)
//        GUILT_DENIAL("죄책/부정", "죄책감(Guilt)", "자기부정(Self-Denial)"),
//        CURIOSITY_SUSPICION("호기심/의구", "호기심(Curiosity)", "의구심(Ambivalence)"),
//        DESPAIR_HELPLESSNESS("절망/무기력", "절망(Despair)", "무기력(Helplessness)"),
//        UNLUCK_SHOCK("불운/경악", "불운(Unbelief)", "혐오적 경악"),
//        RESENTMENT_ENVY("울분/선망", "선망(Envy)", "울분(Resentment)"),
//        CYNICISM_PESSIMISM("냉소/비관", "냉소(Cynicism)", "비관(Pessimism)"),
//        PRIDE_HUBRIS("자부/오만", "자부심(Pride)", "오만(Hubris)"),
//        HOPE_FATALISM("희망/운명", "희망(Hope)", "운명론(Fatalism)"),
//
//        // 3차 혼합 (Tertiary Dyads)
//        ECSTASY_CONFUSION("황홀/혼란", "황홀(Delight)", "혼란스러운 기쁨"),
//        SENTIMENTALITY_PITY("감상/연민", "감상적(Sentimentality)", "자기연민(Self-Pity)"),
//        SHAME_HUMILIATION("수치/굴욕", "수치심(Shame)", "굴욕(Humiliation)"),
//        RESENTMENT_EMBARRASSMENT("분개/당혹", "분개(Outrage)", "당혹(Disorientation)"),
//        PESSIMISM_RESIGNATION("비관/체념", "비관(Pessimism)", "운명적 체념(Resignation)"),
//        FILTH_DERISION("불결/조소", "불결(Morbidness)", "냉소적 조소(Derision)"),
//        DOMINANCE_TYRANNY("지배/강압", "지배(Dominance)", "강압(Tyranny)"),
//        ANXIETY_DREAD("불안/공포탄", "불안(Anxiety)", "공포탄(Dread)"),
//
//        // 반대 축 충돌 (Opposites)
//        BITTERSWEET_NOSTALGIA("비장미/향수", "비장미(Bittersweet)", "우울한 향수"),
//        AMBIVALENCE_DISTRUST("양가성/불신", "양가성(Ambivalence)", "불신(Mistrust)"),
//        FROZEN("얼어붙음", "얼어붙음"),
//        CHAOS("혼돈", "혼돈")
//    }

//    enum class Dyad(val key: String, val primaryLabel: String, val secondaryLabel: String? = null) {
//        // 1차 혼합 (Primary Dyads: 인접 감정)
//        LOVE_OBSESSION("사랑/집착", "사랑(Love)", "집착(Possessiveness)"),
//        SUBMISSION_SERVILITY("굴복/비굴", "굴복(Submission)", "비굴함(Servility)"),
//        AWE_ALARM("경외/공황", "경외(Awe)", "공황(Alarm)"),
//        DISAPPROVAL_SHOCK("불신/충격", "불신(Disapproval)", "충격(Shock)"),
//        REMORSE_SELF_LOATHING("후회/비하", "후회(Remorse)", "자기비하(Self-Loathing)"),
//        CONTEMPT_ENVY("경멸/질투", "경멸(Contempt)", "질투(Envy)"),
//        AGGRESSIVENESS_VENGEANCE("공격/복수", "공격성(Aggressiveness)", "복수심(Vengeance)"),
//        OPTIMISM_NAIVETY("낙관/순진", "낙관(Optimism)", "순진함(Naivety)"),
//
//        // 2차 혼합 (Secondary Dyads: 한 칸 건너)
//        GUILT_DENIAL("죄책/부정", "죄책감(Guilt)", "자기부정(Self-Denial)"),
//        CURIOSITY_SUSPICION("호기심/의구", "호기심(Curiosity)", "의구심(Ambivalence)"),
//        DESPAIR_HELPLESSNESS("절망/무기력", "절망(Despair)", "무기력(Helplessness)"),
//        UNLUCK_SHOCK("불운/경악", "불운(Unbelief)", "혐오적 경악(Abhorrence)"),
//        ENVY_RESENTMENT("선망/울분", "선망(Envy)", "울분(Resentment)"),
//        CYNICISM_DISTRUST("냉소/불신", "냉소(Cynicism)", "불신(Distrust)"), // 혐오 기반으로 변경
//        PRIDE_HUBRIS("자부/오만", "자부심(Pride)", "오만(Hubris)"),
//        HOPE_FATALISM("희망/운명", "희망(Hope)", "운명론(Fatalism)"),
//
//        // 3차 혼합 (Tertiary Dyads: 두 칸 건너)
//        ECSTASY_CONFUSION("황홀/혼란", "황홀(Delight)", "혼란스러운 기쁨"),
//        SENTIMENTALITY_PITY("감상/연민", "감상적(Sentimentality)", "자기연민(Self-Pity)"),
//        SHAME_HUMILIATION("수치/굴욕", "수치심(Shame)", "굴욕(Humiliation)"),
//        OUTRAGE_EMBARRASSMENT("분개/당혹", "분개(Outrage)", "당혹(Disorientation)"),
//        PESSIMISM_RESIGNATION("비관/체념", "비관(Pessimism)", "운명적 체념(Resignation)"), // 슬픔 기반으로 유지
//        FILTH_DERISION("불결/조소", "불결(Morbidness)", "냉소적 조소(Derision)"),
//        DOMINANCE_TYRANNY("지배/강압", "지배(Dominance)", "강압(Tyranny)"),
//        ANXIETY_DREAD("불안/공포탄", "불안(Anxiety)", "공포탄(Dread)"),
//
//        // 반대 축 충돌 (Opposites)
//        BITTERSWEET_NOSTALGIA("비장미/향수", "비장미(Bittersweet)", "우울한 향수(Nostalgia)"),
//        AMBIVALENCE_MISTRUST("양가성/불신", "양가성(Ambivalence)", "회의감(Mistrust)"),
//        FROZEN("얼어붙음", "얼어붙음(Frozen)"),
//        CHAOS("혼돈", "혼돈(Chaos)")
//    }

//    enum class Dyad(
//        val key: String,
//        val primaryLabel: String,
//        val secondaryLabel: String? = null,
//        // 감정 타입을 직접 참조하도록 구성 (Pair<EmotionType, EmotionType>)
//        val components: Pair<EmotionType, EmotionType>
//    ) {
//        // 1차 혼합
//        LOVE_OBSESSION("사랑/집착", "사랑(Love)", "집착(Possessiveness)", EmotionType.JOY to EmotionType.TRUST),
//        SUBMISSION_SERVILITY("굴복/비굴", "굴복(Submission)", "비굴함(Servility)", EmotionType.TRUST to EmotionType.FEAR),
//        AWE_ALARM("경외/공황", "경외(Awe)", "공황(Alarm)", EmotionType.FEAR to EmotionType.SURPRISE),
//        DISAPPROVAL_SHOCK("불신/충격", "불신(Disapproval)", "충격(Shock)", EmotionType.SURPRISE to EmotionType.SADNESS),
//        REMORSE_SELF_LOATHING("후회/비하", "후회(Remorse)", "자기비하(Self-Loathing)", EmotionType.SADNESS to EmotionType.DISGUST),
//        CONTEMPT_ENVY("경멸/질투", "경멸(Contempt)", "질투(Envy)", EmotionType.DISGUST to EmotionType.ANGER),
//        AGGRESSIVENESS_VENGEANCE("공격/복수", "공격성(Aggressiveness)", "복수심(Vengeance)", EmotionType.ANGER to EmotionType.ANTICIPATION),
//        OPTIMISM_NAIVETY("낙관/순진", "낙관(Optimism)", "순진함(Naivety)", EmotionType.ANTICIPATION to EmotionType.JOY),
//
//        // 2차 혼합
//        GUILT_DENIAL("죄책/부정", "죄책감(Guilt)", "자기부정(Self-Denial)", EmotionType.JOY to EmotionType.FEAR),
//        CURIOSITY_SUSPICION("호기심/의구", "호기심(Curiosity)", "의구심(Ambivalence)", EmotionType.TRUST to EmotionType.SURPRISE),
//        DESPAIR_HELPLESSNESS("절망/무기력", "절망(Despair)", "무기력(Helplessness)", EmotionType.FEAR to EmotionType.SADNESS),
//        UNLUCK_SHOCK("불운/경악", "불운(Unbelief)", "혐오적 경악(Abhorrence)", EmotionType.SURPRISE to EmotionType.DISGUST),
//        ENVY_RESENTMENT("선망/울분", "선망(Envy)", "울분(Resentment)", EmotionType.SADNESS to EmotionType.ANGER),
//        CYNICISM_DISTRUST("냉소/불신", "냉소(Cynicism)", "불신(Distrust)", EmotionType.DISGUST to EmotionType.ANTICIPATION),
//        PRIDE_HUBRIS("자부/오만", "자부심(Pride)", "오만(Hubris)", EmotionType.ANGER to EmotionType.JOY),
//        HOPE_FATALISM("희망/운명", "희망(Hope)", "운명론(Fatalism)", EmotionType.ANTICIPATION to EmotionType.TRUST),
//
//        // 3차 혼합
//        ECSTASY_CONFUSION("황홀/혼란", "황홀(Delight)", "혼란스러운 기쁨", EmotionType.JOY to EmotionType.SURPRISE),
//        SENTIMENTALITY_PITY("감상/연민", "감상적(Sentimentality)", "자기연민(Self-Pity)", EmotionType.TRUST to EmotionType.SADNESS),
//        SHAME_HUMILIATION("수치/굴욕", "수치심(Shame)", "굴욕(Humiliation)", EmotionType.FEAR to EmotionType.DISGUST),
//        OUTRAGE_EMBARRASSMENT("분개/당혹", "분개(Outrage)", "당혹(Disorientation)", EmotionType.SURPRISE to EmotionType.ANGER),
//        PESSIMISM_RESIGNATION("비관/체념", "비관(Pessimism)", "운명적 체념(Resignation)", EmotionType.SADNESS to EmotionType.ANTICIPATION),
//        FILTH_DERISION("불결/조소", "불결(Morbidness)", "냉소적 조소(Derision)", EmotionType.DISGUST to EmotionType.JOY),
//        DOMINANCE_TYRANNY("지배/강압", "지배(Dominance)", "강압(Tyranny)", EmotionType.ANGER to EmotionType.TRUST),
//        ANXIETY_DREAD("불안/공포탄", "불안(Anxiety)", "공포탄(Dread)", EmotionType.ANTICIPATION to EmotionType.FEAR),
//
//        // 반대 축
//        BITTERSWEET_NOSTALGIA("비장미/향수", "비장미(Bittersweet)", "우울한 향수(Nostalgia)", EmotionType.JOY to EmotionType.SADNESS),
//        AMBIVALENCE_MISTRUST("양가성/불신", "양가성(Ambivalence)", "회의감(Mistrust)", EmotionType.TRUST to EmotionType.DISGUST),
//        FROZEN("얼어붙음", "얼어붙음(Frozen)", null, EmotionType.FEAR to EmotionType.ANGER),
//        CHAOS("혼돈", "혼돈(Chaos)", null, EmotionType.SURPRISE to EmotionType.ANTICIPATION)
//    }

    fun classify(source: EmotionAnalysis): EmotionResult {
        // 1. 모든 복합 감정(Dyad/Opposite) 점수 계산
        // Category가 SINGLE_EMOTION이 아닌 것들만 대상으로 함
        val complexScores = ComplexEmotionType.entries
            .filter { it.category != ComplexEmotionType.Category.SINGLE_EMOTION }
            .distinctBy { it.composition } // 동일한 조합(예: HOPE/FATALISM)은 하나만 계산
            .associateWith { type ->
                val s1 = source.getEmotionIntensity(type.composition.first)
                val s2 = source.getEmotionIntensity(type.composition.second)
                sqrt((s1 * s2).toDouble()).toFloat()
            }

        // 2. maxByOrNull 호출 시 Entry의 타입을 활용합니다.
        // 결과값은 Map.Entry<ComplexEmotionType, Float>? 타입이 됩니다.
        val topComplexEntry = complexScores.maxByOrNull { it.value }

        // 3. 엘비스 연산자를 사용해 기본값을 설정할 때도 Pair가 아닌 Entry 구조에 맞게 처리하거나,
        // 아래와 같이 null 체크 후 값을 추출합니다.
        val finalComplexType = topComplexEntry?.key ?: ComplexEmotionType.CONFUSION
        val finalComplexScore = topComplexEntry?.value ?: 0f

        // 2. 단일 감정 우선 순위 체크 (0.15f 차이)
        val dominantType = source.getDominantEmotion()
        val dominantScore = source.getEmotionIntensity(dominantType)

        if (dominantScore > (finalComplexScore + 0.15f)) {
            val intensity = when {
                dominantScore >= 0.8f -> ComplexEmotionType.Intensity.STRONG
                dominantScore >= 0.4f -> ComplexEmotionType.Intensity.MEDIUM
                else -> ComplexEmotionType.Intensity.WEAK
            }
            val singleType = ComplexEmotionType.findSingle(dominantType, intensity)

            return EmotionResult(
                primaryEmotion = dominantType,
                secondaryEmotion = null,
                score = dominantScore,
                category = ComplexEmotionType.Category.SINGLE_EMOTION,
                complexEmotionType = singleType,
                source = source
            )
        }

        // 3. 뉘앙스(Nuance) 규칙 적용
        var finalType = finalComplexType
        val activeModifiers = mutableListOf<EmotionType>()
        var logicKey = "BASE_${finalType.name}"

        // 현재 선정된 finalType에 해당하는 규칙이 있는지 확인
        nuanceRules.filter { it.baseType.composition == finalType.composition }.forEach { rule ->
            if (rule.conditions.all { it.isSatisfied(source) }) {
                finalType = rule.targetType
                logicKey = "${rule.baseType.name}_BY_${rule.targetType.name}"

                // 근거 감정 추출 (UI 표시용)
                activeModifiers.addAll(extractEmotionTypes(rule.conditions))
            }
        }

        return EmotionResult(
            primaryEmotion = finalType.composition.first,
            secondaryEmotion = finalType.composition.second,
            score = finalComplexScore,
            category = finalType.category,
            complexEmotionType = finalType,
            source = source,
            modifierEmotions = activeModifiers.distinct(),
            logicKey = logicKey
        )


//    fun classify(s: EmotionAnalysis): String {
//        val combinations = mutableMapOf<Dyad, Float>()
//
//        // --- 1차 혼합 (Primary Dyads) ---
//        combinations[Dyad.LOVE_OBSESSION] = comb(s.joy, s.trust)
//        combinations[Dyad.SUBMISSION_SERVILITY] = comb(s.trust, s.fear)
//        combinations[Dyad.AWE_ALARM] = comb(s.fear, s.surprise)
//        combinations[Dyad.DISAPPROVAL_SHOCK] = comb(s.surprise, s.sadness)
//        combinations[Dyad.REMORSE_SELF_LOATHING] = comb(s.sadness, s.disgust)
//        combinations[Dyad.CONTEMPT_ENVY] = comb(s.disgust, s.anger)
//        combinations[Dyad.AGGRESSIVENESS_VENGEANCE] = comb(s.anger, s.anticipation)
//        combinations[Dyad.OPTIMISM_NAIVETY] = comb(s.anticipation, s.joy)
//
//        // --- 2차 혼합 (Secondary Dyads) ---
//        combinations[Dyad.GUILT_DENIAL] = comb(s.joy, s.fear)
//        combinations[Dyad.CURIOSITY_SUSPICION] = comb(s.trust, s.surprise)
//        combinations[Dyad.DESPAIR_HELPLESSNESS] = comb(s.fear, s.sadness)
//        combinations[Dyad.UNLUCK_SHOCK] = comb(s.surprise, s.disgust)
//        combinations[Dyad.RESENTMENT_ENVY] = comb(s.sadness, s.anger)
//        combinations[Dyad.CYNICISM_PESSIMISM] = comb(s.disgust, s.anticipation)
//        combinations[Dyad.PRIDE_HUBRIS] = comb(s.anger, s.joy)
//        combinations[Dyad.HOPE_FATALISM] = comb(s.anticipation, s.trust)
//
//        // --- 3차 혼합 (Tertiary Dyads) ---
//        combinations[Dyad.ECSTASY_CONFUSION] = comb(s.joy, s.surprise)
//        combinations[Dyad.SENTIMENTALITY_PITY] = comb(s.trust, s.sadness)
//        combinations[Dyad.SHAME_HUMILIATION] = comb(s.fear, s.disgust)
//        combinations[Dyad.RESENTMENT_EMBARRASSMENT] = comb(s.surprise, s.anger)
//        combinations[Dyad.PESSIMISM_RESIGNATION] = comb(s.sadness, s.anticipation)
//        combinations[Dyad.FILTH_DERISION] = comb(s.disgust, s.joy)
//        combinations[Dyad.DOMINANCE_TYRANNY] = comb(s.anger, s.trust)
//        combinations[Dyad.ANXIETY_DREAD] = comb(s.anticipation, s.fear)
//
//        // --- 반대 축 충돌 (Opposites) ---
//        combinations[Dyad.BITTERSWEET_NOSTALGIA] = comb(s.joy, s.sadness)
//        combinations[Dyad.AMBIVALENCE_DISTRUST] = comb(s.trust, s.disgust)
//        combinations[Dyad.FROZEN] = comb(s.fear, s.anger)
//        combinations[Dyad.CHAOS] = comb(s.surprise, s.anticipation)
//
//        // 최상위 조합 추출
//        val topCombEntry = combinations.maxByOrNull { it.value }
//        val topDyad = topCombEntry?.key
//        val topCombScore = topCombEntry?.value ?: 0f
//
//        // 단일 감정 최대치 확인
//        val rawScores = mapOf(
//            "joy" to s.joy, "trust" to s.trust, "fear" to s.fear, "surprise" to s.surprise,
//            "sadness" to s.sadness, "disgust" to s.disgust, "anger" to s.anger, "anticipation" to s.anticipation
//        )
//        // Fix: Use !! as map is guaranteed to be non-empty (8 entries).
//        // Prevents type inference error between Map.Entry and Pair.
//        val topSingle = rawScores.maxByOrNull { it.value }!!
//
//        // 최종 결정 로직
//        return if (topSingle.value > (topCombScore + 0.15f)) {
//            // 1. 단일 감정 강도별 판별
//            getSingleEmotionLabel(topSingle.key, topSingle.value)
//        } else if (topDyad != null) {
//            // 2. 혼합 및 부정 뉘앙스 판별
//            val sec = topDyad.secondaryLabel
//
//            // Helper to safe return secondary if condition met, else primary
//            // Capture 'topDyad' safely. Since we are inside (topDyad != null), it's non-null.
//            // Using a local variable 'dyad' ensures smart cast stability.
//            val dyad = topDyad
//            fun condition(cond: Boolean): String = if (cond && sec != null) sec else dyad.primaryLabel
//
//            when (dyad) {
//                Dyad.LOVE_OBSESSION -> condition(s.fear > 0.5f)
//                Dyad.SUBMISSION_SERVILITY -> condition(s.disgust > 0.4f)
//                Dyad.AWE_ALARM -> condition(s.anger > 0.4f)
//                Dyad.DISAPPROVAL_SHOCK -> condition(s.fear > 0.6f)
//                Dyad.REMORSE_SELF_LOATHING -> condition(s.anger > 0.5f)
//                Dyad.CONTEMPT_ENVY -> condition(s.sadness > 0.5f || s.fear > 0.5f)
//                Dyad.AGGRESSIVENESS_VENGEANCE -> condition(s.sadness > 0.6f)
//                Dyad.OPTIMISM_NAIVETY -> condition(s.trust > 0.8f && s.fear < 0.2f)
//
//                Dyad.GUILT_DENIAL -> condition(s.disgust > 0.5f)
//                Dyad.CURIOSITY_SUSPICION -> condition(s.disgust > 0.4f)
//                Dyad.DESPAIR_HELPLESSNESS -> condition(s.anticipation < 0.3f)
//                Dyad.UNLUCK_SHOCK -> condition(s.anger > 0.5f)
//                Dyad.RESENTMENT_ENVY -> condition(s.disgust > 0.6f)
//                Dyad.CYNICISM_PESSIMISM -> condition(s.sadness > 0.6f)
//                Dyad.PRIDE_HUBRIS -> condition(s.trust < 0.3f)
//                Dyad.HOPE_FATALISM -> condition(s.joy < 0.35f && (s.sadness > 0.4f || s.fear > 0.4f))
//
//                Dyad.ECSTASY_CONFUSION -> condition(s.fear > 0.5f)
//                Dyad.SENTIMENTALITY_PITY -> condition(s.disgust > 0.4f)
//                Dyad.SHAME_HUMILIATION -> condition(s.anger > 0.5f)
//                Dyad.RESENTMENT_EMBARRASSMENT -> condition(s.anticipation < 0.3f)
//                Dyad.PESSIMISM_RESIGNATION -> condition(s.trust > 0.5f)
//                Dyad.FILTH_DERISION -> condition(s.anger > 0.4f)
//                Dyad.DOMINANCE_TYRANNY -> condition(s.disgust > 0.5f)
//                Dyad.ANXIETY_DREAD -> condition(s.sadness > 0.5f)
//
//                Dyad.BITTERSWEET_NOSTALGIA -> condition(s.anticipation < 0.3f)
//                Dyad.AMBIVALENCE_DISTRUST -> condition(s.fear > 0.5f)
//                else -> dyad.primaryLabel // 기본 긍정 명칭 반환
//            }
//        } else {
//            SingleEmotionLevel.UNCLASSIFIED.label
//        }
    }

    private fun extractEmotionTypes(conditions: List<EmotionCondition>): List<EmotionType> {
        val types = mutableListOf<EmotionType>()
        conditions.forEach { condition ->
            when (condition) {
                is EmotionCondition.Single -> types.add(condition.type)
                is EmotionCondition.OrGroup -> types.addAll(extractEmotionTypes(condition.conditions))
            }
        }
        return types
    }

//    data class EmotionAnalysisReport(
//        val finalLabel: String,           // 최종 감정 이름 (예: "운명론(Fatalism)")
//        val baseDyad: Dyad,               // 선택된 핵심 조합 (예: HOPE_FATALISM)
//        val isNuanced: Boolean,           // 부정 뉘앙스 필터가 적용되었는가?
//        val logicKey: String,             // 판별에 결정적이었던 이유 (예: "LowJoy_HighSadness")
//        val intensity: Float,             // 감정의 강도
//        val dominantScores: Map<String, Float> // 판단에 쓰인 주요 점수들
//    )
//
//    private fun generateNuancedReport(dyad: Dyad, score: Float, scoreMap: Map<String, Float>): EmotionAnalysisReport {
//        var finalLabel = dyad.primaryLabel
//        var logicKey = "BASE_${dyad.name}"
//        var isNuanced = false
//
//        // 데이터 기반으로 조건 매칭 및 로직키 생성
//        modifiers[dyad]?.forEach { modifier ->
//            val isAllMatch = modifier.conditions.all { cond ->
//                val currentVal = scoreMap[cond.emotion] ?: 0f
//                if (cond.isHigher) currentVal >= cond.threshold else currentVal <= cond.threshold
//            }
//
//            if (isAllMatch) {
//                finalLabel = modifier.targetLabel
//                logicKey = "${dyad.name}_BY_${modifier.buildLogicKey()}" // 자동 생성된 키
//                isNuanced = true
//            }
//        }
//
//        return EmotionAnalysisReport(finalLabel, "DYAD", dyad.name, logicKey, isNuanced, score)
//    }
}