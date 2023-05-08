package com.example.playlistmaker.player.presentation.models

sealed class PlayerStatus {
    data class Playing(val progress: Int) : PlayerStatus()
    data class Paused(val progress: Int) : PlayerStatus()
    object Ready : PlayerStatus()
    object Complete : PlayerStatus()
}