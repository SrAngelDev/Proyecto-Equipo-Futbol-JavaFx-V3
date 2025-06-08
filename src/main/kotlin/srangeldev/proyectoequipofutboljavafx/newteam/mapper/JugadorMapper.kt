package srangeldev.proyectoequipofutboljavafx.newteam.mapper

import srangeldev.proyectoequipofutboljavafx.newteam.dao.JugadorEntity
import srangeldev.proyectoequipofutboljavafx.newteam.dao.PersonalEntity
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador

/**
 * Convierte un [JugadorEntity] y un [PersonalEntity] en un [Jugador]
 */
fun JugadorEntity.toModel(personalEntity: PersonalEntity): Jugador {
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

/**
 * Convierte un [Jugador] en un [JugadorEntity]
 */
fun Jugador.toJugadorEntity(): JugadorEntity {
    return JugadorEntity(
        id = id,
        posicion = posicion.name,
        dorsal = dorsal,
        altura = altura,
        peso = peso,
        goles = goles,
        partidosJugados = partidosJugados
    )
}

/**
 * Convierte un [Jugador] en un [PersonalEntity]
 */
fun Jugador.toPersonalEntity(): PersonalEntity {
    return PersonalEntity(
        id = id,
        nombre = nombre,
        apellidos = apellidos,
        fechaNacimiento = fechaNacimiento,
        fechaIncorporacion = fechaIncorporacion,
        salario = salario,
        paisOrigen = paisOrigen,
        tipo = "JUGADOR",
        imagenUrl = imagenUrl,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}