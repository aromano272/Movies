package com.andreromano.movies.database

import androidx.room.Dao
import androidx.room.Query
import com.andreromano.movies.common.MovieId
import com.andreromano.movies.database.model.MovieFavoriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


@Dao
abstract class MovieFavoriteDao : BaseDao<MovieFavoriteEntity>() {

    @Query("SELECT * FROM moviefavoriteentity WHERE moviefavorite_movie_id = :id")
    protected abstract fun isFavoriteInternal(id: MovieId): Flow<MovieFavoriteEntity?>

    fun isFavoriteFlow(id: MovieId): Flow<Boolean> = isFavoriteInternal(id).map { it != null }

    suspend fun isFavorite(id: MovieId): Boolean = isFavoriteInternal(id).first() != null
}