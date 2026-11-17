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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class Favact : AppCompatActivity() {

    private lateinit var contenedorFavoritos: LinearLayout
    private lateinit var botonnav: BottomNavigationView
    private lateinit var dbHelper: Databasehelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favact)

        contenedorFavoritos = findViewById(R.id.contenedorFavoritos)
        botonnav = findViewById(R.id.bottomNavigation)
        dbHelper = Databasehelper(this)

        menuNavegacion()
        cargarFavoritos()
    }

    override fun onResume() {
        super.onResume()
        botonnav.selectedItemId = R.id.nav_favoritos
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        contenedorFavoritos.removeAllViews()
        val listaFavoritos = dbHelper.obtenerTodosFavoritos()

        if (listaFavoritos.isEmpty()) {
            val tvVacio = TextView(this).apply {
                text = "No tienes elementos en favoritos"
                setTextColor(android.graphics.Color.WHITE)
                textSize = 18f
                setPadding(16, 32, 16, 16)
                typeface = androidx.core.content.res.ResourcesCompat.getFont(context, R.font.shortstack_regular)
            }
            contenedorFavoritos.addView(tvVacio)
            return
        }

        for (fav in listaFavoritos) {
            val albumId = fav["album_id"] as Long
            val nombreAlbum = fav["nombre_album"] as String
            val artista = fav["artista"] as String
            val genero = fav["genero"] as String
            val canciones = fav["num_canciones"] as Int
            val fecha = fav["fecha"] as String
            val imagen = fav["imagen"] as String

            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedorFavoritos,
                false
            )

            val imgPortada = vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)
            val tvNombreAlbum = vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)
            val tvNombreArtista = vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            tvNombreAlbum.text = nombreAlbum
            tvNombreArtista.text = artista

            val fuentePermanent = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.permanent_marker)
            val fuenteShortstack = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.shortstack_regular)

            tvNombreAlbum.typeface = fuentePermanent
            tvNombreAlbum.setTextColor(android.graphics.Color.WHITE)
            tvNombreArtista.typeface = fuenteShortstack
            tvNombreArtista.setTextColor(android.graphics.Color.WHITE)

            Glide.with(this)
                .load(imagen)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imgPortada)

            vistaAlbum.setOnClickListener {
                val intent = Intent(this, detaActivity::class.java).apply {
                    putExtra("ID_ALBUM", albumId)
                    putExtra("NOMBRE_ALBUM", nombreAlbum)
                    putExtra("ARTISTA_ALBUM", artista)
                    putExtra("GENERO_ALBUM", genero)
                    putExtra("CANCIONES_ALBUM", canciones)
                    putExtra("FECHA_ALBUM", fecha)
                    putExtra("IMAGEN_ALBUM", imagen)
                }
                startActivity(intent)
            }

            contenedorFavoritos.addView(vistaAlbum)
        }
    }

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
                R.id.nav_sensores -> {
                    startActivity(Intent(this, SensorActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_salir -> {
                    cerrarSesion()
                    true
                }
                else -> false
            }
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