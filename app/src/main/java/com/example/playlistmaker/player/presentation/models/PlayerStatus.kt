package com.example.playlistmaker.player.presentation.models

sealed class PlayerStatus {
    data class Playing(val progress: String) : PlayerStatus()
    data class Paused(val progress: String) : PlayerStatus()
    object Ready : PlayerStatus()
    object Complete : PlayerStatus()
}