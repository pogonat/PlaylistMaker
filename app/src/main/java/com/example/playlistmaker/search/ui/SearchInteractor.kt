package com.example.playlistmaker.search.ui

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchTrackResult

interface SearchInteractor {

    fun searchTracks(searchText: String, consumer: TracksConsumer)

    fun saveTrack(track: Track): ArrayList<Track>

    fun getTracksHistory(): ArrayList<Track>

    fun clearTracksHistory()

    interface TracksConsumer {
        fun consume(searchTrackResult: SearchTrackResult)
    }

}