package srangeldev.proyectoequipofutboljavafx.newteam.dao

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidad que representa una fila en la tabla Personal
 */
data class PersonalEntity(
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val fechaNacimiento: LocalDate,
    val fechaIncorporacion: LocalDate,
    val salario: Double,
    val paisOrigen: String,
    val tipo: String, // "ENTRENADOR" o "JUGADOR"
    val imagenUrl: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)