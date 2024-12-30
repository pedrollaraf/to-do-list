package com.plfdev.to_do_list.core.data.networking

import com.plfdev.to_do_list.core.domain.util.DataError.NetworkError

sealed class RequestStates<out T> {
    data class Success<out T>(val data: T) : RequestStates<T>()
    data object Loading : RequestStates<Nothing>()
    data object Initial : RequestStates<Nothing>()
    data class Failure(val error: NetworkError?) : RequestStates<Nothing>()
}