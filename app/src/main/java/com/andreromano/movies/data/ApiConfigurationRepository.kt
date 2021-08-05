package com.andreromano.movies.data

import com.andreromano.movies.common.Millis
import com.andreromano.movies.common.ResultKt
import com.andreromano.movies.data.mapper.toDomain
import com.andreromano.movies.data.mapper.toEntity
import com.andreromano.movies.database.PreferenceStorage
import com.andreromano.movies.domain.model.ApiConfiguration
import com.andreromano.movies.network.Api
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ApiConfigurationRepository(
    private val api: Api,
    private val preferenceStorage: PreferenceStorage,
) {

    private var lastApiConfigurationFailure: ResultKt.Failure? = null
    private var lastApiConfigurationFetch: Millis = 0L
    private val fetchConfigurationMutex = Mutex()

    // We're using a mutex as this method is prone to be called many times concurrently by Glide
    suspend fun getConfiguration(): ResultKt<ApiConfiguration> =
        fetchConfigurationMutex.withLock {
            val now = System.currentTimeMillis()
            val deltaSync = now - preferenceStorage.lastApiConfigurationSync
            val apiConfiguration = preferenceStorage.apiConfiguration

            // Check if api config is still valid and if the user has not changed the clock
            if (apiConfiguration != null && deltaSync < API_CONFIGURATION_VALIDITY && deltaSync > 0) {
                return@withLock ResultKt.Success(apiConfiguration.toDomain())
            }

            val deltaFetch = now - lastApiConfigurationFetch
            val apiConfigurationFailure = lastApiConfigurationFailure
            // Check if last fetch failed and if we shouldn't retry
            if (apiConfigurationFailure != null && deltaFetch < API_CONFIGURATION_RETRY_DELAY) {
                return@withLock apiConfigurationFailure
            }

            return@withLock when (val result = api.getConfiguration()) {
                is ResultKt.Success -> {
                    val entity = result.data.toEntity()
                    preferenceStorage.apiConfiguration = entity
                    preferenceStorage.lastApiConfigurationSync = now
                    lastApiConfigurationFetch = now

                    ResultKt.Success(entity.toDomain())
                }
                is ResultKt.Failure -> {
                    lastApiConfigurationFailure = result
                    lastApiConfigurationFetch = now

                    result
                }
            }
        }

    companion object {
        private const val API_CONFIGURATION_RETRY_DELAY: Millis = 10 * 1000L // 10 seconds
        private const val API_CONFIGURATION_VALIDITY: Millis = 2 * 24 * 60 * 60 * 1000L // 2 days
    }
}