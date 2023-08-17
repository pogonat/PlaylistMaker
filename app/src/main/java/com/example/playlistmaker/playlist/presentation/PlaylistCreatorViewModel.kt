package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.presentation.models.PlaylistCreatorState
import kotlinx.coroutines.launch

open class PlaylistCreatorViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _state = MutableLiveData<PlaylistCreatorState>()
    open val state: LiveData<PlaylistCreatorState> get() = _state

    fun savePlaylist(title: String, description: String, imageUri: String) {
        viewModelScope.launch {
            _state.postValue(PlaylistCreatorState.Saving)
            playlistInteractor.createPlaylist(title, description, imageUri)
            _state.postValue(PlaylistCreatorState.Success)
        }

    }
}