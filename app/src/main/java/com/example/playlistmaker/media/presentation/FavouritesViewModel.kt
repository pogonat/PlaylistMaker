package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.FavouritesInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.media.presentation.models.FavouritesState
import kotlinx.coroutines.launch

class FavouritesViewModel(private val favInteractor: FavouritesInteractor) : ViewModel() {
    private val favouritesState = MutableLiveData<FavouritesState>()
    fun observeState(): LiveData<FavouritesState> = favouritesState

    init {
        getFavouritesList()
    }

    fun getFavouritesList() {
        viewModelScope.launch {
            favInteractor.getFavourites().collect{list ->
                val favList = mutableListOf<Track>()
                favList.addAll(list)
                if (favList.isNotEmpty()) {
                    favouritesState.postValue(FavouritesState.Content(favList))
                } else {
                    favouritesState.postValue(FavouritesState.Error)
                }
            }
        }
    }
}
