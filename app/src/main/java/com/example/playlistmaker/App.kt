package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.core.ThemeManager
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.domainModule
import com.example.playlistmaker.di.uiModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(listOf(uiModule, domainModule, dataModule))
        }

        val themeManager: ThemeManager by inject()
        themeManager.applyTheme()

    }

}