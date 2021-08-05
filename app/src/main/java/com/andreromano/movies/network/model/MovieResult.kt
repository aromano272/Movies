package com.andreromano.movies.network.model

import com.andreromano.movies.common.CountryIso2
import com.andreromano.movies.common.DateIso8601
import com.andreromano.movies.common.GenreId
import com.andreromano.movies.common.MovieId
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieResult(
    val id: MovieId,                    // 379686
    val adult: Boolean,                 // false
    val backdrop_path: String?,         // "/8s4h9friP6Ci3adRGahHARVd76E.jpg"
    val genre_ids: List<GenreId>,       // [16, 35, 10751, 878]
    val original_language: CountryIso2, // "en"
    val original_title: String,         // "Space Jam: A New Legacy"
    val overview: String,               // "When LeBron and his young son Dom are trapped in a digital space by a rogue A.I., LeBron must get them home safe by leading Bugs, Lola Bunny and the whole gang of notoriously undisciplined Looney Tunes to victory over the A.I.'s digitized champions on the court. It's Tunes versus Goons in the highest-stakes challenge of his life."
    val popularity: Float,              // 5712.919
    val poster_path: String?,           // "/5bFK5d3mVTAvBCXi5NPWH0tYjKl.jpg"
    val release_date: DateIso8601?,       // "2021-07-18"
    val title: String,                  // "Space Jam: A New Legacy"
    val video: Boolean,                 // false
    val vote_average: Float,            // 7.8
    val vote_count: Int,                // 1263
)














