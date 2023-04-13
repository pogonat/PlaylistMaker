package com.example.playlistmaker.search.domain

interface SearchInteractor {

    fun searchTracks(searchText: String, consumer: TracksConsumer)

    fun saveTrack(track: Track)

    fun getTracksHistory(): ArrayList<Track>

    fun clearTracksHistory()

    interface TracksConsumer {
        fun consume(searchTrackResult: SearchTrackResult)
    }

}