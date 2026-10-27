package com.example.reviewly

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.reviewly.model.MediaResponse
import com.example.reviewly.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvresultado: TextView
    private lateinit var imgalbum: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        tvresultado = findViewById(R.id.tvresultado)
        imgalbum = findViewById(R.id.imgalbum)

        // Metodo de llamado
        obtenerInformacion()
    }

    // Metodo para obtener informacion de la API
    private fun obtenerInformacion() {

        val busqueda = "El verano del que me enamore"// lo que hay que buscar
        val tipoCategoria = "books" // categorias a buscar: books music movies shows games

        // consulta
        RetrofitClient.api.obtenerSimilares(
            query = busqueda, // lo que hay que buscar
            type = mapearMedia(tipoCategoria),
            limite = 1
        ).enqueue(object : Callback<MediaResponse> {

            override fun onResponse(call: Call<MediaResponse>, response: Response<MediaResponse>) {
                if (response.isSuccessful) {
                    val resultsList = response.body()?.results

                    val item = resultsList?.firstOrNull()

                    if (item != null) {
                        val nombre = item.obtenerTitulo()
                        val creador = item.obtenerCreador()
                        val tipo = traducirTipo(item.kind ?: tipoCategoria)
                        val descripcion = item.obtenerDescripcion()

                        val texto = "Nombre: $nombre \n\n" +
                                "Artista/Autor: $creador \n\n" +
                                "Categoría: $tipo \n\n" +
                                "Descripción: $descripcion \n\n"

                        tvresultado.text = texto

                        // Mostrar la imagen
                        val imagenUrl = item.obtenerImagenGrande()

                        if (imagenUrl.isNotEmpty()) {
                            Glide.with(this@HomeActivity)
                                .load(imagenUrl)
                                .into(imgalbum)
                        } else {
                            imgalbum.setImageDrawable(null)
                        }
                    } else {
                        tvresultado.text = "No se encontraron resultados"
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "No hay respuesta del API (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }

            // MÉTODO PARA HACER UNA ACCIÓN SI EL API FALLA
            override fun onFailure(call: Call<MediaResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Mapea las categorías al formato que entiende la API de medios
    private fun mapearMedia(tipo: String): String {
        return when (tipo.lowercase().trim()) {
            "books", "book", "libro", "libros" -> "ebook"
            "music", "musica", "música" -> "music"
            "movies", "movie", "película", "peliculas" -> "movie"
            "shows", "show", "series", "serie" -> "tvShow"
            "games", "game", "juegos", "videojuego", "videojuegos" -> "software"
            else -> "all"
        }
    }

    // Traduce los tipos devueltos por la API al español
    private fun traducirTipo(tipo: String?): String {
        return when (tipo?.lowercase()) {
            "ebook", "book", "books" -> "Libro"
            "song", "music", "album" -> "Música"
            "feature-movie", "movie", "movies" -> "Película"
            "tv-episode", "show", "shows", "series" -> "Serie de TV"
            "software", "game", "games" -> "Videojuego"
            else -> tipo ?: "General"
        }
    }
}
