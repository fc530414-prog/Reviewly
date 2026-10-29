package com.example.reviewly

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.reviewly.model.MediaItem
import com.example.reviewly.model.MediaResponse
import com.example.reviewly.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvresultado: TextView

    private lateinit var contenedorLibros: LinearLayout
    private lateinit var contenedorSeries: LinearLayout
    private lateinit var contenedorPeliculas: LinearLayout
    private lateinit var contenedorVideojuegos: LinearLayout
    private lateinit var contenedorMusica: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Referencias a las vistas del XML
        tvresultado = findViewById(R.id.tvresultado)

        contenedorLibros = findViewById(R.id.contenedorLibros)
        contenedorSeries = findViewById(R.id.contenedorSeries)
        contenedorPeliculas = findViewById(R.id.contenedorPeliculas)
        contenedorVideojuegos = findViewById(R.id.contenedorVideojuegos)
        contenedorMusica = findViewById(R.id.contenedorMusica)

        // Consultas independientes para cada sección
        obtenerAlbum("Habitos Atomicos", contenedorLibros, "ebook", null)
        obtenerAlbum("greys anatomy", contenedorSeries, "tvShow", null)
        obtenerAlbum("Toy Story", contenedorPeliculas, "movie", null)
        obtenerAlbum("Guardian Tales", contenedorVideojuegos, "software", null)
        obtenerAlbum("YOASOBI", contenedorMusica, "music", "album")
    }

    // Consulta la API de iTunes y recibe los resultados
    private fun obtenerAlbum(
        termino: String,
        contenedor: LinearLayout,
        tipoMedia: String = "music",
        tipoEntidad: String? = null
    ) {
        RetrofitClient.api.obtenerSimilares(
            query = termino,
            type = tipoMedia,
            entidad = tipoEntidad,
            limite = 10
        ).enqueue(object : Callback<MediaResponse> {

            override fun onResponse(
                call: Call<MediaResponse>,
                response: Response<MediaResponse>
            ) {
                if (!response.isSuccessful) {
                    mostrarError("Error ${response.code()}")
                    return
                }

                val albumes = response.body()?.results ?: emptyList()

                // Mostrar los resultados de esta sección
                if (albumes.isNotEmpty()) {
                    mostrarAlbumes(albumes, contenedor)
                    tvresultado.visibility = View.GONE
                } else {
                    mostrarError("Sin resultados para: $termino")
                }
            }

            override fun onFailure(
                call: Call<MediaResponse>,
                t: Throwable
            ) {
                mostrarError("Error de conexión al buscar: $termino")
            }
        })
    }

    // Genera las tarjetas y las agrega al contenedor correspondiente
    private fun mostrarAlbumes(
        albumes: List<MediaItem>,
        contenedor: LinearLayout
    ) {
        // Evitar tarjetas duplicadas
        contenedor.removeAllViews()

        for (album in albumes) {

            // Cargar el diseño de cada tarjeta
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedor,
                false
            )

            val imgPortada =
                vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)

            val tvNombreAlbum =
                vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)

            val tvNombreArtista =
                vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            // Asignar el título y el creador recibidos de la API
            tvNombreAlbum.text = album.obtenerTitulo()
            tvNombreArtista.text = album.obtenerCreador()

            // Cargar la portada con Glide
            Glide.with(this@HomeActivity)
                .load(album.obtenerImagenGrande())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imgPortada)

            // Evento click para abrir el detalle
            vistaAlbum.setOnClickListener {
                val intent = Intent(this@HomeActivity, detaActivity::class.java).apply {
                    putExtra("ID_ALBUM", album.trackId ?: album.collectionId ?: 0L)
                    putExtra("NOMBRE_ALBUM", album.obtenerTitulo())
                    putExtra("ARTISTA_ALBUM", album.obtenerCreador())
                    putExtra("GENERO_ALBUM", album.primaryGenreName ?: "NINGUNO")
                    putExtra("CANCIONES_ALBUM", album.trackCount ?: 1)
                    putExtra("IMAGEN_ALBUM", album.obtenerImagenGrande())
                    putExtra("DESCRIPCION_ALBUM", album.obtenerDescripcion())
                }
                startActivity(intent)
            }

            // Añadir la tarjeta a su sección horizontal
            contenedor.addView(vistaAlbum)
        }
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(
            this@HomeActivity,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }
}
