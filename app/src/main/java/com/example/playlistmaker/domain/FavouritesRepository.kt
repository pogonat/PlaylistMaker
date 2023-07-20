package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {

    suspend fun addToFavourites(track: Track)

    suspend fun removeFromFavourites(track: Track)

    fun getFavourites(): Flow<List<Track>>

}