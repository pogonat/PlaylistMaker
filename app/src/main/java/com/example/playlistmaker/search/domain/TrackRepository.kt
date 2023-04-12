package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.SearchTrackResult
import com.example.playlistmaker.search.domain.Track

interface TrackRepository {
    fun searchTracks(searchInput: String, callback: (searchResult: SearchTrackResult) -> Unit)
    fun getTrackById(trackId: String, callback: (track: Track?) -> Unit)
    fun getTracksHistory(): ArrayList<Track>
}
