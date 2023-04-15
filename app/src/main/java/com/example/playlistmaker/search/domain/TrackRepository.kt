package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource
import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(searchInput: String): Resource<List<Track>>
    fun saveTrack(track: Track): ArrayList<Track>
    fun getTracksHistory(): ArrayList<Track>
    fun clearTracksHistory()
}
