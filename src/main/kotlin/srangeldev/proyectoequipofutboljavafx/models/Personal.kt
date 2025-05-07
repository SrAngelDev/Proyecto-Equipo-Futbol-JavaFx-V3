package srangeldev.proyectoequipofutboljavafx.models

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Clase abstracta que representa a un miembro del personal.
 */
abstract class Personal(
    val id: Int = 0,
    val nombre: String,
    val apellidos: String,
    val fechaNacimiento: LocalDate,
    val fechaIncorporacion: LocalDate,
    val salario: Double,
    val paisOrigen: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)