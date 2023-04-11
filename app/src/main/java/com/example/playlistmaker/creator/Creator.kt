package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.NetworkSearch
import com.example.playlistmaker.search.data.NetworkSearchImpl
import com.example.playlistmaker.search.data.TrackRepositoryImpl
import com.example.playlistmaker.search.data.TrackStorage
import com.example.playlistmaker.search.domain.SearchInteractor
import com.example.playlistmaker.search.domain.SearchInteractorImpl
import com.example.playlistmaker.search.domain.TrackRepository

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(
            NetworkSearch(context),
            TrackStorage(context)
        )
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getTrackRepository(context))
    }

}