package com.example.playlistmaker.search.ui.models

import com.example.playlistmaker.search.domain.Track

sealed interface SearchState {
    object Loading : SearchState
    object ErrorConnection : SearchState
    object NothingFound : SearchState
    data class Success(val tracks: List<Track>) : SearchState
}