package com.andreromano.movies.common

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


fun <T> Flow<Resource<T>>.filterResourceSuccess(): Flow<T> =
    mapNotNull { if (it is Resource.Success) it.data else null }

fun <T> Flow<Resource<T>>.filterResourceFailure(): Flow<ErrorKt> =
    mapNotNull { if (it is Resource.Failure) it.error else null }

fun <T> Flow<Resource<T>>.filterResourceLoading(): Flow<Unit> =
    mapNotNull { if (it is Resource.Loading) Unit else null }

fun <T> Flow<Resource<T>>.toResourceCompletable(): Flow<Resource<Unit>> =
    mapResourceData { Unit }

fun <X, Y> Flow<Resource<X>>.mapResourceData(body: suspend (X) -> Y): Flow<Resource<Y>> =
    map { it.mapData(body) }

fun <T> Flow<ResultKt<T>>.filterResultSuccess(): Flow<T> =
    mapNotNull { if (it is ResultKt.Success) it.data else null }

fun <T> Flow<ResultKt<T>>.filterResultFailure(): Flow<ErrorKt> =
    mapNotNull { if (it is ResultKt.Failure) it.error else null }

fun <T> Flow<ResultKt<T>>.toResultCompletable(): Flow<ResultKt<Unit>> =
    mapResultData { Unit }

fun <X, Y> Flow<ResultKt<X>>.mapResultData(body: suspend (X) -> Y): Flow<ResultKt<Y>> =
    map { it.mapData(body) }

fun <T> Flow<Resource<T>>.onEachResourceSuccess(body: suspend (T) -> Unit): Flow<Resource<T>> =
    onEach { if (it is Resource.Success) body(it.data) }

fun <T> Flow<Resource<T>>.onEachResourceFailure(body: suspend (ErrorKt) -> Unit): Flow<Resource<T>> =
    onEach { if (it is Resource.Failure) body(it.error) }

fun <T> Flow<ResultKt<T>>.onEachResultSuccess(body: suspend (ResultKt.Success<T>) -> Unit): Flow<ResultKt<T>> =
    onEach { if (it is ResultKt.Success) body(it) }

fun <T> Flow<ResultKt<T>>.onEachResultFailure(body: suspend (ResultKt.Failure) -> Unit): Flow<ResultKt<T>> =
    onEach { if (it is ResultKt.Failure) body(it) }

fun <T> Flow<Resource<T>>.toResult(): Flow<ResultKt<T>> =
    mapNotNull {
        when (it) {
            is Resource.Loading -> null
            is Resource.Success -> ResultKt.Success(it.data)
            is Resource.Failure -> ResultKt.Failure(it.error)
        }
    }

fun <T> Flow<T>.conditionalDebounce(
    timeout: Millis = 1000L,
    shouldDebounce: (T) -> Boolean,
): Flow<T> = flatMapLatest {
    if (shouldDebounce(it))
        debounce(timeout)
    else
        flowOf(it)
}

fun <T> Flow<T>.takeUntil(signal: Flow<*>): Flow<T> = flow {
    try {
        coroutineScope {
            launch {
                signal.take(1).collect()
                this@coroutineScope.cancel()
            }

            collect {
                emit(it)
            }
        }
    } catch (e: CancellationException) {
        //ignore
    }
}
