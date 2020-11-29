package com.gaurav.moviesapp.ui.list

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.gaurav.moviesapp.databinding.MovieListItemBinding
import com.gaurav.moviesapp.models.Movie
import java.util.*

class MoviesRecyclerAdapter(
    val onMovieClickListener: (Movie) -> Unit,
    private val preloadSizeProvider: ViewPreloadSizeProvider<String>,
    private val requestManager: RequestManager?
) :
    androidx.recyclerview.widget.ListAdapter<Movie, MoviesRecyclerAdapter.MovieViewHolder>(
        MovieDiffUtil()
    ), ListPreloader.PreloadModelProvider<String> {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.getInstance(parent, onMovieClickListener, preloadSizeProvider)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)

    }

    class MovieViewHolder private constructor(
        private val binding: MovieListItemBinding
        , val onMovieClickListener: (Movie) -> Unit,
        val preloadSizeProvider: ViewPreloadSizeProvider<String>
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            private const val TAG = "MovieViewHolder"
            fun getInstance(
                parent: ViewGroup,
                onMovieClickListener: (Movie) -> Unit,
                preloadSizeProvider: ViewPreloadSizeProvider<String>
            ): MovieViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = MovieListItemBinding.inflate(inflater, parent, false)
                return MovieViewHolder(
                    binding,
                    onMovieClickListener,preloadSizeProvider
                )
            }
        }

        fun bind(movie: Movie) {
            binding.movie = movie
            binding.viewHolder = this
            preloadSizeProvider.setView(binding.movieImage)
            binding.executePendingBindings()
        }
    }

    class MovieDiffUtil : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    override fun getPreloadItems(position: Int): MutableList<String> {
        val url = getItem(position).posterPath
        return if (url == null || TextUtils.isEmpty(url)) {
            Collections.emptyList()
        } else {
            Collections.singletonList(url)
        }
    }

    override fun getPreloadRequestBuilder(item: String): RequestBuilder<*>? =
        requestManager?.load(item)
}