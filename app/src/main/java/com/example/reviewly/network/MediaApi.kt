package com.example.reviewly.network

import com.example.reviewly.model.MediaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MediaApi {

    @GET("search")
    fun obtenerSimilares(
        @Query("term") query: String,
        @Query("media") type: String = "all",
        @Query("entity") entidad: String? = null,
        @Query("limit") limite: Int = 10
    ): Call<MediaResponse>
}
