package com.kminder.minder.util

import android.content.Context
import com.kminder.domain.model.ComplexEmotionType
import com.kminder.minder.R

object EmotionImageUtil {

    /**
     * ComplexEmotionType에 해당하는 이미지 리소스 ID를 반환합니다.
     * 파일명 규칙: img_emotion_{name_lowercase}.png
     * 예: LOVE -> img_emotion_love
     */
    fun getEmotionImageRecourceId(context: Context, type: ComplexEmotionType?): Int {
        if (type == null) return 0 // Default or empty image

        val resourceName = "img_emotion_${type.name.lowercase()}"
        val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        
        return if (resId != 0) resId else R.drawable.ic_launcher_foreground // Fallback
    }

    /**
     * Preview 등 Context가 없는 환경에서 사용하기 위한 직접 매핑 (필요 시 사용)
     * 현재는 Context 방식이 주력이므로 사용 안 함.
     */
}
