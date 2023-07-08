package com.example.playlistmaker.search.domain

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchTrackResult
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {

    fun searchTracks(searchText: String): Flow<SearchTrackResult>

    fun saveTrack(track: Track): ArrayList<Track>

    fun getTracksHistory(): ArrayList<Track>

    fun clearTracksHistory()

}