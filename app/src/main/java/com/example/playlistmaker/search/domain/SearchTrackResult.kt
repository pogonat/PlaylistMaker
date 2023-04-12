package com.example.playlistmaker.search.domain

data class SearchTrackResult(
    val searchResultStatus: SearchResultStatus,
    val resultTrackList: ArrayList<Track>?
)