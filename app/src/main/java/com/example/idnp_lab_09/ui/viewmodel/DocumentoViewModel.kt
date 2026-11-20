package com.example.idnp_lab_09.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.idnp_lab_09.data.local.entity.DocumentoEntity
import com.example.idnp_lab_09.data.repository.DocumentoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado de los documentos y el formulario de registro (Tarea 4).
 * Conecta la UI de Jetpack Compose con el [DocumentoRepository] de forma reactiva y segura.
 */
class DocumentoViewModel(private val repository: DocumentoRepository) : ViewModel() {

    // Lista reactiva de documentos almacenados en Room
    val documentos: StateFlow<List<DocumentoEntity>> = repository.todosLosDocumentos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ── ESTADOS DEL FORMULARIO DE REGISTRO (TAREA 4) ──────────────────────────

    var titulo by mutableStateOf("")
        private set

    var curso by mutableStateOf("")
        private set

    var autor by mutableStateOf("")
        private set

    var descripcion by mutableStateOf("")
        private set

    var numeroPaginas by mutableStateOf("")
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun actualizarTitulo(nuevoTitulo: String) {
        titulo = nuevoTitulo
        mensajeError = null
    }

    fun actualizarCurso(nuevoCurso: String) {
        curso = nuevoCurso
        mensajeError = null
    }

    fun actualizarAutor(nuevoAutor: String) {
        autor = nuevoAutor
        mensajeError = null
    }

    fun actualizarDescripcion(nuevaDescripcion: String) {
        descripcion = nuevaDescripcion
    }

    fun actualizarNumeroPaginas(nuevoNumero: String) {
        numeroPaginas = nuevoNumero
    }

    /**
     * Valida e inserta un nuevo documento en la base de datos a través del repositorio.
     */
    fun guardarDocumento(onSuccess: () -> Unit) {
        if (titulo.isBlank() || curso.isBlank() || autor.isBlank()) {
            mensajeError = "Por favor complete los campos obligatorios (Título, Curso y Autor)."
            return
        }

        val paginasInt = numeroPaginas.toIntOrNull() ?: 1

        viewModelScope.launch {
            val nuevoDocumento = DocumentoEntity(
                titulo = titulo.trim(),
                curso = curso.trim(),
                autor = autor.trim(),
                descripcion = descripcion.trim(),
                numeroPaginas = paginasInt
            )
            repository.insertar(nuevoDocumento)

            // Limpiar campos del formulario tras inserción exitosa
            titulo = ""
            curso = ""
            autor = ""
            descripcion = ""
            numeroPaginas = ""
            mensajeError = null

            onSuccess()
        }
    }

    /**
     * Factory para inyectar el [DocumentoRepository] en el [DocumentoViewModel].
     */
    class Factory(private val repository: DocumentoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DocumentoViewModel::class.java)) {
                return DocumentoViewModel(repository) as T
            }
            throw IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}
