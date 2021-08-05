package com.andreromano.movies.ui.screens.movie_list

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.andreromano.movies.domain.model.Movie
import com.andreromano.movies.domain.model.MoviesFilter
import com.andreromano.movies.ui.utils.Event

interface MovieListContract {

    interface ViewModel : ViewState, ViewActions

    interface ViewState {

        val navigation: LiveData<Event<ViewInstructions>>

        val selectedFilter: LiveData<MoviesFilter>
        val results: LiveData<PagingData<Movie>>
    }

    interface ViewActions {

        fun filterChanged(filter: MoviesFilter)

        fun movieClicked(movie: Movie)
    }

    sealed class ViewInstructions {
        data class NavigateToDetails(val movie: Movie) : ViewInstructions()
    }
}