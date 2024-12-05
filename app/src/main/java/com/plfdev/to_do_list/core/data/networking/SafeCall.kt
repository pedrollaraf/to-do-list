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
        return Either.error(NetworkError.NO_INTERNET)
    } catch (e: SerializationException) {
        Log.d("BRATISLAV:", e.toString())
        return Either.error(NetworkError.SERIALIZATION)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        return Either.error(NetworkError.UNKNOWN)
    }
    return responseToResult(response)
}