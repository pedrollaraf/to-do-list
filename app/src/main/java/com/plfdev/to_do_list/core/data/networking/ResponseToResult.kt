package com.plfdev.to_do_list.core.data.networking

import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.core.domain.util.DataError.NetworkError
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> responseToResult(
    response: HttpResponse,
): Either<T> {
    return when (response.status.value) {
        in 200..299 -> handleSuccess(response)
        else -> handleError(response)
    }
}

// Função para lidar com sucesso
suspend inline fun <reified T> handleSuccess(
    response: HttpResponse
): Either<T> {
    return try {
        Either.success(response.body())
    } catch (e: NoTransformationFoundException) {
        Either.error(
            NetworkError.SERIALIZATION(
                code = response.status.value,
                message = response.extractErrorMessage()
            )
        )
    }
}

// Função para lidar com erros
suspend fun handleError(
    response: HttpResponse
): Either<Nothing> {
    val statusCode = response.status.value
    val errorMessage = response.extractErrorMessage()

    val networkError = when (statusCode) {
        400 -> NetworkError.BAD_REQUEST(code = statusCode, message = errorMessage)
        404 -> NetworkError.NOT_FOUND(code = statusCode, message = errorMessage)
        408 -> NetworkError.REQUEST_TIMEOUT(code = statusCode, message = errorMessage)
        in 500..599 -> NetworkError.SERVER_ERROR(code = statusCode, message = errorMessage)
        else -> NetworkError.UNKNOWN(code = statusCode, message = errorMessage)
    }

    return Either.error(networkError)
}

// Função para extrair mensagem de erro
suspend fun HttpResponse.extractErrorMessage(): String {
    return try {
        this.body<Map<String, String>?>()?.get("message").orEmpty()
    } catch (e: Exception) {
        "" // Retorna uma string vazia se ocorrer erro ao processar o body
    }
}