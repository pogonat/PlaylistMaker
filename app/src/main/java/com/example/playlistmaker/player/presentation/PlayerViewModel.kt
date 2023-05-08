package com.example.playlistmaker.player.presentation

import androidx.lifecycle.*
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.player.presentation.models.PlayerState
import com.example.playlistmaker.player.presentation.models.PlayerStatus
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.player.domain.PlayerInteractor

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val trackPlayer: TrackPlayer
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    private val playStatusLiveData = MutableLiveData<PlayerStatus>()

    fun getScreenStateLiveData(): LiveData<PlayerScreenState> = stateLiveData
    fun getPayerStateLiveData(): LiveData<PlayerStatus> = playStatusLiveData

    val foundTrack = mutableListOf<Track>()

    fun loadTrack(trackId: String) {
        foundTrack.clear()
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
        playerInteractor.getTrackById(
            trackId,
            object : PlayerInteractor.PlayerConsumer {
                override fun consume(searchTrackResult: SearchTrackResult) {
                    if (searchTrackResult.resultTrackList != null) {
                        foundTrack.add(searchTrackResult.resultTrackList[0])
                    }

                    when (searchTrackResult.searchResultStatus) {
                        SearchResultStatus.ERROR_CONNECTION -> {
                            searchTrackById(trackId)
                        }
                        SearchResultStatus.NOTHING_FOUND -> {
                            searchTrackById(trackId)
                        }
                        SearchResultStatus.SUCCESS -> {
                            renderState(PlayerScreenState.Content(foundTrack[0]))
                            trackPlayer.preparePlayer(foundTrack[0].previewUrl)
                        }
                    }
                }
            }
        )
    }

    private fun searchTrackById(trackId: String) {
        playerInteractor.getTrackById(
            trackId,
            object : PlayerInteractor.PlayerConsumer {
                override fun consume(searchTrackResult: SearchTrackResult) {
                    if (searchTrackResult.resultTrackList != null) {
                        foundTrack.add(searchTrackResult.resultTrackList[0])
                    }

                    when (searchTrackResult.searchResultStatus) {
                        SearchResultStatus.ERROR_CONNECTION -> {
                            renderState(PlayerScreenState.Destroy)
                        }
                        SearchResultStatus.NOTHING_FOUND -> {
                            renderState(PlayerScreenState.Destroy)
                        }
                        SearchResultStatus.SUCCESS -> {
                            renderState(PlayerScreenState.Content(foundTrack[0]))
                            trackPlayer.preparePlayer(foundTrack[0].previewUrl)
                        }
                    }
                }
            }
        )
    }

    fun playBackControl() {
        when (getPlayerState()) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }
            PlayerState.STATE_DEFAULT -> {
                trackPlayer.preparePlayer(foundTrack[0].previewUrl)
                trackPlayer.startPlayer()
                renderPlayer(PlayerStatus.Playing(progress = trackPlayer.getCurrentPosition()))
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                trackPlayer.startPlayer()
                renderPlayer(PlayerStatus.Playing(progress = trackPlayer.getCurrentPosition()))
            }
            PlayerState.STATE_COMPLETE -> {
                renderPlayer(PlayerStatus.Complete)
            }
        }

    }

    fun pausePlayer() {
        trackPlayer.pausePlayer()
        renderPlayer(PlayerStatus.Paused(progress = trackPlayer.getCurrentPosition()))
    }

    fun releasePlayer() {
        trackPlayer.releasePlayer()
    }

    private fun getPlayerState(): PlayerState {
        return trackPlayer.getPlayerState()
    }

    fun getCurrentPosition() {
        if (getPlayerState() == PlayerState.STATE_COMPLETE) {
            trackPlayer.resetPlayer()
            renderPlayer(PlayerStatus.Complete)
        } else {
            renderPlayer(PlayerStatus.Playing(progress = trackPlayer.getCurrentPosition()))
        }
    }

}
