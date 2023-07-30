package com.example.playlistmaker.playlist.data

import com.example.playlistmaker.data.converters.PlaylistDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.playlist.domain.PlaylistRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase
            .playListDao()
            .insertPlaylist(convertToPlaylistEntity(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist, trackId: String) {
        if (playlist.playlistId != null) {
            appDatabase
                .playListDao()
                .updatePlaylist(
                    id = playlist.playlistId,
                    quantity = playlist.tracksQuantity + 1,
                    trackList = updateTrackList(playlist.trackList, trackId)
                )
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

    private fun updateTrackList(trackList: String, trackId: String): String {
        val oldList = ArrayList<String>()
        if (trackList.isNotEmpty()) {
            oldList.addAll(gson.fromJson(trackList, Array<String>::class.java))
        }
        val newList = oldList.add(trackId)
        return gson.toJson(newList)
    }

}