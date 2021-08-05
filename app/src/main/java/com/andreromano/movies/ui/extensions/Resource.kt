package com.andreromano.movies.ui.extensions

import com.andreromano.movies.common.Resource
import com.andreromano.movies.ui.utils.ListState

fun <T> Resource<List<T>>.toListState(): ListState<T> =
    when (this) {
        is Resource.Loading -> ListState.Loading(this.data)
        is Resource.Success ->
            if (this.data.isEmpty()) ListState.EmptyState
            else ListState.Results(this.data)
        is Resource.Failure -> ListState.Error(this.error)
    }