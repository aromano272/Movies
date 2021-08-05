package com.andreromano.movies.data.utils

import com.andreromano.movies.common.ErrorKt
import com.andreromano.movies.common.Resource
import com.andreromano.movies.common.ResultKt
import com.andreromano.movies.common.takeUntil
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class NetworkBoundResource<EntityModel : Any?, NetworkModel : Any, DomainModel : Any>(
    private val fetchStrategy: FetchStrategy = FetchStrategy.ALWAYS,
) {

    fun asFlow(): Flow<Resource<DomainModel>> = flow {
        coroutineScope {

            emit(Resource.Loading(null))
            val shouldFetch = shouldFetch(loadFromDb().first())
            if (shouldFetch) {
                val networkResponse = MutableSharedFlow<ResultKt<NetworkModel>>(1)

                launch {
                    val response = fetchFromNetworkAndSaveCallResult()
                    networkResponse.emit(response)
                }

                emitAll(loadFromDb().takeUntil(networkResponse).mapLatest { dbValue -> Resource.Loading(mapToDomainInternal(dbValue)) })
                emitAll(
                    combine(loadFromDb(), networkResponse) { dbValue, result ->
                        when (result) {
                            is ResultKt.Success -> Resource.Success(mapToDomain(dbValue))
                            is ResultKt.Failure -> Resource.Failure(mapToDomainInternal(dbValue), result.error)
                        }
                    }
                )
            } else {
                emitAll(loadFromDb().mapLatest { dbValue ->
                    if (dbValue != null) Resource.Success(mapToDomain(dbValue)) else Resource.Failure(null, ErrorKt.NotFound)
                })
            }
        }
    }

    private suspend fun fetchFromNetworkAndSaveCallResult(): ResultKt<NetworkModel> = createCall().also {
        if (it is ResultKt.Success) saveCallResult(it.data)
    }

    protected abstract suspend fun saveCallResult(result: NetworkModel)

    protected abstract fun loadFromDb(): Flow<EntityModel>

    protected open fun shouldFetch(data: EntityModel): Boolean = when (fetchStrategy) {
        FetchStrategy.ALWAYS -> true
        FetchStrategy.NEVER -> false
        FetchStrategy.IF_LOCAL_IS_NULL -> data == null || (data is Collection<*> && data.isEmpty())
    }

    protected abstract suspend fun mapToDomain(entity: EntityModel): DomainModel

    private suspend fun mapToDomainInternal(entity: EntityModel): DomainModel? = entity?.let { mapToDomain(it) }

    protected abstract suspend fun createCall(): ResultKt<NetworkModel>
}

enum class FetchStrategy {
    ALWAYS,
    NEVER,
    IF_LOCAL_IS_NULL;
}
