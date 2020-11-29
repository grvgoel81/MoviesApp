package com.gaurav.moviesapp.ui.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.gaurav.moviesapp.db.PreferencesStorage
import com.gaurav.moviesapp.models.Movie
import com.gaurav.moviesapp.repo.MoviesRepository
import com.gaurav.moviesapp.util.Category
import com.gaurav.moviesapp.util.Resource

class MovieListViewModel(private val repository: MoviesRepository, val app: Application) :
    AndroidViewModel(app) {
    companion object {
        private const val TAG = "MovieListViewModel"
    }

    private var isQueryExhausted: Boolean =
        false

    private var isPerformingQuery: Boolean =
        false
    var query: String? = PreferencesStorage.getStoredQuery(app.applicationContext)
    var category: Category = PreferencesStorage.getStoredCategory(app.applicationContext)
    var cancelRequest = false

    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: LiveData<Int>
        get() = _pageNumber


    private val _movies = MediatorLiveData<Resource<List<Movie>>>()
    val movies: LiveData<Resource<List<Movie>>>
        get() = _movies

    init {
        _pageNumber.value = 1
    }

    //for first page
    fun getList() {
        if (!isPerformingQuery) {
            isQueryExhausted = false
            isPerformingQuery = true
            executeRequest()
        }
    }

    //for next page
    fun getNextPage() {
        Log.d(TAG, "getNextPage: ")
        if (!isQueryExhausted && !isPerformingQuery) {
            _pageNumber.value = _pageNumber.value?.plus(1)
            executeRequest()
        }
    }

    private fun executeRequest() {
        Log.d(TAG, "executeRequest: ${pageNumber.value}")
        val repositorySource: LiveData<Resource<List<Movie>>>
        if (query != null) {
            repositorySource = repository.searchListMovie(pageNumber.value!!, query!!)
        } else {
            repositorySource = repository.getListMovie(pageNumber.value!!, this.category)
        }
        registerMediatorLiveData(repositorySource)
    }


    fun registerMediatorLiveData(repositorySource: LiveData<Resource<List<Movie>>>) {
        _movies.addSource(repositorySource) { resourceListMovie ->
            if (!cancelRequest) {
                if (resourceListMovie != null) {
                    _movies.value = resourceListMovie
                    if (resourceListMovie is Resource.Success || resourceListMovie is Resource.Error) {
                        unregisterMediatorLiveData(repositorySource)
                        resourceListMovie.data?.let {
                            if (it.size < _pageNumber.value!! * 10) {
                                isQueryExhausted = true
                            }
                        }
                    }
                } else {
                    unregisterMediatorLiveData(repositorySource)
                }
            } else {
                unregisterMediatorLiveData(repositorySource)
            }

        }
    }

    private fun unregisterMediatorLiveData(repositorySource: LiveData<Resource<List<Movie>>>) {
        isPerformingQuery = false
        _movies.removeSource(repositorySource)
    }

    fun resetPageNumber() {
        _pageNumber.value = 1
    }

    fun cancelRequest() {
        cancelRequest = true
        _pageNumber.value = 1
    }
}
