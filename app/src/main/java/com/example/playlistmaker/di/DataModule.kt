package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.NetworkSearch
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.TrackStorage
import com.example.playlistmaker.data.models.TracksResponseToTrackMapper
import com.example.playlistmaker.data.network.NetworkSearchItunesApi
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.storage.TrackStorageImpl
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.player.domain.TrackPlayerRepository
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsStorage
import com.example.playlistmaker.settings.data.SettingsStorageImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<NetworkSearchItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NetworkSearchItunesApi::class.java)
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(StorageKeys.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<Gson> {
        Gson()
    }

    single<TracksResponseToTrackMapper>  {
        TracksResponseToTrackMapper()
    }

    single<TrackStorage> {
        TrackStorageImpl(gson = get(), sharedPrefs = get())
    }

    single<NetworkSearch> {
        RetrofitNetworkClient(itunesApi = get(),context = get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(networkSearch = get(), trackStorage = get(), mapper = get())
    }

    single<TrackPlayerRepository> {
        TrackRepositoryImpl(networkSearch = get(), trackStorage = get(), mapper = get())
    }

    single<SettingsStorage> {
        SettingsStorageImpl(sharedPrefs = get())
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