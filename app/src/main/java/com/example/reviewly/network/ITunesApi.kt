package com.example.reviewly.network

import com.example.reviewly.model.ITunesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("search")
    fun obtenerAlbumGenero(
        @Query("term") genero: String,
        @Query("country") pais: String = "MX",
        @Query("media") media: String = "music",
        @Query("entity") entidad: String = "album",
        @Query("limit") limite: Int = 5
    ): Call<ITunesResponse>
}
