package com.andreromano.movies.ui.screens.movie_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andreromano.movies.R
import com.andreromano.movies.common.ErrorKt
import com.andreromano.movies.data.utils.GlideApp
import com.andreromano.movies.data.utils.ImageNetworkPath
import com.andreromano.movies.domain.model.Movie
import com.andreromano.movies.ui.mapper.getErrorString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_movie_recommendation.view.*
import kotlinx.android.synthetic.main.item_movie_recommendation_error_state.view.*

class MovieRecommendationsAdapter(
    private val onClick: (Movie) -> Unit,
) : ListAdapter<MovieRecommendationsAdapter.Item, MovieRecommendationsAdapter.VH>(DIFF_CALLBACK) {

    sealed class Item {
        object Loading : Item()
        data class Result(val movie: Movie) : Item()
        data class Error(val error: ErrorKt) : Item()
        object EmptyState : Item()
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        Item.Loading -> R.layout.item_movie_recommendation_loading_state
        is Item.Result -> R.layout.item_movie_recommendation
        is Item.Error -> R.layout.item_movie_recommendation_error_state
        Item.EmptyState -> R.layout.item_movie_recommendation_empty_state
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = when (viewType) {
        R.layout.item_movie_recommendation_loading_state -> VH.Loading(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_recommendation_loading_state, parent, false))
        R.layout.item_movie_recommendation -> VH.Result(LayoutInflater.from(parent.context).inflate(R.layout.item_movie_recommendation, parent, false), onClick)
        R.layout.item_movie_recommendation_error_state -> VH.Error(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_recommendation_error_state, parent, false))
        R.layout.item_movie_recommendation_empty_state -> VH.EmptyState(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie_recommendation_empty_state, parent, false))
        else -> throw IllegalStateException()
    }

    override fun onBindViewHolder(holder: VH, position: Int) = when (holder) {
        is VH.Loading -> {
        }
        is VH.Result -> holder.bind((getItem(position) as Item.Result).movie)
        is VH.Error -> holder.bind((getItem(position) as Item.Error).error)
        is VH.EmptyState -> {
        }
    }

    sealed class VH(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        class Result(override val containerView: View, private val onClick: (Movie) -> Unit) : VH(containerView) {
            fun bind(movie: Movie) = with(containerView) {
                GlideApp.with(iv_poster)
                    .load(movie.posterPath?.let { ImageNetworkPath.Poster(it) })
                    .centerCrop()
                    .into(iv_poster)

                tv_title.text = movie.title
                tv_rating.text = tv_rating.context.getString(R.string.movie_details_rating, movie.voteAverage, movie.voteCount)

                card_movie_recommendation.setOnClickListener {
                    onClick(movie)
                }
            }
        }

        class Loading(override val containerView: View) : VH(containerView)

        class Error(override val containerView: View) : VH(containerView) {
            fun bind(error: ErrorKt) = with(containerView) {
                tv_error.text = error.getErrorString(containerView.context)
            }
        }

        class EmptyState(override val containerView: View) : VH(containerView)
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
                if (oldItem::class != newItem::class) false
                else when (oldItem) {
                    Item.Loading -> oldItem == newItem
                    is Item.Result -> oldItem.movie.id == (newItem as Item.Result).movie.id
                    is Item.Error -> oldItem == newItem
                    is Item.EmptyState -> oldItem == newItem
                }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem == newItem
        }
    }
}