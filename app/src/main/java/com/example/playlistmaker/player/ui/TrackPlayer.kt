package com.example.playlistmaker.player.ui

import com.example.playlistmaker.player.ui.models.PlayerState

interface TrackPlayer {
    fun preparePlayer(trackUrl: String)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): PlayerState
    fun getCurrentPosition(): Long
    fun playBackControl()
    fun enablePlayButton()
}