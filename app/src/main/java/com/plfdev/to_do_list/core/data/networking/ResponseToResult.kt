package com.plfdev.to_do_list.core.data.networking

import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.core.domain.util.DataError.NetworkError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline  fun <reified T> responseToResult(
    response: HttpResponse,
): Either<T> {
    return when(response.status.value) {
        in 200.. 299 -> {
            try {
                Either.success(response.body<T>())
            } catch (e: NoTransformationFoundException) {
                Either.error(NetworkError.SERIALIZATION)
            }
        }
        408 -> Either.error(NetworkError.REQUEST_TIMEOUT)
        429 -> Either.error(NetworkError.TOO_MANY_REQUESTS)
        in 500.. 599 -> Either.error(NetworkError.SERVER_ERROR)
        else -> Either.error(NetworkError.UNKNOWN)
    }
}