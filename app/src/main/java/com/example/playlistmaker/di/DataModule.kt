package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.playlistmaker.data.network.NetworkSearch
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.TrackStorage
import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.converters.TracksResponseToTrackMapper
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.NetworkSearchItunesApi
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.storage.TrackStorageImpl
import com.example.playlistmaker.domain.models.StorageKeys
import com.example.playlistmaker.data.favourites.FavouritesRepositoryImpl
import com.example.playlistmaker.domain.FavouritesRepository
import com.example.playlistmaker.player.domain.TrackPlayerRepository
import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.playlist.data.PlaylistRepositoryImpl
import com.example.playlistmaker.playlist.domain.PlaylistRepository
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

    single<AppDatabase> {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single<TracksResponseToTrackMapper>  {
        TracksResponseToTrackMapper()
    }

    single<TrackDbConverter> {
        TrackDbConverter()
    }

    single<PlaylistDbConverter> {
        PlaylistDbConverter()
    }

    single<TrackStorage> {
        TrackStorageImpl(gson = get(), sharedPrefs = get())
    }

    single<NetworkSearch> {
        RetrofitNetworkClient(itunesApi = get(),context = get())
    }

    single<TrackRepository> {
        TrackRepositoryImpl(networkSearch = get(), trackStorage = get(), mapper = get(), appDatabase = get())
    }

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(appDatabase = get(), trackDbConverter = get())
    }

    single<TrackPlayerRepository> {
        TrackRepositoryImpl(networkSearch = get(), trackStorage = get(), mapper = get(), appDatabase = get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(appDatabase = get(), playlistDbConverter = get(), gson = get())
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