package com.clayman.cameraapphabr.presentation.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clayman.cameraapphabr.domain.model.ThemeMode
import com.clayman.cameraapphabr.domain.repository.SettingsRepository

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _themeMode = MutableLiveData(ThemeMode.SYSTEM)
    val themeMode: LiveData<ThemeMode> = _themeMode

    init {
        val savedThemeMode = settingsRepository.loadThemeMode()

//        _themeMode.postValue(savedThemeMode)
    }

    fun onEvent(event: SettingsEvent) {
        when(event) {
            is SettingsEvent.SelectTheme -> {
                settingsRepository.saveThemeMode(event.themeMode)
                _themeMode.postValue(event.themeMode)
            }
        }
    }
}