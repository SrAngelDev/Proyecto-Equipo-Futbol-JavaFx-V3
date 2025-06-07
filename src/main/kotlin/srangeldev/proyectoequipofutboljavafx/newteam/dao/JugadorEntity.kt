package srangeldev.proyectoequipofutboljavafx.newteam.dao

import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador

/**
 * Entidad que representa una fila en la tabla Jugadores
 */
data class JugadorEntity(
    val id: Int,
    val posicion: String, // "PORTERO", "DEFENSA", "CENTROCAMPISTA", "DELANTERO"
    val dorsal: Int,
    val altura: Double,
    val peso: Double,
    val goles: Int,
    val partidosJugados: Int
) {
    /**
     * Convierte la entidad a un objeto de dominio Jugador
     */
    fun toJugador(personalEntity: PersonalEntity): Jugador {
        return Jugador(
            id = id,
            nombre = personalEntity.nombre,
            apellidos = personalEntity.apellidos,
            fechaNacimiento = personalEntity.fechaNacimiento,
            fechaIncorporacion = personalEntity.fechaIncorporacion,
            salario = personalEntity.salario,
            paisOrigen = personalEntity.paisOrigen,
            createdAt = personalEntity.createdAt,
            updatedAt = personalEntity.updatedAt,
            posicion = Jugador.Posicion.valueOf(posicion),
            dorsal = dorsal,
            altura = altura,
            peso = peso,
            goles = goles,
            partidosJugados = partidosJugados,
            imagenUrl = personalEntity.imagenUrl
        )
    }
    
    companion object {
        /**
         * Crea una entidad a partir de un objeto de dominio Jugador
         */
        fun fromJugador(jugador: Jugador): JugadorEntity {
            return JugadorEntity(
                id = jugador.id,
                posicion = jugador.posicion.name,
                dorsal = jugador.dorsal,
                altura = jugador.altura,
                peso = jugador.peso,
                goles = jugador.goles,
                partidosJugados = jugador.partidosJugados
            )
        }
    }
}