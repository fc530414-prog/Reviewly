package com.example.reviewly.model

data class Album(
    val wrapperType: String?,
    val collectionType: String?,

    val artistId: Long?,
    val collectionId: Long?,

    val artistName: String?,
    val collectionName: String?,

    val artistViewUrl: String?,
    val collectionViewUrl: String?,

    val artworkUrl100: String?,

    val collectionPrice: Double?,
    val trackCount: Int?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val currency: String?
) {
    // metodo para obtener la imagen mas grande
    fun obtenerImagenGrande(): String {
        return artworkUrl100?.replace("100x100bb.jpg", "600x600.jpg")
            ?.replace("100x100.jpg", "600x600.jpg") ?: ""
    }
}
