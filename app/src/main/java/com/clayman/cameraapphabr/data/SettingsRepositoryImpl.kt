package com.clayman.cameraapphabr.data

import android.content.SharedPreferences
import com.clayman.cameraapphabr.domain.model.ThemeMode
import com.clayman.cameraapphabr.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override fun saveThemeMode(themeMode: ThemeMode) {
        sharedPreferences.edit().apply {
            putString(THEME_MODE_SETTINGS_KEY, themeMode.name)
            apply()
        }
    }

    override fun loadThemeMode(): ThemeMode {
        val defaultValue = ThemeMode.SYSTEM
        val result = sharedPreferences.getString(THEME_MODE_SETTINGS_KEY, defaultValue.name)

        return when (result) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            "SYSTEM" -> ThemeMode.SYSTEM
            else -> ThemeMode.SYSTEM
        }
    }

    companion object {

        private const val THEME_MODE_SETTINGS_KEY = "theme_mode"
    }
}