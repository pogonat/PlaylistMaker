package com.example.playlistmaker.mediaactivity.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediaactivity.presentation.models.PlaylistsState

class PlaylistsViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    init {
        stateLiveData.postValue(PlaylistsState.Error)
    }

}
