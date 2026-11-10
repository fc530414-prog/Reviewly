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
        private const val DATABASE_VERSION = 4

        //informacion de las tablas en la bd
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_CONTRASEÑA = "contraseña"

        //informacion de la tabla de favoritos
        private const val TABLE_FAVORITOS = "favoritos"
        private const val COL_ID_FAVORITO = "id"
        private const val COL_ALBUM_ID = "album_id"
        private const val COL_NOMBRE_ALUBM = "nombre_album"
        private const val COL_ARTISTA = "artista"
        private const val COL_GENERO = "genero"
        private const val COL_CANCIONES = "num_canciones"
        private const val COL_FECHA = "fecha"
        private const val COL_IMAGEN = "imagen"

        //Crear ambas tablas de la base de datos
        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL,
                $COL_CONTRASEÑA TEXT NOT NULL
            )
        """

        private const val CREATE_TABLE_FAVORITOS = """
            CREATE TABLE $TABLE_FAVORITOS (
                $COL_ID_FAVORITO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ALBUM_ID INTEGER UNIQUE NOT NULL,
                $COL_NOMBRE_ALUBM TEXT NOT NULL,
                $COL_ARTISTA TEXT, 
                $COL_GENERO TEXT,
                $COL_CANCIONES INTEGER,
                $COL_FECHA TEXT,
                $COL_IMAGEN TEXT
            )
        """
    }

    //metodo oncreate - para crear la bd
    override fun onCreate(db: SQLiteDatabase?) {
        //crear tabla de usuarios y favoritos
        db?.execSQL(CREATE_TABLE_USUARIOS)
        db?.execSQL(CREATE_TABLE_FAVORITOS)
    }

    // metodo onupgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //verificar la version de la BD para evitar borrar los datos
        if (oldVersion < 3) {
            db?.execSQL(CREATE_TABLE_FAVORITOS)
        }
    }

    //metodo para registrar un usuario es decir un insert
    fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {
        val db = writableDatabase

        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_CONTRASEÑA, password)
        }

        val resultado = db.insert(TABLE_USUARIOS, null, datos)
        db.close()

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

    //metodo para iniciar sesion consulta
    fun iniciarSesion(correo: String, password: String): Boolean {
        return validarUsuario(correo, password)
    }

    //Metodo para guardar la informacion del elemento en favoritos
    fun guardarAlbumFavoritos(
        albumId: Long,
        nombreAlbum: String,
        artista: String,
        genero: String,
        canciones: Int,
        fecha: String,
        imagen: String
    ): Boolean {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put(COL_ALBUM_ID, albumId)
            put(COL_NOMBRE_ALUBM, nombreAlbum)
            put(COL_ARTISTA, artista)
            put(COL_GENERO, genero)
            put(COL_CANCIONES, canciones)
            put(COL_FECHA, fecha)
            put(COL_IMAGEN, imagen)
        }

        val resultado = db.insertWithOnConflict(
            TABLE_FAVORITOS,
            null,
            valores,
            SQLiteDatabase.CONFLICT_IGNORE
        )
        db.close()

        return resultado != -1L
    }

    //metodo para eliminar el elemento de favoritos
    fun eliminaralbumfavorito(albumId: Long): Boolean {
        val db = writableDatabase

        val filasAfectadas = db.delete(
            TABLE_FAVORITOS,
            "$COL_ALBUM_ID = ?",
            arrayOf(albumId.toString())
        )
        db.close()

        return filasAfectadas > 0
    }

    //METODO PARA VERIFICAR SI UN ELEMENTO ESTA REGISTRADO EN FAVORITOS
    fun existealbumfavorito(albumId: Long): Boolean {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT $COL_ALBUM_ID FROM $TABLE_FAVORITOS WHERE $COL_ALBUM_ID = ?",
            arrayOf(albumId.toString())
        )

        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()

        return existe
    }
}
