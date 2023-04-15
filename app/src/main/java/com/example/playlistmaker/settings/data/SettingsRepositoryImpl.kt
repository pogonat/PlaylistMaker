package com.example.playlistmaker.settings.data

import com.example.playlistmaker.data.TrackStorage
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.ThemeSettings

class SettingsRepositoryImpl(private val settingsStorage: SettingsStorage): SettingsRepository {
    override fun getThemeSettings(): ThemeSettings {
        TODO("Not yet implemented")
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        TODO("Not yet implemented")
    }

}