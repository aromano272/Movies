package com.andreromano.movies.data.mapper

import com.andreromano.movies.common.DateIso8601
import com.andreromano.movies.database.model.ApiConfigurationEntity
import com.andreromano.movies.database.model.MovieDetailsEntity
import com.andreromano.movies.database.model.MovieDetailsWithRecommendationsQuery
import com.andreromano.movies.database.model.MovieQuery
import com.andreromano.movies.domain.model.ApiConfiguration
import com.andreromano.movies.domain.model.Movie
import org.joda.time.LocalDate

fun MovieQuery.toDomain(): Movie = Movie(
    id = movie.id,
    adult = movie.adult,
    backdropPath = movie.backdropPath,
    originalLanguage = movie.originalLanguage,
    originalTitle = movie.originalTitle,
    overview = movie.overview,
    popularity = movie.popularity,
    posterPath = movie.posterPath,
    releaseDate = movie.releaseDate?.toLocalDate(),
    title = movie.title,
    video = movie.video,
    voteAverage = movie.voteAverage,
    voteCount = movie.voteCount,
    details = details?.toDomain(),
    recommendations = null,
)

fun MovieDetailsWithRecommendationsQuery.toDomain(): Movie = movie.toDomain().copy(
    recommendations = recommendations.map { it.toDomain() }
)

fun MovieDetailsEntity.toDomain(): Movie.Details = Movie.Details(
    homepage = homepage,
    genres = genres,
    runtime = runtime,
)

fun ApiConfigurationEntity.toDomain(): ApiConfiguration = ApiConfiguration(
    images = images.toDomain(),
    changeKeys = change_keys,
)

private fun ApiConfigurationEntity.Images.toDomain(): ApiConfiguration.Images = ApiConfiguration.Images(
    baseUrl = base_url,
    secureBaseUrl = secure_base_url,
    backdropSizes = backdrop_sizes,
    logoSizes = logo_sizes,
    posterSizes = poster_sizes,
    profileSizes = profile_sizes,
    stillSizes = still_sizes,
)

private fun DateIso8601.toLocalDate(): LocalDate? = try {
    LocalDate.parse(this)
} catch (ex: Exception) {
    null
}