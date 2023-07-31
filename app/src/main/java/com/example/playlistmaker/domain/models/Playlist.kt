package com.example.playlistmaker.domain.models

data class Playlist(
    val playlistId: Int?,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String,
    val trackList: String,
    var tracksQuantity: Int,
    var tracksQuantityText: String?
)