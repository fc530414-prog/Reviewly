package com.example.reviewly.model

import com.google.gson.annotations.SerializedName

// Archivo para obtener la información que regresa el API

data class MediaItem(
    @SerializedName("wrapperType")
    val wrapperType: String?,

    @SerializedName("kind")
    val kind: String?,

    @SerializedName("artistName")
    val artistName: String?,

    @SerializedName("collectionName")
    val collectionName: String?,

    @SerializedName("trackName")
    val trackName: String?,

    @SerializedName("artworkUrl100")
    val artworkUrl100: String?,

    @SerializedName("primaryGenreName")
    val primaryGenreName: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("longDescription")
    val longDescription: String?
) {
    // Método para obtener el título (sirve para libros, canciones, películas, videojuegos, series)
    fun obtenerTitulo(): String {
        return trackName ?: collectionName ?: "Sin título"
    }

    // Método para obtener el creador/artista/autor
    fun obtenerCreador(): String {
        return artistName ?: "Desconocido"
    }

    // Método para obtener la descripción
    fun obtenerDescripcion(): String {
        return description ?: longDescription ?: "Sin descripción disponible"
    }

    // Método para obtener la imagen en alta resolución
    fun obtenerImagenGrande(): String {
        return artworkUrl100?.replace("100x100bb.jpg", "600x600.jpg")
            ?.replace("100x100.jpg", "600x600.jpg") ?: ""
    }
}
