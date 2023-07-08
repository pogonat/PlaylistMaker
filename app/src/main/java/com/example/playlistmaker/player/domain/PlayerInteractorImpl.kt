package com.example.playlistmaker.player.domain

import com.example.playlistmaker.core.Resource
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayerInteractorImpl(private val trackRepository: TrackPlayerRepository) : PlayerInteractor {

    override fun searchTrackById(trackId: String): Flow<SearchTrackResult> {
        return trackRepository.searchTrackById(trackId).map { result ->
            when (result) {
                is Resource.Success -> {
                    if (result.data?.isNotEmpty() == true) {
                        SearchTrackResult(SearchResultStatus.SUCCESS, result.data)
                    } else {
                        SearchTrackResult(SearchResultStatus.NOTHING_FOUND, null)
                    }
                }

                is Resource.Error -> {
                    SearchTrackResult(SearchResultStatus.ERROR_CONNECTION,null)
                }
            }
        }
    }

    override fun getTrackById(trackId: String): Flow<Track?> {
        return trackRepository.getTrackById(trackId)
    }
}