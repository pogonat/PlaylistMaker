package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(searchInput: String, callback:(searchResult: SearchTrackResult)-> Unit)
    fun getTrackById(trackId: String, callback: (track: Track) -> Unit)
}
