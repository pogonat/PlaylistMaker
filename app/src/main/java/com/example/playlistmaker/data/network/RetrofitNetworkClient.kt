package com.example.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.data.models.NetworkResultCode
import com.example.playlistmaker.data.models.Response
import com.example.playlistmaker.data.models.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val itunesApi: NetworkSearchItunesApi,
    private val context: Context
) : NetworkSearch {

    override suspend fun searchTracks(dto: TracksSearchRequest): Response {

        if (isConnected() == false) {
            return Response().apply { resultCode = NetworkResultCode.CONNECTION_ERROR }
        }

//        if (dto !is TracksSearchRequest) {
//            return Response().apply { resultCode = NetworkResultCode.BAD_REQUEST }
//        }

        return withContext(Dispatchers.IO) {
            try {
                val response = itunesApi.search(dto.expression)
                response.apply { resultCode = NetworkResultCode.SUCCESS }
            } catch (e: Throwable) {
                Response().apply { resultCode = NetworkResultCode.SERVER_ERROR }
            }
        }
    }

    override suspend fun searchTrackById(dto: TracksSearchRequest): Response {

        if (isConnected() == false) {
            return Response().apply { resultCode = NetworkResultCode.CONNECTION_ERROR }
        }

//        if (dto !is TracksSearchRequest) {
//            return Response().apply { resultCode = NetworkResultCode.BAD_REQUEST }
//        }

        return withContext(Dispatchers.IO) {
            try {
                val response = itunesApi.getTrackDetails(dto.expression)
                response.apply { resultCode = NetworkResultCode.SUCCESS }
            } catch (e: Throwable) {
                Response().apply { resultCode = NetworkResultCode.SERVER_ERROR }
            }
        }
    }


    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}
