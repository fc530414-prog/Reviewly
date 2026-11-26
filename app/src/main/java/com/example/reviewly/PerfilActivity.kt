package com.example.reviewly

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import java.net.URI

class PerfilActivity : AppCompatActivity() {

    private lateinit var etNombreUsuario: EditText
    private lateinit var btnGuardarNombre: Button
    private lateinit var gridfavoritosPerfil: GridLayout
    private lateinit var tvEstadoFavoritosPerfil: TextView
    private lateinit var botonnav: BottomNavigationView
    private lateinit var dbhelper: Databasehelper

    private lateinit var tvLabelNombre : TextView

    private lateinit var imgPerfilDefault : ImageView

    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)


        gridfavoritosPerfil = findViewById(R.id.gridfavoritosPerfil)
        tvEstadoFavoritosPerfil = findViewById(R.id.tvEstadoFavoritosPerfil)
        botonnav = findViewById(R.id.bottomNavigation)
        dbhelper = Databasehelper(this)
        auth = FirebaseAuth.getInstance()
        imgPerfilDefault = findViewById(R.id.imgPerfilDefault)
        tvLabelNombre = findViewById(R.id.tvLabelNombre)

        val usuario = auth.currentUser
        val imagen : Uri? = usuario?.photoUrl

        Glide.with(this)
            .load(imagen)
            .centerCrop()
            .into(imgPerfilDefault)

        tvLabelNombre.text = usuario?.displayName


        cargarNombreUsuario()

        btnGuardarNombre.setOnClickListener {
            guardarNombreUsuario()
        }

        menuNavegacion()
        consultarFavoritos()
    }

    override fun onResume() {
        super.onResume()
        botonnav.selectedItemId = R.id.nav_perfil
        consultarFavoritos()
    }

    private fun cargarNombreUsuario() {
        val prefs = getSharedPreferences("SesionPrefs", MODE_PRIVATE)
        val savedName = prefs.getString("username", "")
        if (!savedName.isNullOrEmpty()) {
            etNombreUsuario.setText(savedName)
        }
    }

    private fun guardarNombreUsuario() {
        val nuevoNombre = etNombreUsuario.text.toString().trim()
        if (nuevoNombre.isNotEmpty()) {
            val prefs = getSharedPreferences("SesionPrefs", MODE_PRIVATE)
            prefs.edit().putString("username", nuevoNombre).apply()
            Toast.makeText(this, "Nombre de usuario guardado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ingresa un nombre válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun consultarFavoritos() {
        val favoritos = dbhelper.obteneralbumesfav()
        gridfavoritosPerfil.removeAllViews()

        if (favoritos.isEmpty()) {
            tvEstadoFavoritosPerfil.visibility = View.VISIBLE
            tvEstadoFavoritosPerfil.text = "No tienes ningún álbum en favoritos"
        } else {
            tvEstadoFavoritosPerfil.visibility = View.GONE
            mostrarAlbunesFavoritos(favoritos)
        }
    }

    private fun mostrarAlbunesFavoritos(favoritos: List<albumfav>) {
        gridfavoritosPerfil.removeAllViews()

        for (album in favoritos) {
            val vistaalbum = layoutInflater.inflate(
                R.layout.item_favorito,
                gridfavoritosPerfil,
                false
            )

            val imgalbun = vistaalbum.findViewById<ImageView>(R.id.imgfavorito)

            Glide.with(this)
                .load(album.imagen)
                .centerCrop()
                .into(imgalbun)

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

            gridfavoritosPerfil.addView(vistaalbum)
        }
    }

    private fun menuNavegacion() {
        botonnav.selectedItemId = R.id.nav_perfil

        botonnav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_favoritos -> {
                    startActivity(Intent(this, Favact::class.java))
                    finish()
                    true
                }
                R.id.nav_sensores -> {
                    startActivity(Intent(this, SensorActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> true
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