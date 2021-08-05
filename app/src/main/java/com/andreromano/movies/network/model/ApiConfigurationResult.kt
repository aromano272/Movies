package com.andreromano.movies.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiConfigurationResult(
    val images: Images,
    val change_keys: List<String>,
) {
    @JsonClass(generateAdapter = true)
    data class Images(
        val base_url: String,               // "http://image.tmdb.org/t/p/"
        val secure_base_url: String,        // "https://image.tmdb.org/t/p/"
        val backdrop_sizes: List<String>,   // ["w300","w780","w1280","original"]
        val logo_sizes: List<String>,       // ["w45","w92","w154","w185","w300","w500","original"]
        val poster_sizes: List<String>,     // ["w92","w154","w185","w342","w500","w780","original"]
        val profile_sizes: List<String>,    // ["w45","w185","h632","original"]
        val still_sizes: List<String>,      // ["w45","w185","h632","original"]
    )
}