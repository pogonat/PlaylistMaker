package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.LookupTrackResults
import com.example.playlistmaker.search.domain.SearchTrackResult
import retrofit2.Response

interface NetworkSearch {

    fun doRequest(dto: Any): Response

//    fun searchTracks(searchInput: String, callback: (searchResult: SearchTrackResult) -> Unit)
//    fun searchTrackById(
//        trackId: String,
//        callback: (searchResult: LookupTrackResults) -> Unit
//    )
}