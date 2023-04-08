package com.example.playlistmaker.domain.models

data class LookupTrackResults(
    var searchResultStatus: SearchResultStatus,
    var resultTrackInfo: Track?
)