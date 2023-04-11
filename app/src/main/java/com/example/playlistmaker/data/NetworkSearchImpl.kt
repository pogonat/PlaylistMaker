package com.example.playlistmaker.data

import com.example.playlistmaker.data.api.NetworkSearchItunesApi
import com.example.playlistmaker.data.models.TracksResponse
import com.example.playlistmaker.domain.models.LookupTrackResults
import com.example.playlistmaker.domain.models.SearchTrackResult
import com.example.playlistmaker.domain.models.SearchResultStatus
import com.example.playlistmaker.domain.models.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkSearchImpl : NetworkSearch {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(NetworkSearchItunesApi::class.java)

    override fun searchTracks(
        searchInput: String,
        callback: (searchResult: SearchTrackResult) -> Unit
    ) {
        val resultsTracksList = ArrayList<Track>()
        itunesService
            .search(searchInput)
            .enqueue(object : Callback<TracksResponse> {

                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {

                    if (response.code() == 200) {

                        val searchResultsDto = response.body()?.searchResults!!

                        for (dto in searchResultsDto) {
                            resultsTracksList.add(dto.toDomain())
                        }

                        if (resultsTracksList.isEmpty()) {

                            callback.invoke(
                                SearchTrackResult(
                                    SearchResultStatus.NOTHING_FOUND,
                                    resultsTracksList
                                )
                            )
                        } else {

                            callback.invoke(
                                SearchTrackResult(
                                    SearchResultStatus.SUCCESS,
                                    resultsTracksList
                                )
                            )
                        }
                    } else {
                        callback.invoke(
                            SearchTrackResult(
                                SearchResultStatus.ERROR_CONNECTION,
                                resultsTracksList
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    callback.invoke(
                        SearchTrackResult(
                            SearchResultStatus.ERROR_CONNECTION,
                            resultsTracksList
                        )
                    )
                }
            })
    }

    override fun searchTrackById(
        trackId: String,
        callback: (searchResult: LookupTrackResults) -> Unit
    ) {
        itunesService
            .getTrackDetails(trackId)
            .enqueue(object : Callback<TracksResponse> {


                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if ((response.code() == 200)) {
                        val searchResultsDto = response.body()?.searchResults!!
                        if (searchResultsDto.size == 1) {
                            val trackInfo = searchResultsDto[0].toDomain()
                            callback.invoke(
                                LookupTrackResults(
                                    SearchResultStatus.SUCCESS,
                                    trackInfo
                                )
                            )
                        } else {
                            callback.invoke(
                                LookupTrackResults(
                                    SearchResultStatus.ERROR_CONNECTION,
                                    null
                                )
                            )
                        }

                    } else {
                        callback.invoke(
                            LookupTrackResults(
                                SearchResultStatus.ERROR_CONNECTION,
                                null
                            )
                        )
                    }

                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    callback.invoke(LookupTrackResults(SearchResultStatus.ERROR_CONNECTION, null))
                }
            })
    }
}
