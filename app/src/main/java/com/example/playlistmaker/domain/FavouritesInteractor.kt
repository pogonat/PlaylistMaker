package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesInteractor {

    fun getFavourites(): Flow<List<Track>>

    suspend fun saveFavourite(track: Track)

    suspend fun deleteFavourite(track: Track)

}