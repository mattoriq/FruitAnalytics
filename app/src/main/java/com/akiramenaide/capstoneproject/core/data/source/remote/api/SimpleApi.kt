package com.akiramenaide.capstoneproject.core.data.source.remote.api

import com.akiramenaide.capstoneproject.core.data.source.remote.response.GetIndex
import com.akiramenaide.capstoneproject.core.data.source.remote.response.PostedImage
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface SimpleApi {
    /*
    @FormUrlEncoded
    @POST("api/prediction/")
    fun post2(
        @Field("predClass")predClass: Int,
        @Field("percentage")percentage: Float,
        @Field("prediction")prediction: ArrayList<Float>
    ): Call<PostedImage>
     */

    @FormUrlEncoded
    @POST("/api/predict/")
    fun getPredict(
        @Field("data")data: String
    ): Call<PostedImage>

    @GET("/")
    fun getValue(): Call<GetIndex>

}