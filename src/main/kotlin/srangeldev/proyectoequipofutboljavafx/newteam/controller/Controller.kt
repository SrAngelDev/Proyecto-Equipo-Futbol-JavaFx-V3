package srangeldev.proyectoequipofutboljavafx.newteam.controller

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalServiceImpl
import srangeldev.proyectoequipofutboljavafx.newteam.storage.FileFormat
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale
import java.util.Locale.getDefault

/**
 * Controlador principal para gestionar las operaciones relacionadas con el personal.
 */
class Controller {
    private val logger = logging()
    private val service = PersonalServiceImpl()

    init {
        logger.debug { "Inicializando controlador" }
    }

    fun cargarDatos(formato: String) {
        logger.debug { "Cargando datos" }
        val normalizedFormat = formato.trim().uppercase()
        val filePath = constructFilePath(normalizedFormat)
        val inputFormat = FileFormat.valueOf(normalizedFormat)
        service.importFromFile(filePath, inputFormat)
    }

    private fun constructFilePath(formato: String): String {
        logger.debug { "Construyendo la ruta del fichero para el formato: $formato" }
        val fileName = when (formato.uppercase()) {
            "CSV" -> "personal.csv"
            "XML" -> "personal.xml"
            "JSON" -> "personal.json"
            else -> throw IllegalArgumentException("Formato no soportado: $formato")
        }

        // List of possible resource paths to try
        val possiblePaths = listOf(
            "/srangeldev/proyectoequipofutboljavafx/data/$fileName",
            "/data/$fileName",
            "/$fileName",
            "/srangeldev/proyectoequipofutboljavafx/data/${fileName.lowercase()}"
        )

        // Try each possible path
        for (resourcePath in possiblePaths) {
            val resourceUrl = this::class.java.getResource(resourcePath)

            if (resourceUrl != null) {
                logger.debug { "Archivo encontrado en recursos: $resourceUrl" }

                // If running from JAR, extract the file to a temporary location
                if (resourceUrl.protocol == "jar") {
                    val extension = formato.lowercase()
                    val tempFile = File.createTempFile("personal", ".$extension")
                    tempFile.deleteOnExit()

                    // Get the resource stream and copy it to the temporary file
                    val resourceStream = this::class.java.getResourceAsStream(resourcePath)
                    if (resourceStream == null) {
                        logger.error { "No se pudo obtener el stream del recurso: $resourcePath" }
                        continue
                    }

                    try {
                        resourceStream.use { input ->
                            tempFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }

                        if (tempFile.exists() && tempFile.length() > 0) {
                            logger.debug { "Archivo extraído a: ${tempFile.absolutePath}" }
                            return tempFile.absolutePath
                        }
                    } catch (e: Exception) {
                        logger.error { "Error al copiar el recurso al archivo temporal: ${e.message}" }
                        continue
                    }
                }

                // If running from IDE, use the file directly
                return resourceUrl.file
            }
        }

        // If not found in resources, try the configured data directory
        val dataDir = Config.configProperties.dataDir
        val filePath = Paths.get(dataDir, fileName).toString()

        // Check if the file exists in the data directory
        val file = File(filePath)
        if (file.exists()) {
            return filePath
        }

        // Try with lowercase filename
        val lowercaseFilePath = Paths.get(dataDir, fileName.lowercase()).toString()
        val lowercaseFile = File(lowercaseFilePath)
        if (lowercaseFile.exists()) {
            return lowercaseFilePath
        }

        // If file doesn't exist, create an empty one
        try {
            file.parentFile?.mkdirs()

            // Create an empty file with appropriate content based on format
            val emptyContent = when (formato.uppercase()) {
                "JSON" -> "[]"
                "CSV" -> ""
                "XML" -> "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<personal></personal>"
                else -> ""
            }

            file.writeText(emptyContent)
            logger.debug { "Archivo vacío creado en: $filePath" }
            return filePath
        } catch (e: Exception) {
            logger.error { "Error al crear archivo vacío: ${e.message}" }
        }

        // Return the original path even if the file doesn't exist
        return filePath
    }
}
