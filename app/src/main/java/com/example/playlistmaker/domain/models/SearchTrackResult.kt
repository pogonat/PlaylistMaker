package com.example.playlistmaker.domain.models

data class SearchTrackResult (
    var searchResultStatus: SearchResultStatus,
    var resultTrackList: ArrayList<Track>
)