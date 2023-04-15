package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource

interface SearchInteractor {

    fun searchTracks(searchText: String, consumer: TracksConsumer)

    fun saveTrack(track: Track): ArrayList<Track>

    fun getTracksHistory(): ArrayList<Track>

    fun clearTracksHistory()

//    fun searchTrackById(trackId: String): Track
//
//    fun getTrackById(trackId: String): Track?

    interface TracksConsumer {
        fun consume(searchTrackResult: SearchTrackResult)
    }

}