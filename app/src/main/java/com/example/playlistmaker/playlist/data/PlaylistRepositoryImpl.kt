package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TracksInPlaylistsEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase
            .playListDao()
            .insertPlaylist(convertToPlaylistEntity(playlist))
    }

    override fun updatePlaylist(playlist: Playlist, track: Track): Flow<Boolean> = flow {
        if (playlist.playlistId == null) {
            emit(false)
        } else {
            val updateResult =
                appDatabase.playListDao()
                    .updatePlaylist(
                        trackList = updateTrackList(playlist.trackList, track.trackId),
                        quantity = playlist.tracksQuantity + 1,
                        id = playlist.playlistId,
                    )

            if (updateResult != NUMBER_OF_LINES_WHEN_UPDATE_FAILED) {
                appDatabase
                    .tracksInPlaylistsDao()
                    .insertTrack(convertTrackToEntity(track))
                emit(true)
            } else {
                emit(false)
            }
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playListDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

    private fun convertToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConverter
            .map(playlist)
            .copy(createdTimeStamp = System.currentTimeMillis())
    }

    private fun convertTrackToEntity(track: Track): TracksInPlaylistsEntity {
        return trackDbConverter.mapToTracksInPlaylistsEntity(track)
    }

    private fun updateTrackList(trackList: List<String>?, trackId: String): String {
        val newList = ArrayList<String>()
        if (trackList.isNullOrEmpty()) {
            newList.add(trackId)
        } else {
            newList.addAll(trackList)
            newList.add(trackId)
        }
        return gson.toJson(newList)
    }

    companion object {
        private const val NUMBER_OF_LINES_WHEN_UPDATE_FAILED = 0
    }

}