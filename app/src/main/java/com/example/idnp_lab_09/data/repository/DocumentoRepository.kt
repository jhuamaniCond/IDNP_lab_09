package com.example.idnp_lab_09.data.repository

import com.example.idnp_lab_09.data.local.dao.DocumentoDao
import com.example.idnp_lab_09.data.local.entity.DocumentoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de documentos para CampusDocs.
 *
 * Actúa como la **única fuente de verdad** (Single Source of Truth) para los
 * datos de documentos en la aplicación. La capa de UI (ViewModel → Compose)
 * nunca accede directamente al DAO: siempre pasa por este repositorio.
 *
 * Responsabilidades:
 *  - Exponer las operaciones del [DocumentoDao] con una API limpia y
 *    desacoplada de los detalles de Room.
 *  - En el futuro, centralizar aquí la lógica de fusión entre fuentes
 *    remotas (API REST) y la caché local (Room), sin modificar la UI.
 *
 * Patrón de inyección:
 *  El repositorio recibe el [dao] por constructor para facilitar la
 *  sustitución por dobles de prueba (fakes/mocks) en tests unitarios.
 *  En la Tarea 3 (configurar RoomDatabase), se instanciará como:
 *
 *  ```kotlin
 *  val db  = Room.databaseBuilder(context, CampusDocsDatabase::class.java, "campus_docs.db").build()
 *  val repo = DocumentoRepository(db.documentoDao())
 *  ```
 *
 * @param dao Instancia de [DocumentoDao] proporcionada por [CampusDocsDatabase].
 */
class DocumentoRepository(private val dao: DocumentoDao) {

    // ── CONSULTAS (delegación directa al DAO) ─────────────────────────────────

    /**
     * Flujo reactivo con todos los documentos ordenados del más reciente al
     * más antiguo. Se expone directamente a la capa de presentación mediante
     * [androidx.lifecycle.ViewModel] y [kotlinx.coroutines.flow.stateIn].
     */
    val todosLosDocumentos: Flow<List<DocumentoEntity>> = dao.obtenerTodos()

    /**
     * Total de documentos en la tabla, como flujo reactivo.
     * Útil para mostrar badges o mensajes de lista vacía en la UI.
     */
    val totalDocumentos: Flow<Int> = dao.contarDocumentos()

    /**
     * Busca un documento por su clave primaria.
     *
     * @param id Identificador único del documento.
     * @return [DocumentoEntity] si existe, [null] en caso contrario.
     */
    suspend fun obtenerPorId(id: Int): DocumentoEntity? = dao.obtenerPorId(id)

    /**
     * Flujo reactivo filtrado por [curso].
     *
     * @param curso Nombre exacto del curso a filtrar.
     * @return [Flow] que emite la lista actualizada ante cada cambio en la tabla.
     */
    fun buscarPorCurso(curso: String): Flow<List<DocumentoEntity>> =
        dao.buscarPorCurso(curso)

    // ── INSERCIONES ───────────────────────────────────────────────────────────

    /**
     * Inserta un nuevo documento en la base de datos.
     *
     * Función suspend: debe llamarse desde un [kotlinx.coroutines.CoroutineScope]
     * (típicamente [androidx.lifecycle.viewModelScope] en el ViewModel).
     *
     * @param documento Documento a persistir. El [DocumentoEntity.id] debe ser 0.
     * @return rowid asignado por SQLite al nuevo registro.
     */
    suspend fun insertar(documento: DocumentoEntity): Long = dao.insertar(documento)

    /**
     * Inserta una lista de documentos en una única transacción Room.
     *
     * @param documentos Lista de documentos a persistir.
     * @return Lista de rowids asignados, en el mismo orden de la entrada.
     */
    suspend fun insertarTodos(documentos: List<DocumentoEntity>): List<Long> =
        dao.insertarTodos(documentos)

    // ── ACTUALIZACIÓN Y ELIMINACIÓN (TAREA 6) ─────────────────────────────────

    /**
     * Sobrescribe un documento existente (lo localiza por su id).
     *
     * @return número de filas modificadas.
     */
    suspend fun actualizar(documento: DocumentoEntity): Int = dao.actualizar(documento)

    /**
     * Elimina un documento de la base de datos.
     *
     * @return número de filas eliminadas.
     */
    suspend fun eliminar(documento: DocumentoEntity): Int = dao.eliminar(documento)
}
