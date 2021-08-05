package com.andreromano.movies.database.model

import androidx.room.*
import com.andreromano.movies.common.MovieId

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["movie_id"],
            childColumns = ["nowplayingmovieorder_movie_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["nowplayingmovieorder_movie_id"], unique = true)],
)
data class NowPlayingMovieOrderEntity(
    @ColumnInfo(name = "nowplayingmovieorder_movie_id") val movie_id: Int?,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "nowplayingmovieorder_order")
    var order: MovieId = 0
}