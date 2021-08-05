package com.andreromano.movies.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.andreromano.movies.common.Millis
import com.andreromano.movies.common.Minutes
import com.andreromano.movies.common.MovieId

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["movie_id"],
            childColumns = ["moviedetails_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
)
data class MovieDetailsEntity(
    @PrimaryKey
    @ColumnInfo(name = "moviedetails_id") val id: MovieId,

    @ColumnInfo(name = "moviedetails_homepage") val homepage: String?,
    @ColumnInfo(name = "moviedetails_genres") val genres: List<String>,
    @ColumnInfo(name = "moviedetails_runtime") val runtime: Minutes?,

    @ColumnInfo(name = "moviedetails_updatedAt") val updatedAt: Millis,
)