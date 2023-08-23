package com.example.playlistmaker.media.presentation.models

import com.example.playlistmaker.presentation.models.TrackUIModel

sealed class FavouritesState {

    object Loading : FavouritesState()
    object Error : FavouritesState()

    data class Content(val favList: List<TrackUIModel>) : FavouritesState()

}