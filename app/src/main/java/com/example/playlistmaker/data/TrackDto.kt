package com.example.playlistmaker.data

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
    @SerializedName("previewUrl") val previewUrl: String?
) {
//    fun toDomain(): Track {
//        return Track(
//            trackId = this.trackId,
//            trackName = this.trackName ?: "",
//            artistName = this.artistName ?: "",
//            trackTime = this.trackTime ?: "",
//            artworkUrl100 = this.artworkUrl100?: "",
//            collectionName = this.collectionName?: "",
//            releaseDate = this.releaseDate?: "",
//            primaryGenreName = this.primaryGenreName?: "",
//            country = this.country?: "",
//            previewUrl = this.previewUrl?: ""
//        )
//    }
}
