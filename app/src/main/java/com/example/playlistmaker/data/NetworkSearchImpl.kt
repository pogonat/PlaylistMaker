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
import android.util.Log

class NetworkSearchImpl: NetworkSearch {

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(NetworkSearchItunesApi::class.java)

    override fun searchTracks(searchInput: String, callback:(searchResult: SearchTrackResult) -> Unit) {
        val resultsTracksList = ArrayList<Track>()
//        val result: SearchTrackResult =
//            SearchTrackResult(SearchResultStatus.NOTHING_FOUND, ArrayList<Track>())

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
//                            result.searchResultStatus = SearchResultStatus.NOTHING_FOUND
//                            result.resultTrackList = resultsTracksList
                            callback.invoke(SearchTrackResult(SearchResultStatus.NOTHING_FOUND, resultsTracksList))
                        } else {
//                            result.searchResultStatus = SearchResultStatus.SUCCESS
//                            result.resultTrackList = resultsTracksList
                            callback.invoke(SearchTrackResult(SearchResultStatus.SUCCESS, resultsTracksList))
//                            Log.e("network", result.resultTrackList.toString())
                        }
                    } else {
//                        result.searchResultStatus = SearchResultStatus.ERROR_CONNECTION
//                        result.resultTrackList = resultsTracksList
                        callback.invoke(SearchTrackResult(SearchResultStatus.ERROR_CONNECTION, resultsTracksList))
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
//                    result.searchResultStatus = SearchResultStatus.ERROR_CONNECTION
//                    result.resultTrackList = resultsTracksList
                    callback.invoke(SearchTrackResult(SearchResultStatus.ERROR_CONNECTION, resultsTracksList))
                }
            })
//        Log.e("network_2", result.resultTrackList.toString())
//        return result
    }

    override fun searchTrackById(
        trackId: String,
        callback: (searchResult: LookupTrackResults)-> Unit
    ){

//        val results: LookupTrackResults = LookupTrackResults(SearchResultStatus.ERROR_CONNECTION, null)
        Log.e("TRACK REPOSITORY 1", "WE ARE HERE")
        itunesService
            .getTrackDetails(trackId)
            .enqueue(object : Callback<TracksResponse>{


                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    Log.e("TRACK REPOSITORY", "WE ARE HERE")
                    if ((response.code() == 200)) {
                        val searchResultsDto = response.body()?.searchResults!!
                        if (searchResultsDto.size == 1) {
                            val trackInfo = searchResultsDto[0].toDomain()
                            Log.e("TRACK REPOSITORY 2", " WE ARE HERE ${trackInfo.toString()}")
                            callback.invoke(LookupTrackResults(SearchResultStatus.SUCCESS, trackInfo))
                        } else {
                            Log.e("TRACK REPOSITORY 2", " WE ARE HERE  NOT EQUAL 1")
                            callback.invoke(LookupTrackResults(SearchResultStatus.ERROR_CONNECTION, null))
                        }
//                        results.searchResultStatus = SearchResultStatus.SUCCESS
//                        results.resultTrackInfo = response.body()?.searchResults!!.toDomain()

//                        val trackInfo = response.body()?.searchResults!!.toDomain()
//                        Log.e("TRACK REPOSITORY 2", response.body()?.searchResults!!.toDomain().toString())

                    } else {
//                        results.searchResultStatus = SearchResultStatus.ERROR_CONNECTION
//                        results.resultTrackInfo = response.body()?.searchResults!!.toDomain()
                        Log.e("TRACK REPOSITORY 3", " WE ARE HERE FAILURE")
                        callback.invoke(LookupTrackResults(SearchResultStatus.ERROR_CONNECTION, null))
                    }

                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
//                    results.searchResultStatus = SearchResultStatus.ERROR_CONNECTION
//                    results.resultTrackInfo = null
                    Log.e("TRACK REPOSITORY 4", "WE ARE HERE ${call.toString()} ${t.toString()}")
                    callback.invoke(LookupTrackResults(SearchResultStatus.ERROR_CONNECTION, null))
                }
            })
//        return results
    }
}
