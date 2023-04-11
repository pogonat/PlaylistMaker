package com.example.playlistmaker.settings.domain

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}