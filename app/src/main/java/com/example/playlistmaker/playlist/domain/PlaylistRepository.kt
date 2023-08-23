package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist)

    fun addPlaylist(playlist: Playlist, track: Track): Flow<Boolean>

    fun getPlaylists(): Flow<List<Playlist>>

    fun getPlaylistById(playlistId: Int): Flow<Playlist>

    fun getTracksFromPlaylist(trackIds: List<String>): Flow<List<Track>>

    fun deleteTrackAndGetUpdatedList(playlistId: Int, trackId: String): Flow<List<Track>?>

    fun deletePlaylistAndItsTracks(playlistId: Int): Flow<Boolean>

    fun updateEditedPlaylist(title: String, description: String, path: String ,id: Int): Flow<Boolean>
}