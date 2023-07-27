package com.example.playlistmaker.domain.models

data class Playlist(
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val playlistPath: String,
    val trackList: String,
    var tracksQuantity: Int
)