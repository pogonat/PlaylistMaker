package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.DateTimeUtil
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
    private val sharingInteractor: SharingInteractor,
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistContentsState>()
    val state: LiveData<PlaylistContentsState> get() = _state

    fun getPlaylistById(playlistId: Int) {

        _state.postValue(PlaylistContentsState.Loading)

        viewModelScope.launch {

            var foundPlaylist: Playlist? = null
            var foundTrackList: List<Track>? = null

            playlistInteractor.getPlaylistById(playlistId)
                .collect { playlist ->
                    foundPlaylist = playlist
                }

            if (foundPlaylist != null && !foundPlaylist!!.trackList.isNullOrEmpty()) {
                val trackIds = foundPlaylist!!.trackList
                if (!trackIds.isNullOrEmpty()) {
                    playlistInteractor.getTracksFromPlaylists(trackIds)
                        .collect { trackList ->
                            foundTrackList = trackList
                        }
                }
            }
            renderState(foundPlaylist, foundTrackList)
        }
    }

    private fun renderState(foundPlaylist: Playlist?, foundTrackList: List<Track>?) {
        foundPlaylist?.let {
            if (foundTrackList.isNullOrEmpty()) {
                _state.postValue(PlaylistContentsState.Content(foundPlaylist, null, null))
            } else {

                val trackUIModelList = foundTrackList.map { trackUIModelConverter.map(it) }
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