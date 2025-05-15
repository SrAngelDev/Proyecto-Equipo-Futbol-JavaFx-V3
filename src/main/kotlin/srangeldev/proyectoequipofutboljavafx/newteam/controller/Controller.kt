package srangeldev.controller

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.service.PersonalServiceImpl
import srangeldev.storage.FileFormat
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Controlador principal para gestionar las operaciones relacionadas con el personal.
 */
class Controller {
    private val logger = logging()
    private val service = PersonalServiceImpl()

    // En la función cargarDatos

    fun cargarDatos(formato: String) {
        logger.debug { "Cargando datos" }
        val filePath = constructFilePath(formato)
        val inputFormat = FileFormat.valueOf(formato.trim())
        service.importFromFile(filePath, inputFormat)
    }

    fun copiarDatos(formato: String) {
        logger.debug { "Copiando datos" }
        if (formato.isBlank()) {
            println("El formato proporcionado está vacío. No se puede copiar.")
            return
        }
        Config.configProperties.outputFormats.split(",").forEach {
            val outputFormat = FileFormat.valueOf(it.trim())
            if (outputFormat.name.equals(formato.trim(), ignoreCase = true)) {
                service.exportToFile(Config.configProperties.dataDir, outputFormat)
            }
        }
    }

    private fun constructFilePath(formato: String): String {
        // Use the data directory directly from config without converting to absolute path
        val dataDir = Config.configProperties.dataDir
        val fileName = when (formato) {
            "CSV" -> "personal.csv"
            "XML" -> "personal.xml"
            "JSON" -> "personal.json"
            else -> throw IllegalArgumentException("Formato no soportado: $formato")
        }
        return Paths.get(dataDir, fileName).toString()
    }


    /**
     * Crea un nuevo miembro del personal, ya sea entrenador o jugador.
     */
    fun crearMiembro() {
        logger.debug { "Creando miembro" }
        println("Quieres crear entrenador o jugador?")
        when (readln().uppercase()) {
            "ENTRENADOR" -> crearEntrenador()
            "JUGADOR" -> crearJugador()
            else -> println("Tipo no válido.")
        }
    }

    /**
     * Crea un nuevo entrenador.
     */
    private fun crearEntrenador() {
        val id = 0
        println("Introduce el nombre del entrenador")
        val nombre = readln()
        println("Introduce el apellido del entrenador")
        val apellido = readln()
        println("Introduce la fecha de nacimiento del entrenador")
        val fechaNacimiento = leerFecha()
        println("Introduce la fecha de incorporación al club del entrenador")
        val fechaIncorporacion = leerFecha()
        println("Introduce el salario del entrenador")
        val salario = readln().toDouble()
        println("Introduce el país de origen del entrenador")
        val paisOrigen = readln()
        println("Introduce la especialización del entrenador")
        val especializacion = leerEspecializacion()
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()
        val entrenador =
            Entrenador(
                id, nombre, apellido, fechaNacimiento, fechaIncorporacion, salario, paisOrigen, updatedAt, createdAt, especializacion
            )
        service.save(entrenador)
    }

    /**
     * Crea un nuevo jugador.
     */
    private fun crearJugador() {
        val id = 0
        println("Introduce el nombre del jugador")
        val nombre = readln()
        println("Introduce el apellido del jugador")
        val apellido = readln()
        println("Introduce la fecha de nacimiento del jugador")
        val fechaNacimiento = leerFecha()
        println("Introduce la fecha de incorporación al club del jugador")
        val fechaIncorporacion = leerFecha()
        println("Introduce el salario del jugador")
        val salario = readln().toDouble()
        println("Introduce el país de origen del jugador")
        val paisOrigen = readln()
        println("Introduce la posición del jugador")
        val posicion = leerPosicion()
        println("Introduce el dorsal del jugador")
        val dorsal = readln().toInt()
        println("Introduce la altura del jugador")
        val altura = readln().toDouble()
        println("Introduce el peso del jugador")
        val peso = readln().toDouble()
        println("Introduce los goles del jugador")
        val goles = readln().toInt()
        println("Introduce los partidos jugados del jugador")
        val partidosJugados = readln().toInt()
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()
        val jugador = Jugador(
            id,
            nombre,
            apellido,
            fechaNacimiento,
            fechaIncorporacion,
            salario,
            paisOrigen,
            createdAt,
            updatedAt,
            posicion,
            dorsal,
            altura,
            peso,
            goles,
            partidosJugados
        )
        service.save(jugador)
    }

    /**
     * Actualiza los datos de un miembro del personal.
     */
    fun actualizarMiembro() {
        logger.debug { "Actualizando miembro" }
        println("Introduce el id del miembro a actualizar")
        val id = readln().toInt()
        val miembro = service.getById(id)
        when (miembro) {
            is Entrenador -> actualizarEntrenador()
            is Jugador -> actualizarJugador()
            else -> throw IllegalArgumentException("Miembro no encontrado.")
        }
    }

