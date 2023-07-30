package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import com.example.playlistmaker.playlist.presentation.models.PlaylistCreatorState
import kotlinx.coroutines.launch

class PlaylistCreatorViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val plCreatorStateLiveData = MutableLiveData<PlaylistCreatorState>()
    fun observeState(): LiveData<PlaylistCreatorState> = plCreatorStateLiveData

    fun savePlaylist(title: String, description: String, imageUri: String) {
        viewModelScope.launch {
            plCreatorStateLiveData.postValue(PlaylistCreatorState.Saving)
            playlistInteractor.createPlaylist(title, description, imageUri)
            plCreatorStateLiveData.postValue(PlaylistCreatorState.Success)
        }

    }
}