# ContactsApp - Aplicacion de Contactos con Jetpack Compose

Aplicacion Android de gestion de contactos desarrollada con **Jetpack Compose**, **Room**, **Navigation Compose** y arquitectura **MVVM**. Este proyecto sirve como guia practica para aprender los conceptos fundamentales del desarrollo Android moderno.

---

## Requisitos previos

- **Android Studio** Ladybug o superior
- **JDK 11** o superior
- **SDK minimo**: Android 8.0 (API 26)
- **SDK objetivo**: Android 15 (API 36)

---

## Como ejecutar el proyecto

1. Clonar el repositorio:
   ```bash
   git clone git@github.com:carevalojesus/contactsApp.git
   ```
2. Abrir el proyecto en **Android Studio**.
3. Esperar a que Gradle sincronice las dependencias.
4. Conectar un dispositivo fisico o iniciar un emulador.
5. Presionar **Run** (boton verde) o `Shift + F10`.

---

## Estructura del proyecto

```
app/src/main/java/com/carevalojesus/contactsapp/
|
|-- MainActivity.kt                  # Punto de entrada de la app
|
|-- data/
|   |-- model/
|   |   |-- Contact.kt              # Entidad Room (tabla de la BD)
|   |-- dao/
|   |   |-- ContactDao.kt           # Operaciones de base de datos (CRUD)
|   |-- database/
|       |-- ContactDatabase.kt      # Configuracion de la base de datos Room
|
|-- ui/
    |-- navigation/
    |   |-- AppNavigation.kt        # Rutas y navegacion entre pantallas
    |-- screens/
    |   |-- ContactListScreen.kt    # Pantalla principal (lista de contactos)
    |   |-- AddEditContactScreen.kt # Pantalla para agregar/editar contacto
    |   |-- FavoritesScreen.kt      # Pantalla de contactos favoritos
    |-- viewmodel/
    |   |-- ContactViewModel.kt     # Logica de negocio y estado de la UI
    |-- theme/
        |-- Color.kt, Theme.kt, Type.kt  # Tema visual de la app
```

---

## Arquitectura MVVM

La app sigue el patron **Model - View - ViewModel**:

```
[Vista (Screens)]  <-->  [ViewModel]  <-->  [Room DAO]  <-->  [SQLite]
```

| Capa | Archivo | Responsabilidad |
|------|---------|-----------------|
| **Model** | `Contact.kt` | Define la estructura de datos (entidad Room) |
| **Model** | `ContactDao.kt` | Define las consultas SQL a la base de datos |
| **Model** | `ContactDatabase.kt` | Crea y gestiona la base de datos |
| **ViewModel** | `ContactViewModel.kt` | Maneja el estado y la logica de negocio |
| **View** | `*Screen.kt` | Composables que renderizan la interfaz |

---

## Conceptos clave explicados

### 1. Room Database (Persistencia de datos)

Room es la libreria oficial de Android para bases de datos SQLite. Se compone de 3 partes:

- **Entity** (`Contact.kt`): Clase que representa una tabla. Cada propiedad es una columna.
  ```kotlin
  @Entity(tableName = "contacts")
  data class Contact(
      @PrimaryKey(autoGenerate = true) val id: Int = 0,
      val firstName: String,
      val lastName: String,
      val phone: String,
      // ...
  )
  ```

- **DAO** (`ContactDao.kt`): Interfaz que define las operaciones (insertar, actualizar, eliminar, consultar).
  ```kotlin
  @Dao
  interface ContactDao {
      @Query("SELECT * FROM contacts ORDER BY firstName ASC")
      fun getAllContacts(): Flow<List<Contact>>

      @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun insertContact(contact: Contact)
  }
  ```

- **Database** (`ContactDatabase.kt`): Clase abstracta que conecta las entidades con los DAOs. Usa el patron **Singleton** para tener una sola instancia.

### 2. Jetpack Compose (Interfaz de usuario)

En lugar de XML, la UI se construye con funciones `@Composable`:

```kotlin
@Composable
fun ContactItem(contact: Contact) {
    Card {
        Text(contact.fullName)
        Text(contact.phone)
    }
}
```

