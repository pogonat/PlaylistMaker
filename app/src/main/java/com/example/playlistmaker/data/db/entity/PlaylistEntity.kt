package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity (

    @PrimaryKey(autoGenerate = true)
    val playlistId: Int? = null,

    @ColumnInfo(name = "playlist_title")
    val playlistName: String,

    @ColumnInfo(name = "playlist_description")
    val playlistDescription: String,

    @ColumnInfo(name = "playlist_path")
    val imagePath: String,

    @ColumnInfo(name = "track_list")
    var trackList: String,

    @ColumnInfo(name = "tracks_quantity")
    var tracksQuantity: Int,

    @ColumnInfo(name = "created_at")
    val createdTimeStamp: Long
)