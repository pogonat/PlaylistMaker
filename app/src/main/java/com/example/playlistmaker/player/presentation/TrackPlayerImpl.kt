package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import com.example.playlistmaker.player.presentation.TrackPlayer
import com.example.playlistmaker.player.presentation.models.PlayerState

class TrackPlayerImpl(private val mediaPlayer: MediaPlayer) : TrackPlayer {

    private var playerState = PlayerState.STATE_DEFAULT

    override fun preparePlayer(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.STATE_COMPLETE
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
        playerState = PlayerState.STATE_DEFAULT
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun resetPlayer() {
        playerState = PlayerState.STATE_PREPARED
    }

}