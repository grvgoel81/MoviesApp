package com.gaurav.moviesapp.api.responses.inner

import com.gaurav.moviesapp.models.Review
import com.google.gson.annotations.SerializedName

class ReviewsResponse {
    @SerializedName("results")
    val reviews:List<Review>?=null
}