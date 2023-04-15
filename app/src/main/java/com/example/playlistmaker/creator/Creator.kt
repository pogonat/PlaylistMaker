package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerInteractorImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.TrackStorageImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.TrackPlayerRepository
import com.example.playlistmaker.player.ui.TrackPlayer
import com.example.playlistmaker.player.ui.TrackPlayerImpl
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.domain.SearchInteractorImpl
import com.example.playlistmaker.search.domain.TrackRepository

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

    fun provideTrackPlayer(): TrackPlayer {
        return TrackPlayerImpl()
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getTrackRepository(context))
    }

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository(context))
    }

}