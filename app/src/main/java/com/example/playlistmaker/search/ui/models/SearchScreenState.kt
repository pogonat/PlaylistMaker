package com.example.playlistmaker.search.ui.models

import com.example.playlistmaker.search.domain.Track

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    object ErrorConnection : SearchScreenState()
    object NothingFound : SearchScreenState()
    data class Success(val foundTracks: List<Track>, val historyTracks: List<Track>) : SearchScreenState()
}