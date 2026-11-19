package com.example.reviewly

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.reviewly.model.albumfav
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class Favact : AppCompatActivity() {

    private lateinit var botonnav: BottomNavigationView
    private lateinit var gridfavoritos: GridLayout
    private lateinit var tvestadofavorito: TextView
    private lateinit var dbhelper: Databasehelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favact)

        botonnav = findViewById(R.id.bottomNavigation)
        gridfavoritos = findViewById(R.id.gridfavoritos)
        tvestadofavorito = findViewById(R.id.tvEstadoFavoritos)
        dbhelper = Databasehelper(this)

        menuNavegacion()
        consultarfavorito()
    }

    override fun onResume() {
        super.onResume()
        botonnav.selectedItemId = R.id.nav_favoritos
        consultarfavorito()
    }

    // Metodo para el funcionamiento del menu
    private fun menuNavegacion() {
        botonnav.selectedItemId = R.id.nav_favoritos

        botonnav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_favoritos -> true
               /* R.id.nav_sensores -> {
                    startActivity(Intent(this, SensorActivity::class.java))
                    finish()
                    true
                }*/
                R.id.nav_salir -> {
                    cerrarSesion()
                    true
                }
                else -> false
            }
        }
    }

    // metodo para consultar la informacion y mostrarla
    private fun mostraralbunesfavoritos(favoritos: List<albumfav>) {
        gridfavoritos.removeAllViews()

        if (favoritos.isEmpty()) {
            tvestadofavorito.text = "No hay álbumes favoritos"
            tvestadofavorito.visibility = View.VISIBLE
        } else {
            tvestadofavorito.visibility = View.GONE
        }

        // ciclo para recorrer la consulta de la bd
        for (album in favoritos) {
            val vistaalbum = layoutInflater.inflate(
                R.layout.item_favorito,
                gridfavoritos,
                false
            )

            val imgalbun = vistaalbum.findViewById<ImageView>(R.id.imgfavorito)

            Glide.with(this)
                .load(album.imagen)
                .centerCrop()
                .into(imgalbun)

            // evento para que el usuario acceda a los albumes individuales
            vistaalbum.setOnClickListener {
                val intent = Intent(this, detaActivity::class.java).apply {
                    putExtra("ID_ALBUM", album.albumid)
                    putExtra("NOMBRE_ALBUM", album.nombreAlbum)
                    putExtra("ARTISTA_ALBUM", album.artista)
                    putExtra("GENERO_ALBUM", album.genero)
                    putExtra("CANCIONES_ALBUM", album.canciones)
                    putExtra("FECHA_ALBUM", album.fecha)
                    putExtra("IMAGEN_ALBUM", album.imagen)
                }
                startActivity(intent)
            }

            gridfavoritos.addView(vistaalbum)
        }
    }

    // metodo para la consulta
    private fun consultarfavorito() {
        val favoritos = dbhelper.obteneralbumesfav()

        // mostrar los albumes y sus imagenes en el gridlayout
        gridfavoritos.removeAllViews()

        // valida que los elementos tengan un registro en la bd
        if (favoritos.isEmpty()) {
            tvestadofavorito.visibility = View.VISIBLE
            tvestadofavorito.text = "No tienes ningún álbum en favoritos :V"
        } else {
            tvestadofavorito.visibility = View.GONE
            mostraralbunesfavoritos(favoritos)
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val sharedPreferences = getSharedPreferences("SesionPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("is_logged_in", false).apply()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}