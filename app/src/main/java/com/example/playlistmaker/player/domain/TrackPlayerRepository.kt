package com.example.playlistmaker.player.domain

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackPlayerRepository {
    fun searchTrackById(trackId: String): Flow<Resource<List<Track>>>
    fun getTrackById(trackId: String): Flow<Track?>
}