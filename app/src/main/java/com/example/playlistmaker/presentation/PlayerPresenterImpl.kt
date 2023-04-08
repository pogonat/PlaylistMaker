package com.example.playlistmaker.presentation

import com.example.playlistmaker.App
import com.example.playlistmaker.domain.AudioPlayerInteractor
import com.example.playlistmaker.domain.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.PlayerPresenter

class PlayerPresenterImpl(
    private val view: PlayerView,
    private val audioPlayer: AudioPlayerInteractor,
    private val repository: TrackRepository
) : PlayerPresenter {

    override fun loadTrack() {
//        val gson = App.instance.gson
        val trackId = view.getTrackId()
//        val stringTrack = view.getTrackId()
//        val track = gson.fromJson(stringTrack, Track::class.java)
//        val track = repository.getTrackById(trackId = trackId, ::presentTrack)
        repository.getTrackById(trackId = trackId, ::presentTrack)

        view.initViews()
    }

    fun presentTrack(track: Track) {
        view.fillViews(track)
        val trackUrl = track.getAudioPreviewUrl()
        preparePlayer(trackUrl, onPrepared, onCompletion)
    }

    override fun playBackControl() {
        when (audioPlayer.getPlayerState()) {
            STATE_PLAYING -> {
                audioPlayer.pausePlayer()
                view.updatePlaybackControlButton()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                audioPlayer.startPlayer()
                view.progressTextRenew()
                view.updatePlaybackControlButton()
            }
        }

    }

    override fun preparePlayer(trackUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        audioPlayer.preparePlayer(trackUrl, onPrepared, onCompletion)
    }

    override fun pausePlayer() {
        audioPlayer.pausePlayer()
    }

    override fun releasePlayer() {
        audioPlayer.releasePlayer()
    }

    override fun getPlayerState(): Int {
        return audioPlayer.getPlayerState()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayer.getCurrentPosition()
    }

    override fun enablePlayButton() {
        view.enablePlayButton()
    }

    private val onPrepared: () -> Unit = {
        view.enablePlayButton()
        view.updatePlaybackControlButton()
    }

    private val onCompletion: () -> Unit = {
        view.stopProgressUpdate()
        view.updatePlaybackControlButton()
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

}