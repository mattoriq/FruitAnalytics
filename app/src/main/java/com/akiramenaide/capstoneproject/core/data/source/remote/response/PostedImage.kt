package com.akiramenaide.capstoneproject.core.data.source.remote.response

import org.json.JSONObject
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

data class PostedImage (
    val predClass: Int,
    val className: String,
    val percentage: Float,
    val prediction: ArrayList<Float>,
    val data: String
    )

data class PostedData (
    val data: FloatArray
)

data class GetIndex (
    val status: Int,
    val data: String
    )