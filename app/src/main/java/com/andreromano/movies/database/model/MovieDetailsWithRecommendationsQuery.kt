package com.andreromano.movies.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MovieDetailsWithRecommendationsQuery(
    @Embedded
    val movie: MovieQuery,

    @Relation(
        parentColumn = "movie_id",
        entityColumn = "movie_id",
        entity = MovieEntity::class,
        associateBy = Junction(
            value = MovieWithRecommendationsJoinEntity::class,
            parentColumn = "moviewithrecommendationsjoin_movie_id",
            entityColumn = "moviewithrecommendationsjoin_recommendation_movie_id",
        )
    )
    val recommendations: List<MovieQuery>,
)