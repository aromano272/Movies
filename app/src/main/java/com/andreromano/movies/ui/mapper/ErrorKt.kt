package com.andreromano.movies.ui.mapper

import android.content.Context
import androidx.annotation.StringRes
import com.andreromano.movies.R
import com.andreromano.movies.common.ErrorKt


val ErrorKt.errorMessage: Int
    @StringRes
    get() = when (this) {
        is ErrorKt.Unknown -> R.string.error_unknown
        ErrorKt.Unauthorized -> R.string.error_unauthorized
        ErrorKt.Generic -> R.string.error_generic
        ErrorKt.Network -> R.string.error_network
        ErrorKt.NotFound -> R.string.error_not_found
    }

fun ErrorKt.getErrorString(context: Context) = context.getString(errorMessage)