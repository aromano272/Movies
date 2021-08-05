package com.andreromano.movies.ui.screens.movie_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.andreromano.movies.common.ErrorKt
import com.andreromano.movies.common.Resource
import com.andreromano.movies.common.filterResourceFailure
import com.andreromano.movies.common.onEachResourceFailure
import com.andreromano.movies.data.MoviesRepository
import com.andreromano.movies.domain.model.Movie
import com.andreromano.movies.ui.extensions.launch
import com.andreromano.movies.ui.extensions.shareHere
import com.andreromano.movies.ui.utils.Event
import com.andreromano.movies.ui.utils.ListState
import kotlinx.coroutines.flow.*

class MovieDetailsViewModel(
    private val initialMovie: Movie,
    moviesRepository: MoviesRepository,
) : ViewModel(), MovieDetailsContract.ViewModel {

    private val _error = MutableSharedFlow<ErrorKt>()
    override val error: LiveData<Event<ErrorKt>> = _error.map { Event(it) }.asLiveData()

    private val _navigation = MutableSharedFlow<MovieDetailsContract.ViewInstructions>()
    override val navigation: LiveData<Event<MovieDetailsContract.ViewInstructions>> = _navigation.map { Event(it) }.asLiveData()

    private val getMovieDetailsResult = moviesRepository.getMovieDetails(initialMovie.id).shareHere(this)

    override val movieState: LiveData<MovieDetailsContract.ViewState.MovieState> =
        getMovieDetailsResult
            .map { result ->
                when (result) {
                    is Resource.Loading -> MovieDetailsContract.ViewState.MovieState.Loading(result.data)
                    is Resource.Success -> MovieDetailsContract.ViewState.MovieState.Result(result.data)
                    is Resource.Failure -> MovieDetailsContract.ViewState.MovieState.Result(result.data ?: initialMovie)
                }
            }.asLiveData()

    override val movieRecommendationsState: LiveData<ListState<Movie>> =
        getMovieDetailsResult
            .map { result ->
                when (result) {
                    is Resource.Loading -> ListState.Loading(result.data?.recommendations)
                    is Resource.Success ->
                        if (result.data.recommendations.isNullOrEmpty()) ListState.EmptyState
                        else ListState.Results(result.data.recommendations)
                    is Resource.Failure -> ListState.Error(result.error)
                }
            }.asLiveData()

    private val isFavoriteResult = moviesRepository.isFavorite(initialMovie.id).shareHere(this)
    override val isFavorite: LiveData<Boolean> = isFavoriteResult.map { it.data ?: false }.asLiveData()

    private val changeFavoriteAction = MutableSharedFlow<Boolean>()
    private val changeFavoriteResult = changeFavoriteAction.transform { newState ->
        emit(Resource.Loading(null))
        emit(moviesRepository.changeFavorite(initialMovie.id, newState).toResource())
    }.shareHere(this)

    override val isFavoriteLoading: LiveData<Boolean> =
        combine(
            isFavoriteResult.map { it is Resource.Loading }.onStart { emit(true) },
            changeFavoriteResult.map { it is Resource.Loading }.onStart { emit(false) },
        ) { isLoadingFavorite, isChangingFavorite ->
            isLoadingFavorite || isChangingFavorite
        }.asLiveData()

    init {
        merge(
            // We're taking 1 failure only because these are backed by a NetworkBoundResource which, if it failed the first time, would emit a Failure every time the DB changed
            getMovieDetailsResult.filterResourceFailure().take(1),
            isFavoriteResult.filterResourceFailure().take(1),
            changeFavoriteResult.filterResourceFailure(),
        ).onEach { _error.emit(it) }
            .launchIn(viewModelScope)
    }

    override fun movieRecommendationClicked(movie: Movie) = launch {
        _navigation.emit(MovieDetailsContract.ViewInstructions.NavigateToMovieDetails(movie))
    }

    override fun favoriteClicked() = launch {
        if (isFavoriteLoading.value == true) return@launch

        val newState = !(isFavorite.value ?: false)
        changeFavoriteAction.emit(newState)
    }
}