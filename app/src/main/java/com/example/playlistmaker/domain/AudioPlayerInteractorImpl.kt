package com.example.playlistmaker.domain

import android.media.MediaPlayer

class AudioPlayerInteractorImpl : AudioPlayerInteractor {

    private var playerState = STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            onCompletion()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getPlayerState(): Int {
        return playerState
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

}