package com.andreromano.movies.data.mapper

import com.andreromano.movies.common.Millis
import com.andreromano.movies.database.model.ApiConfigurationEntity
import com.andreromano.movies.database.model.MovieDetailsEntity
import com.andreromano.movies.database.model.MovieEntity
import com.andreromano.movies.database.model.MovieWithRecommendationsJoinEntity
import com.andreromano.movies.network.model.ApiConfigurationResult
import com.andreromano.movies.network.model.MovieDetailsWithRecommendationsResult
import com.andreromano.movies.network.model.MovieResult

fun MovieResult.toEntity(updatedAt: Millis): MovieEntity = MovieEntity(
    id = id,
    adult = adult,
    backdropPath = backdrop_path,
    originalLanguage = original_language,
    originalTitle = original_title,
    overview = overview,
    popularity = popularity,
    posterPath = poster_path,
    releaseDate = release_date,
    title = title,
    video = video,
    voteAverage = vote_average,
    voteCount = vote_count,
    updatedAt = updatedAt,
)

fun MovieDetailsWithRecommendationsResult.toMovieEntity(updatedAt: Millis): MovieEntity = MovieEntity(
    id = id,
    adult = adult,
    backdropPath = backdrop_path,
    originalLanguage = original_language,
    originalTitle = original_title,
    overview = overview,
    popularity = popularity,
    posterPath = poster_path,
    releaseDate = release_date,
    title = title,
    video = video,
    voteAverage = vote_average,
    voteCount = vote_count,
    updatedAt = updatedAt,
)

fun MovieDetailsWithRecommendationsResult.toDetailsEntity(updatedAt: Millis): MovieDetailsEntity = MovieDetailsEntity(
    id = id,
    homepage = homepage,
    genres = genres?.map { it.name }.orEmpty(),
    runtime = runtime,
    updatedAt = updatedAt,
)

fun MovieDetailsWithRecommendationsResult.toRecommendationsJoinEntities(): List<MovieWithRecommendationsJoinEntity> =
    recommendations.results.map { recommendation ->
        MovieWithRecommendationsJoinEntity(
            movie_id = id,
            recommendation_movie_id = recommendation.id
        )
    }

fun ApiConfigurationResult.toEntity(): ApiConfigurationEntity = ApiConfigurationEntity(
    images = images.toEntity(),
    change_keys = change_keys,
)

private fun ApiConfigurationResult.Images.toEntity(): ApiConfigurationEntity.Images = ApiConfigurationEntity.Images(
    base_url = base_url,
    secure_base_url = secure_base_url,
    backdrop_sizes = backdrop_sizes,
    logo_sizes = logo_sizes,
    poster_sizes = poster_sizes,
    profile_sizes = profile_sizes,
    still_sizes = still_sizes,
)