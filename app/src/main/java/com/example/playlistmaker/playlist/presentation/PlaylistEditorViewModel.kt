package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.presentation.models.PlaylistEditorState
import kotlinx.coroutines.launch

class PlaylistEditorViewModel(
    private val playlistInteractor: PlaylistInteractor
): PlaylistCreatorViewModel(playlistInteractor) {

    private val _state = MutableLiveData<PlaylistEditorState>()
    val editorState: LiveData<PlaylistEditorState> get() = _state

    fun getPlaylistById(playlistId: Int) {

        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect {playlist ->
                _state.postValue(PlaylistEditorState.Content(playlist))
            }
        }
    }

}