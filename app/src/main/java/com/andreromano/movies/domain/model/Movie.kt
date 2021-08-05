package com.andreromano.movies.domain.model

import android.os.Parcelable
import com.andreromano.movies.common.CountryIso2
import com.andreromano.movies.common.Minutes
import com.andreromano.movies.common.MovieId
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate

// NOTE: We're mixing Android code in Domain layer for brevity sake, one should have a specific parcelable UI Model.
@Parcelize
data class Movie(
    val id: MovieId,
    val adult: Boolean,
    val backdropPath: String?,
    val originalLanguage: CountryIso2,
    val originalTitle: String,
    val overview: String,
    val popularity: Float,
    val posterPath: String?,
    val releaseDate: LocalDate?,
    val title: String,
    val video: Boolean,
    val voteAverage: Float,
    val voteCount: Int,
    val details: Details?,
    val recommendations: List<Movie>?,
) : Parcelable {
    @Parcelize
    data class Details(
        val homepage: String?,
        val genres: List<String>,
        val runtime: Minutes?,
    ) : Parcelable
}
