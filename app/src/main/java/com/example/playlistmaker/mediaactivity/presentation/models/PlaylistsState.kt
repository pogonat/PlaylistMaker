package com.example.playlistmaker.mediaactivity.presentation.models

import com.example.playlistmaker.domain.models.Track

sealed class PlaylistsState {

    object Loading : PlaylistsState()
    object Error : PlaylistsState()

    data class Content(val playlists: ArrayList<ArrayList<Track>>) : PlaylistsState()

}