package com.example.playlistmaker.media.presentation.models

import com.example.playlistmaker.domain.models.Playlist

sealed class PlaylistsState {

    object Loading : PlaylistsState()
    object Error : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()

}