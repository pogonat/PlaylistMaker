package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource
import com.example.playlistmaker.search.domain.SearchTrackResult
import com.example.playlistmaker.search.domain.Track

interface TrackRepository {
    fun searchTracks(searchInput: String): Resource<List<Track>>
    fun getTrackById(trackId: String, callback: (track: Track?) -> Unit)
    fun saveTrack(track: Track)
    fun getTracksHistory(): ArrayList<Track>
    fun clearTracksHistory()
}
