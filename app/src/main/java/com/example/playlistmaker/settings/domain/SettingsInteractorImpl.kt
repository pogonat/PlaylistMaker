package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        settingsRepository.updateThemeSettings(settings)
    }
}
