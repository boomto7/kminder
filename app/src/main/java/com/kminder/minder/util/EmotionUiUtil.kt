package com.kminder.minder.util

import com.kminder.domain.model.ComplexEmotionType
import com.kminder.domain.model.EmotionResult
import com.kminder.domain.provider.EmotionStringProvider

object EmotionUiUtil {
    fun getLabel(result: EmotionResult, stringProvider: EmotionStringProvider): String {
        return result.complexEmotionType?.let { stringProvider.getComplexEmotionTitle(it) }?.takeIf { it.isNotEmpty() }
            ?: run {
                val primaryName = stringProvider.getEmotionName(result.primaryEmotion)
                val secondaryName = result.secondaryEmotion?.let { stringProvider.getEmotionName(it) }

                when (result.category) {
                    ComplexEmotionType.Category.SINGLE_EMOTION -> primaryName
                    ComplexEmotionType.Category.OPPOSITE -> 
                        if (secondaryName != null) stringProvider.getConflictLabel(primaryName, secondaryName) else primaryName
                    ComplexEmotionType.Category.PRIMARY_DYAD -> 
                        if (secondaryName != null) "$primaryName + $secondaryName" else primaryName
                    ComplexEmotionType.Category.SECONDARY_DYAD -> stringProvider.getComplexEmotionDefaultLabel()
                    ComplexEmotionType.Category.TERTIARY_DYAD -> stringProvider.getComplicatedEmotionDefaultLabel()
                    else -> primaryName
                }
            }
    }

    fun getDescription(result: EmotionResult, stringProvider: EmotionStringProvider): String {
        val label = getLabel(result, stringProvider)
        return result.complexEmotionType?.let { stringProvider.getComplexEmotionDescription(it) }?.takeIf { it.isNotEmpty() }
            ?: run {
                when (result.category) {
                    ComplexEmotionType.Category.SINGLE_EMOTION -> stringProvider.getSingleEmotionDescription(label)
                    ComplexEmotionType.Category.OPPOSITE -> stringProvider.getConflictDescription()
                    ComplexEmotionType.Category.PRIMARY_DYAD -> stringProvider.getHarmonyDescription()
                    ComplexEmotionType.Category.SECONDARY_DYAD -> stringProvider.getComplexEmotionDefaultDescription()
                    ComplexEmotionType.Category.TERTIARY_DYAD -> stringProvider.getComplicatedEmotionDefaultDescription()
                    else -> stringProvider.getSingleEmotionDescription(label)
                }
            }
    }

    fun getAdvice(result: EmotionResult, stringProvider: EmotionStringProvider): String {
        return result.complexEmotionType?.let { stringProvider.getComplexEmotionAdvice(it) }?.takeIf { it.isNotEmpty() }
            ?: stringProvider.getAdvice(result.primaryEmotion)
    }

    fun getEmotionImageResId(context: android.content.Context, result: EmotionResult): Int? {
        val emotionName = result.complexEmotionType?.name?.lowercase() ?: result.primaryEmotion.name.lowercase()
        val key = "img_emotion_$emotionName"
        val resId = context.resources.getIdentifier(key, "drawable", context.packageName)
        return if (resId != 0) resId else null
    }
}
