package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_in_playlists_table")
data class TracksInPlaylistsEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val trackId: String,

    @ColumnInfo(name = "cover_image_url")
    val artworkUrl100: String?,

    @ColumnInfo(name = "big_image_url")
    val largeArtworkUrl: String?,

    @ColumnInfo(name = "track_title")
    val trackName: String?,

    @ColumnInfo(name = "artist_name")
    val artistName: String?,

    @ColumnInfo(name = "collection_title")
    val collectionName: String?,

    @ColumnInfo(name = "collection_year")
    val releaseDate: String?,

    @ColumnInfo(name = "genre")
    val primaryGenreName: String?,

    @ColumnInfo(name = "artist_country")
    val country: String?,

    @ColumnInfo(name = "track_duration")
    val trackTime: String?,

    @ColumnInfo(name = "track_sample_url")
    val previewUrl: String?,

    @ColumnInfo(name = "created_at")
    val createdTimeStamp: Long,

    @ColumnInfo(name = "cover_image_url_60")
    val artworkUrl60: String?
)