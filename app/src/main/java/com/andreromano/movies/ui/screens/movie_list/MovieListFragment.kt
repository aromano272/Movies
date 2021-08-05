package com.andreromano.movies.ui.screens.movie_list

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andreromano.movies.NavMainDirections
import com.andreromano.movies.R
import com.andreromano.movies.common.ErrorKt
import com.andreromano.movies.common.exhaustive
import com.andreromano.movies.domain.model.MoviesFilter
import com.andreromano.movies.ui.mapper.getErrorString
import com.andreromano.movies.ui.screens.PagingLoadStateAdapter
import com.andreromano.movies.ui.utils.EventObserver
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_movie_list.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MovieListFragment : Fragment(R.layout.fragment_movie_list) {

    private val viewModel: MovieListViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MovieListAdapter(viewModel::movieClicked)
        rv_movies.layoutManager = LinearLayoutManager(requireContext())
        rv_movies.adapter = adapter.withLoadStateFooter(PagingLoadStateAdapter(adapter::retry))
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        var refreshPagingErrorSnackbar: Snackbar? = null
        var wasRefreshing = false
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow
                .map { it.refresh }
                .distinctUntilChanged()
                .collect { refreshState ->
                    Timber.e(refreshState.toString())
                    val isRefreshing = refreshState is LoadState.Loading
                    if (!isRefreshing && wasRefreshing) {
                        // Not the prettiest but it works, sometimes when there are no cached items, when the first page is fetched the loading state view of the 2nd page fetching
                        // is faster to diff than the items of the 1st page, this will keep the loading state view on the screen while Inserting the 1st page on top, leaving the scroll in the middle
                        // Another solution is to add a dummy 1dp(or any non-zero value) view to the 0th position, using concatadapter,
                        // this would cause the 'focused item' by the recyclerview to be this dummy view leaving the scroll on top as expected
                        Handler(Looper.getMainLooper()).postDelayed({
                            rv_movies.scrollToPosition(0)
                        }, 100)
                    }
                    wasRefreshing = isRefreshing

                    swipe_refresh.isRefreshing = isRefreshing

                    if (refreshState is LoadState.Error) {
                        Timber.e("BOOM SHAKA true")
                        val message = (refreshState.error as? ErrorKt)?.getErrorString(requireContext()) ?: refreshState.error.message
                        // Because dismiss is async, if there's a non-Error LoadState immediately followed by an Error the snackbar would not be shown,
                        // therefore we need to create a new instance rather than reusing the same
                        refreshPagingErrorSnackbar?.dismiss()
                        refreshPagingErrorSnackbar = Snackbar.make(rv_movies, message.orEmpty(), Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.paging_load_state_retry_button) {
                                adapter.retry()
                            }.also {
                                it.show()
                            }
                    } else {
                        Timber.e("BOOM SHAKA false")
                        refreshPagingErrorSnackbar?.dismiss()
                        refreshPagingErrorSnackbar = null
                    }
                }
        }

        viewModel.navigation.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is MovieListContract.ViewInstructions.NavigateToDetails -> {
                    if (resources.getBoolean(R.bool.isTablet))
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(NavMainDirections.actionGlobalToMovieDetails(it.movie))
                    else findNavController().navigate(MovieListFragmentDirections.actionMovieListToMovieDetails(it.movie))
                }
            }.exhaustive
        })

        viewModel.results.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        viewModel.selectedFilter.observe(viewLifecycleOwner) {
            tv_toolbar_title.setText(when (it) {
                MoviesFilter.NOW_PLAYING -> R.string.movie_filter_now_playing
                MoviesFilter.POPULAR -> R.string.movie_filter_popular
                MoviesFilter.TOP_RATED -> R.string.movie_filter_top_rated
                MoviesFilter.UPCOMING -> R.string.movie_filter_upcoming
            })
        }

        swipe_refresh.setOnRefreshListener {
            adapter.refresh()
        }

        btn_filter.setOnClickListener {
            movieFiltersMenu.show()
        }

        setupMovieFiltersMenu()
    }

    private lateinit var movieFiltersMenu: PopupMenu
    private fun setupMovieFiltersMenu() {
        movieFiltersMenu = PopupMenu(requireContext(), abl_toolbar)

        movieFiltersMenu.setOnMenuItemClickListener {
            when {
                it.itemId == R.id.mi_now_playing -> {
                    viewModel.filterChanged(MoviesFilter.NOW_PLAYING)
                    true
                }
                it.itemId == R.id.mi_popular -> {
                    viewModel.filterChanged(MoviesFilter.POPULAR)
                    true
                }
                it.itemId == R.id.mi_top_rated -> {
                    viewModel.filterChanged(MoviesFilter.TOP_RATED)
                    true
                }
                it.itemId == R.id.mi_upcoming -> {
                    viewModel.filterChanged(MoviesFilter.UPCOMING)
                    true
                }
                else -> false
            }
        }
        movieFiltersMenu.menuInflater.inflate(R.menu.menu_movie_filters, movieFiltersMenu.menu)
        movieFiltersMenu.gravity = Gravity.END
    }
}