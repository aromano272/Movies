package com.andreromano.movies.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.andreromano.movies.common.MovieId
import com.andreromano.movies.database.model.MovieDetailsWithRecommendationsQuery
import com.andreromano.movies.database.model.MovieEntity
import com.andreromano.movies.database.model.MovieQuery
import kotlinx.coroutines.flow.Flow


@Dao
abstract class MoviesDao : BaseDao<MovieEntity>() {

    @Query("""
        SELECT MovieEntity.* FROM MovieEntity
        INNER JOIN NowPlayingMovieOrderEntity ON MovieEntity.movie_id = NowPlayingMovieOrderEntity.nowplayingmovieorder_movie_id
        ORDER BY nowplayingmovieorder_order
    """)
    abstract fun getNowPlayingPaged(): PagingSource<Int, MovieQuery>

    @Query("""
        SELECT MovieEntity.* FROM MovieEntity
        INNER JOIN PopularMovieOrderEntity ON MovieEntity.movie_id = PopularMovieOrderEntity.popularmovieorder_movie_id
        ORDER BY popularmovieorder_order
    """)
    abstract fun getPopularPaged(): PagingSource<Int, MovieQuery>

    @Query("""
        SELECT MovieEntity.* FROM MovieEntity
        INNER JOIN TopRatedMovieOrderEntity ON MovieEntity.movie_id = TopRatedMovieOrderEntity.topratedmovieorder_movie_id
        ORDER BY topratedmovieorder_order
    """)
    abstract fun getTopRatedPaged(): PagingSource<Int, MovieQuery>

    @Query("""
        SELECT MovieEntity.* FROM MovieEntity
        INNER JOIN UpcomingMovieOrderEntity ON MovieEntity.movie_id = UpcomingMovieOrderEntity.upcomingmovieorder_movie_id
        ORDER BY upcomingmovieorder_order
    """)
    abstract fun getUpcomingPaged(): PagingSource<Int, MovieQuery>

    @Query("""
        SELECT * FROM MovieEntity
        WHERE MovieEntity.movie_id = :id
    """)
    abstract fun getMovieDetailsWithRecommendations(id: MovieId): Flow<MovieDetailsWithRecommendationsQuery?>
}