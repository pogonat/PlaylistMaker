package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TracksInPlaylistsEntity

@Dao
interface TracksInPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: TracksInPlaylistsEntity): Long

    @Query("SELECT * FROM tracks_in_playlists_table")
    suspend fun getTracksFromPlaylists(): List<TracksInPlaylistsEntity>

    @Delete
    suspend fun deleteTrack(track: TracksInPlaylistsEntity)

}