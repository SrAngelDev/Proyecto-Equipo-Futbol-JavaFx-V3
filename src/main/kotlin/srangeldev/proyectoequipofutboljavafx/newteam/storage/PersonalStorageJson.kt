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
import srangeldev.proyectoequipofutboljavafx.newteam.validator.ValidatorFactory
import java.io.File

class PersonalStorageJson: PersonalStorageFile {

    private val logger = logging()

    init {
        logger.debug { "Inicializando almacenamiento de productos en JSON" }
    }


    override fun readFromFile(file: File): List<Personal> {
        logger.debug { "Leyendo personal de fichero JSON: $file" }
        try {
            ValidatorFactory.validate(file)

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

        try {
            // Ensure parent directory exists
            file.parentFile?.let {
                if (!it.exists()) {
                    it.mkdirs()
                }
            }

            // For writing, we don't validate if the file exists since we're creating it
            // Only validate if it's an existing file
            if (file.exists()) {
                ValidatorFactory.validate(file)
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
        } catch (e: Exception) {
            throw PersonalException.PersonalStorageException("Error en el almacenamiento: ${e.message}")
        }
    }
}
