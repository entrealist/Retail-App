package com.drugstore.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.core.os.LocaleListCompat
import java.util.*

fun Context.createConfigurationContext(): Context {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val locale = languageTag?.getLocaleForLanguageTags() ?: return this
        Locale.setDefault(locale)

        createConfigurationContext(locale)
    } else {
        this
    }
}

fun Context.updateConfiguration() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        val locale = languageTag?.getLocaleForLanguageTags() ?: return
        Locale.setDefault(locale)

        resources.configuration.setLocale(locale)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
    }
}

fun Context.setLanguageTag(value: String) {
    ConfigurationPrefs.getInstance(this).languageTag = value
}

private val Context.languageTag get() = ConfigurationPrefs.getInstance(this).languageTag

private fun String.getLocaleForLanguageTags() = LocaleListCompat.forLanguageTags(this).run {
    if (isEmpty) null else this[0]
}

private fun Context.createConfigurationContext(locale: Locale) = Configuration(resources.configuration).run {
    setLocale(locale)
    createConfigurationContext(this)
}

@SuppressLint("ApplySharedPref")
private class ConfigurationPrefs(private val prefs: SharedPreferences) {

    var languageTag
        get() = prefs.getString(PREF_LOCALE_ID, null)
        set(value) {
            prefs.edit().putString(PREF_LOCALE_ID, value).commit()
        }

    companion object {
        private const val PREFS_NAME = "configuration"
        private const val PREF_LOCALE_ID = "locale_id"
        @Volatile
        private var INSTANCE: ConfigurationPrefs? = null

        fun getInstance(context: Context): ConfigurationPrefs = INSTANCE ?: synchronized(this) {
            INSTANCE ?: create(context).also { INSTANCE = it }
        }

        private fun create(context: Context) = ConfigurationPrefs(
            context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        )
    }
}