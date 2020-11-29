package com.gaurav.moviesapp.repo

import androidx.lifecycle.LiveData
import com.android.myapplication.movies.util.NetworkBoundResourceNoCaching
import com.gaurav.moviesapp.api.MoviesApi
import com.gaurav.moviesapp.api.responses.inner.*
import com.gaurav.moviesapp.models.MovieDetails
import com.gaurav.moviesapp.util.Resource

class MovieDetailRepository(
    private val movieApi: MoviesApi
) {

    fun getMovieDetail(movieId: Long): LiveData<Resource<MovieDetails>> {
        return object : NetworkBoundResourceNoCaching<MovieDetails, MovieDetailsResponse>() {
            override fun handleApiSuccessResponse(response: ApiSuccessResponse<MovieDetailsResponse>) {
                val detailsResponse= response.body
                val videos = detailsResponse.videoResponse?.videos
                val casts = detailsResponse.creditsResponse?.casts
                val reviews = detailsResponse.reviewResponse?.reviews
                result.value = Resource.Success(MovieDetails(videos,reviews,casts))
            }

            override fun handleApiEmptyResponse(response: ApiEmptyResponse<MovieDetailsResponse>) {
                //MovieDetails,has empty videos,casts,reviews
                result.value = Resource.Success(MovieDetails())
            }

            override fun handleApiErrorResponse(response: ApiErrorResponse<MovieDetailsResponse>) {
                result.value = Resource.Error(response.errorMessage,null)
            }

            override fun createCall(): LiveData<ApiResponse<MovieDetailsResponse>> =
                movieApi.getMovieDetail(movieId)

        }.asLiveData()
    }
}