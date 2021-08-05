package com.andreromano.movies.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andreromano.movies.common.CountryIso2
import com.andreromano.movies.common.DateIso8601
import com.andreromano.movies.common.Millis
import com.andreromano.movies.common.MovieId

@Entity
data class MovieEntity(
    @PrimaryKey
    @ColumnInfo(name = "movie_id") val id: MovieId,
    @ColumnInfo(name = "movie_adult") val adult: Boolean,
    @ColumnInfo(name = "movie_backdropPath") val backdropPath: String?,
    @ColumnInfo(name = "movie_originalLanguage") val originalLanguage: CountryIso2,
    @ColumnInfo(name = "movie_originalTitle") val originalTitle: String,
    @ColumnInfo(name = "movie_overview") val overview: String,
    @ColumnInfo(name = "movie_popularity") val popularity: Float,
    @ColumnInfo(name = "movie_posterPath") val posterPath: String?,
    @ColumnInfo(name = "movie_releaseDate") val releaseDate: DateIso8601?,
    @ColumnInfo(name = "movie_title") val title: String,
    @ColumnInfo(name = "movie_video") val video: Boolean,
    @ColumnInfo(name = "movie_voteAverage") val voteAverage: Float,
    @ColumnInfo(name = "movie_voteCount") val voteCount: Int,

    @ColumnInfo(name = "movie_updatedAt") val updatedAt: Millis,
)