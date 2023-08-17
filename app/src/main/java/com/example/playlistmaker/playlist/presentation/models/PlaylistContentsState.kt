package com.example.playlistmaker.playlist.presentation.models

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.models.TrackUIModel

sealed class PlaylistContentsState {
    object Loading : PlaylistContentsState()

    object Error : PlaylistContentsState()

    data class Content(
        val playlist: Playlist,
        val trackList: List<TrackUIModel>?,
        val playlistDuration: String?
    ) : PlaylistContentsState()

    data class UpdatePlaylist(
        val trackList: List<TrackUIModel>,
        val playlistDuration: String?
    ) : PlaylistContentsState()

    object PlaylistDeleted : PlaylistContentsState()

}