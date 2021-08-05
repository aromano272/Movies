package com.andreromano.movies.data.mapper

import com.andreromano.movies.domain.model.MoviesFilter

fun MoviesFilter.toNetwork(): String = when (this) {
    MoviesFilter.NOW_PLAYING -> "now_playing"
    MoviesFilter.POPULAR -> "popular"
    MoviesFilter.TOP_RATED -> "top_rated"
    MoviesFilter.UPCOMING -> "upcoming"
}