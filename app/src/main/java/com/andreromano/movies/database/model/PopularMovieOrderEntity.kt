package com.andreromano.movies.database.model

import androidx.room.*
import com.andreromano.movies.common.MovieId

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["movie_id"],
            childColumns = ["popularmovieorder_movie_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["popularmovieorder_movie_id"], unique = true)],
)
data class PopularMovieOrderEntity(
    @ColumnInfo(name = "popularmovieorder_movie_id") val movie_id: Int?,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "popularmovieorder_order")
    var order: MovieId = 0
}