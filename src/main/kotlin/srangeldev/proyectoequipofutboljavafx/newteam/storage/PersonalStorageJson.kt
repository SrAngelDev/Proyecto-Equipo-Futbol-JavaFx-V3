package srangeldev.proyectoequipofutboljavafx.newteam.storage

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalJsonDto
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toJsonDto
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toEntrenador
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toJugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File

class PersonalStorageJson: PersonalStorageFile {

    private val logger = logging()

    init {
        logger.debug { "Inicializando almacenamiento de productos en JSON" }
    }

    private fun validateFileForReading(file: File) {
        if (!file.exists()) {
            // Intentar con extensiones de diferentes casos
            val parentDir = file.parentFile
            val baseName = file.nameWithoutExtension
            val alternativeFile = File(parentDir, "$baseName.json")

            if (alternativeFile.exists() && alternativeFile.isFile && alternativeFile.canRead()) {
                return validateFileForReading(alternativeFile)
            }

            throw PersonalException.PersonalStorageException("El fichero no existe: $file")
        }

        if (!file.isFile) {
            throw PersonalException.PersonalStorageException("No es un fichero: $file")
        }

        if (!file.canRead()) {
            throw PersonalException.PersonalStorageException("No se puede leer el fichero: $file")
        }

        if (file.length() == 0L) {
            throw PersonalException.PersonalStorageException("El fichero está vacío: $file")
        }

        // Verificar si el contenido parece JSON para extensiones que no son JSON
        if (!file.name.endsWith(".json", ignoreCase = true)) {
            try {
                val firstChars = file.readText(Charsets.UTF_8).take(10).trim()
                if (!firstChars.startsWith("[") && !firstChars.startsWith("{")) {
                    throw PersonalException.PersonalStorageException("El contenido del archivo no parece ser JSON válido")
                }
            } catch (e: Exception) {
                throw PersonalException.PersonalStorageException("Error al leer el archivo: ${e.message}")
            }
        }
    }

    override fun readFromFile(file: File): List<Personal> {
        logger.debug { "Leyendo personal de fichero JSON: $file" }

        if (!file.exists()) {
            val alternativeFile = File(file.parentFile, "${file.nameWithoutExtension}.json")
            if (alternativeFile.exists()) {
                return readFromFile(alternativeFile)
            }

            try {
                file.parentFile?.mkdirs()
                file.writeText("[]")
                return emptyList()
            } catch (e: Exception) {
                throw PersonalException.PersonalStorageException("El fichero no existe y no se pudo crear uno nuevo: $file")
            }
        }

        try {
            validateFileForReading(file)

            val fileContent = file.readText(Charsets.UTF_8)

            val json = Json { 
                ignoreUnknownKeys = true 
                isLenient = true
            }

            val personalList = json.decodeFromString(ListSerializer(PersonalJsonDto.serializer()), fileContent).map {
                when (it.rol) {
                    "Entrenador" -> it.toEntrenador()
                    "Jugador" -> it.toJugador()
                    else -> throw PersonalException.PersonalStorageException("Tipo de personal desconocido: ${it.rol}")
                }
            }

            return personalList
        } catch (e: PersonalException.PersonalStorageException) {
            throw e
        } catch (e: Exception) {
            throw PersonalException.PersonalStorageException("Error en el almacenamiento: ${e.message}")
        }
    }

    override fun writeToFile(file: File, personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en fichero JSON: $file" }

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        // Check file extension case-insensitively
        if (!file.name.endsWith(".json", ignoreCase = true)) {
            logger.error { "El fichero no tiene extensión JSON: $file" }
            throw PersonalException.PersonalStorageException("El fichero no tiene extensión JSON: $file")
        }

        val json = Json {
            prettyPrint = true
            encodeDefaults = true
        }

        val jsonDtos = personalList.map { personal ->
            when (personal) {
                is Entrenador -> personal.toJsonDto()
                is Jugador -> personal.toJsonDto()
                else -> throw PersonalException.PersonalStorageException("Tipo de personal no soportado")
            }
        }

        file.writeText(json.encodeToString(ListSerializer(PersonalJsonDto.serializer()), jsonDtos))
        logger.debug { "Personal guardado en fichero JSON: $file" }
    }
}