Conceptos importantes:
- **`remember`**: Guarda un valor en memoria mientras el composable este activo.
- **`mutableStateOf`**: Crea un estado reactivo. Cuando cambia, la UI se recompone automaticamente.
- **`collectAsState()`**: Convierte un `Flow` en un estado observable por Compose.

### 3. Navigation Compose (Navegacion)

Se definen rutas como strings y se navega entre pantallas:

```kotlin
// Definir rutas
sealed class Screen(val route: String) {
    object ContactList : Screen("contacts")
    object AddContact : Screen("add_contact")
    object EditContact : Screen("edit_contact/{contactId}")
}

// Navegar
navController.navigate(Screen.AddContact.route)
```

### 4. ViewModel (Estado y logica)

El `ViewModel` sobrevive a cambios de configuracion (como rotar la pantalla) y expone datos como `StateFlow`:

```kotlin
class ContactViewModel(application: Application) : AndroidViewModel(application) {
    val contacts: StateFlow<List<Contact>> = ...

    fun addContact(contact: Contact) = viewModelScope.launch {
        contactDao.insertContact(contact)
    }
}
```

### 5. Flow y Coroutines (Asincronismo)

- **`Flow`**: Emite datos de forma reactiva. Cuando se inserta un contacto, la lista se actualiza automaticamente.
- **`suspend`**: Marca funciones que se ejecutan en segundo plano sin bloquear la UI.
- **`viewModelScope.launch`**: Lanza una coroutine dentro del ciclo de vida del ViewModel.

### 6. Coil (Carga de imagenes)

Se usa para cargar fotos desde una URI (camara o galeria):

```kotlin
AsyncImage(
    model = photoUri,
    contentDescription = "Foto de perfil",
    modifier = Modifier.size(48.dp).clip(CircleShape)
)
```

### 7. FileProvider (Camara)

Para tomar fotos con la camara, se necesita:
1. Declarar el `FileProvider` en `AndroidManifest.xml`.
2. Definir rutas accesibles en `res/xml/file_paths.xml`.
3. Crear una URI temporal donde se guardara la foto.

---

## Funcionalidades de la app

- **Ver contactos**: Lista con nombre completo, telefono y email.
- **Buscar**: Filtrar por nombre, apellido o telefono en tiempo real.
- **Agregar contacto**: Formulario con nombres, apellidos, telefono, email y foto.
- **Editar contacto**: Misma pantalla de agregar, precargada con los datos existentes.
- **Eliminar contacto**: Con dialogo de confirmacion.
- **Favoritos**: Marcar/desmarcar contactos como favoritos con animacion.
- **Foto de perfil**: Tomar foto con la camara o elegir de la galeria.

---

## Dependencias principales

| Libreria | Uso |
|----------|-----|
| `androidx.room` | Base de datos local (SQLite) |
| `androidx.navigation:navigation-compose` | Navegacion entre pantallas |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | ViewModel con Compose |
| `androidx.compose.material3` | Componentes de Material Design 3 |
| `androidx.compose.material:material-icons-extended` | Iconos adicionales |
| `io.coil-kt:coil-compose` | Carga de imagenes desde URI |
| `com.google.devtools.ksp` | Procesador de anotaciones para Room |

Las versiones se gestionan en `gradle/libs.versions.toml` (Version Catalog).

---

## Flujo de datos

```
Usuario toca "Guardar"
        |
        v
AddEditContactScreen llama viewModel.addContact(contact)
        |
        v
ContactViewModel ejecuta contactDao.insertContact(contact) en una coroutine
        |
        v
Room inserta en SQLite y notifica al Flow
        |
        v
ContactListScreen recibe la nueva lista via collectAsState()
        |
        v
La UI se recompone y muestra el nuevo contacto
```

---

## Ejercicios sugeridos

1. Agregar un campo de **direccion** al contacto.
2. Implementar navegacion a la pantalla de **Favoritos** desde la lista principal.
3. Agregar **ordenamiento** (por nombre, por fecha de creacion).
4. Implementar **validacion** del formato de email y telefono.
5. Agregar un **contador** de contactos en la TopAppBar.
