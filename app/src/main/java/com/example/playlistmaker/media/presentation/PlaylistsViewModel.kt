package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.media.presentation.models.PlaylistsState
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>()
    val state: LiveData<PlaylistsState> get() = _state

    fun getPlaylists() {
        viewModelScope.launch {

            _state.postValue(PlaylistsState.Loading)

            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    render(playlists)
                }
        }

    }

    private fun render(playlists: List<Playlist>?) {
        if (playlists.isNullOrEmpty()) {
            _state.postValue(PlaylistsState.Error)
        } else {
            _state.postValue(PlaylistsState.Content(playlists))
        }
    }

}
