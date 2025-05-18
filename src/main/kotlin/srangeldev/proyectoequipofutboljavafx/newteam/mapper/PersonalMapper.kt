package srangeldev.mapper

import org.lighthousegames.logging.logging
import srangeldev.dto.*
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val logger = logging()

// Formatters for parsing dates consistently
private val isoDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val dashDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

// Function to safely parse dates with multiple formatters
private fun parseDate(dateString: String): LocalDate {
    return try {
        // Primero se intenta el formato ISO
        LocalDate.parse(dateString, isoDateFormatter)
    } catch (e: DateTimeParseException) {
        try {
            // Si falla, se intenta el formato con guiones
            LocalDate.parse(dateString, dashDateFormatter)
        } catch (e: DateTimeParseException) {
            logger.error { "Error parsing date: $dateString. Using current date instead." }
            // Si no, de ultima opción, se devuelve la fecha actual
            LocalDate.now()
        }
    }
}

fun Entrenador.toCsvDto(): PersonalCsvDto {
    return PersonalCsvDto(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = this.fechaNacimiento.toString(),
        fechaIncorporacion = this.fechaIncorporacion.toString(),
        salario = this.salario,
        paisOrigen = this.paisOrigen,
        rol = "Entrenador",
        especializacion = this.especializacion.toString(),
        imagenUrl = this.imagenUrl
    )
}

fun Jugador.toCsvDto(): PersonalCsvDto {
    return PersonalCsvDto(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = this.fechaNacimiento.toString(),
        fechaIncorporacion = this.fechaIncorporacion.toString(),
        salario = this.salario,
        paisOrigen = this.paisOrigen,
        rol = "Jugador",
        posicion = this.posicion.toString(),
        dorsal = this.dorsal.toString(),
        altura = this.altura.toString(),
        peso = this.peso.toString(),
        goles = this.goles.toString(),
        partidosJugados = this.partidosJugados.toString(),
        imagenUrl = this.imagenUrl
    )
}

fun Entrenador.toJsonDto(): PersonalJsonDto {
    return PersonalJsonDto(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = this.fechaNacimiento.toString(),
        fechaIncorporacion = this.fechaIncorporacion.toString(),
        salario = this.salario,
        pais = this.paisOrigen,
        rol = "Entrenador",
        especializacion = this.especializacion.toString(),
        imagenUrl = this.imagenUrl
    )
}

fun Jugador.toJsonDto(): PersonalJsonDto {
    return PersonalJsonDto(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = this.fechaNacimiento.toString(),
        fechaIncorporacion = this.fechaIncorporacion.toString(),
        salario = this.salario,
        pais = this.paisOrigen,
        rol = "Jugador",
        posicion = this.posicion.toString(),
        dorsal = this.dorsal,
        altura = this.altura,
        peso = this.peso,
        goles = this.goles,
        partidosJugados = this.partidosJugados,
        imagenUrl = this.imagenUrl
    )
}

fun Entrenador.toXmlDto(): PersonalXmlDto {
    return PersonalXmlDto(
        id = this.id,
        tipo = "Entrenador",
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = this.fechaNacimiento.toString(),
        fechaIncorporacion = this.fechaIncorporacion.toString(),
        salario = this.salario,
        pais = this.paisOrigen,
        especialidad = this.especializacion.toString(),
        imagenUrl = this.imagenUrl
    )
}

fun Jugador.toXmlDto(): PersonalXmlDto {
    return PersonalXmlDto(
        id = this.id,
        tipo = "Jugador",
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = this.fechaNacimiento.toString(),
        fechaIncorporacion = this.fechaIncorporacion.toString(),
        salario = this.salario,
        pais = this.paisOrigen,
        posicion = this.posicion.toString(),
        dorsal = this.dorsal.toString(),
        altura = this.altura.toString(),
        peso = this.peso.toString(),
        goles = this.goles.toString(),
        partidosJugados = this.partidosJugados.toString(),
        imagenUrl = this.imagenUrl
    )
}

