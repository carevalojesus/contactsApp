package com.carevalojesus.contactsapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.carevalojesus.contactsapp.data.model.Contact
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object para la tabla de contactos.
 * Define los métodos para interactuar con la base de datos.
 */
@Dao
interface ContactDao {

    // Obtiene todos los contactos ordenados por nombre en un Flow, que emite automáticamente cuando los datos cambian.
    @Query("SELECT * FROM contacts ORDER BY firstName ASC, lastName ASC")
    fun getAllContacts(): Flow<List<Contact>>

    // Obtiene solo los contactos marcados como favoritos.
    @Query("SELECT * FROM contacts WHERE isFavorite = 1 ORDER BY firstName ASC, lastName ASC")
    fun getFavorites(): Flow<List<Contact>>

    // Obtiene un contacto específico por su ID.
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Int): Contact?

    // Inserta un nuevo contacto. Si ya existe, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    // Actualiza un contacto existente.
    @Update
    suspend fun updateContact(contact: Contact)

    // Elimina un contacto.
    @Delete
    suspend fun deleteContact(contact: Contact)

    // Cambia el estado de favorito de un contacto usando su ID.
    @Query("UPDATE contacts SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Int)
}
