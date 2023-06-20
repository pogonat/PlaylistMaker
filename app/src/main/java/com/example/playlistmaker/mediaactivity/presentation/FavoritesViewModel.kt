package com.example.playlistmaker.mediaactivity.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.mediaactivity.presentation.models.FavoritesState

class FavoritesViewModel : ViewModel() {
    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    init {
        stateLiveData.postValue(FavoritesState.Error)
    }
}
