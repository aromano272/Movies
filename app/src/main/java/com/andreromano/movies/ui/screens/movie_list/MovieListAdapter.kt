package com.andreromano.movies.ui.screens.movie_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.andreromano.movies.R
import com.andreromano.movies.data.utils.GlideApp
import com.andreromano.movies.data.utils.ImageNetworkPath
import com.andreromano.movies.domain.model.Movie
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_movie.view.*

class MovieListAdapter(
    private val onClick: (Movie) -> Unit,
) : PagingDataAdapter<Movie, MovieListAdapter.VH>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false), onClick)

    override fun onBindViewHolder(holder: VH, position: Int) =
        // forcing nonnull because placeholders are disabled on Pager
        holder.bind(getItem(position)!!)


    class VH(
        override val containerView: View,
        private val onClick: (Movie) -> Unit,
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(movie: Movie) = with(containerView) {
            GlideApp.with(iv_poster)
                .load(movie.posterPath?.let { ImageNetworkPath.Poster(it) })
                .centerCrop()
                .into(iv_poster)
            tv_title.text = movie.title
            tv_overview.text = movie.overview
            this.setOnClickListener {
                onClick(movie)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
        }
    }
}
