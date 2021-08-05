package com.andreromano.movies.ui.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andreromano.movies.R
import com.andreromano.movies.common.ErrorKt
import com.andreromano.movies.ui.extensions.toVisibility
import com.andreromano.movies.ui.mapper.getErrorString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_paging_load_state.view.*

class PagingLoadStateAdapter(
    private val retryClicked: () -> Unit,
) : LoadStateAdapter<PagingLoadStateAdapter.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_paging_load_state, parent, false), retryClicked)

    override fun onBindViewHolder(holder: VH, loadState: LoadState) = holder.bind(loadState)

    class VH(
        override val containerView: View,
        private val retryClicked: () -> Unit,
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(loadState: LoadState) = with(containerView) {
            group_loading.toVisibility = loadState is LoadState.Loading
            group_error.toVisibility = loadState is LoadState.Error
            if (loadState is LoadState.Error) {
                val message = (loadState.error as? ErrorKt)?.getErrorString(containerView.context) ?: loadState.error.message
                tv_error.text = message
            }
            btn_retry.setOnClickListener {
                retryClicked()
            }
        }
    }
}