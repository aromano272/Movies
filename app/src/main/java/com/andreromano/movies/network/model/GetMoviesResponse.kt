package com.andreromano.movies.network.model

import com.andreromano.movies.common.DateIso8601
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMoviesResponse(
    val page: Int,
    val dates: Dates?,
    val total_pages: Int,
    val total_results: Int,
    val results: List<MovieResult>,
) {
    @JsonClass(generateAdapter = true)
    data class Dates(
        val maximum: DateIso8601,
        val minimum: DateIso8601,
    )
}