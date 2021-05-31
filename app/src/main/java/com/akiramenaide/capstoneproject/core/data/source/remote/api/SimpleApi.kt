package com.akiramenaide.capstoneproject.core.data.source.remote.api

import com.akiramenaide.capstoneproject.core.data.source.remote.response.GetIndex
import com.akiramenaide.capstoneproject.core.data.source.remote.response.PostedImage
import retrofit2.Call
import retrofit2.http.*

interface SimpleApi {

    @POST("/api/predict/img/")
    fun getPredict(
        @Body data: String
    ): Call<PostedImage>

    @GET("/")
    fun getValue(): Call<GetIndex>

}