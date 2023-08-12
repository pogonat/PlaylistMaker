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
        appDatabase.playListDao().insertPlaylist(convertToPlaylistEntity(playlist))
    }

    override fun addPlaylist(playlist: Playlist, track: Track): Flow<Boolean> = flow {
        if (playlist.playlistId == null) {
            emit(false)
        } else {
            val updateResult = appDatabase.playListDao().updatePlaylist(
                trackList = getUpdatedTrackListJson(playlist.trackList, track.trackId),
                quantity = (playlist.trackList?.size ?: 0) + 1,
                id = playlist.playlistId
            )

            if (updateResult != NUMBER_OF_LINES_WHEN_UPDATE_FAILED) {
                appDatabase.tracksInPlaylistsDao().insertTrack(convertTrackToEntity(track))
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

    override fun getPlaylistById(playlistId: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playListDao().getPlaylistById(playlistId)
        emit(playlistDbConverter.map(playlist))
    }

    override fun getTracksFromPlaylist(trackIds: List<String>): Flow<List<Track>> = flow {
        val tracks = appDatabase.tracksInPlaylistsDao().getTracksFromPlaylists()
        val tracksFiltered = filterTracksByListIds(trackIds, tracks)
        emit(tracksFiltered.map { convertToTrack(it) })
    }

    override fun deleteTrackAndGetUpdatedList(
        playlistId: Int,
        trackId: String
    ): Flow<List<Track>?> = flow {
        val playlistEntity = appDatabase.playListDao().getPlaylistById(playlistId)
        val playlist = playlistDbConverter.map(playlistEntity)

        if (playlist.trackList.isNullOrEmpty()) {
            emit(null)
        } else {

            val upatedTrackList: List<String> = deleteFromTrackList(playlist.trackList!!, trackId)
            val updateResult: Int = appDatabase.playListDao().updatePlaylist(
                trackList = gson.toJson(upatedTrackList),
                quantity = upatedTrackList.size,
                id = playlistId
            )

            if (updateResult != NUMBER_OF_LINES_WHEN_UPDATE_FAILED) {
                deleteTrackFromBaseOfAllTracks(trackId)

                if (upatedTrackList.isNotEmpty()) {
                    val tracksFromPlaylists: List<TracksInPlaylistsEntity> =
                        appDatabase.tracksInPlaylistsDao().getTracksFromPlaylists()

                    val tracksInPlaylist: List<TracksInPlaylistsEntity> =
                        filterTracksByListIds(upatedTrackList, tracksFromPlaylists)

                    val updatedListOfTracks: List<Track> = tracksInPlaylist.map { convertToTrack(it) }
                    emit(updatedListOfTracks)
                } else {
                    emit(emptyList<Track>())
                }
            } else {
                emit(null)
            }
        }
    }

    private fun getUpdatedTrackListJson(trackList: List<String>?, trackId: String): String {
        val newList = mutableListOf<String>()
        trackList?.let { newList.addAll(trackList) }
        newList.add(trackId)
        return gson.toJson(newList)
    }

    private fun filterTracksByListIds(
        trackIds: List<String>,
        trackList: List<TracksInPlaylistsEntity>
    ): List<TracksInPlaylistsEntity> {
        val resultList = mutableListOf<TracksInPlaylistsEntity>()
        for (id in trackIds) {
            for (track in trackList) {
                if (track.trackId == id) {
                    resultList.add(track)
                    break
                }
            }
        }
        return resultList
    }

    private fun deleteFromTrackList(trackList: List<String>, trackId: String): List<String> {
        return trackList.filter { it != trackId }
    }

    private fun checkTrackInPlaylists(trackId: String, playlists: List<PlaylistEntity>): Boolean {
        for (playlist in playlists) {
            val trackList = playlistDbConverter.convertFromJsonToStringList(playlist.trackList)
            if (!trackList.isNullOrEmpty()) {
                if (trackId in trackList) return true
            }
        }
        return false
    }

    private suspend fun deleteTrackFromBaseOfAllTracks(id: String) {
        val playlists = appDatabase.playListDao().getPlaylists()
        val isInAnyPlaylist = checkTrackInPlaylists(id, playlists)

        if (isInAnyPlaylist) return

        val tracksFromPlaylistsList =
            appDatabase.tracksInPlaylistsDao().getTracksFromPlaylists()
        for (entity in tracksFromPlaylistsList) {
            if (id == entity.trackId) {
                appDatabase.tracksInPlaylistsDao().deleteTrack(entity)
                break
            }
        }
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

    private fun convertToTrack(tracksInPlaylistsEntity: TracksInPlaylistsEntity): Track {
        return trackDbConverter.mapFromTracksInPlaylistsEntity(tracksInPlaylistsEntity)
    }

    companion object {
        private const val NUMBER_OF_LINES_WHEN_UPDATE_FAILED = 0
    }

}