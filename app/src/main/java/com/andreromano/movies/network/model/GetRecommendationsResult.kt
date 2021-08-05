package com.andreromano.movies.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetRecommendationsResult(
    val page: Int,
    val total_pages: Int,
    val total_results: Int,
    val results: List<MovieResult>,
)