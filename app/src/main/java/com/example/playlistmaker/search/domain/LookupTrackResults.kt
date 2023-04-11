package com.example.playlistmaker.search.domain

data class LookupTrackResults(
    var searchResultStatus: SearchResultStatus,
    var resultTrackInfo: Track?
)