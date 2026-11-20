package com.example.idnp_lab_09.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.idnp_lab_09.data.local.dao.DocumentoDao
import com.example.idnp_lab_09.data.local.entity.DocumentoEntity

/**
 * Base de datos Room para CampusDocs.
 *
 * Configura la base de datos SQLite local utilizando Room y define el patrón Singleton
 * para evitar abrir múltiples instancias concurrentes de la base de datos, lo cual
 * puede causar problemas de rendimiento y fugas de memoria.
 */
@Database(
    entities = [DocumentoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CampusDocsDatabase : RoomDatabase() {

    abstract fun documentoDao(): DocumentoDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDocsDatabase? = null

        private const val DATABASE_NAME = "campus_docs.db"

        /**
         * Obtiene la instancia única (Singleton) de la base de datos [CampusDocsDatabase].
         *
         * @param context Contexto de la aplicación.
         * @return Instancia singleton de [CampusDocsDatabase].
         */
        fun obtenerInstancia(context: Context): CampusDocsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDocsDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
