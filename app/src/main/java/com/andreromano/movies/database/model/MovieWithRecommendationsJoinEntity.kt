package com.andreromano.movies.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.andreromano.movies.common.MovieId

@Entity(
    primaryKeys = ["moviewithrecommendationsjoin_movie_id", "moviewithrecommendationsjoin_recommendation_movie_id"],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["movie_id"],
            childColumns = ["moviewithrecommendationsjoin_movie_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["movie_id"],
            childColumns = ["moviewithrecommendationsjoin_recommendation_movie_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MovieWithRecommendationsJoinEntity(
    @ColumnInfo(name = "moviewithrecommendationsjoin_movie_id") val movie_id: MovieId,
    @ColumnInfo(name = "moviewithrecommendationsjoin_recommendation_movie_id") val recommendation_movie_id: MovieId,
)