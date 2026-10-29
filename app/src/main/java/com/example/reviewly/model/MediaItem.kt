package com.example.reviewly.model

import com.google.gson.annotations.SerializedName

data class MediaItem(
    @SerializedName("trackId") val trackId: Long?,
    @SerializedName("collectionId") val collectionId: Long?,

    // Títulos alternativos según el tipo de contenido
    @SerializedName("trackName") val trackName: String?,       // Canciones, películas, apps
    @SerializedName("collectionName") val collectionName: String?, // Álbumes, series, audiolibros

    // Creadores alternativos
    @SerializedName("artistName") val artistName: String?,     // Músicos, directores de cine, autores

    // Género
    @SerializedName("primaryGenreName") val primaryGenreName: String?,

    // Canciones
    @SerializedName("trackCount") val trackCount: Int?,

    // Descripción
    @SerializedName("description") val description: String?,
    @SerializedName("longDescription") val longDescription: String?,

    // Imagen
    @SerializedName("artworkUrl100") val artworkUrl100: String?
) {
    // Retorna el título correcto dependiendo de lo que responda la API
    fun obtenerTitulo(): String {
        return trackName ?: collectionName ?: "Sin título"
    }

    // Retorna el creador correcto
    fun obtenerCreador(): String {
        return artistName ?: "Desconocido"
    }

    // Retorna la descripción disponible
    fun obtenerDescripcion(): String {
        return description ?: longDescription ?: ""
    }

    // Reemplaza el tamaño de la imagen por una de mejor calidad
    fun obtenerImagenGrande(): String? {
        return artworkUrl100?.replace("100x100bb.jpg", "500x500bb.jpg")
    }
}
