package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(title: String, description: String, imageUri: String)

    suspend fun updatePlaylist(playlist: Playlist, trackId: String)

    fun getPlaylists(): Flow<List<Playlist>>

}