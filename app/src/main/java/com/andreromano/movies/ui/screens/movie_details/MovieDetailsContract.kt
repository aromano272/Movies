package com.andreromano.movies.ui.screens.movie_details

import androidx.lifecycle.LiveData
import com.andreromano.movies.common.ErrorKt
import com.andreromano.movies.domain.model.Movie
import com.andreromano.movies.ui.utils.Event
import com.andreromano.movies.ui.utils.ListState

interface MovieDetailsContract {

    interface ViewModel : ViewState, ViewActions

    interface ViewState {

        val error: LiveData<Event<ErrorKt>>
        val navigation: LiveData<Event<ViewInstructions>>

        val movieState: LiveData<MovieState>

        val movieRecommendationsState: LiveData<ListState<Movie>>

        val isFavorite: LiveData<Boolean>
        val isFavoriteLoading: LiveData<Boolean>

        sealed class MovieState {
            data class Loading(val movie: Movie?) : MovieState()
            data class Result(val movie: Movie) : MovieState()
        }
    }

    interface ViewActions {
        fun movieRecommendationClicked(movie: Movie)
        fun favoriteClicked()
    }

    sealed class ViewInstructions {
        data class NavigateToMovieDetails(val movie: Movie) : ViewInstructions()
    }
}
