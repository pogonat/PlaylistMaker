package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.Response

data class SearchTrackResult(
    val searchResultStatus: SearchResultStatus,
    val resultTrackList: List<Track>?
): Response()