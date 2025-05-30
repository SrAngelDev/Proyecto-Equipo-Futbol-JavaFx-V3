package srangeldev.proyectoequipofutboljavafx.newteam.models

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Clase que representa a un jugador.
 */
class Entrenador(
    id: Int,
    nombre: String,
    apellidos: String,
    fechaNacimiento: LocalDate,
    fechaIncorporacion: LocalDate,
    salario: Double,
    paisOrigen: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
    val especializacion: Especializacion,
    imagenUrl: String = ""
): Personal(id, nombre, apellidos, fechaNacimiento, fechaIncorporacion, salario, paisOrigen, createdAt, updatedAt, imagenUrl) {
    enum class Especializacion {
        ENTRENADOR_PRINCIPAL, ENTRENADOR_ASISTENTE, ENTRENADOR_PORTEROS
    }
    override fun toString(): String {
        return "$nombre $apellidos"
    }
}
