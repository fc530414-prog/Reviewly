package com.example.reviewly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Toast
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    private lateinit var etCorreo: EditText
    private lateinit var etPass: EditText
    private lateinit var BtnIngresar: Button

    private lateinit var dbHelper: Databasehelper


    //crear objetos para el funcionamiento con Google

    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var  btngoogle : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        etCorreo = findViewById(R.id.etCorreo)
        etPass = findViewById(R.id.etPass)
        BtnIngresar = findViewById(R.id.BtnIngresar)
        dbHelper = Databasehelper(this)

        btngoogle = findViewById(R.id.btngoogle)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        val tvcrearcuenta = findViewById<TextView>(R.id.tvcrearcuenta)

        tvcrearcuenta.setOnClickListener {
            val intent = Intent(this, Activity_Registro::class.java)
            startActivity(intent)
        }

        BtnIngresar.setOnClickListener {
            iniciarSesion()
        }

        btngoogle.setOnClickListener {
            iniciarSesionGoogle()
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


    // 3MÉTODO PARA AUTENTICAR EN FIREBSAE
    private fun auntefire(idToken: String){

        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) {

                inicio ->

            //validra si el iniaior de sesion es correto
            if(inicio.isSuccessful){

                //crear variable para guardar al usuairoa ctual
                val usuario = auth.currentUser

                Toast.makeText(this, "Bienbenido ${usuario?.displayName}", Toast.LENGTH_SHORT).show()

                //enviar al usuario al HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

                finish()
            }else{
                Toast.makeText(this, "Error al iniciar con firebase ", Toast.LENGTH_SHORT).show()
            }

        }

    }



    // MÉTODO PARA PROCESAR LA CREDENCIAL
    private fun procesarcredencial(credential: Credential){

        if(credential is CustomCredential && credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){

            try {

            // validar la credencial del provedor
                val credentialGoogle = GoogleIdTokenCredential.createFrom(
                    credential.data
                )

                // inicar el proceso de autentiascion
                auntefire(credentialGoogle.idToken)


            }catch (e : GoogleIdTokenParsingException){

                Toast.makeText(this, "Credencial invalida ", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // 5.MÉTODO PARA INICIAR SESIÓN
    private fun iniciarSesionGoogle(){

        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)).build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        //procesor para iniciar con firebase
        lifecycleScope.launch {

            try {

                //verificar que el resultado sea correcto
                val resultado = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = solicitud
                )

                //llamar al metodo que permite procesar la credencial
                procesarcredencial(resultado.credential)



            } catch (e: GetCredentialException){
                Toast.makeText(this@MainActivity, "Error al iniciar", Toast.LENGTH_SHORT).show()

            }

        }


    }



}
