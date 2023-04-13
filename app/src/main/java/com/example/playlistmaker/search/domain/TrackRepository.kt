package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource

interface TrackRepository {
    fun searchTracks(searchInput: String): Resource<List<Track>>
    fun searchTrackById(trackId: String): Resource<List<Track>>
    fun getTrackById(trackId: String): Track?
    fun saveTrack(track: Track)
    fun getTracksHistory(): ArrayList<Track>
    fun clearTracksHistory()
}
