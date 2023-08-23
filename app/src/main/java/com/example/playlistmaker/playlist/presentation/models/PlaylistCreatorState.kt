package com.example.playlistmaker.playlist.presentation.models

sealed class PlaylistCreatorState {
    object Saving : PlaylistCreatorState()
    object Error : PlaylistCreatorState()
    object Success : PlaylistCreatorState()
}