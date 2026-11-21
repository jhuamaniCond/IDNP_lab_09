package com.example.idnp_lab_09.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.idnp_lab_09.data.local.entity.DocumentoEntity
import kotlinx.coroutines.flow.Flow

/**
 * TAREA 2 — DAO (Data Access Object) para la entidad [DocumentoEntity].
 *
 * Define el contrato de acceso a la tabla "documentos" mediante anotaciones
 * de Room. KSP genera en tiempo de compilación la clase concreta
 * [DocumentoDao_Impl] que implementa cada función aquí declarada.
 *
 * Decisiones de diseño:
 *  - Las funciones de **escritura** (INSERT) son [suspend]: se ejecutan de
 *    forma asíncrona en un hilo de IO sin bloquear el hilo principal.
 *  - Las funciones de **lectura** devuelven [Flow]: Room emite una nueva
 *    lista automáticamente cada vez que cambia la tabla, permitiendo que
 *    la UI de Compose se recomponga de forma reactiva sin polling manual.
 *  - [OnConflictStrategy.REPLACE] en inserciones: si se intenta insertar un
 *    registro con la misma PK (raro dado el autoGenerate, pero posible en
 *    upserts futuros), el registro existente se elimina y se reemplaza.
 *
 * Operaciones implementadas:
 *  Inserción:
 *   - [insertar]       → inserta un único documento, retorna el rowid asignado.
 *   - [insertarTodos]  → inserta una lista de documentos en una transacción.
 *
 *  Consulta:
 *   - [obtenerTodos]        → flujo reactivo con todos los documentos,
 *                             ordenados del más reciente al más antiguo.
 *   - [obtenerPorId]        → busca un documento por su clave primaria.
 *   - [buscarPorCurso]      → flujo reactivo filtrado por nombre de curso
 *                             (útil para la lista filtrable de la Tarea 5).
 *   - [contarDocumentos]    → total de filas en la tabla (útil para validar
 *                             la persistencia en la Tarea 7).
 */
@Dao
interface DocumentoDao {

    // ── OPERACIONES DE INSERCIÓN ──────────────────────────────────────────────

    /**
     * Inserta un único [documento] en la tabla "documentos".
     *
     * @param documento Instancia de [DocumentoEntity] a persistir.
     *                  El campo [DocumentoEntity.id] debe ser 0 para que Room
     *                  asigne el id autoincremental.
     * @return rowid (Long) asignado por SQLite al nuevo registro.
     *         Devolver Long en lugar de Unit permite confirmar la inserción.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(documento: DocumentoEntity): Long

    /**
     * Inserta una lista de [documentos] en la tabla en una sola transacción.
     * Útil para cargar datos semilla (seed data) o importar lotes.
     *
     * @param documentos Lista de [DocumentoEntity] a persistir.
     * @return Lista de rowids asignados por SQLite, en el mismo orden.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(documentos: List<DocumentoEntity>): List<Long>

    // ── OPERACIONES DE CONSULTA ───────────────────────────────────────────────

    /**
     * Retorna un [Flow] con **todos** los documentos almacenados, ordenados
     * del más reciente al más antiguo (DESC por fecha_registro).
     *
     * Room mantiene activo el observer de la tabla y emite una nueva lista
     * automáticamente ante cualquier INSERT, UPDATE o DELETE, lo que permite
     * que [androidx.compose.runtime.collectAsState] recomponga la LazyColumn
     * sin código adicional (Tarea 5).
     *
     * @return [Flow] que emite [List]<[DocumentoEntity]> ante cada cambio.
     */
    @Query("SELECT * FROM documentos ORDER BY fecha_registro DESC")
    fun obtenerTodos(): Flow<List<DocumentoEntity>>

    /**
     * Busca un documento por su clave primaria [id].
     *
     * Función suspend (no devuelve Flow) porque se espera un resultado único
     * puntual, p. ej. para pre-cargar un formulario de edición (Tarea 6).
     *
     * @param id Clave primaria del documento buscado.
     * @return [DocumentoEntity] si existe, [null] si no se encuentra.
     */
    @Query("SELECT * FROM documentos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): DocumentoEntity?

    /**
     * Retorna un [Flow] con los documentos cuyo campo [DocumentoEntity.curso]
     * coincide exactamente con [curso].
     *
     * El parámetro [curso] es case-sensitive según la colación por defecto
     * de SQLite (BINARY). Para búsquedas insensibles, usar LIKE con UPPER().
     *
     * @param curso Nombre del curso exacto a filtrar.
     * @return [Flow] reactivo con los documentos del curso indicado.
     */
    @Query("SELECT * FROM documentos WHERE curso = :curso ORDER BY fecha_registro DESC")
    fun buscarPorCurso(curso: String): Flow<List<DocumentoEntity>>

    /**
     * Cuenta el total de documentos registrados en la tabla.
     *
     * Devuelve [Flow]<[Int]> para que la UI pueda mostrar el contador
     * en tiempo real (p. ej. un badge en el ícono de la lista).
     *
     * @return [Flow] que emite el número de filas actuales en la tabla.
     */
    @Query("SELECT COUNT(*) FROM documentos")
    fun contarDocumentos(): Flow<Int>

    // ── OPERACIONES ADICIONALES (TAREA 6) ─────────────────────────────────────

    /**
     * Actualiza un documento existente. Room localiza la fila por su clave
     * primaria ([DocumentoEntity.id]) y sobrescribe el resto de columnas.
     *
     * SQL generado: UPDATE `documentos` SET `titulo` = ?, ... WHERE `id` = ?
     *
     * @return número de filas modificadas (1 si el id existía, 0 si no).
     */
    @Update
    suspend fun actualizar(documento: DocumentoEntity): Int

    /**
     * Elimina un documento identificado por su clave primaria.
     *
     * SQL generado: DELETE FROM `documentos` WHERE `id` = ?
     *
     * @return número de filas eliminadas (1 si el id existía, 0 si no).
     */
    @Delete
    suspend fun eliminar(documento: DocumentoEntity): Int
}
