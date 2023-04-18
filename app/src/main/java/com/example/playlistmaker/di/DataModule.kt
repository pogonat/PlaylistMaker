package com.example.playlistmaker.di

import com.example.playlistmaker.data.NetworkSearch
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.TrackStorage
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.storage.TrackStorageImpl
import com.example.playlistmaker.player.domain.TrackPlayerRepository
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsStorage
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingRepository
import org.koin.dsl.module

val dataModule = module {

    single<TrackStorage> {
        TrackStorageImpl()
    }

    single<NetworkSearch> {
        RetrofitNetworkClient(context = get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(networkSearch = get(), trackStorage = get())
    }

    single<TrackPlayerRepository> {
        TrackRepositoryImpl(networkSearch = get(), trackStorage = get())
    }

    single<SettingsStorage> {
        SettingsStorage()
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(settingsStorage = get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl()
    }

    single<SharingRepository> {
        SharingRepositoryImpl(get())
    }

}