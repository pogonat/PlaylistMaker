package com.example.playlistmaker.data

import com.example.playlistmaker.domain.models.LookupTrackResults
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.Track

interface NetworkSearch {
    fun searchTracks(searchInput: String, callback: (searchResult: SearchTrackResult)-> Unit)
    fun searchTrackById(
        trackId: String,
        callback: (searchResult: LookupTrackResults)-> Unit
    )
}