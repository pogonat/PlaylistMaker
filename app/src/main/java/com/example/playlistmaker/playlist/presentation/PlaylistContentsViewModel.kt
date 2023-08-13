package com.example.playlistmaker.playlist.presentation

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.DateTimeUtil
import com.example.playlistmaker.core.SingleLiveEvent
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.playlist.presentation.models.PlaylistContentsState
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.presentation.models.TrackToTrackUIModelConverter
import com.example.playlistmaker.presentation.models.TrackUIModel
import com.example.playlistmaker.sharing.presentation.SharingInteractor
import kotlinx.coroutines.launch

class PlaylistContentsViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val trackUIModelConverter: TrackToTrackUIModelConverter,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistContentsState>()
    val state: LiveData<PlaylistContentsState> get() = _state

    val navigationEvent = SingleLiveEvent<Intent>()

    fun getPlaylistById(playlistId: Int) {

        viewModelScope.launch {

            _state.postValue(PlaylistContentsState.Loading)

            var foundPlaylist: Playlist? = null
            var foundTrackList: List<Track>? = null

            playlistInteractor.getPlaylistById(playlistId)
                .collect { playlist ->
                    foundPlaylist = playlist
                }

            val trackIds = foundPlaylist?.trackList
            if (!trackIds.isNullOrEmpty()) {
                playlistInteractor.getTracksFromPlaylist(trackIds)
                    .collect { trackList ->
                        foundTrackList = trackList
                    }
            }
            renderState(foundPlaylist, foundTrackList)
        }
    }

    fun deleteTrackFromPlaylist(playlistId: Int, trackId: String) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackAndGetUpdatedList(playlistId, trackId)
                .collect { trackList ->
                    trackList?.let {
                        val trackUIModelList = mapTotrackUIModels(trackList).reversed()
                        val playlistDuration = getPlaylistDurationString(trackUIModelList.toSet())

                        _state.postValue(
                            PlaylistContentsState.UpdatePlaylist(
                                trackList = trackUIModelList,
                                playlistDuration = playlistDuration
                            )
                        )
                    } ?: run {
                        _state.postValue(PlaylistContentsState.Error)
                    }
                }
        }
    }

    fun sharePlaylist(playlistId: Int, quantityText: String, tracks: List<TrackUIModel>) {
        var message = ""
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId)
                .collect { playlist ->
                    message = buildMessage(playlist, quantityText, tracks)
                }
            val intent = sharingInteractor.shareApp(message)
            navigationEvent.postValue(intent)
        }
    }

    private fun buildMessage(
        playlist: Playlist,
        quantityText: String,
        tracks: List<TrackUIModel>
    ): String {
        var resultString =
            "${playlist.playlistName}\n${playlist.playlistDescription}\n$quantityText\n"
        for (i in tracks.indices) {
            resultString += "${i + 1}. ${tracks[i].artistName} - ${tracks[i].trackName} (${tracks[i].trackDurationFormatted})\n"
        }
        return resultString
    }

    private fun renderState(foundPlaylist: Playlist?, foundTrackList: List<Track>?) {
        foundPlaylist?.let {
            if (foundTrackList.isNullOrEmpty()) {
                _state.postValue(PlaylistContentsState.Content(foundPlaylist, null, null))
            } else {

                val trackUIModelList = mapTotrackUIModels(foundTrackList).reversed()
                val playlistDuration = getPlaylistDurationString(trackUIModelList.toSet())

                _state.postValue(
                    PlaylistContentsState
                        .Content(
                            playlist = foundPlaylist,
                            trackList = trackUIModelList,
                            playlistDuration = playlistDuration
                        )
                )
            }
        } ?: run {
            _state.postValue(PlaylistContentsState.Error)
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistAndItsTracks(playlistId).collect { isDeleted ->
                if (isDeleted) {
                    _state.postValue(PlaylistContentsState.PlaylistDeleted)
                } else {
                    _state.postValue(PlaylistContentsState.Error)
                }
            }
        }
    }

    private fun mapTotrackUIModels(trackList: List<Track>): List<TrackUIModel> {
        return trackList.map { trackUIModelConverter.map(it) }
    }

    private fun getPlaylistDurationString(trackUIModelList: Set<TrackUIModel>): String {
        var duration: Long = 0L
        for (track in trackUIModelList) {
            if (track.trackTimeMillis.isNotEmpty()) {
                duration += track.trackTimeMillis.toLong()
            }
        }
        return DateTimeUtil.formatDurationMillisToTime(duration)
    }
}