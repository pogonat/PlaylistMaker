package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TracksInPlaylistsEntity

@Dao
interface TracksInPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: TracksInPlaylistsEntity): Long

    @Query("SELECT id FROM track_table")
    suspend fun getTrackIdsFromPlaylists(): List<String>

}