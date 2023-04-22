package com.example.playlistmaker.mediaactivity.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediaactivity.ui.models.FavoritesState

class PlaylistsViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = stateLiveData
}
