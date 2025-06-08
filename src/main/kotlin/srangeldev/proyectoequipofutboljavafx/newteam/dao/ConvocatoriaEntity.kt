package srangeldev.proyectoequipofutboljavafx.newteam.dao

import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entidad que representa una fila en la tabla Convocatorias
 */
data class ConvocatoriaEntity(
    val id: Int = 0,
    val fecha: LocalDate,
    val descripcion: String,
    val equipoId: Int,
    val entrenadorId: Int,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Convierte la entidad a un objeto de dominio Convocatoria
     */
    fun toConvocatoria(jugadores: List<Int>, titulares: List<Int>): Convocatoria {
        return Convocatoria(
            id = id,
            fecha = fecha,
            descripcion = descripcion,
            equipoId = equipoId,
            entrenadorId = entrenadorId,
            jugadores = jugadores,
            titulares = titulares,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        /**
         * Crea una entidad a partir de un objeto de dominio Convocatoria
         */
        fun fromConvocatoria(convocatoria: Convocatoria): ConvocatoriaEntity {
            return ConvocatoriaEntity(
                id = convocatoria.id,
                fecha = convocatoria.fecha,
                descripcion = convocatoria.descripcion,
                equipoId = convocatoria.equipoId,
                entrenadorId = convocatoria.entrenadorId,
                createdAt = convocatoria.createdAt,
                updatedAt = convocatoria.updatedAt
            )
        }
    }
}