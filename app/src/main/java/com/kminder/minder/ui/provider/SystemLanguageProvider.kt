package com.kminder.minder.ui.provider

import com.kminder.domain.provider.LanguageProvider
import java.util.Locale
import javax.inject.Inject

class SystemLanguageProvider @Inject constructor() : LanguageProvider {
    override fun getLanguageCode(): String {
        val locale = Locale.getDefault()
        return if (locale.language == "ko") "ko" else "en"
    }
}
