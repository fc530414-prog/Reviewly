package com.example.reviewly

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class SensorActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    private lateinit var tvLuxValue: TextView
    private lateinit var tvEstadoModo: TextView
    private lateinit var rootLayoutSensor: ConstraintLayout
    private lateinit var botonnav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sensor)

        tvLuxValue = findViewById(R.id.tvLuxValue)
        tvEstadoModo = findViewById(R.id.tvEstadoModo)
        rootLayoutSensor = findViewById(R.id.rootLayoutSensor)
        botonnav = findViewById(R.id.bottomNavigation)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (lightSensor == null) {
            Toast.makeText(this, "Dispositivo sin sensor de luz ambiental", Toast.LENGTH_LONG).show()
        }

        menuNavegacion()
    }

    override fun onResume() {
        super.onResume()
        botonnav.selectedItemId = R.id.nav_sensores
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lux = event.values[0]
            tvLuxValue.text = "$lux lx"

            if (lux < 50f) {
                // Ambiente oscuro -> Modo Oscuro / Nocturno suave adaptado a Reviewly
                rootLayoutSensor.setBackgroundColor(android.graphics.Color.parseColor("#121212"))
                tvEstadoModo.text = "🌙 Ambiente Oscuro: Modo nocturno activado para proteger tu vista."
            } else {
                // Ambiente iluminado -> Estilo vibrante original de Reviewly
                rootLayoutSensor.setBackgroundColor(android.graphics.Color.parseColor("#7D004E"))
                tvEstadoModo.text = "☀️ Ambiente Luminoso: Modo vibrante optimizado para luz ambiental."
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No requerido
    }

    private fun menuNavegacion() {
        botonnav.selectedItemId = R.id.nav_sensores

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
                R.id.nav_sensores -> true
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