package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun createPlaylist(title: String, description: String, imageUri: String)

    fun updatePlaylist(playlist: Playlist, track: Track): Flow<Boolean>

    fun getPlaylists(): Flow<List<Playlist>>

}