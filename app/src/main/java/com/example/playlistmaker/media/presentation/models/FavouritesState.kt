package com.example.playlistmaker.media.presentation.models

import com.example.playlistmaker.domain.models.Track

sealed class FavouritesState {

    object Loading : FavouritesState()
    object Error : FavouritesState()

    data class Content(val favList: List<Track>) : FavouritesState()

}