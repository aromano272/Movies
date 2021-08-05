package com.andreromano.movies.ui.screens.movie_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andreromano.movies.data.MoviesRepository
import com.andreromano.movies.domain.model.Movie
import com.andreromano.movies.domain.model.MoviesFilter
import com.andreromano.movies.ui.extensions.launch
import com.andreromano.movies.ui.utils.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class MovieListViewModel(
    private val moviesRepository: MoviesRepository,
) : ViewModel(), MovieListContract.ViewModel {

    private val _navigation = MutableSharedFlow<MovieListContract.ViewInstructions>()
    override val navigation: LiveData<Event<MovieListContract.ViewInstructions>> = _navigation.map { Event(it) }.asLiveData()

    private val _selectedFilter = MutableStateFlow(MoviesFilter.POPULAR)
    override val selectedFilter: LiveData<MoviesFilter> = _selectedFilter.asLiveData()

    private val _results = _selectedFilter.flatMapLatest { filter ->
        moviesRepository.getMoviesPaged(filter)
    }.cachedIn(viewModelScope)
    override val results: LiveData<PagingData<Movie>> = _results.asLiveData()

    override fun filterChanged(filter: MoviesFilter) {
        _selectedFilter.value = filter
    }

    override fun movieClicked(movie: Movie) = launch {
        _navigation.emit(MovieListContract.ViewInstructions.NavigateToDetails(movie))
    }
}