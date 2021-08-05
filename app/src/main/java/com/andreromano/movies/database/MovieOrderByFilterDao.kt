package com.andreromano.movies.database

import androidx.room.*
import com.andreromano.movies.database.model.NowPlayingMovieOrderEntity
import com.andreromano.movies.database.model.PopularMovieOrderEntity
import com.andreromano.movies.database.model.TopRatedMovieOrderEntity
import com.andreromano.movies.database.model.UpcomingMovieOrderEntity

@Dao
abstract class MovieOrderByFilterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOrIgnoreNowPlaying(ordering: List<NowPlayingMovieOrderEntity>)

    @Query("DELETE FROM nowplayingmovieorderentity")
    protected abstract suspend fun deleteAllNowPlaying()

    @Transaction
    open suspend fun replaceAllNowPlaying(ordering: List<NowPlayingMovieOrderEntity>) {
        deleteAllNowPlaying()
        insertOrIgnoreNowPlaying(ordering)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOrIgnorePopular(ordering: List<PopularMovieOrderEntity>)

    @Query("DELETE FROM popularmovieorderentity")
    protected abstract suspend fun deleteAllPopular()

    @Transaction
    open suspend fun replaceAllPopular(ordering: List<PopularMovieOrderEntity>) {
        deleteAllPopular()
        insertOrIgnorePopular(ordering)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOrIgnoreTopRated(ordering: List<TopRatedMovieOrderEntity>)

    @Query("DELETE FROM topratedmovieorderentity")
    protected abstract suspend fun deleteAllTopRated()

    @Transaction
    open suspend fun replaceAllTopRated(ordering: List<TopRatedMovieOrderEntity>) {
        deleteAllTopRated()
        insertOrIgnoreTopRated(ordering)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOrIgnoreUpcoming(ordering: List<UpcomingMovieOrderEntity>)

    @Query("DELETE FROM upcomingmovieorderentity")
    protected abstract suspend fun deleteAllUpcoming()

    @Transaction
    open suspend fun replaceAllUpcoming(ordering: List<UpcomingMovieOrderEntity>) {
        deleteAllUpcoming()
        insertOrIgnoreUpcoming(ordering)
    }
}