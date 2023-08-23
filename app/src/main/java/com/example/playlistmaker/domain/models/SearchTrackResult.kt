package com.example.playlistmaker.domain.models

import com.example.playlistmaker.data.models.Response

data class SearchTrackResult(
    val searchResultStatus: SearchResultStatus,
    val resultTrackList: List<Track>?
) : Response()