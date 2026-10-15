package com.example.reviewly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {


    private lateinit var etCorreo: EditText
    private lateinit var etPass: EditText
    private lateinit var BtnIngresar: Button

    private lateinit var dbHelper: Databasehelper




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        etCorreo = findViewById(R.id.etCorreo)
        etPass = findViewById(R.id.etPass)
        BtnIngresar = findViewById(R.id.BtnIngresar)
        dbHelper = Databasehelper(this)

        val tvcrearcuenta = findViewById<TextView>(R.id.tvcrearcuenta)

        tvcrearcuenta.setOnClickListener {
            val intent = Intent(this, Activity_Registro::class.java)
            startActivity(intent)
        }

        BtnIngresar.setOnClickListener {
            iniciarSesion()
        }
    }

    //metodo para iniciar sesion

    private fun iniciarSesion(){
        val correo = etCorreo.text.toString().trim()
        val password = etPass.text.toString().trim()

        if(correo.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //validar si el usuario existe en la base de datos
        val resultado = dbHelper.validarUsuario(correo, password)

        //validar si el inicisio de sesion fue correcto

        if(resultado){
            //el resutlado es verdadero
            val intent = Intent(this, HomeActivity::class.java)

            Toast.makeText(this, "Inicio de sesion correcto", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }else{
            //el resultado es falso
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }
    }



}
