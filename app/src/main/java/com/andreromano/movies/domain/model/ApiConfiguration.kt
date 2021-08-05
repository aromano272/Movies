package com.andreromano.movies.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiConfiguration(
    val images: Images,
    val changeKeys: List<String>,
) {
    @JsonClass(generateAdapter = true)
    data class Images(
        val baseUrl: String,               // "http://image.tmdb.org/t/p/"
        val secureBaseUrl: String,         // "https://image.tmdb.org/t/p/"
        val backdropSizes: List<String>,   // ["w300","w780","w1280","original"]
        val logoSizes: List<String>,       // ["w45","w92","w154","w185","w300","w500","original"]
        val posterSizes: List<String>,     // ["w92","w154","w185","w342","w500","w780","original"]
        val profileSizes: List<String>,    // ["w45","w185","h632","original"]
        val stillSizes: List<String>,      // ["w45","w185","h632","original"]
    )
}