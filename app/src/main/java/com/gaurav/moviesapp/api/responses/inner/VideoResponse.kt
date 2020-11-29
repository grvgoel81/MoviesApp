package com.gaurav.moviesapp.api.responses.inner

import com.gaurav.moviesapp.models.Video
import com.google.gson.annotations.SerializedName

class VideoResponse {
    @SerializedName("results")
    val videos:List<Video>?=null
}