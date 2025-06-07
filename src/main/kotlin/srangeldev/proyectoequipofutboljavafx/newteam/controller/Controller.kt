package srangeldev.proyectoequipofutboljavafx.newteam.controller

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalService
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
class Controller : KoinComponent {
    private val logger = logging()

    // This allows for dependency injection in tests
    var service: PersonalService? = null
        get() {
            // Only try to get from Koin if not already set (e.g. in tests)
            if (field == null) {
                try {
                    field = getKoin().get<PersonalService>()
                } catch (e: Exception) {
                    logger.error { "Error getting PersonalService: ${e.message}" }
                    throw e
                }
            }
            return field
        }

    init {
        logger.debug { "Inicializando controlador" }
    }

    fun cargarDatos(formato: String) {
        logger.debug { "Cargando datos de formato: $formato" }
        val normalizedFormat = formato.trim().uppercase()
        val filePath = constructFilePath(normalizedFormat)
        val inputFormat = FileFormat.valueOf(normalizedFormat)

        try {
            service!!.importFromFile(filePath, inputFormat)
            logger.debug { "Datos importados correctamente desde: $filePath" }
        } catch (e: Exception) {
            logger.error { "Error al importar datos desde $filePath: ${e.message}" }
            throw e
        }
    }

    fun constructFilePath(formato: String): String {
        logger.debug { "Construyendo la ruta del fichero para el formato: $formato" }
        val fileName = when (formato.uppercase()) {
            "CSV" -> "personal.csv"
            "XML" -> "personal.xml"
            "JSON" -> "personal.json"
            else -> throw IllegalArgumentException("Formato no soportado: $formato")
        }

        // Lista de posibles rutas para el recurso
        val possiblePaths = listOf(
            "/srangeldev/proyectoequipofutboljavafx/data/$fileName",
            "/data/$fileName",
            "/$fileName",
            "/srangeldev/proyectoequipofutboljavafx/data/${fileName.lowercase()}"
        )

        // Intenta cargar el recurso desde las rutas posibles
        for (resourcePath in possiblePaths) {
            val resourceUrl = this::class.java.getResource(resourcePath)

            if (resourceUrl != null) {
                logger.debug { "Archivo encontrado en recursos: $resourceUrl" }

                // Si se ejecuta desde un JAR, extrae el recurso a un archivo temporal
                if (resourceUrl.protocol == "jar") {
                    val extension = formato.lowercase()
                    val tempFile = File.createTempFile("personal", ".$extension")
                    tempFile.deleteOnExit()

                    // Obtiene el stream del recurso
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

                // Si se ejecuta desde un entorno de desarrollo, devuelve la ruta del recurso directamente
                return resourceUrl.file
            }
        }

        // Si no se encuentra el recurso, intenta buscar en el directorio de datos configurado
        val dataDir = Config.configProperties.dataDir
        val filePath = Paths.get(dataDir, fileName).toString()

        // Comprobar si el archivo existe en el directorio de datos
        val file = File(filePath)
        if (file.exists()) {
            return filePath
        }

        // Intenta buscar el archivo con nombre en minúsculas
        val lowercaseFilePath = Paths.get(dataDir, fileName.lowercase()).toString()
        val lowercaseFile = File(lowercaseFilePath)
        if (lowercaseFile.exists()) {
            return lowercaseFilePath
        }

        // Devuelve la ruta del archivo original si no se encuentra en recursos ni en el directorio de datos
        return filePath
    }
}
