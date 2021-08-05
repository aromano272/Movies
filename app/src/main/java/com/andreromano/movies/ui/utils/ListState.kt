package com.andreromano.movies.ui.utils

import com.andreromano.movies.common.ErrorKt


sealed class ListState<out T> {
    data class Loading<T>(val results: List<T>?) : ListState<T>()
    data class Results<T>(val results: List<T>) : ListState<T>()
    object EmptyState : ListState<Nothing>()
    data class Error(val error: ErrorKt) : ListState<Nothing>()
}

