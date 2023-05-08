package com.example.playlistmaker

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.settings.domain.models.DarkThemeSwitcher

class ThemeManager(private val sharedPrefs: SharedPreferences) {

    fun applyTheme() {
        val darkThemeStatus = sharedPrefs.getString(StorageKeys.THEME_SWITCHER, "")
        val darkTheme = when (darkThemeStatus) {
            DarkThemeSwitcher.DARK_THEME_SWITCHER_ON.toString() -> true
            DarkThemeSwitcher.DARK_THEME_SWITCHER_OFF.toString() -> false
            else -> isSystemDarkThemeEnabled()
        }
        switchTheme(darkTheme)
    }

    private fun isSystemDarkThemeEnabled(): Boolean {
        return when (Resources.getSystem().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
