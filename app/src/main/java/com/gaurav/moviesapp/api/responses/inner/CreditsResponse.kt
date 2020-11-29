package com.gaurav.moviesapp.api.responses.inner

import com.gaurav.moviesapp.models.Cast
import com.google.gson.annotations.SerializedName

class CreditsResponse {
    @SerializedName("cast")
    val casts:List<Cast>?=null
}