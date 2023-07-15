package com.example.playlistmaker.player.presentation

import com.example.playlistmaker.player.presentation.models.PlayerState

interface TrackPlayer {
    fun preparePlayer(trackUrl: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): PlayerState
    fun getCurrentPosition(): String
    fun resetPlayer()
}