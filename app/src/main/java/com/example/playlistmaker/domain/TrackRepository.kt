package com.example.playlistmaker.domain

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(searchInput: String): Flow<Resource<List<Track>>>
    fun saveTrack(track: Track)
    fun getTracksHistory(): Flow<List<Track>>
    fun clearTracksHistory()
}
