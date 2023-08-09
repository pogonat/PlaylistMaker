package com.example.playlistmaker.presentation.models

data class TrackUIModel (
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val trackDurationFormatted: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val largeArtworkUrl: String,
    val collectionYear: String,
    var isFavourite: Boolean
)