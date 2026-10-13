package com.example.reviewly

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val tvcrearcuenta = findViewById<TextView>(R.id.tvcrearcuenta)






        tvcrearcuenta.setOnClickListener {
            val intent = Intent(this, Activity_Registro::class.java)
            startActivity(intent)
        }












    }
}