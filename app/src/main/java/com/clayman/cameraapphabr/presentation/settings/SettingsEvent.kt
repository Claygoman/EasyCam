package com.clayman.cameraapphabr.presentation.settings

import com.clayman.cameraapphabr.domain.model.ThemeMode

sealed class SettingsEvent {

    data class SelectTheme(val themeMode: ThemeMode): SettingsEvent()
}