package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(

    @PrimaryKey
    val id: Int,
    val coverImageUrl: String,
    val bigImageUrl: String,
    val trackTitle: String,
    val artistName: String,
    val collectionTitle: String?,
    val collectionYear: String,
    val genre: String,
    val artistCountry: String,
    val trackDuration: String,
    val trackSampleUrl: String

)