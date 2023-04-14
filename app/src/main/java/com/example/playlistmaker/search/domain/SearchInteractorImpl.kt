package com.example.playlistmaker.search.domain

import com.example.playlistmaker.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(private val trackRepository: TrackRepository): SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(searchText: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = trackRepository.searchTracks(searchText)) {
                is Resource.Success -> {
                    if (resource.data!!.size == 0) {
                        consumer.consume(SearchTrackResult(SearchResultStatus.NOTHING_FOUND, resource.data))
                    } else consumer.consume(SearchTrackResult(SearchResultStatus.SUCCESS, resource.data))}
                is Resource.Error -> {consumer.consume(SearchTrackResult(SearchResultStatus.ERROR_CONNECTION, null))}
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

    override fun searchTrackById(trackId: String): Track {
        TODO("Not yet implemented")
    }

    override fun getTrackById(trackId: String): Track? {
        TODO("Not yet implemented")
    }

}