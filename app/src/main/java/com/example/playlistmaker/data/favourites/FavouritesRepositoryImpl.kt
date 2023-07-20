package com.example.playlistmaker.data.favourites

import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.FavouritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
): FavouritesRepository {
    override suspend fun addToFavourites(track: Track) {
        appDatabase.trackDao().insertTrack(convertToTrackEntity(track))
    }

    override suspend fun removeFromFavourites(track: Track) {
        appDatabase.trackDao().deleteTrack(convertToTrackEntity(track))
    }

    override fun getFavourites(): Flow<List<Track>> = flow {
        val favourites = appDatabase.trackDao().getTracksFromFavourites()
        emit(convertFromTrackEntity(favourites))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConverter.map(track) }
    }

    private fun convertToTrackEntity(track: Track): TrackEntity {
        return trackDbConverter.map(track).copy(createdTimeStamp = System.currentTimeMillis())
    }

}