package com.gaurav.moviesapp.api.responses.inner

import com.gaurav.moviesapp.models.Movie
import com.google.gson.annotations.SerializedName

class MoviesResponse{

    @SerializedName("page")
    var page:Int=1

    @SerializedName("total_results")
    val totalResults:Int=0

    @SerializedName("total_pages")
    val total_pages:Int=0

    @SerializedName("results")
    val movies:List<Movie>?=null
}