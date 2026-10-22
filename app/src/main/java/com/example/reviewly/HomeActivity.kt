package com.example.reviewly

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.reviewly.model.ITunesResponse
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

        // Metodo de llamado del album
        obtenerAlbum()
    }

    // Metodo para obtener informacion del album
    private fun obtenerAlbum() {
        RetrofitClient.api.obtenerAlbumGenero(
            genero = "Habitos Atomicos",
            pais = "MX",
            media = "books",
            entidad = "books",
            limite = 5
        ).enqueue(object : Callback<ITunesResponse> {

            override fun onResponse(call: Call<ITunesResponse>, response: Response<ITunesResponse>) {
                if (response.isSuccessful) {
                    val albumes = response.body()?.results
                    if (!albumes.isNullOrEmpty()) {
                        val album = albumes[0]

                        tvresultado.text =
                            "Album: ${ album.collectionName } \n\n" +
                                    "Artista: ${album.artistName} \n\n" +
                                    "Numero canciones: ${album.trackCount} \n\n"

                        // Mostrar la imagen del álbum
                        Glide.with(this@HomeActivity)
                            .load(album.obtenerImagenGrande())
                            .into(imgalbum)
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "No hay respuesta del API", Toast.LENGTH_SHORT).show()
                }
            }

            // MÉTODO PARA HACER UNA ACCIÓN SI EL API FALLA
            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
