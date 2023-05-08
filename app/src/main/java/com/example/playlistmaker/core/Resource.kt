package com.example.playlistmaker.core

import com.example.playlistmaker.domain.models.SearchResultStatus

sealed class Resource<T>(val data: T? = null, val status: SearchResultStatus? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(status: SearchResultStatus, data: T? = null): Resource<T>(data, status)
}