package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {

    fun getFavourites(): Flow<List<Track>>

    fun toggleFavourite(track: Track)

}