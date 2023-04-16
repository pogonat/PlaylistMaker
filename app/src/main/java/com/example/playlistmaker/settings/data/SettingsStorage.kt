package com.example.playlistmaker.settings.data

import com.example.playlistmaker.App
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.settings.domain.models.DarkThemeSwitcher
import com.example.playlistmaker.settings.domain.models.ThemeSettings

class SettingsStorage {
    private val sharedPrefs = App.instance.sharedPrefs
    private val storageThemeKey = StorageKeys.THEME_SWITCHER.toString()

    fun getThemeSettings(): ThemeSettings {
        when (sharedPrefs.getString(storageThemeKey, "")) {
            DarkThemeSwitcher.DARK_THEME_SWITCHER_ON.toString() -> {
                return ThemeSettings(DarkThemeSwitcher.DARK_THEME_SWITCHER_ON)
            }
            else -> {
                return ThemeSettings(DarkThemeSwitcher.DARK_THEME_SWITCHER_OFF)
            }
        }
    }

    fun updateThemeSettings(settings: ThemeSettings) {
        when (settings.darkThemeSwitcher) {
            DarkThemeSwitcher.DARK_THEME_SWITCHER_ON ->
                sharedPrefs.edit().putString(
                    storageThemeKey,
                    DarkThemeSwitcher.DARK_THEME_SWITCHER_ON.toString()
                ).apply()
            DarkThemeSwitcher.DARK_THEME_SWITCHER_OFF ->
                sharedPrefs.edit().putString(
                    storageThemeKey,
                    DarkThemeSwitcher.DARK_THEME_SWITCHER_OFF.toString()
                ).apply()
        }
    }
}
