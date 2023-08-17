package com.example.playlistmaker.data.models

import com.google.gson.annotations.SerializedName

data class TrackDto(
    @SerializedName("trackId") val trackId: String,
    @SerializedName("trackName") val trackName: String?,
    @SerializedName("artistName") val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTime: String?,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("primaryGenreName") val primaryGenreName: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("previewUrl") val previewUrl: String?,
    @SerializedName("artworkUrl60") val artworkUrl60: String?,
)