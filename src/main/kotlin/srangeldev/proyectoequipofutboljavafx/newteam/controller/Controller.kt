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

    private fun constructFilePath(formato: String): String {
        logger.debug { "Construyendo la ruta del fichero para el formato: $formato" }
        val dataDir = Config.configProperties.dataDir
        val fileName = when (formato) {
            "CSV" -> "personal.csv"
            "XML" -> "personal.xml"
            "JSON" -> "personal.json"
            else -> throw IllegalArgumentException("Formato no soportado: $formato")
        }
        return Paths.get(dataDir, fileName).toString()
    }
}
