package com.andreromano.movies.data

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.andreromano.movies.common.ResultKt
import com.andreromano.movies.data.mapper.toEntity
import com.andreromano.movies.data.mapper.toNetwork
import com.andreromano.movies.database.MovieOrderByFilterDao
import com.andreromano.movies.database.MoviesDao
import com.andreromano.movies.database.PreferenceStorage
import com.andreromano.movies.database.TransactionRunner
import com.andreromano.movies.database.model.*
import com.andreromano.movies.domain.model.MoviesFilter
import com.andreromano.movies.network.Api

class GetMoviesRemoteMediator(
    private val filter: MoviesFilter,
    private val moviesDao: MoviesDao,
    private val movieOrderByFilterDao: MovieOrderByFilterDao,
    private val preferenceStorage: PreferenceStorage,
    private val transactionRunner: TransactionRunner,
    private val api: Api,
) : RemoteMediator<Int, MovieQuery>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, MovieQuery>): MediatorResult {
        val loadKey = when (loadType) {
            LoadType.REFRESH -> 1
            // Only paging forward
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> when (filter) {
                MoviesFilter.NOW_PLAYING -> preferenceStorage.nowPlayingMoviesPagingNextRemoteKey
                MoviesFilter.POPULAR -> preferenceStorage.popularMoviesPagingNextRemoteKey
                MoviesFilter.TOP_RATED -> preferenceStorage.topRatedMoviesPagingNextRemoteKey
                MoviesFilter.UPCOMING -> preferenceStorage.upcomingMoviesPagingNextRemoteKey
            } ?: return MediatorResult.Success(endOfPaginationReached = true)
        }

        return when (val result = api.getMovies(filter.toNetwork(), loadKey)) {
            is ResultKt.Success -> {
                val nextRemoteKey = if (result.data.page < result.data.total_pages) result.data.page + 1 else null

                transactionRunner.run {
                    val now = System.currentTimeMillis()
                    val movieEntities = result.data.results.map { it.toEntity(now) }
                    val movieEntitiesIds = movieEntities.map { it.id }

                    moviesDao.upsert(movieEntities)

                    if (loadType == LoadType.REFRESH) {
                        when (filter) {
                            MoviesFilter.NOW_PLAYING -> movieOrderByFilterDao.replaceAllNowPlaying(movieEntitiesIds.map { NowPlayingMovieOrderEntity(it) })
                            MoviesFilter.POPULAR -> movieOrderByFilterDao.replaceAllPopular(movieEntitiesIds.map { PopularMovieOrderEntity(it) })
                            MoviesFilter.TOP_RATED -> movieOrderByFilterDao.replaceAllTopRated(movieEntitiesIds.map { TopRatedMovieOrderEntity(it) })
                            MoviesFilter.UPCOMING -> movieOrderByFilterDao.replaceAllUpcoming(movieEntitiesIds.map { UpcomingMovieOrderEntity(it) })
                        }
                    } else {
                        when (filter) {
                            MoviesFilter.NOW_PLAYING -> movieOrderByFilterDao.insertOrIgnoreNowPlaying(movieEntitiesIds.map { NowPlayingMovieOrderEntity(it) })
                            MoviesFilter.POPULAR -> movieOrderByFilterDao.insertOrIgnorePopular(movieEntitiesIds.map { PopularMovieOrderEntity(it) })
                            MoviesFilter.TOP_RATED -> movieOrderByFilterDao.insertOrIgnoreTopRated(movieEntitiesIds.map { TopRatedMovieOrderEntity(it) })
                            MoviesFilter.UPCOMING -> movieOrderByFilterDao.insertOrIgnoreUpcoming(movieEntitiesIds.map { UpcomingMovieOrderEntity(it) })
                        }
                    }
                }

                when (filter) {
                    MoviesFilter.NOW_PLAYING -> preferenceStorage.nowPlayingMoviesPagingNextRemoteKey = nextRemoteKey
                    MoviesFilter.POPULAR -> preferenceStorage.popularMoviesPagingNextRemoteKey = nextRemoteKey
                    MoviesFilter.TOP_RATED -> preferenceStorage.topRatedMoviesPagingNextRemoteKey = nextRemoteKey
                    MoviesFilter.UPCOMING -> preferenceStorage.upcomingMoviesPagingNextRemoteKey = nextRemoteKey
                }

                MediatorResult.Success(nextRemoteKey == null)
            }
            is ResultKt.Failure -> MediatorResult.Error(result.error)
        }
    }
}