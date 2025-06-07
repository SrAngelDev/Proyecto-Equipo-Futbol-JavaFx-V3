package srangeldev.proyectoequipofutboljavafx.newteam.dao

import srangeldev.proyectoequipofutboljavafx.newteam.models.Equipo
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidad que representa una fila en la tabla Equipos
 */
data class EquipoEntity(
    val id: Int = 0,
    val nombre: String,
    val fechaFundacion: LocalDate,
    val escudoUrl: String = "",
    val ciudad: String,
    val estadio: String,
    val pais: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Convierte la entidad a un objeto de dominio Equipo
     */
    fun toEquipo(): Equipo {
        return Equipo(
            id = id,
            nombre = nombre,
            fechaFundacion = fechaFundacion,
            escudoUrl = escudoUrl,
            ciudad = ciudad,
            estadio = estadio,
            pais = pais,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        /**
         * Crea una entidad a partir de un objeto de dominio Equipo
         */
        fun fromEquipo(equipo: Equipo): EquipoEntity {
            return EquipoEntity(
                id = equipo.id,
                nombre = equipo.nombre,
                fechaFundacion = equipo.fechaFundacion,
                escudoUrl = equipo.escudoUrl,
                ciudad = equipo.ciudad,
                estadio = equipo.estadio,
                pais = equipo.pais,
                createdAt = equipo.createdAt,
                updatedAt = equipo.updatedAt
            )
        }
    }
}