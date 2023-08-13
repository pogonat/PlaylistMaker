package com.example.playlistmaker.playlist.domain

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun createPlaylist(title: String, description: String, imageUri: String) {
        val playlist = Playlist(
            playlistId = null,
            playlistName = title,
            playlistDescription = description,
            imagePath = imageUri,
            trackList = null,
            tracksQuantity = 0,
            tracksQuantityText = null
        )
        playlistRepository.createPlaylist(playlist)
    }

    override fun updatePlaylist(playlist: Playlist, track: Track): Flow<Boolean> {
        return playlistRepository.addPlaylist(playlist, track)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override fun getTracksFromPlaylist(trackIds: List<String>): Flow<List<Track>> {
        return playlistRepository.getTracksFromPlaylist(trackIds)
    }

    override fun deleteTrackAndGetUpdatedList(
        playlistId: Int,
        trackId: String
    ): Flow<List<Track>?> {
        return playlistRepository.deleteTrackAndGetUpdatedList(playlistId, trackId)
    }

    override fun deletePlaylistAndItsTracks(playlistId: Int): Flow<Boolean> {
        return playlistRepository.deletePlaylistAndItsTracks(playlistId)
    }

}