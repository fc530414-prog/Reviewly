package com.example.reviewly

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Activity_Registro : AppCompatActivity() {
    //aqui se declara los elemenos
    private lateinit var etnombre: EditText
    private lateinit var etcorreo: EditText
    private lateinit var etpass: EditText
    private lateinit var btnregistrar: TextView


    // crear instancia del dabasehelper
    private lateinit var dbHelper: Databasehelper




    //metodo principal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)


        val tviniciarsesion = findViewById<TextView>(R.id.tvcrearcuenta)
        tviniciarsesion.setOnClickListener {
            finish()
        }

        //iniciarlizar los objetos del los compontentes
        etnombre = findViewById(R.id.etNombre)
        etcorreo = findViewById(R.id.etCorreo)
        etpass = findViewById(R.id.etPass)
        btnregistrar = findViewById(R.id.BtnIngresar)

        //iniciarlizar la base de datos en un objeto
        dbHelper = Databasehelper(this)

        //evento para registrar al usuario
        btnregistrar.setOnClickListener {
            //registrar al usuario
            registrarUsuario()
        }

    }

    //metodos por aqui
    private fun registrarUsuario() {
        //variables para guardar los texto
        val nombre = etnombre.text.toString().trim()
        val correo = etcorreo.text.toString().trim()
        val password = etpass.text.toString().trim()

        if(nombre.isEmpty() || correo.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Completa todos los campos si", Toast.LENGTH_SHORT).show()
            return
        }

        //variable que guarda el resutlado del registro y la llama BD
        val registro = dbHelper.registrarUsuario(nombre, correo, password)
        //validar que el resutlado de insercion sea verdadrero o falso
        if(registro) {
            //si el resigro es correco
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            //cierra la pantalla caual yregresa a la pantalla anterior
            finish()
        }else{
            //si el registro fallo
            Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()
        }



    }
}