package com.andreromano.movies.database

import androidx.room.Dao
import com.andreromano.movies.database.model.MovieWithRecommendationsJoinEntity

@Dao
abstract class MovieWithRecommendationJoinDao : BaseDao<MovieWithRecommendationsJoinEntity>()