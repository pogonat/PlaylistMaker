package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.FavouritesInteractor
import com.example.playlistmaker.media.presentation.models.FavouritesState
import com.example.playlistmaker.presentation.models.TrackToTrackUIModelConverter
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val favInteractor: FavouritesInteractor,
    private val trackToTrackUIModelConverter: TrackToTrackUIModelConverter
) : ViewModel() {
    private val _state = MutableLiveData<FavouritesState>()
    private val state = _state

    fun observeState(): LiveData<FavouritesState> {
        return state
    }

    init {
        getFavouritesList()
    }

    fun getFavouritesList() {
        viewModelScope.launch {
            favInteractor.getFavourites().collect { list ->
                if (list.isNotEmpty()) {
                    val favList = trackToTrackUIModelConverter.mapListToTrackUIModels(list)
                    state.postValue(FavouritesState.Content(favList))
                } else {
                    state.postValue(FavouritesState.Error)
                }
            }
        }
    }
}
