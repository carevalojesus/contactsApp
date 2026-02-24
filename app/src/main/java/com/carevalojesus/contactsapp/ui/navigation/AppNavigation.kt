package com.carevalojesus.contactsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.carevalojesus.contactsapp.ui.screens.AddEditContactScreen
import com.carevalojesus.contactsapp.ui.screens.ContactListScreen
import com.carevalojesus.contactsapp.ui.screens.FavoritesScreen
import com.carevalojesus.contactsapp.ui.viewmodel.ContactViewModel

// Define las rutas de la aplicación para una gestión centralizada y segura.
sealed class Screen(val route: String) {
    object ContactList : Screen("contacts")
    object Favorites : Screen("favorites")
    object AddContact : Screen("add_contact")
    object EditContact : Screen("edit_contact/{contactId}") {
        fun createRoute(contactId: Int) = "edit_contact/$contactId"
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    // El ViewModel se crea a nivel del NavHost para que sea compartido por todas las pantallas.
    val viewModel: ContactViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.ContactList.route) {
        composable(Screen.ContactList.route) {
            ContactListScreen(
                viewModel = viewModel,
                onAddContact = { navController.navigate(Screen.AddContact.route) },
                onEditContact = { contactId ->
                    navController.navigate(Screen.EditContact.createRoute(contactId))
                }
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel,
                onEditContact = { contactId ->
                    navController.navigate(Screen.EditContact.createRoute(contactId))
                }
            )
        }
        composable(Screen.AddContact.route) {
            AddEditContactScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = Screen.EditContact.route,
            arguments = listOf(navArgument("contactId") { type = NavType.IntType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getInt("contactId")
            AddEditContactScreen(
                viewModel = viewModel,
                navController = navController,
                contactId = contactId
            )
        }
    }
}
