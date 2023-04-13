package com.example.playlistmaker

import com.example.playlistmaker.search.domain.SearchResultStatus

sealed class Resource<T>(val data: T? = null, val status: SearchResultStatus? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(status: SearchResultStatus, data: T? = null): Resource<T>(data, status)
}