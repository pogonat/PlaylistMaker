package com.example.playlistmaker.player.domain

import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlayerInteractor {

    fun searchTrackById(trackId: String): Flow<SearchTrackResult>
    fun getTrackById(trackId: String): Flow<Track?>

}