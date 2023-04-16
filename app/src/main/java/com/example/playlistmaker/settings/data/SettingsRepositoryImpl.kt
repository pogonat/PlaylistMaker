package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage): SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        return settingsStorage.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        settingsStorage.updateThemeSettings(settings)
    }

}