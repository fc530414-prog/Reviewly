package com.example.reviewly

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class detaActivity : AppCompatActivity() {

    companion object{
        private const val CANAL_ID = "canal_general"
        private const val NOTIFICACION_ID = 1
        private const val CODIGO_PERMISO = 100
    }

    private lateinit var imgPortadaDetalle: ImageView
    private lateinit var tvNombreAlbumDetalle: TextView
    private lateinit var tvArtistaAlbumDetalle: TextView
    private lateinit var tvAlbumDetalle: TextView
    private lateinit var btnFavorito: ImageButton

    private lateinit var db: Databasehelper

    private var albmufavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_deta)

        imgPortadaDetalle = findViewById(R.id.imgPortadaDetalle)
        tvNombreAlbumDetalle = findViewById(R.id.tvNombreAlbumDetalle)
        tvArtistaAlbumDetalle = findViewById(R.id.tvArtistaAlbumDetalle)
        tvAlbumDetalle = findViewById(R.id.tvAlbumDetalle)
        btnFavorito = findViewById(R.id.btnFavorito)

        db = Databasehelper(this)

        configurarEfectoCristal()

        mostrarInformacionAlbm()
    }

    // Método para mostrar la información de un álbum o elemento
    private fun mostrarInformacionAlbm() {

        val idAlbum = intent.getLongExtra("ID_ALBUM", 1)
        val nombreAlbum = intent.getStringExtra("NOMBRE_ALBUM") ?: "Sin nombre"
        val artistaAlbum = intent.getStringExtra("ARTISTA_ALBUM") ?: "Desconocido"
        val generoAlbum = intent.getStringExtra("GENERO_ALBUM") ?: "N/A"
        val cancionesAlbum = intent.getIntExtra("CANCIONES_ALBUM", 1)
        val fecha = intent.getStringExtra("FECHA_ALBUM") ?: "2026"
        val imagenAlbum = intent.getStringExtra("IMAGEN_ALBUM") ?: ""
        val descripcionAlbum = intent.getStringExtra("DESCRIPCION_ALBUM") ?: ""

        tvNombreAlbumDetalle.text = nombreAlbum
        tvArtistaAlbumDetalle.text = artistaAlbum

        if (descripcionAlbum.isNotEmpty()) {
            tvAlbumDetalle.text = "$generoAlbum - $descripcionAlbum"
        } else {
            tvAlbumDetalle.text = "$generoAlbum - $cancionesAlbum canciones"
        }

        // Mostrar la imagen en la tarjeta
        Glide.with(this@detaActivity)
            .load(imagenAlbum)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(imgPortadaDetalle)

        // Verificar si el elemento existe como favorito en la BD
        if (idAlbum != -1L) {
            albmufavorito = db.existealbumfavorito(idAlbum)
            if (albmufavorito) {
                btnFavorito.setImageResource(R.drawable.ic_favorite_filled)
            } else {
                btnFavorito.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        // Evento del botón guardado / favorito conectado a la base de datos
        btnFavorito.setOnClickListener {
            if (albmufavorito) {
                val eliminar = db.eliminaralbumfavorito(idAlbum)
                if (eliminar) {
                    albmufavorito = false
                    btnFavorito.setImageResource(R.drawable.ic_favorite_border)
                    Toast.makeText(this, "Elemento $nombreAlbum eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }
            } else {
                val guardado = db.guardarAlbumFavoritos(
                    albumId = idAlbum,
                    nombreAlbum = nombreAlbum,
                    artista = artistaAlbum,
                    genero = generoAlbum,
                    canciones = cancionesAlbum,
                    fecha = fecha,
                    imagen = imagenAlbum
                )

                if (guardado) {
                    albmufavorito = true
                    btnFavorito.setImageResource(R.drawable.ic_favorite_filled)
                    Toast.makeText(this, "Elemento $nombreAlbum agregado a favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al guardar $nombreAlbum en favoritos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun configurarEfectoCristal() {
        val blurView = findViewById<BlurView>(R.id.blurViewDetalle) ?: return
        val radius = 16f
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = window.decorView.background

        blurView.setupWith(rootView, RenderScriptBlur(this))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)
        blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
        blurView.clipToOutline = true
    }


    //metdodo para crear canal de notificaciones

    private fun crearCanalNoti(){

        //evaluar el permiso del usuario para manjear notifiaciones
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val canal = NotificationChannel(
                CANAL_ID,
                "Notificaciones generales",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            canal.description = "Canal de envio de ntifaciones sobre la BD"

            //indicar el uso de notificaciones

            val adminNotificaciones = getSystemService(NotificationManager::class.java)

            adminNotificaciones.createNotificationChannel(canal)

        }


    }

    //metodo para mostrar la notifiacion
    private fun mostrarnoti(){
        //verificar los permisos seleccionados por el usuario
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && 
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // volver a pedir permiso al usuaio

            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),CODIGO_PERMISO)
            Toast.makeText(this, "Autoriza el envio de notificaciones", Toast.LENGTH_SHORT).show()
            return
        }

        val notificacion = NotificationCompat.Builder(this, CANAL_ID)

            .setContentTitle(" 67 ")
            .setContentText("Buena eleccion , se a guardado ")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

    }




}
