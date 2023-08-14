package com.example.playlistmaker.playlist.presentation.models

import com.example.playlistmaker.domain.models.Playlist

sealed class PlaylistEditorState {

    data class Content(val playlist: Playlist) : PlaylistEditorState()

}