fun PersonalCsvDto.toEntrenador(): Entrenador {
    val especializacion = if (this.especializacion.isNullOrEmpty()) {
        Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
    } else {
        try {
            Entrenador.Especializacion.valueOf(this.especializacion.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Especialización no válida: ${this.especializacion}")
        }
    }

    return Entrenador(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = parseDate(this.fechaNacimiento),
        fechaIncorporacion = parseDate(this.fechaIncorporacion),
        salario = this.salario,
        paisOrigen = this.paisOrigen,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        especializacion = especializacion,
        imagenUrl = this.imagenUrl
    )
}

fun PersonalCsvDto.toJugador(): Jugador {
    return Jugador(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = parseDate(this.fechaNacimiento),
        fechaIncorporacion = parseDate(this.fechaIncorporacion),
        salario = this.salario,
        paisOrigen = this.paisOrigen,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        posicion = Jugador.Posicion.valueOf(this.posicion ?: "DESCONOCIDO"),
        dorsal = this.dorsal.toInt(),
        altura = this.altura.toDouble(),
        peso = this.peso.toDouble(),
        goles = this.goles.toInt(),
        partidosJugados = this.partidosJugados.toInt(),
        imagenUrl = this.imagenUrl
    )
}

fun PersonalJsonDto.toEntrenador(): Entrenador {
    val especializacion = if (this.especializacion.isNullOrEmpty()) {
        Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
    } else {
        try {
            Entrenador.Especializacion.valueOf(this.especializacion.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Especialización no válida: ${this.especializacion}")
        }
    }

    return Entrenador(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = parseDate(this.fechaNacimiento),
        fechaIncorporacion = parseDate(this.fechaIncorporacion),
        salario = this.salario,
        paisOrigen = this.pais,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        especializacion = especializacion,
        imagenUrl = this.imagenUrl
    )
}

fun PersonalJsonDto.toJugador(): Jugador {
    return Jugador(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = parseDate(this.fechaNacimiento),
        fechaIncorporacion = parseDate(this.fechaIncorporacion),
        salario = this.salario,
        paisOrigen = this.pais,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        posicion = Jugador.Posicion.valueOf(this.posicion ?: "DESCONOCIDO"),
        dorsal = this.dorsal?.toInt() ?: 0,
        altura = this.altura?.toDouble() ?: 0.0,
        peso = this.peso?.toDouble() ?: 0.0,
        goles = this.goles?.toInt() ?: 0,
        partidosJugados = this.partidosJugados?.toInt() ?: 0,
        imagenUrl = this.imagenUrl
    )
}

fun PersonalXmlDto.toEntrenador(): Entrenador {
    val especializacion = if (this.especialidad.isNullOrEmpty()) {
        Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
    } else {
        try {
            Entrenador.Especializacion.valueOf(this.especialidad.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Especialización no válida: ${this.especialidad}")
        }
    }

    return Entrenador(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = parseDate(this.fechaNacimiento),
        fechaIncorporacion = parseDate(this.fechaIncorporacion),
        salario = this.salario,
        paisOrigen = this.pais,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        especializacion = especializacion,
        imagenUrl = this.imagenUrl
    )
}

fun PersonalXmlDto.toJugador(): Jugador {
    return Jugador(
        id = this.id,
        nombre = this.nombre,
        apellidos = this.apellidos,
        fechaNacimiento = parseDate(this.fechaNacimiento),
        fechaIncorporacion = parseDate(this.fechaIncorporacion),
        salario = this.salario,
        paisOrigen = this.pais,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        posicion = Jugador.Posicion.valueOf(this.posicion ?: "DESCONOCIDO"),
        dorsal = this.dorsal.toInt(),
        altura = this.altura.toDouble(),
        peso = this.peso.toDouble(),
        goles = this.goles.toInt(),
        partidosJugados = this.partidosJugados.toInt(),
        imagenUrl = this.imagenUrl
    )
}
