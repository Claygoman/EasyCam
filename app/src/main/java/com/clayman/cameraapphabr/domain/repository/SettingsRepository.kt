package com.clayman.cameraapphabr.domain.repository

import com.clayman.cameraapphabr.domain.model.ThemeMode

interface SettingsRepository {

    fun saveThemeMode(themeMode: ThemeMode)

    fun loadThemeMode(): ThemeMode
}