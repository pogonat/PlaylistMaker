package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist)

    fun updatePlaylist(playlist: Playlist, track: Track): Flow<Boolean>

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(playlistId: Int): Flow<Playlist>

    fun getTracksFromPlaylists(trackIds: List<String>): Flow<List<Track>>

}