package com.gaurav.moviesapp.ui.list

import IMAGE_BASE_URL
import IMAGE_FILE_SIZE
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gaurav.moviesapp.R
import com.gaurav.moviesapp.models.Movie
import com.gaurav.moviesapp.util.Resource

@BindingAdapter("paginationRepoResult", "pageNumber")
fun paginationLoadingVisibility(
    view: View,
    repoResult: Resource<List<Movie>>?,
    pageNumber: Int = 1
) {
    repoResult?.let {
        repoResult.data?.let {
            if (repoResult is Resource.Loading && pageNumber > 1) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }
}
//should be visible when in loading state + pageNumber = 1
@BindingAdapter("firstPageRepoResult", "pageNumber")
fun firstPageLoadingVisibility(
    view: View,
    repoResult: Resource<List<Movie>>?,
    pageNumber: Int = 1
) {
    repoResult?.let {
        repoResult.data?.let {
            if (repoResult is Resource.Loading && pageNumber == 1) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }
}

@BindingAdapter("movieList")
fun RecyclerView.submitMovieList(movies: List<Movie>?) {
    val adapter = this.adapter as MoviesRecyclerAdapter
    movies?.let {
        adapter.submitList(movies)
    }
}

//recyclerview should be visible only when data is available
@BindingAdapter("rvVisibility")
fun RecyclerView.checkVisibility(repoResult: Resource<List<Movie>>?) {
    repoResult?.let {
        repoResult.data?.let {
            if (repoResult.data.isNullOrEmpty()) {
                this.visibility = View.GONE
            } else {
                this.visibility = View.VISIBLE
            }
        }
    }
}

//should be visible when Success but no data returned
@BindingAdapter("emptyDataVisibility")
fun emptyDataVisibility(view: View, repoResult: Resource<List<Movie>>?) {
    repoResult?.let {
        repoResult.data?.let {
            if (repoResult is Resource.Success && repoResult.data.isNullOrEmpty()) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }
}

@BindingAdapter("networkErrorVisibility")
fun networkErrorVisibility(view: View, repoResult: Resource<List<Movie>>?) {
   repoResult?.let {
        if (repoResult is Resource.Error && repoResult.data.isNullOrEmpty()) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}

@BindingAdapter("movieFrontImage")
fun ImageView.loadFrontMovieImage(imageUrl: String?) {
    val image = IMAGE_BASE_URL + IMAGE_FILE_SIZE + imageUrl
    Glide.with(this.context)
        .load(image)
        .apply(
            RequestOptions()
                .error(R.drawable.ic_broken_image)
        ).into(this)
}

