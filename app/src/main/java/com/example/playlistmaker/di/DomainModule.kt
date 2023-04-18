package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.search.ui.SearchInteractor
import com.example.playlistmaker.search.domain.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.example.playlistmaker.sharing.presentation.SharingInteractor
import org.koin.dsl.module

val domainModule = module {

    factory<PlayerInteractor> {
        PlayerInteractorImpl(trackRepository = get())
    }

    factory<SearchInteractor> {
        SearchInteractorImpl(trackRepository = get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(settingsRepository = get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(externalNavigator = get(), repository = get())
    }

}