package com.carevalojesus.contactsapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.carevalojesus.contactsapp.data.dao.ContactDao
import com.carevalojesus.contactsapp.data.model.Contact

/**
 * Clase de la base de datos de Room.
 * Define las entidades y la versión de la base de datos.
 */
@Database(entities = [Contact::class], version = 2, exportSchema = false)
abstract class ContactDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        // La anotación @Volatile asegura que el valor de INSTANCE sea siempre el más actualizado
        // y visible para todos los hilos de ejecución.
        @Volatile
        private var INSTANCE: ContactDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos (patrón Singleton).
         * Si la instancia no existe, la crea de forma segura para hilos.
         */
        fun getDatabase(context: Context): ContactDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contacts_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                // retorna la instancia
                instance
            }
        }
    }
}
