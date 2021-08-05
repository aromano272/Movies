package com.andreromano.movies.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andreromano.movies.database.mapper.RoomTypeConverters
import com.andreromano.movies.database.model.*


@Database(
    entities = [
        MovieEntity::class,
        MovieDetailsEntity::class,
        MovieWithRecommendationsJoinEntity::class,
        NowPlayingMovieOrderEntity::class,
        PopularMovieOrderEntity::class,
        TopRatedMovieOrderEntity::class,
        UpcomingMovieOrderEntity::class,
        MovieFavoriteEntity::class,
    ],
    version = 5,
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
    abstract fun movieOrderByFilterDao(): MovieOrderByFilterDao
    abstract fun movieDetailsDao(): MovieDetailsDao
    abstract fun movieWithRecommendationJoinDao(): MovieWithRecommendationJoinDao
    abstract fun movieFavoriteDao(): MovieFavoriteDao
}