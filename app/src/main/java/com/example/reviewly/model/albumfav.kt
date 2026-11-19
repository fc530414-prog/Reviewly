package com.example.reviewly.model

data class albumfav(
    val id: Long,
    val albumid: Long,
    val nombreAlbum: String,
    val artista: String,
    val genero: String,
    val canciones: Int,
    val fecha: String,
    val imagen: String
)