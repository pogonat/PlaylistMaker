package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.models.ThemeSettings

interface SettingsStorage {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}