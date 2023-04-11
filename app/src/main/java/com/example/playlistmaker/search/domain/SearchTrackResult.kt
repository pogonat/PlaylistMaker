package com.example.playlistmaker.search.domain

data class SearchTrackResult(
    var searchResultStatus: SearchResultStatus,
    var resultTrackList: ArrayList<Track>
)