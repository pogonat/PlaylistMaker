package com.example.playlistmaker.player.presentation.models

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track

sealed class PlayerScreenState {

    object Loading : PlayerScreenState()
    object Destroy : PlayerScreenState()

    data class Content(val track: Track) : PlayerScreenState()

    data class BottomSheet(val playlist: List<Playlist>) : PlayerScreenState()

}
