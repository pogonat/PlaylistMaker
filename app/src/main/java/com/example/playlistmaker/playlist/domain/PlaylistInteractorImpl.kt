package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun createPlaylist(title: String, description: String, imageUri: String) {
        val playlist = Playlist(
            playlistId = null,
            playlistName = title,
            playlistDescription = description,
            imagePath = imageUri,
            trackList = "",
            tracksQuantity = 0
        )
        playlistRepository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist, trackId: String) {
        playlistRepository.updatePlaylist(playlist, trackId)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

}