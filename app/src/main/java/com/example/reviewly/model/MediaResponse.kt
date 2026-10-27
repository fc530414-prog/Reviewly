package com.example.reviewly.model

import com.google.gson.annotations.SerializedName

// Archivo para recibir la respuesta de confirmación del API

data class MediaResponse(
    @SerializedName("resultCount")
    val resultCount: Int,

    @SerializedName("results")
    val results: List<MediaItem>?
)
