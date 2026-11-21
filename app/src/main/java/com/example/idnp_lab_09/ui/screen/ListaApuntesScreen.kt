package com.example.idnp_lab_09.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.idnp_lab_09.data.local.entity.DocumentoEntity
import com.example.idnp_lab_09.ui.viewmodel.DocumentoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla principal: muestra los apuntes guardados en Room con una LazyColumn (Tarea 5)
 * y permite editarlos o eliminarlos (Tarea 6).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaApuntesScreen(
    viewModel: DocumentoViewModel,
    onNuevoApunte: () -> Unit,
    onEditarApunte: (DocumentoEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // collectAsState convierte el StateFlow en estado de Compose: cada emisión de Room
    // provoca una recomposición de la lista.
    val documentos by viewModel.documentos.collectAsState()
    val total by viewModel.totalDocumentos.collectAsState()

    var documentoAEliminar by remember { mutableStateOf<DocumentoEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusDocs - Mis Apuntes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onNuevoApunte) {
                Text("+  Nuevo apunte")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        if (documentos.isEmpty()) {
            EstadoVacio(Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "$total apunte(s) guardado(s) en campus_docs.db",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // key = id: Compose reutiliza cada tarjeta aunque la lista cambie de orden
                items(items = documentos, key = { it.id }) { documento ->
                    ApunteCard(
                        documento = documento,
                        onEditar = { onEditarApunte(documento) },
                        onEliminar = { documentoAEliminar = documento }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación antes de borrar
    documentoAEliminar?.let { documento ->
        AlertDialog(
            onDismissRequest = { documentoAEliminar = null },
            title = { Text("Eliminar apunte") },
            text = { Text("¿Seguro que deseas eliminar \"${documento.titulo}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarDocumento(documento)
                    documentoAEliminar = null
                    scope.launch {
                        val resultado = snackbarHostState.showSnackbar(
                            message = "Apunte eliminado",
                            actionLabel = "Deshacer",
                            duration = SnackbarDuration.Short
                        )
                        if (resultado == SnackbarResult.ActionPerformed) {
                            viewModel.restaurarDocumento(documento)
                        }
                    }
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { documentoAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

/** Tarjeta que representa un registro de la tabla "documentos". */
@Composable
private fun ApunteCard(
    documento: DocumentoEntity,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditar),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = documento.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${documento.curso} · ${documento.autor}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (documento.descripcion.isNotBlank()) {
                Text(
                    text = documento.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${documento.numeroPaginas} pág. · ${formatearFecha(documento.fechaRegistro)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onEditar) { Text("Editar") }
                TextButton(onClick = onEliminar) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun EstadoVacio(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Aún no hay apuntes guardados.\nPulsa \"Nuevo apunte\" para registrar el primero.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Convierte el epoch en milisegundos guardado en Room a texto legible (dd/MM/yyyy HH:mm). */
private fun formatearFecha(epochMs: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(epochMs))
