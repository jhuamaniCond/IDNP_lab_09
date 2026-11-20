package com.example.idnp_lab_09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.idnp_lab_09.data.local.CampusDocsDatabase
import com.example.idnp_lab_09.data.repository.DocumentoRepository
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
                SubirApunteScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
