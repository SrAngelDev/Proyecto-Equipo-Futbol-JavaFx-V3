package srangeldev.proyectoequipofutboljavafx.newteam.dao

import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador

/**
 * Entidad que representa una fila en la tabla Entrenadores
 */
data class EntrenadorEntity(
    val id: Int,
    val especializacion: String // "ENTRENADOR_PRINCIPAL", "ENTRENADOR_ASISTENTE", "ENTRENADOR_PORTEROS"
) {
    /**
     * Convierte la entidad a un objeto de dominio Entrenador
     */
    fun toEntrenador(personalEntity: PersonalEntity): Entrenador {
        return Entrenador(
            id = id,
            nombre = personalEntity.nombre,
            apellidos = personalEntity.apellidos,
            fechaNacimiento = personalEntity.fechaNacimiento,
            fechaIncorporacion = personalEntity.fechaIncorporacion,
            salario = personalEntity.salario,
            paisOrigen = personalEntity.paisOrigen,
            createdAt = personalEntity.createdAt,
            updatedAt = personalEntity.updatedAt,
            especializacion = Entrenador.Especializacion.valueOf(especializacion),
            imagenUrl = personalEntity.imagenUrl
        )
    }
    
    companion object {
        /**
         * Crea una entidad a partir de un objeto de dominio Entrenador
         */
        fun fromEntrenador(entrenador: Entrenador): EntrenadorEntity {
            return EntrenadorEntity(
                id = entrenador.id,
                especializacion = entrenador.especializacion.name
            )
        }
    }
}