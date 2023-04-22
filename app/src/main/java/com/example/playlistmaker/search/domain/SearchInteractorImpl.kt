package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.search.ui.SearchInteractor
import java.util.concurrent.Executors

class SearchInteractorImpl(private val trackRepository: TrackRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(searchText: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = trackRepository.searchTracks(searchText)) {
                is Resource.Success -> {
                    if (resource.data!!.isEmpty()) {
                        consumer.consume(
                            SearchTrackResult(
                                SearchResultStatus.NOTHING_FOUND,
                                resource.data
                            )
                        )
                    } else consumer.consume(
                        SearchTrackResult(
                            SearchResultStatus.SUCCESS,
                            resource.data
                        )
                    )
                }
                is Resource.Error -> {
                    consumer.consume(SearchTrackResult(SearchResultStatus.ERROR_CONNECTION, null))
                }
            }
        }
    }

    override fun saveTrack(track: Track): ArrayList<Track> {
        return trackRepository.saveTrack(track)
    }

    override fun getTracksHistory(): ArrayList<Track> {
        return trackRepository.getTracksHistory()
    }

    override fun clearTracksHistory() {
        trackRepository.clearTracksHistory()
    }

}