    /**
     * Actualiza un jugador.
     *
     * @return El jugador actualizado.
     */
    private fun actualizarJugador(): Jugador {
        logger.debug { "Actualizando jugador" }
        println("Introduce el nombre del jugador")
        val nombre = readln()
        println("Introduce el apellido del jugador")
        val apellido = readln()
        println("Introduce la fecha de nacimiento del jugador")
        val fechaNacimiento = leerFecha()
        println("Introduce la fecha de incorporación al club del jugador")
        val fechaIncorporacion = leerFecha()
        println("Introduce el salario del jugador")
        val salario = readln().toDouble()
        println("Introduce el país de origen del jugador")
        val paisOrigen = readln()
        println("Introduce la posición del jugador")
        val posicion = leerPosicion()
        println("Introduce el dorsal del jugador")
        val dorsal = readln().toInt()
        println("Introduce la altura del jugador")
        val altura = readln().toDouble()
        println("Introduce el peso del jugador")
        val peso = readln().toDouble()
        println("Introduce los goles del jugador")
        val goles = readln().toInt()
        println("Introduce los partidos jugados del jugador")
        val partidosJugados = readln().toInt()
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()
        val jugador = Jugador(
            0,
            nombre,
            apellido,
            fechaNacimiento,
            fechaIncorporacion,
            salario,
            paisOrigen,
            createdAt,
            updatedAt,
            posicion,
            dorsal,
            altura,
            peso,
            goles,
            partidosJugados,
        )
        return service.update(jugador.id, jugador) as Jugador
    }

    /**
     * Actualiza un entrenador.
     *
     * @return El entrenador actualizado.
     */
    private fun actualizarEntrenador(): Entrenador {
        logger.debug { "Actualizando entrenador" }
        println("Introduce el nombre del entrenador")
        val nombre = readln()
        println("Introduce el apellido del entrenador")
        val apellido = readln()
        println("Introduce la fecha de nacimiento del entrenador")
        val fechaNacimiento = leerFecha()
        println("Introduce la fecha de incorporación al club del entrenador")
        val fechaIncorporacion = leerFecha()
        println("Introduce el salario del entrenador")
        val salario = readln().toDouble()
        println("Introduce el país de origen del entrenador")
        val paisOrigen = readln()
        println("Introduce la especialización del entrenador")
        val especializacion = leerEspecializacion()

        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()

        val entrenador =
            Entrenador(0, nombre, apellido, fechaNacimiento, fechaIncorporacion, salario, paisOrigen, createdAt, updatedAt, especializacion)
        return service.update(entrenador.id, entrenador) as Entrenador
    }

    /**
     * Elimina un miembro del personal.
     */
    fun eliminarMiembro() {
        logger.debug { "Eliminando miembro" }
        println("Introduce el id del miembro a eliminar")
        val id = readln().toInt()
        val result = service.delete(id)
        if (result is Entrenador) {
            println("Entrenador eliminado: $result")
        } else if (result is Jugador) {
            println("Jugador eliminado: $result")
        } else {
            println("Miembro no encontrado.")
        }
    }

    /**
     * Lee una fecha desde la entrada estándar.
     *
     * @return La fecha leída.
     */
    fun leerFecha(): LocalDate {
        println("Introduce el día")
        val dia = readln().toInt()
        println("Introduce el mes")
        val mes = readln().toInt()
        println("Introduce el año")
        val año = readln().toInt()
        return LocalDate.of(año, mes, dia)
    }

    /**
     * Lee la especialización de un entrenador desde la entrada estándar.
     *
     * @return La especialización leída.
     */
    fun leerEspecializacion(): Entrenador.Especializacion {
        println("Introduce la especialización (principal, asistente, porteros)")
        return when (readln().uppercase()) {
            "PRINCIPAL" -> Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
            "ASISTENTE" -> Entrenador.Especializacion.ENTRENADOR_ASISTENTE
            "PORTEROS" -> Entrenador.Especializacion.ENTRENADOR_PORTEROS
            else -> throw IllegalArgumentException("Especialización no válida.")
        }
    }

    /**
     * Lee la posición de un jugador desde la entrada estándar.
     *
     * @return La posición leída.
     */
    fun leerPosicion(): Jugador.Posicion {
        println("Introduce la posición (portero, defensa, centrocampista, delantero)")
        return when (readln().uppercase()) {
            "PORTERO" -> Jugador.Posicion.PORTERO
            "DEFENSA" -> Jugador.Posicion.DEFENSA
            "CENTROCAMPISTA" -> Jugador.Posicion.CENTROCAMPISTA
            "DELANTERO" -> Jugador.Posicion.DELANTERO
            else -> throw IllegalArgumentException("Posición no válida.")
        }
    }
}
