package srangeldev.proyectoequipofutboljavafx.newteam.mapper

import srangeldev.proyectoequipofutboljavafx.newteam.dao.EntrenadorEntity
import srangeldev.proyectoequipofutboljavafx.newteam.dao.PersonalEntity
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador

/**
 * Convierte un [EntrenadorEntity] y un [PersonalEntity] en un [Entrenador]
 */
fun EntrenadorEntity.toModel(personalEntity: PersonalEntity): Entrenador {
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

/**
 * Convierte un [Entrenador] en un [EntrenadorEntity]
 */
fun Entrenador.toEntrenadorEntity(): EntrenadorEntity {
    return EntrenadorEntity(
        id = id,
        especializacion = especializacion.name
    )
}

/**
 * Convierte un [Entrenador] en un [PersonalEntity]
 */
fun Entrenador.toPersonalEntity(): PersonalEntity {
    return PersonalEntity(
        id = id,
        nombre = nombre,
        apellidos = apellidos,
        fechaNacimiento = fechaNacimiento,
        fechaIncorporacion = fechaIncorporacion,
        salario = salario,
        paisOrigen = paisOrigen,
        tipo = "ENTRENADOR",
        imagenUrl = imagenUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}