package com.example.reviewly

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// crear clase dabatabehelper que hereda de SQLiteOpenHelper

class Databasehelper(context: Context) : SQLiteOpenHelper(

    context,
    DATABSE_NAME,
    null,
    DATABASE_VERSION

) {
    //datos de la base de datos
    companion object {
        //informacion de la bd
        private const val DATABSE_NAME = "music.db"
        private const val DATABASE_VERSION = 2

        //informacion de las trabjas en la bd
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_CONTRASEÑA = "contraseña"
    }

    //metodo oncreate - para crear la bd
    override fun onCreate(db: SQLiteDatabase?) {
        //crea la tabla
        val createTable = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL,
                $COL_CONTRASEÑA TEXT NOT NULL
            )
        """.trimIndent()

        //crear tabla usuario
        db?.execSQL(createTable)




    }



    // metodo onupagrande

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    //metodo para registrar un usuario es decir un insert
    fun registrarUsuario(nombre: String, correo: String, password: String) : Boolean {

        //crear variable para escribir en la base de datos
        val db = writableDatabase

        //insertar los datos
        val datos = ContentValues().apply{
            put(COL_NOMBRE, nombre )
            put(COL_CORREO, correo)
            put(COL_CONTRASEÑA, password)

        }
        //ejecutar insercion de datos
        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        //cerrar conexcion de la bd
        db.close()

        //regresar el valor obtenido de la insecion si es verdadero o es falso
        return resultado != -1L
    }

    //metodo para validar inicio de sesion
    fun validarUsuario(correo: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIOS WHERE $COL_CORREO = ? AND $COL_CONTRASEÑA = ?",
            arrayOf(correo, password)
        )
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }
}