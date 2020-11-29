package com.gaurav.moviesapp.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.gaurav.moviesapp.models.Movie
import com.gaurav.moviesapp.models.MovieDetails
import com.gaurav.moviesapp.repo.MovieDetailRepository
import com.gaurav.moviesapp.util.Resource

class DetailFragmentViewModel(
    app: Application,
    private val repository: MovieDetailRepository,
    val movie: Movie
) : AndroidViewModel(app) {


    private val _movieDetails = MediatorLiveData<Resource<MovieDetails>>()
    val movieDetails: LiveData<Resource<MovieDetails>>
        get() = _movieDetails


    private var cancelRequest = false
    private var isPerformingQuery: Boolean = false

    fun getMovieDetails() {
        if (!isPerformingQuery) {
            isPerformingQuery = true
            executeRequest()
        }
    }

    private fun executeRequest() {
        val repositorySource = repository.getMovieDetail(movie.id)
        registerMediatorLiveData(repositorySource)
    }

    private fun registerMediatorLiveData(repositorySource: LiveData<Resource<MovieDetails>>) {
        _movieDetails.addSource(repositorySource) { resourceMovieDetails ->
            if (!cancelRequest) {
                if (resourceMovieDetails != null) {
                    _movieDetails.value = resourceMovieDetails
                    if (resourceMovieDetails is Resource.Success || resourceMovieDetails is Resource.Error) {
                        unregisterMediatorLiveData(repositorySource)
                    }
                } else {
                    unregisterMediatorLiveData(repositorySource)
                }
            } else {
                unregisterMediatorLiveData(repositorySource)
            }
        }
    }

    private fun unregisterMediatorLiveData(repositorySource: LiveData<Resource<MovieDetails>>) {
        isPerformingQuery = false
        _movieDetails.removeSource(repositorySource)
    }

    fun cancelRequest() {
        cancelRequest = true
    }
}