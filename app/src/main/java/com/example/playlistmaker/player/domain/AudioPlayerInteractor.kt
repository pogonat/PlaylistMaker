package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.PlayerState

interface AudioPlayerInteractor {
    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getPlayerState(): PlayerState
    fun getCurrentPosition(): Int
}