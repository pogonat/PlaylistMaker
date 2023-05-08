package com.example.playlistmaker.mediaactivity.ui.models

import com.example.playlistmaker.domain.models.Track

sealed class FavoritesState {

    object Loading : FavoritesState()
    object Error : FavoritesState()

    data class Content(val favList: List<Track>) : FavoritesState()

}