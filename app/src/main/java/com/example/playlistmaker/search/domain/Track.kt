package com.example.playlistmaker.search.domain

import com.google.gson.annotations.SerializedName

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val largeArtworkUrl: String,
    val collectionYear: String
) {
//    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

//    fun getYear(): String {
//        return if (releaseDate.isNotEmpty()) {
//            releaseDate.substring(0, 4)
//        } else ""
//    }
//
//    fun getAudioPreviewUrl() = previewUrl

}
