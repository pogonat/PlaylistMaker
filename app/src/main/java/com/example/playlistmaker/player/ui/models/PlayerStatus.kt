package com.example.playlistmaker.player.ui.models

sealed class PlayerStatus {
    data class Playing(val progress: Long, val isPlaying: Boolean) : PlayerStatus()
    data class Paused(val progress: Long, val isPlaying: Boolean) : PlayerStatus()
    data class Ready(val isPlaying: Boolean) : PlayerStatus()
}