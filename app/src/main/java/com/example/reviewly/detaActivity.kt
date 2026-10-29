package com.example.reviewly

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class detaActivity : AppCompatActivity() {

    private lateinit var tvresultado: TextView
    private lateinit var imgPortada: ImageView
    private lateinit var tvNombreAlbum: TextView
    private lateinit var tvArtistaAlbum: TextView
    private lateinit var tvGeneroAlbum: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deta)

        tvresultado = findViewById(R.id.tvresultado)
        imgPortada = findViewById(R.id.imgPortadaDetail)
        tvNombreAlbum = findViewById(R.id.tvNombreAlbumDetail)
        tvArtistaAlbum = findViewById(R.id.tvArtistaAlbumDetail)
        tvGeneroAlbum = findViewById(R.id.tvGeneroAlbumDetail)

        mostrarInformacionAlbm()
    }

    // Método para mostrar la información de un álbum o elemento
    private fun mostrarInformacionAlbm() {
        val idAlbum = intent.getLongExtra("ID_ALBUM", 1)
        val nombreAlbum = intent.getStringExtra("NOMBRE_ALBUM") ?: "Sin nombre"
        val artistaAlbum = intent.getStringExtra("ARTISTA_ALBUM") ?: "Desconocido"
        val generoAlbum = intent.getStringExtra("GENERO_ALBUM") ?: "N/A"
        val cancionesAlbum = intent.getIntExtra("CANCIONES_ALBUM", 1)
        val imagenAlbum = intent.getStringExtra("IMAGEN_ALBUM")
        val descripcionAlbum = intent.getStringExtra("DESCRIPCION_ALBUM") ?: ""

        tvNombreAlbum.text = nombreAlbum
        tvArtistaAlbum.text = "Artista / Creador: $artistaAlbum"
        tvGeneroAlbum.text = "Género: $generoAlbum"

        if (descripcionAlbum.isNotEmpty()) {
            tvresultado.text = "Descripción:\n\n$descripcionAlbum"
        } else {
            tvresultado.text = "ID: $idAlbum | Canciones / Elementos: $cancionesAlbum"
        }

        if (!imagenAlbum.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagenAlbum)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imgPortada)
        }
    }
}
