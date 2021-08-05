package com.andreromano.movies.database

import androidx.room.Dao
import com.andreromano.movies.database.model.MovieDetailsEntity

@Dao
abstract class MovieDetailsDao : BaseDao<MovieDetailsEntity>()