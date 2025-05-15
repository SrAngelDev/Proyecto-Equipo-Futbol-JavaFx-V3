package srangeldev.storage

import kotlinx.serialization.json.Json
import org.lighthousegames.logging.logging
import srangeldev.dto.PersonalJsonDto
import srangeldev.exceptions.PersonalException
import srangeldev.mapper.toJsonDto
import srangeldev.mapper.toEntrenador
import srangeldev.mapper.toJugador
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
        return json.decodeFromString<List<PersonalJsonDto>>(file.readText()).map {
            when (it.rol) {
                "Entrenador" -> it.toEntrenador()
                "Jugador" -> it.toJugador()
                else -> throw IllegalArgumentException("Tipo de personal desconocido: ${it.rol}")
            }
        }
    }

    override fun writeToFile(personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en fichero JSON" }

        val file = File("backup/personal_back.json")
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
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

        file.writeText(json.encodeToString<List<PersonalJsonDto>>(jsonDtos))
        logger.debug { "Personal guardado en fichero JSON: $file" }
    }
}
