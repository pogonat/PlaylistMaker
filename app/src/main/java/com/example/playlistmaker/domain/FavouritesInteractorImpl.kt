package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavouritesInteractorImpl(private val favoritesRepository: FavouritesRepository): FavouritesInteractor {
    override fun getFavourites(): Flow<List<Track>> {
        return favoritesRepository.getFavourites()
    }

    override fun toggleFavourite(track: Track) {
    }
}