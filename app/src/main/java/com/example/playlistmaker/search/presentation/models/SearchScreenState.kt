package com.example.playlistmaker.search.presentation.models

import com.example.playlistmaker.domain.models.Track

sealed class SearchScreenState {

    object Loading : SearchScreenState()
    object ErrorConnection : SearchScreenState()
    object NothingFound : SearchScreenState()

    data class Success(
        val foundTracks: List<Track>,
        val historyTracks: List<Track>
        ) : SearchScreenState()
}