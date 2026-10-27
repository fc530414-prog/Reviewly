package com.example.reviewly.network

import com.example.reviewly.model.MediaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Archivo para la consulta de información (local o internet)

interface MediaApi {
    // Indicar método HTTP a usar
    @GET("search")

    // Método para obtener la información con la URL del API
    fun obtenerSimilares(
        @Query("term") query: String,
        @Query("media") type: String = "all",
        @Query("limit") limite: Int = 5
    ): Call<MediaResponse>
}
