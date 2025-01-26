package com.plfdev.to_do_list.core.data.networking


import android.util.Log
import com.plfdev.to_do_list.core.domain.util.Either
import com.plfdev.to_do_list.core.domain.util.DataError.NetworkError
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> safeCall(
    execute: () -> HttpResponse
): Either<T> {
    val response = try {
        execute()
    } catch (e: UnresolvedAddressException) {
        logError("No Internet error: ", e)
        return Either.error(
            NetworkError.NO_INTERNET (
                code = null,
                e.message.orEmpty()
            )
        )
    } catch (e: SerializationException) {
        logError("Serialization error: ", e)
        return Either.error(
            NetworkError.SERIALIZATION(
                code = null,
                message = e.message.orEmpty()
            )
        )
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        logError("Unknown error: ", e)
        return Either.error(
            NetworkError.UNKNOWN(
                code = null,
                message = e.message.orEmpty()
            )
        )
    }
    return responseToResult(response)
}

// Função para logar erros (pode ser adaptada para usar uma ferramenta de monitoramento)
fun logError(title: String, exception: Exception) {
    Log.e(title, exception.message.orEmpty())
}