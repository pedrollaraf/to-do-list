package com.plfdev.to_do_list.core.domain.util

sealed interface DataError: Error {
    enum class NetworkError: DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
        SYNC
    }

    enum class Local: DataError {
        DISK_FULL,
        UNKNOWN,
        DATABASE_ERROR,
        GET_TASKS_ERROR,
        GET_UN_SYNCED_ERROR,
        UPDATE_ERROR,
        INSERT_ERROR,
        DELETE_ERROR
    }
}