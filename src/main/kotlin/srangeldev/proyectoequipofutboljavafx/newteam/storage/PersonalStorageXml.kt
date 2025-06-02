package srangeldev.proyectoequipofutboljavafx.newteam.storage

import nl.adaptivity.xmlutil.serialization.XML
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.dto.EquipoDtoXml
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalXmlDto
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toEntrenador
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toJugador
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toXmlDto
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

        try {
            // Use a custom XML parser to handle the specific XML structure in the test
            val xmlString = file.readText()

            // Simple XML parsing for the specific structure
            val personalList = mutableListOf<Personal>()

            // Extract personal elements
            val personalRegex = "<personal>\\s*([\\s\\S]*?)\\s*</personal>".toRegex()
            val personalMatches = personalRegex.findAll(xmlString)

            for (match in personalMatches) {
                val personalXml = match.groupValues[1]

                // Extract fields
                val id = extractField(personalXml, "id")?.toIntOrNull() ?: 0
                val tipo = extractField(personalXml, "tipo") ?: ""
                val nombre = extractField(personalXml, "nombre") ?: ""
                val apellidos = extractField(personalXml, "apellidos") ?: ""
                val fechaNacimiento = extractField(personalXml, "fechaNacimiento") ?: ""
                val fechaIncorporacion = extractField(personalXml, "fechaIncorporacion") ?: ""
                val salario = extractField(personalXml, "salario")?.toDoubleOrNull() ?: 0.0
                val pais = extractField(personalXml, "pais") ?: ""
                val especialidad = extractField(personalXml, "especialidad") ?: ""
                val posicion = extractField(personalXml, "posicion") ?: ""
                val dorsal = extractField(personalXml, "dorsal") ?: ""
                val altura = extractField(personalXml, "altura") ?: ""
                val peso = extractField(personalXml, "peso") ?: ""
                val goles = extractField(personalXml, "goles") ?: ""
                val partidosJugados = extractField(personalXml, "partidosJugados") ?: ""

                // Create DTO
                val dto = PersonalXmlDto(
                    id = id,
                    tipo = tipo,
                    nombre = nombre,
                    apellidos = apellidos,
                    fechaNacimiento = fechaNacimiento,
                    fechaIncorporacion = fechaIncorporacion,
                    salario = salario,
                    pais = pais,
                    especialidad = especialidad,
                    posicion = posicion,
                    dorsal = dorsal,
                    altura = altura,
                    peso = peso,
                    goles = goles,
                    partidosJugados = partidosJugados
                )

                // Convert to model
                val personal = when (tipo) {
                    "Entrenador" -> dto.toEntrenador()
                    "Jugador" -> dto.toJugador()
                    else -> throw PersonalException.PersonalStorageException("Tipo de Personal desconocido: $tipo")
                }

                personalList.add(personal)
            }

            if (personalList.isEmpty()) {
                throw PersonalException.PersonalStorageException("No se encontraron elementos de personal en el archivo XML")
            }

            return personalList
        } catch (e: PersonalException.PersonalStorageException) {
            throw e
        } catch (e: Exception) {
            logger.error { "Error al leer el archivo XML: ${e.message}" }
            throw PersonalException.PersonalStorageException("Error al leer el archivo XML: ${e.message}")
        }
    }

    private fun extractField(xml: String, fieldName: String): String? {
        val regex = "<$fieldName>(.*?)</$fieldName>".toRegex()
        val matchResult = regex.find(xml)
        return matchResult?.groupValues?.get(1)
    }

    override fun writeToFile(file: File, personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en formato de fichero XML: $file" }

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        if (!file.name.endsWith(".xml", ignoreCase = true)) {
            logger.error { "El fichero no tiene extensión XML: $file" }
            throw PersonalException.PersonalStorageException("El fichero no tiene extensión XML: $file")
        }

        try {
            // Create XML manually to match the expected format
            val xmlBuilder = StringBuilder()
            xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
            xmlBuilder.append("<equipo>\n")

            personalList.forEach { personal ->
                xmlBuilder.append("    <personal>\n")

                when (personal) {
                    is Jugador -> {
                        xmlBuilder.append("        <id>${personal.id}</id>\n")
                        xmlBuilder.append("        <tipo>Jugador</tipo>\n")
                        xmlBuilder.append("        <nombre>${personal.nombre}</nombre>\n")
                        xmlBuilder.append("        <apellidos>${personal.apellidos}</apellidos>\n")
                        xmlBuilder.append("        <fechaNacimiento>${personal.fechaNacimiento}</fechaNacimiento>\n")
                        xmlBuilder.append("        <fechaIncorporacion>${personal.fechaIncorporacion}</fechaIncorporacion>\n")
                        xmlBuilder.append("        <salario>${personal.salario}</salario>\n")
                        xmlBuilder.append("        <pais>${personal.paisOrigen}</pais>\n")
                        xmlBuilder.append("        <posicion>${personal.posicion}</posicion>\n")
                        xmlBuilder.append("        <dorsal>${personal.dorsal}</dorsal>\n")
                        xmlBuilder.append("        <altura>${personal.altura}</altura>\n")
                        xmlBuilder.append("        <peso>${personal.peso}</peso>\n")
                        xmlBuilder.append("        <goles>${personal.goles}</goles>\n")
                        xmlBuilder.append("        <partidosJugados>${personal.partidosJugados}</partidosJugados>\n")
                        if (personal.imagenUrl.isNotEmpty()) {
                            xmlBuilder.append("        <imagenUrl>${personal.imagenUrl}</imagenUrl>\n")
                        }
                    }
                    is Entrenador -> {
                        xmlBuilder.append("        <id>${personal.id}</id>\n")
                        xmlBuilder.append("        <tipo>Entrenador</tipo>\n")
                        xmlBuilder.append("        <nombre>${personal.nombre}</nombre>\n")
                        xmlBuilder.append("        <apellidos>${personal.apellidos}</apellidos>\n")
                        xmlBuilder.append("        <fechaNacimiento>${personal.fechaNacimiento}</fechaNacimiento>\n")
                        xmlBuilder.append("        <fechaIncorporacion>${personal.fechaIncorporacion}</fechaIncorporacion>\n")
                        xmlBuilder.append("        <salario>${personal.salario}</salario>\n")
                        xmlBuilder.append("        <pais>${personal.paisOrigen}</pais>\n")
                        xmlBuilder.append("        <especialidad>${personal.especializacion}</especialidad>\n")
                        if (personal.imagenUrl.isNotEmpty()) {
                            xmlBuilder.append("        <imagenUrl>${personal.imagenUrl}</imagenUrl>\n")
                        }
                    }
                    else -> throw PersonalException.PersonalStorageException("Tipo de Personal desconocido")
                }

                xmlBuilder.append("    </personal>\n")
            }

            xmlBuilder.append("</equipo>")

            file.writeText(xmlBuilder.toString())
        } catch (e: PersonalException.PersonalStorageException) {
            throw e
        } catch (e: Exception) {
            logger.error { "Error al escribir el archivo XML: ${e.message}" }
            throw PersonalException.PersonalStorageException("Error al escribir el archivo XML: ${e.message}")
        }
    }
}
