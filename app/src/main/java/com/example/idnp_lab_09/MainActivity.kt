package com.example.idnp_lab_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.idnp_lab_09.data.local.CampusDocsDatabase
import com.example.idnp_lab_09.data.repository.DocumentoRepository
import com.example.idnp_lab_09.ui.screen.ListaApuntesScreen
import com.example.idnp_lab_09.ui.screen.SubirApunteScreen
import com.example.idnp_lab_09.ui.theme.IDNP_lab_09Theme
import com.example.idnp_lab_09.ui.viewmodel.DocumentoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos Room y el repositorio
        val database = CampusDocsDatabase.obtenerInstancia(applicationContext)
        val repository = DocumentoRepository(database.documentoDao())

        enableEdgeToEdge()
        setContent {
            IDNP_lab_09Theme {
                val viewModel: DocumentoViewModel = viewModel(
                    factory = DocumentoViewModel.Factory(repository)
                )
                CampusDocsApp(viewModel)
            }
        }
    }
}

/** Pantallas de la aplicación. */
private enum class Pantalla { LISTA, FORMULARIO }

/**
 * Navegación simple por estado entre la lista (Tarea 5) y el formulario
 * de registro/edición (Tareas 4 y 6). Ambas pantallas comparten el mismo ViewModel.
 */
@Composable
private fun CampusDocsApp(viewModel: DocumentoViewModel) {
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.LISTA) }

    when (pantalla) {
        Pantalla.LISTA -> ListaApuntesScreen(
            viewModel = viewModel,
            onNuevoApunte = {
                viewModel.iniciarRegistro()
                pantalla = Pantalla.FORMULARIO
            },
            onEditarApunte = { documento ->
                viewModel.iniciarEdicion(documento)
                pantalla = Pantalla.FORMULARIO
            },
            modifier = Modifier.fillMaxSize()
        )

        Pantalla.FORMULARIO -> {
            // El botón Atrás del sistema vuelve a la lista sin guardar
            BackHandler { pantalla = Pantalla.LISTA }
            SubirApunteScreen(
                viewModel = viewModel,
                onGuardado = { pantalla = Pantalla.LISTA },
                onCancelar = { pantalla = Pantalla.LISTA },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
