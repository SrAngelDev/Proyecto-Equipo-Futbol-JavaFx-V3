package srangeldev.storage

import nl.adaptivity.xmlutil.serialization.XML
import org.lighthousegames.logging.logging
import srangeldev.dto.EquipoDtoXml
import srangeldev.dto.PersonalXmlDto
import srangeldev.exceptions.PersonalException
import srangeldev.mapper.toEntrenador
import srangeldev.mapper.toJugador
import srangeldev.mapper.toXmlDto
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File

/**
 * Clase que implementa el almacenamiento de personal en formato XML.
 */
class PersonalStorageXml : PersonalStorageFile {
    private val logger = logging()

    /**
     * Inicializa el almacenamiento de personal en formato XML.
     */
    init {
        logger.debug { "Inicializando almacenamiento de personal en formato XML" }
    }

    /**
     * Lee los datos de personal desde un archivo XML.
     *
     * @param file El archivo XML desde el cual leer.
     * @return Una lista de datos de personal.
     * @throws PersonalException.PersonalStorageException Si el archivo no existe, no es legible, o no es un archivo XML válido.
     */
    override fun readFromFile(file: File): List<Personal> {
        if (!file.exists() || !file.isFile || !file.canRead() || file.length() == 0L || !file.name.endsWith(
                ".xml",
                true
            )
        ) {
            logger.error { "El fichero no existe o es un fichero que no se puede leer: $file" }
            throw PersonalException.PersonalStorageException("El fichero no existe o es un fichero que no se puede leer: $file")
        }
        val xml = XML {}
        val xmlString = file.readText()
        val personalDto: EquipoDtoXml = xml.decodeFromString(EquipoDtoXml.serializer(), xmlString)
        val personalListDto = personalDto.equipo
        return personalListDto.map {
            when (it.tipo) {
                "Entrenador" -> it.toEntrenador()
                "Jugador" -> it.toJugador()
                else -> throw IllegalArgumentException("Tipo de Personal desconocido")
            }
        }
    }

    override fun writeToFile(personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en formato de fichero XML" }
        val file = File("backup/personal_back.xml")

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        if (!file.parentFile.isDirectory || !file.name.endsWith(".xml", true)) {
            logger.error { "El directorio padre del fichero no es un directorio o el fichero no tiene extensión XML: ${file.parentFile.absolutePath}" }
            throw PersonalException.PersonalStorageException("El directorio padre del fichero no es un directorio o el fichero no tiene extensión XML: ${file.parentFile.absolutePath}")
        }

        val xml = XML { indent = 4 }
        val personalListDto: List<PersonalXmlDto> = personalList.map {
            when (it) {
                is Entrenador -> it.toXmlDto()
                is Jugador -> it.toXmlDto()
                else -> throw IllegalArgumentException("Tipo de Personal desconocido")
            }
        }
        val personalDto = EquipoDtoXml(equipo = personalListDto)
        file.writeText(xml.encodeToString(EquipoDtoXml.serializer(), personalDto))
    }
}
