package com.example.playlistmaker.domain.models

data class Playlist(
    val playlistId: Int?,
    val playlistName: String,
    val playlistDescription: String,
    val imagePath: String,
    var trackList: List<String>?,
    var tracksQuantity: Int,
    var tracksQuantityText: String?
)