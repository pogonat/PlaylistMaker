package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.domainModule
import com.example.playlistmaker.di.uiModule
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.settings.domain.models.DarkThemeSwitcher
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    lateinit var sharedPrefs: SharedPreferences
        private set

    lateinit var gson: Gson
        private set

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        instance = this
        
        val storageKey = StorageKeys.PLAYLIST_MAKER_PREFERENCES.toString()
        sharedPrefs = getSharedPreferences(storageKey, MODE_PRIVATE)

        gson = Gson()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(uiModule, domainModule, dataModule))
        }


        val storageThemeKey = StorageKeys.THEME_SWITCHER.toString()
        val darkThemeStatus = sharedPrefs.getString(storageThemeKey, "")
        if (darkThemeStatus == "") {
            when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    switchTheme(true)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    switchTheme(false)
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    switchTheme(darkTheme)
                }
            }
        } else {
            darkTheme = (darkThemeStatus == DarkThemeSwitcher.DARK_THEME_SWITCHER_ON.toString())
            switchTheme(darkTheme)
        }

    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        lateinit var instance: App
            private set
    }
}