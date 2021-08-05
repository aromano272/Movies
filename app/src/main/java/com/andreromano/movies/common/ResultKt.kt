package com.andreromano.movies.common


sealed class ResultKt<out T> {
    data class Success<out T>(val data: T) : ResultKt<T>()
    data class Failure(val error: ErrorKt) : ResultKt<Nothing>()

    suspend fun <R> mapData(body: suspend (T) -> R): ResultKt<R> = when (this) {
        is Success -> Success(body(data))
        is Failure -> this
    }

    fun toResource(): Resource<T> = when (this) {
        is Success -> Resource.Success(this.data)
        is Failure -> Resource.Failure(null, this.error)
    }

    suspend fun doOnSuccess(body: suspend (Success<T>) -> Unit): ResultKt<T> =
        apply { if (this is Success) body(this) }

    suspend fun doOnFailure(body: suspend (Failure) -> Unit): ResultKt<T> =
        apply { if (this is Failure) body(this) }
}