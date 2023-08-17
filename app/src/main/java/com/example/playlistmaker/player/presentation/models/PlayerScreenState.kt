package com.example.playlistmaker.player.presentation.models

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.models.TrackUIModel

sealed class PlayerScreenState {

    object Loading : PlayerScreenState()
    object Destroy : PlayerScreenState()

    data class Content(val track: TrackUIModel) : PlayerScreenState()

    data class BottomSheet(val playlist: List<Playlist>) : PlayerScreenState()

    data class BottomSheetHidden(val playlistTitle: String) : PlayerScreenState()

    data class ShowMessage(val playlistTitle: String?) : PlayerScreenState()

}
