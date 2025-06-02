package srangeldev.proyectoequipofutboljavafx.newteam.models

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Clase que representa una convocatoria de jugadores para un partido.
 */
data class Convocatoria(
    val id: Int = 0,
    val fecha: LocalDate,
    val descripcion: String,
    val equipoId: Int,
    val entrenadorId: Int,
    val jugadores: List<Int> = emptyList(), // IDs de los jugadores convocados (máximo 18)
    val titulares: List<Int> = emptyList(), // IDs de los jugadores titulares (11)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    override fun toString(): String {
        return "Convocatoria(id=$id, fecha=$fecha, descripcion='$descripcion', equipoId=$equipoId, entrenadorId=$entrenadorId, jugadores=$jugadores, titulares=$titulares, createdAt=$createdAt, updatedAt=$updatedAt)"
    }
    /**
     * Valida que la convocatoria cumpla con las reglas:
     * - Máximo 18 jugadores
     * - Máximo 2 porteros
     * - 11 titulares que deben estar en la lista de convocados
     *
     * @param jugadoresMap Mapa de jugadores por ID para validar posiciones
     * @return true si la convocatoria es válida, false en caso contrario
     */
    fun esValida(jugadoresMap: Map<Int, Jugador>): Boolean {
        // Validar número máximo de jugadores
        if (jugadores.size > 18) {
            return false
        }

        // Validar número máximo de porteros
        val porteros = jugadores.count { jugadorId ->
            jugadoresMap[jugadorId]?.posicion == Jugador.Posicion.PORTERO
        }
        if (porteros > 2) {
            return false
        }

        // Validar que todos los titulares estén en la lista de convocados
        if (titulares.size != 11 || !jugadores.containsAll(titulares)) {
            return false
        }

        return true
    }
}
