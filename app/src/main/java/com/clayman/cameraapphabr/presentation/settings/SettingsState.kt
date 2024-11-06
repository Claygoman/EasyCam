package com.clayman.cameraapphabr.presentation.settings

import com.clayman.cameraapphabr.domain.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
