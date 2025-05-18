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

    override fun readFromFile(file: File): List<Personal> {
        logger.debug { "Leyendo personal de fichero JSON: $file" }
        if (!file.exists() || !file.isFile || !file.canRead() || file.length() == 0L || !file.name.endsWith(".json")) {
            logger.error { "El fichero no existe, o no es un fichero o no se puede leer: $file" }
            throw PersonalException.PersonalStorageException("El fichero no existe, o no es un fichero o no se puede leer: $file")
        }
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(kotlinx.serialization.builtins.ListSerializer(PersonalJsonDto.serializer()), file.readText()).map {
            when (it.rol) {
                "Entrenador" -> it.toEntrenador()
                "Jugador" -> it.toJugador()
                else -> throw IllegalArgumentException("Tipo de personal desconocido: ${it.rol}")
            }
        }
    }

    override fun writeToFile(file: File, personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en fichero JSON: $file" }

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

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
