package com.clayman.cameraapphabr.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.clayman.cameraapphabr.data.SettingsRepositoryImpl
import com.clayman.cameraapphabr.databinding.SettingsBinding
import com.clayman.cameraapphabr.domain.model.ThemeMode
import com.clayman.cameraapphabr.presentation.home.MainActivity

    class SettingsActivity : AppCompatActivity() {

        private lateinit var binding: SettingsBinding
        private lateinit var viewModel: SettingsViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = SettingsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val sharedPreferences = getSharedPreferences(SETTINGS_SP_NAME, Context.MODE_PRIVATE)
            val repository = SettingsRepositoryImpl(sharedPreferences)
            viewModel = SettingsViewModel(repository)

            binding.btDone.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            binding.btSystemTheme.setOnClickListener {
                viewModel.onEvent(SettingsEvent.SelectTheme(ThemeMode.SYSTEM))
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            binding.btDarkTheme.setOnClickListener {
                viewModel.onEvent(SettingsEvent.SelectTheme(ThemeMode.DARK))
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            binding.btLightTheme.setOnClickListener {
                viewModel.onEvent(SettingsEvent.SelectTheme(ThemeMode.LIGHT))
                AppCompatDelegate
                    .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        companion object {

            private const val SETTINGS_SP_NAME = "user_settings"
        }
    }