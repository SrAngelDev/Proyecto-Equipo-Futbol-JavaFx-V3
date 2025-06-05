package srangeldev.proyectoequipofutboljavafx.newteam.models

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Clase que representa a un jugador.
 */
class Jugador(
    id: Int,
    nombre: String,
    apellidos: String,
    fechaNacimiento: LocalDate,
    fechaIncorporacion: LocalDate,
    salario: Double,
    paisOrigen: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
    val posicion: Posicion,
    val dorsal: Int,
    val altura: Double,
    val peso: Double,
    val goles: Int,
    val partidosJugados: Int,
    imagenUrl: String = ""
): Personal(id, nombre, apellidos, fechaNacimiento, fechaIncorporacion, salario, paisOrigen, createdAt, updatedAt, imagenUrl) {
    enum class Posicion {
        PORTERO,
        DEFENSA,
        CENTROCAMPISTA,
        DELANTERO
    }
    override fun toString(): String {
        return "Jugador(id=$id, nombre='$nombre', apellidos='$apellidos', fechaNacimiento=$fechaNacimiento, fechaIncorporacion=$fechaIncorporacion, salario=$salario, paisOrigen='$paisOrigen', posicion=$posicion, dorsal=$dorsal, altura=$altura, peso=$peso, goles=$goles, partidosJugados=$partidosJugados)"
    }
}
