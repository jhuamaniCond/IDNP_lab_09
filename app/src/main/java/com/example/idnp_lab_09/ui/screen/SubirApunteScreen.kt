package com.example.idnp_lab_09.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.idnp_lab_09.ui.viewmodel.DocumentoViewModel

/**
 * Pantalla de formulario para registrar un nuevo apunte (Tarea 4) o editar uno
 * existente (Tarea 6). Permite ingresar título, curso, autor, descripción y número de páginas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubirApunteScreen(
    viewModel: DocumentoViewModel,
    onGuardado: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editando = viewModel.enModoEdicion

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editando) "CampusDocs - Editar Apunte" else "CampusDocs - Registrar Apunte") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (editando) "Editar apunte #${viewModel.documentoEnEdicion?.id}" else "Formulario de Registro de Apuntes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = viewModel.titulo,
                onValueChange = { viewModel.actualizarTitulo(it) },
                label = { Text("Título del Apunte *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.curso,
                onValueChange = { viewModel.actualizarCurso(it) },
                label = { Text("Curso / Asignatura *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.autor,
                onValueChange = { viewModel.actualizarAutor(it) },
                label = { Text("Autor / Estudiante *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.descripcion,
                onValueChange = { viewModel.actualizarDescripcion(it) },
                label = { Text("Descripción / Comentarios") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = viewModel.numeroPaginas,
                onValueChange = { viewModel.actualizarNumeroPaginas(it) },
                label = { Text("Número de Páginas") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // Mensaje de error si falta algún campo obligatorio
            val errorMsg = viewModel.mensajeError
            if (errorMsg != null) {
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                // Al terminar la inserción/actualización se vuelve a la lista,
                // que ya muestra el cambio porque observa el Flow de Room.
                onClick = { viewModel.guardarDocumento(onSuccess = onGuardado) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (editando) "Guardar cambios" else "Guardar Apunte")
            }

            OutlinedButton(
                onClick = onCancelar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}
