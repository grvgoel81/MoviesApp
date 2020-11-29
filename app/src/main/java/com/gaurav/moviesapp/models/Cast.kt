package com.gaurav.moviesapp.models

import com.google.gson.annotations.SerializedName

data class Cast(
    @SerializedName("cast_id")
    val castId:Int=0,

    @SerializedName("name")
    val name:String?=null,

    @SerializedName("profile_path")
    val profilePath:String?=null,

    @SerializedName("character")
    val character:String?=null

    )