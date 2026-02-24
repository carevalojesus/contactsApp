package com.carevalojesus.contactsapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Define la tabla 'contacts' para la base de datos Room.
 * Cada instancia de esta clase representa una fila en la tabla.
 */
@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String = "",
    val photoUri: String? = null,
    val isFavorite: Boolean = false
) {
    val fullName: String get() = "$firstName $lastName".trim()
}
