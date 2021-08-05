package com.andreromano.movies.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class MovieQuery(
    @Embedded
    val movie: MovieEntity,

    @Relation(parentColumn = "movie_id", entityColumn = "moviedetails_id", entity = MovieDetailsEntity::class)
    val details: MovieDetailsEntity?,
)

