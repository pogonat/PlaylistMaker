package com.example.playlistmaker.search.data

interface NetworkSearch {

    fun searchTracks(dto: Any): Response
    fun searchTrackById(dto: Any): Response

//    fun searchTracks(searchInput: String, callback: (searchResult: SearchTrackResult) -> Unit)
//    fun searchTrackById(
//        trackId: String,
//        callback: (searchResult: LookupTrackResults) -> Unit
//    )
}