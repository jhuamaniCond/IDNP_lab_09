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
 * ViewModel que gestiona el estado de los documentos (Tareas 4, 5 y 6).
 * Conecta la UI de Jetpack Compose con el [DocumentoRepository] de forma reactiva y segura.
 */
class DocumentoViewModel(private val repository: DocumentoRepository) : ViewModel() {

    // ── LISTA REACTIVA (TAREA 5) ──────────────────────────────────────────────

    // Lista de documentos almacenados en Room; se reemite ante cada INSERT/UPDATE/DELETE
    val documentos: StateFlow<List<DocumentoEntity>> = repository.todosLosDocumentos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Total de filas de la tabla (contador del encabezado de la lista)
    val totalDocumentos: StateFlow<Int> = repository.totalDocumentos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
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

    // Documento que se está editando; null = el formulario registra uno nuevo (Tarea 6)
    var documentoEnEdicion by mutableStateOf<DocumentoEntity?>(null)
        private set

    val enModoEdicion: Boolean
        get() = documentoEnEdicion != null

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

    // ── EDICIÓN (TAREA 6) ─────────────────────────────────────────────────────

    /** Prepara el formulario para registrar un documento nuevo. */
    fun iniciarRegistro() {
        documentoEnEdicion = null
        limpiarFormulario()
    }

    /** Carga los datos de [documento] en el formulario para editarlo. */
    fun iniciarEdicion(documento: DocumentoEntity) {
        documentoEnEdicion = documento
        titulo = documento.titulo
        curso = documento.curso
        autor = documento.autor
        descripcion = documento.descripcion
        numeroPaginas = documento.numeroPaginas.toString()
        mensajeError = null
    }

    /**
     * Valida el formulario y, según el modo, inserta un documento nuevo o
     * actualiza el que se está editando.
     */
    fun guardarDocumento(onSuccess: () -> Unit) {
        if (titulo.isBlank() || curso.isBlank() || autor.isBlank()) {
            mensajeError = "Por favor complete los campos obligatorios (Título, Curso y Autor)."
            return
        }

        val paginasInt = numeroPaginas.toIntOrNull() ?: 1
        val original = documentoEnEdicion

        viewModelScope.launch {
            if (original == null) {
                repository.insertar(
                    DocumentoEntity(
                        titulo = titulo.trim(),
                        curso = curso.trim(),
                        autor = autor.trim(),
                        descripcion = descripcion.trim(),
                        numeroPaginas = paginasInt
                    )
                )
            } else {
                // copy() conserva id y fechaRegistro: Room ubica la fila por el id
                repository.actualizar(
                    original.copy(
                        titulo = titulo.trim(),
                        curso = curso.trim(),
                        autor = autor.trim(),
                        descripcion = descripcion.trim(),
                        numeroPaginas = paginasInt
                    )
                )
            }

            documentoEnEdicion = null
            limpiarFormulario()
            onSuccess()
        }
    }

    // ── ELIMINACIÓN (TAREA 6) ─────────────────────────────────────────────────

    /** Elimina [documento] de Room; la LazyColumn se actualiza sola vía Flow. */
    fun eliminarDocumento(documento: DocumentoEntity) {
        viewModelScope.launch { repository.eliminar(documento) }
    }

    /** Deshace una eliminación reinsertando el documento con su mismo id. */
    fun restaurarDocumento(documento: DocumentoEntity) {
        viewModelScope.launch { repository.insertar(documento) }
    }

    private fun limpiarFormulario() {
        titulo = ""
        curso = ""
        autor = ""
        descripcion = ""
        numeroPaginas = ""
        mensajeError = null
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
