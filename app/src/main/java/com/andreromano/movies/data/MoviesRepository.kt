package com.andreromano.movies.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.andreromano.movies.common.MovieId
import com.andreromano.movies.common.Resource
import com.andreromano.movies.common.ResultKt
import com.andreromano.movies.data.mapper.*
import com.andreromano.movies.data.utils.NetworkBoundResource
import com.andreromano.movies.database.*
import com.andreromano.movies.database.model.MovieDetailsWithRecommendationsQuery
import com.andreromano.movies.database.model.MovieFavoriteEntity
import com.andreromano.movies.domain.model.Movie
import com.andreromano.movies.domain.model.MoviesFilter
import com.andreromano.movies.network.Api
import com.andreromano.movies.network.changeFavorite
import com.andreromano.movies.network.isFavorite
import com.andreromano.movies.network.model.MovieDetailsWithRecommendationsResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MoviesRepository(
    private val api: Api,
    private val preferenceStorage: PreferenceStorage,
    private val transactionRunner: TransactionRunner,
    private val moviesDao: MoviesDao,
    private val movieOrderByFilterDao: MovieOrderByFilterDao,
    private val movieDetailsDao: MovieDetailsDao,
    private val movieWithRecommendationJoinDao: MovieWithRecommendationJoinDao,
    private val movieFavoriteDao: MovieFavoriteDao,
) {

    suspend fun getMovies(filter: MoviesFilter) =
        api.getMovies(filter.toNetwork(), 1)
            .mapData { it.results }

    fun getMoviesPaged(filter: MoviesFilter): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
        ),
        initialKey = null,
        remoteMediator = GetMoviesRemoteMediator(
            filter,
            moviesDao,
            movieOrderByFilterDao,
            preferenceStorage,
            transactionRunner,
            api,
        ),
        pagingSourceFactory = {
            when (filter) {
                MoviesFilter.NOW_PLAYING -> moviesDao.getNowPlayingPaged()
                MoviesFilter.POPULAR -> moviesDao.getPopularPaged()
                MoviesFilter.TOP_RATED -> moviesDao.getTopRatedPaged()
                MoviesFilter.UPCOMING -> moviesDao.getUpcomingPaged()
            }
        },
    ).flow.map { it.map { result -> result.toDomain() } }

    fun getMovieDetails(id: MovieId): Flow<Resource<Movie>> =
        object : NetworkBoundResource<MovieDetailsWithRecommendationsQuery?, MovieDetailsWithRecommendationsResult, Movie>() {
            override suspend fun saveCallResult(result: MovieDetailsWithRecommendationsResult) {
                val now = System.currentTimeMillis()
                transactionRunner.run {
                    moviesDao.upsert(listOf(result.toMovieEntity(now)) + result.recommendations.results.take(5).map { it.toEntity(now) })
                    movieDetailsDao.upsert(result.toDetailsEntity(now))
                    movieWithRecommendationJoinDao.upsert(result.toRecommendationsJoinEntities().take(5))
                }
            }

            override fun loadFromDb(): Flow<MovieDetailsWithRecommendationsQuery?> = moviesDao.getMovieDetailsWithRecommendations(id)

            override suspend fun mapToDomain(entity: MovieDetailsWithRecommendationsQuery?): Movie = entity!!.toDomain()

            override suspend fun createCall(): ResultKt<MovieDetailsWithRecommendationsResult> = api.getMovieDetailsWithRecommendations(id)
        }.asFlow()

    fun isFavorite(id: MovieId): Flow<Resource<Boolean>> =
        object : NetworkBoundResource<Boolean, Boolean, Boolean>() {
            override suspend fun saveCallResult(result: Boolean) {
                if (result) movieFavoriteDao.insertOrIgnore(MovieFavoriteEntity(id))
                else movieFavoriteDao.delete(MovieFavoriteEntity(id))
            }

            override fun loadFromDb(): Flow<Boolean> = movieFavoriteDao.isFavoriteFlow(id)

            override suspend fun mapToDomain(entity: Boolean): Boolean = entity

            override suspend fun createCall(): ResultKt<Boolean> =
                // current favorite is being passed in order to keep the data consistent in this demo since we're using Random.nextBoolean()
                api.isFavorite(id, movieFavoriteDao.isFavorite(id))
        }.asFlow()

    suspend fun changeFavorite(id: MovieId, newState: Boolean): ResultKt<Unit> =
        api.changeFavorite(id, newState)
            .doOnSuccess {
                if (newState) movieFavoriteDao.insertOrIgnore(MovieFavoriteEntity(id))
                else movieFavoriteDao.delete(MovieFavoriteEntity(id))
            }
}