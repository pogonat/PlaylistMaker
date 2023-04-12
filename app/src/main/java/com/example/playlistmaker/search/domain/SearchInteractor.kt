package com.example.playlistmaker.search.domain

interface SearchInteractor {

    fun searchTracks(searchText: String, consumer: TracksConsumer)

    fun saveTrack(track: Track)

    fun getTracksHistory(): ArrayList<Track>

    interface TracksConsumer {
        fun consume(searchTrackResult: SearchTrackResult)
    }

}