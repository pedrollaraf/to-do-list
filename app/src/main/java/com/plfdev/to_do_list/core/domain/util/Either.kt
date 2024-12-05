package com.plfdev.to_do_list.core.domain.util

data class Either<out T>(val status: Status, val data: T?, val error: DataError?) {

    enum class Status {
        SUCCESS,
        LOADING,
        ERROR,
        EMPTY
    }

    var isSuccess:Boolean = error == null
    val value:Any? = data

    companion object {
        fun <T> success(data: T): Either<T> {
            return Either(Status.SUCCESS, data, null)
        }

        fun <T> emptyResult(): Either<T> {
            return Either(Status.EMPTY, null, null)
        }

        fun <T> loading(): Either<T> {
            return Either(Status.LOADING, null, null)
        }

        fun <T> error(error: DataError): Either<T> {
            return Either(Status.ERROR, null, error)
        }

        fun <T> Either<T>.onSuccess(result:(T) -> Unit):Either<T> {
            if(status == Status.SUCCESS){ this.data?.let(result) }
            return this
        }

        fun <T> Either<T>.onFailure(result:(DataError) -> Unit):Either<T> {
            if(status == Status.ERROR){   this.error?.let { result(it) } }
            return this
        }

        fun <T> Either<T>.onLoading(result:() -> Unit):Either<T> {
            if(status == Status.LOADING){ result() }
            return this
        }
    }
}

fun Either<*>.get(onSuccess: (Any) -> Unit, onFailure: (DataError) -> Unit){
    this.data?.let {
        onSuccess(it)
    }
    this.error?.let {
        onFailure(it)
    }
}

suspend fun <T> getResult(call: suspend () -> Either<T?>): Either<T> {
    try {
        val response = call()
        return if(response.data != null){
            Either.success(response.data)
        }else{
            Either.error(DataError.NetworkError.SERIALIZATION)
        }
    } catch (e: Exception) {
        return error(e)
    }
}