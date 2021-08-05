package com.andreromano.movies.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.andreromano.movies.common.MovieId

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["movie_id"],
            childColumns = ["moviefavorite_movie_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class MovieFavoriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "moviefavorite_movie_id") val movie_id: MovieId,
)