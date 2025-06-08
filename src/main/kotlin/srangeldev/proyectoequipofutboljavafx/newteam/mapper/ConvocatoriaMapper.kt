package srangeldev.proyectoequipofutboljavafx.newteam.mapper

import srangeldev.proyectoequipofutboljavafx.newteam.dao.ConvocatoriaEntity
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria

/**
 * Convierte un [ConvocatoriaEntity] en un [Convocatoria]
 */
fun ConvocatoriaEntity.toModel(jugadores: List<Int> = emptyList(), titulares: List<Int> = emptyList()): Convocatoria {
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

/**
 * Convierte un [Convocatoria] en un [ConvocatoriaEntity]
 */
fun Convocatoria.toEntity(): ConvocatoriaEntity {
    return ConvocatoriaEntity(
        id = id,
        fecha = fecha,
        descripcion = descripcion,
        equipoId = equipoId,
        entrenadorId = entrenadorId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}