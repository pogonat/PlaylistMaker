package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import com.example.playlistmaker.player.ui.models.PlayerState

class TrackPlayerImpl: TrackPlayer {

    private var playerState = PlayerState.STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
//        mediaPlayer.setOnPreparedListener {
//            playerState = PlayerState.STATE_PREPARED
//        }
//        mediaPlayer.setOnCompletionListener {
//            playerState = PlayerState.STATE_PREPARED
//        }
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
    }

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    override fun getCurrentPosition(): Long {
        return mediaPlayer.currentPosition.toLong()
    }

    override fun playBackControl() {
        TODO("Not yet implemented")
    }

    override fun enablePlayButton() {
        TODO("Not yet implemented")
    }


//    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
//    fun pausePlayer()
//    fun releasePlayer()
//    fun getPlayerState(): PlayerState
//    fun getCurrentPosition(): Int
//    fun playBackControl()
//    fun enablePlayButton()
}