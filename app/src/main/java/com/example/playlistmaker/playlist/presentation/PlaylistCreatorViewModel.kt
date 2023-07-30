package com.example.playlistmaker.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.playlist.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistCreatorViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    fun saveImage() {}
    fun savePlaylist(title: String, description: String, imageUri: String) {
        viewModelScope.launch {
            playlistInteractor.createPlaylist(title, description, imageUri)
        }

    }
}