package com.andreromano.movies.ui.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn


fun <T> Flow<T>.shareHere(viewModel: ViewModel): SharedFlow<T> = this.shareIn(viewModel.viewModelScope, SharingStarted.Lazily, 1)
