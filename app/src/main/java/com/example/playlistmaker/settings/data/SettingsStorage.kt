package com.example.playlistmaker.settings.data

import com.example.playlistmaker.App
import com.example.playlistmaker.data.TrackStorage
import com.example.playlistmaker.domain.models.StorageKeys

class SettingsStorage {
    private val sharedPrefs = App.instance.sharedPrefs
    private val gson = App.instance.gson
    private val storageThemeKey = StorageKeys.THEME_SWITCHER.toString()

    fun getThemeSettings() {

    }

    fun updateThemeSettings(settings: ThemeSettings) {

    }
}