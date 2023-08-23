package com.example.playlistmaker.domain.models

data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val largeArtworkUrl: String,
    var isFavourite: Boolean = false,
    val artworkUrl60: String
)
