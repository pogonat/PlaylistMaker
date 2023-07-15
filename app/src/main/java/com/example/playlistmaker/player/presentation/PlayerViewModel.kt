package com.example.playlistmaker.player.presentation

import androidx.lifecycle.*
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.player.presentation.models.PlayerState
import com.example.playlistmaker.player.presentation.models.PlayerStatus
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.player.domain.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val trackPlayer: TrackPlayer
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    private val playStatusLiveData = MutableLiveData<PlayerStatus>()

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = stateLiveData
    fun getPlayerStateLiveData(): LiveData<PlayerStatus> = playStatusLiveData

    private var timerJob: Job? = null

    private var foundTrack: Track? = null

    fun loadTrack(trackId: String) {
        foundTrack = null
        renderState(PlayerScreenState.Loading)
        getTrackById(trackId)
    }

    private fun renderState(state: PlayerScreenState) {
        stateLiveData.postValue(state)
    }

    private fun renderPlayer(playerStatus: PlayerStatus) {
        playStatusLiveData.postValue(playerStatus)
    }

    private fun getTrackById(trackId: String) {
        if (trackId.isNotEmpty()) {
            viewModelScope.launch {
                playerInteractor
                    .getTrackById(trackId)
                    .collect { result ->
                        if (result == null) {
                            searchTrackById(trackId)
                        } else {
                            foundTrack = result
                            renderState(PlayerScreenState.Content(result))
                            trackPlayer.preparePlayer(result.previewUrl)
                        }
                    }
            }
        }
    }

    private fun searchTrackById(trackId: String) {
        viewModelScope.launch {
            playerInteractor
                .searchTrackById(trackId)
                .collect { searchResult ->
                    processResult(searchResult)
                }
        }
    }

    private fun processResult(searchResult: SearchTrackResult) {

        when (searchResult.searchResultStatus) {
            SearchResultStatus.ERROR_CONNECTION -> {
                renderState(PlayerScreenState.Destroy)
            }

            SearchResultStatus.NOTHING_FOUND -> {
                renderState(PlayerScreenState.Destroy)
            }

            SearchResultStatus.SUCCESS -> {
                if (searchResult.resultTrackList != null) {
                    foundTrack = searchResult.resultTrackList[0]
                    renderState(PlayerScreenState.Content(foundTrack!!))
                    trackPlayer.preparePlayer(foundTrack!!.previewUrl)
                }
            }
        }
    }

    fun playBackControl() {
        when (getPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_DEFAULT -> {
                trackPlayer.preparePlayer(foundTrack!!.previewUrl)
                startPlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            PlayerState.STATE_COMPLETE -> {
                renderPlayer(PlayerStatus.Complete)
            }
        }

    }

    private fun startPlayer() {
        trackPlayer.startPlayer()
        renderPlayer(PlayerStatus.Playing(progress = trackPlayer.getCurrentPosition()))
        startTimer()
    }

    fun pausePlayer() {
        trackPlayer.pausePlayer()
        timerJob?.cancel()
        renderPlayer(PlayerStatus.Paused(progress = trackPlayer.getCurrentPosition()))
    }

    fun releasePlayer() {
        trackPlayer.releasePlayer()
    }

    private fun getPlayerState(): PlayerState {
        return trackPlayer.getPlayerState()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (getPlayerState() == PlayerState.STATE_PLAYING) {
                delay(TIMER_DELAY)
                renderCurrentPosition()
            }
        }
    }

    private fun renderCurrentPosition() {
        if (getPlayerState() == PlayerState.STATE_COMPLETE) {
            trackPlayer.resetPlayer()
            renderPlayer(PlayerStatus.Complete)
        } else {
            renderPlayer(PlayerStatus.Playing(progress = trackPlayer.getCurrentPosition()))
        }
    }

    companion object {
        private const val TIMER_DELAY = 300L
    }

}
