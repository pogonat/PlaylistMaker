package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist, trackId: String)

    fun getPlaylists(): Flow<List<Playlist>>

}