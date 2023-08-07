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
    val state = _state

    fun observeState(): LiveData<PlaylistsState> {
        return state
    }

    fun getPlaylists() {
        viewModelScope.launch {

            state.postValue(PlaylistsState.Loading)

            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
                    render(playlists)
                }
        }

    }

    private fun render(playlists: List<Playlist>?) {
        if (playlists.isNullOrEmpty()) {
            state.postValue(PlaylistsState.Error)
        } else {
            state.postValue(PlaylistsState.Content(playlists))
        }
    }

}
