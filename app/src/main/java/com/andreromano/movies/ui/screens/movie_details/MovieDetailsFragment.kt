package com.andreromano.movies.ui.screens.movie_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andreromano.movies.R
import com.andreromano.movies.common.exhaustive
import com.andreromano.movies.data.utils.GlideApp
import com.andreromano.movies.data.utils.ImageNetworkPath
import com.andreromano.movies.domain.model.Movie
import com.andreromano.movies.ui.extensions.toVisibility
import com.andreromano.movies.ui.mapper.getErrorString
import com.andreromano.movies.ui.utils.EventObserver
import com.andreromano.movies.ui.utils.ListState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movie_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {

    private val args: MovieDetailsFragmentArgs by navArgs()

    private val viewModel: MovieDetailsViewModel by viewModel { parametersOf(args.movie) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        if (args.movie != null) inflater.inflate(R.layout.fragment_movie_details, container, false)
        else null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.movie == null) return

        // If we're displaying tablet layout and the current destination is the first on the stack we hide the back button
        val shouldShowBackButton = !(resources.getBoolean(R.bool.isTablet) && findNavController().previousBackStackEntry == null)
        if (shouldShowBackButton) {
            val previousEntry = findNavController().previousBackStackEntry
            val previousMovieTitle = previousEntry?.arguments?.getParcelable<Movie>("movie")?.title
            val shouldShowExtendedFab = previousEntry?.destination?.id == R.id.movieDetails && previousMovieTitle != null

            fab_back_extended.text = previousMovieTitle
            if (shouldShowExtendedFab) fab_back_extended.extend() else fab_back_extended.hide()
        }
        fab_back_extended.toVisibility = shouldShowBackButton

        val recommendationsAdapter = MovieRecommendationsAdapter(viewModel::movieRecommendationClicked)
        rv_movie_recommendations.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rv_movie_recommendations.adapter = recommendationsAdapter

        viewModel.error.observe(viewLifecycleOwner, EventObserver {
            Snackbar.make(fab_back_extended, it.getErrorString(requireContext()), Snackbar.LENGTH_LONG).show()
        })
        viewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is MovieDetailsContract.ViewInstructions.NavigateToMovieDetails ->
                    findNavController().navigate(MovieDetailsFragmentDirections.actionMovieDetailsToMovieDetails(it.movie))
            }.exhaustive
        })
        viewModel.movieState.observe(viewLifecycleOwner) { state ->
            pb_loading.toVisibility = state is MovieDetailsContract.ViewState.MovieState.Loading

            val movie = when (state) {
                is MovieDetailsContract.ViewState.MovieState.Loading -> state.movie
                is MovieDetailsContract.ViewState.MovieState.Result -> state.movie
            }

            if (movie != null) {
                GlideApp.with(iv_poster)
                    .load(movie.posterPath?.let { ImageNetworkPath.Poster(it) })
                    .centerCrop()
                    .into(iv_poster)

                tv_title.text = movie.title
                tv_rating.text = getString(R.string.movie_details_rating, movie.voteAverage, movie.voteCount)

                tv_genres.toVisibility = !movie.details?.genres.isNullOrEmpty()
                tv_genres.text = movie.details?.genres?.joinToString()

                tv_duration.toVisibility = movie.details?.runtime != null
                tv_duration.text = movie.details?.runtime?.let { getString(R.string.movie_details_runtime, it) }

                tv_homepage.toVisibility = movie.details?.homepage != null
                tv_homepage.text = movie.details?.homepage

                tv_overview_body.text = movie.overview
            }
        }

        viewModel.movieRecommendationsState.observe(viewLifecycleOwner) { state ->
            val items: List<MovieRecommendationsAdapter.Item> = when (state) {
                is ListState.Loading ->
                    if (state.results.isNullOrEmpty()) listOf(MovieRecommendationsAdapter.Item.Loading)
                    else state.results.map { MovieRecommendationsAdapter.Item.Result(it) }
                is ListState.Results -> state.results.map { MovieRecommendationsAdapter.Item.Result(it) }
                ListState.EmptyState -> listOf(MovieRecommendationsAdapter.Item.EmptyState)
                is ListState.Error -> listOf(MovieRecommendationsAdapter.Item.Error(state.error))
            }

            recommendationsAdapter.submitList(items)
        }

        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            fab_favorite.setImageResource(
                if (isFavorite) R.drawable.ic_baseline_star_16
                else R.drawable.ic_baseline_star_outline_24
            )
        }
        viewModel.isFavoriteLoading.observe(viewLifecycleOwner) { isLoading ->
            Timber.e("LOADING $isLoading")
            fab_favorite.alpha = if (isLoading) 0.5f else 1f
        }

        fab_back_extended.setOnClickListener {
            findNavController().navigateUp()
        }
        fab_favorite.setOnClickListener {
            viewModel.favoriteClicked()
        }
    }
}