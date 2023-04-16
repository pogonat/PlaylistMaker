package com.example.playlistmaker.settings.ui

import com.example.playlistmaker.settings.domain.models.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}