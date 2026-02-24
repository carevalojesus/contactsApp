package com.carevalojesus.contactsapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carevalojesus.contactsapp.ui.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: ContactViewModel,
    onEditContact: (Int) -> Unit
) {
    val favoriteContacts by viewModel.favoriteContacts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (favoriteContacts.isEmpty()) {
            Box(modifier = Modifier.padding(padding)) {
                EmptyState(message = "No tienes contactos favoritos.\n¡Márcalos con un corazón!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteContacts, key = { it.id }) { contact ->
                    ContactItem(
                        contact = contact,
                        onItemClick = { onEditContact(contact.id) },
                        onToggleFavorite = { viewModel.toggleFavorite(contact.id) },
                        onDelete = { viewModel.deleteContact(contact) }
                    )
                }
            }
        }
    }
}
