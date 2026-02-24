package com.carevalojesus.contactsapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.carevalojesus.contactsapp.data.dao.ContactDao
import com.carevalojesus.contactsapp.data.database.ContactDatabase
import com.carevalojesus.contactsapp.data.model.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val contactDao: ContactDao = ContactDatabase.getDatabase(application).contactDao()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    // Flujo base que cambia entre todos los contactos y solo los favoritos.
    val contacts: StateFlow<List<Contact>> = _showFavoritesOnly.flatMapLatest { showFavorites ->
        if (showFavorites) {
            contactDao.getFavorites()
        } else {
            contactDao.getAllContacts()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Flujo que combina la lista de contactos con la búsqueda, para ContactListScreen.
    val filteredContacts: StateFlow<List<Contact>> = combine(contacts, _searchQuery) { contacts, query ->
        if (query.isBlank()) {
            contacts
        } else {
            contacts.filter {
                it.firstName.contains(query, ignoreCase = true) ||
                it.lastName.contains(query, ignoreCase = true) ||
                it.phone.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flujo dedicado solo para la pantalla de favoritos.
    val favoriteContacts: StateFlow<List<Contact>> = contactDao.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleShowFavorites() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    suspend fun getContactById(id: Int): Contact? {
        return contactDao.getContactById(id)
    }

    fun addContact(contact: Contact) = viewModelScope.launch {
        contactDao.insertContact(contact)
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        contactDao.updateContact(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        contactDao.deleteContact(contact)
    }

    fun toggleFavorite(id: Int) = viewModelScope.launch {
        contactDao.toggleFavorite(id)
    }
}
