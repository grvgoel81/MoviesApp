package com.gaurav.moviesapp.util

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.gaurav.moviesapp.api.responses.inner.ApiEmptyResponse
import com.gaurav.moviesapp.api.responses.inner.ApiErrorResponse
import com.gaurav.moviesapp.api.responses.inner.ApiResponse
import com.gaurav.moviesapp.api.responses.inner.ApiSuccessResponse

abstract class NetworkBoundResource<CacheObject, RequestObject>
@MainThread constructor(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<CacheObject>>()

    init {
        result.value = Resource.Loading(null) //returns to the UI a Loading State
        @Suppress("LeakingThis")
        val dbSource = loadFromDb() //get the data available in the Database
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { currentCachedData ->
                    setValue(Resource.Success(currentCachedData))
                }
            }
        }
    }

    @MainThread
    private fun setValue(newValue: Resource<CacheObject>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    //represents the Task of fetching the network
    private fun fetchFromNetwork(dbSource: LiveData<CacheObject>) {
        //get the retrofit response, which is LiveData<ApiResponse<T>>
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { currentCachedData ->
            setValue(Resource.Loading(currentCachedData))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            //Based on the ApiResponse Class, we have 3 cases:
            when (response) {
                is ApiSuccessResponse -> {
                    //We  need diskIO to interact with our database
                    appExecutors.diskIO.execute {
                        //if we get the data From the network, we want to save the response to the db
                        //because the ui ONLY gets its data from the database
                        saveCallResult(processResponse(response))
                        //Main executor is needed since we are using setValue instead 'postValue'
                        appExecutors.mainThreadExecutor.execute {
                            // we specially request a new live data,
                            // otherwise we will get immediately last cached value,
                            // which may not be updated with latest results received from network.
                            result.addSource(loadFromDb()) { newCachedObject ->
                                //passing the value back to the ui
                                setValue(Resource.Success(newCachedObject))
                            }
                        }
                    }
                }
                is ApiEmptyResponse -> {
                    appExecutors.mainThreadExecutor.execute {
                        result.addSource(loadFromDb()) { oldCachedObject ->
                            setValue(Resource.Success(oldCachedObject))
                        }
                    }
                }
                is ApiErrorResponse -> {
                    result.addSource(dbSource) { oldCachedObject ->
                        setValue(Resource.Error(response.errorMessage, oldCachedObject))
                    }
                }
            }
        }
    }


    //Turns the result from mediatorLiveData into LiveData
    fun asLiveData() = result as LiveData<Resource<CacheObject>>

    @WorkerThread
    //only retrieve the body from the retrofit Response
    // body is = to the Response (exam:MoviesResponse) we modeled based on the REST Api we are connecting to
    protected open fun processResponse(response: ApiSuccessResponse<RequestObject>) = response.body

    //Called to Save the Data coming from the api into the database
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestObject?)

    //Called with the data in the DATABASE to decide whether to fetch
    @MainThread
    protected abstract fun shouldFetch(data: CacheObject?): Boolean

    //Called to get the cached data from the database wrapped inside LiveData
    @MainThread
    protected abstract fun loadFromDb(): LiveData<CacheObject>

    //Fire a request to the Network Api
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestObject>>
}