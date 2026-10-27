package com.example.reviewly.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Archivo que permite tener un objeto para la conexión a internet

object RetrofitClient {
    // Constante para tener la base URL pública sin necesidad de API Key
    private const val BASE_URL = "https://itunes.apple.com/"

    // Variable que heredará de "lazy"
    val api: MediaApi by lazy {
        // Construir la URL completa
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MediaApi::class.java)
    }
}
