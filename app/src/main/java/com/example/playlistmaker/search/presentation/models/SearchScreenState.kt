package com.example.playlistmaker.search.presentation.models

import com.example.playlistmaker.presentation.models.TrackUIModel

sealed class SearchScreenState {

    object Loading : SearchScreenState()
    object ErrorConnection : SearchScreenState()
    object NothingFound : SearchScreenState()

    data class Success(
        val foundTracks: List<TrackUIModel>,
        val historyTracks: List<TrackUIModel>
        ) : SearchScreenState()
}