package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("UPDATE playlist_table SET track_list=:trackList, tracks_quantity=:quantity WHERE playlistId=:id")
    suspend fun updatePlaylist(trackList: String, quantity: Int, id: Int): Int

    @Query("SELECT * FROM playlist_table ORDER BY created_at DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE playlistId=:id")
    suspend fun getPlaylistById(id: Int): PlaylistEntity

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity): Int
}