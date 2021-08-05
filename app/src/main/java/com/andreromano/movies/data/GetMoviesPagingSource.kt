package com.andreromano.movies.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andreromano.movies.common.ResultKt
import com.andreromano.movies.data.mapper.toNetwork
import com.andreromano.movies.domain.model.MoviesFilter
import com.andreromano.movies.network.Api
import com.andreromano.movies.network.model.MovieResult
import timber.log.Timber

class GetMoviesPagingSource(
    private val api: Api,
    private val filter: MoviesFilter,
) : PagingSource<Int, MovieResult>() {
    override fun getRefreshKey(state: PagingState<Int, MovieResult>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        val refreshKey = state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
        Timber.e("getRefreshKey refreshKey: $refreshKey, state: $state")
        return refreshKey
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResult> =
        when (val result = api.getMovies(filter.toNetwork(), params.key ?: 1)) {
            is ResultKt.Success -> LoadResult.Page(
                result.data.results,
                null, // Only paging forward
                if (result.data.page < result.data.total_pages) result.data.page + 1 else null
            )
            is ResultKt.Failure -> LoadResult.Error(result.error)
        }
}