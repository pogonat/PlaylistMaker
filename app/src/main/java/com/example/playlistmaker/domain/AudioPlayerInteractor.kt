package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.PlayerState

interface AudioPlayerInteractor {
    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): PlayerState
    fun getCurrentPosition(): Int
}