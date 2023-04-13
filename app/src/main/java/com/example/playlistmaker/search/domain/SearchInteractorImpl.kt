package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(private val trackRepository: TrackRepository): SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(searchText: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = trackRepository.searchTracks(searchText)) {
                is Resource.Success -> {consumer.consume(resource)}
                is Resource.Error -> {consumer.consume(resource)}
            }
        }
    }

    override fun saveTrack(track: Track) {
        trackRepository.saveTrack(track)
    }

    override fun getTracksHistory(): ArrayList<Track> {
        return trackRepository.getTracksHistory()
    }

    override fun clearTracksHistory() {
        trackRepository.clearTracksHistory()
    }

}