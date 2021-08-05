package com.andreromano.movies.network

import com.andreromano.movies.common.MovieId
import com.andreromano.movies.common.ResultKt
import com.andreromano.movies.network.model.ApiConfigurationResult
import com.andreromano.movies.network.model.GetMoviesResponse
import com.andreromano.movies.network.model.MovieDetailsWithRecommendationsResult
import kotlinx.coroutines.delay
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("movie/{filter}")
    suspend fun getMovies(
        @Path("filter") filter: String,
        @Query("page") page: Int,
    ): ResultKt<GetMoviesResponse>

    @GET("configuration")
    suspend fun getConfiguration(): ResultKt<ApiConfigurationResult>

    @GET("movie/{id}")
    suspend fun getMovieDetailsWithRecommendations(
        @Path("id") id: MovieId,
        @Query("append_to_response") appendToResponse: String = "recommendations",
    ): ResultKt<MovieDetailsWithRecommendationsResult>
}

// These are extension functions because Retrofit requires all methods in the interface to be annotated and these methods shouldn't be used by retrofit

// currentValue is being passed in order to keep the data consistent in this demo
suspend fun Api.isFavorite(id: MovieId, currentValue: Boolean): ResultKt<Boolean> {
    delay(1000)
    return ResultKt.Success(currentValue)
}

suspend fun Api.changeFavorite(id: MovieId, newState: Boolean): ResultKt<Unit> {
    delay(1000)
    return ResultKt.Success(Unit)
}