package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.storage.TrackStorageImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.TrackPlayerRepository
import com.example.playlistmaker.player.ui.TrackPlayer
import com.example.playlistmaker.player.ui.TrackPlayerImpl
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.domain.SearchInteractorImpl
import com.example.playlistmaker.search.domain.TrackRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsStorage
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.ui.SettingsInteractor
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.SharingRepository
import com.example.playlistmaker.sharing.presentation.SharingInteractor

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(
            RetrofitNetworkClient(context),
            TrackStorageImpl()
        )
    }

    private fun getPlayerRepository(context: Context): TrackPlayerRepository {
        return TrackRepositoryImpl(
            RetrofitNetworkClient(context),
            TrackStorageImpl()
        )
    }

    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl()
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(SettingsStorage())
    }

    private fun getSharingRepository(context: Context): SharingRepository {
        return SharingRepositoryImpl(context)
    }

    fun provideTrackPlayer(): TrackPlayer {
        return TrackPlayerImpl()
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getTrackRepository(context))
    }

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(), getSharingRepository(context))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

}