package com.example.playlistmaker.adapters

import com.google.gson.annotations.SerializedName

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    val artworkUrl100: String
)
