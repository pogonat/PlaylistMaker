package com.example.playlistmaker.player.ui.models

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.models.SearchScreenState

sealed class PlayerScreenState {

    object Loading : PlayerScreenState()
    object Destroy : PlayerScreenState()

    data class Content(val track: Track) : PlayerScreenState()

}
