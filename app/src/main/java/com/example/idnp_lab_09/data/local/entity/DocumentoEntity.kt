package com.example.idnp_lab_09.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TAREA 1 — Entidad Room para CampusDocs.
 *
 * Representa un documento/apunte académico que un estudiante comparte en la
 * plataforma CampusDocs. Esta clase es el espejo directo de la tabla SQL
 * "documentos" en la base de datos Room del dispositivo.
 *
 * Consistencia con los laboratorios anteriores:
 *  - Los campos [titulo], [curso], [autor], [descripcion] y [numeroPaginas]
 *    provienen del formulario «Subir apunte» modelado en Lab 05
 *    (FormularioApunte.kt).
 *  - La clase [DocumentoEntity] amplía el data class [Apunte] de Lab 07
 *    con persistencia real en SQLite a través de Room.
 *  - [fechaRegistro] (Long/epoch-ms) permite ordenar la LazyColumn de la
 *    Tarea 5 cronológicamente sin depender de formato de texto.
 *
 * Anotaciones Room utilizadas:
 *  - [@Entity]       → declara la tabla y su nombre en SQLite.
 *  - [@PrimaryKey]   → columna id, autoGenerada (AUTOINCREMENT en SQL).
 *  - [@ColumnInfo]   → asigna un nombre de columna explícito, independiente
 *                      del nombre de la propiedad Kotlin (snake_case en SQL).
 */
@Entity(tableName = "documentos")
data class DocumentoEntity(

    /**
     * Clave primaria autogenerada por SQLite.
     * El valor 0 indica «sin asignar»; Room lo reemplaza con el rowid real
     * al insertar.
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    /**
     * Título descriptivo del apunte, p. ej. «Resumen Capítulo 3 — Álgebra».
     * Indexado para soportar búsquedas futuras por texto.
     */
    @ColumnInfo(name = "titulo", index = true)
    val titulo: String,

    /**
     * Nombre de la asignatura a la que pertenece el documento,
     * p. ej. «Cálculo Diferencial».
     * Indexado para filtrar documentos por curso en la LazyColumn (Tarea 5).
     */
    @ColumnInfo(name = "curso", index = true)
    val curso: String,

    /**
     * Nombre del estudiante o docente que sube el apunte.
     */
    @ColumnInfo(name = "autor")
    val autor: String,

    /**
     * Breve descripción del contenido del documento, notas o comentarios
     * adicionales del autor.
     */
    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    /**
     * Número de páginas del documento. Permite filtrar por extensión
     * y es parte del formulario de Lab 05.
     */
    @ColumnInfo(name = "numero_paginas")
    val numeroPaginas: Int,

    /**
     * Marca de tiempo de registro en milisegundos (epoch Unix).
     * Valor por defecto: instante en que se construye el objeto.
     * Usado para ordenar los registros de más reciente a más antiguo
     * (ORDER BY fecha_registro DESC) en las consultas del DAO.
     */
    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long = System.currentTimeMillis(),
)
