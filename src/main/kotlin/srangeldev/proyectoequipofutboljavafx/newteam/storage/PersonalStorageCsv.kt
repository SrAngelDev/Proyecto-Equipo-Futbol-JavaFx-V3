package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalCsvDto
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toCsvDto
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toEntrenador
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toJugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.validator.ValidatorFactory
import java.io.File

class PersonalStorageCsv : PersonalStorageFile {
    private val logger = logging()

    companion object {
        private const val CSV_HEADER =
            "id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados\n"
    }

    init {
        logger.debug { "Inicializando almacenamiento de personal en formato CSV" }
    }


    override fun readFromFile(file: File): List<Personal> {
        logger.debug { "Leyendo personal de fichero CSV: $file" }
        
        ValidatorFactory.validate(file)

        try {
            return file.readLines()
                .drop(1) // Skip header
                .filter { it.isNotBlank() } // Skip empty lines
                .map { it.split(",") }
                .map { values ->
                    // Ensure we have at least the required fields, pad with empty strings if needed
                    val paddedValues = values.map { it.trim() }.toMutableList()
                    while (paddedValues.size < 15) {
                        paddedValues.add("")
                    }

                    try {
                        val dto = PersonalCsvDto(
                            id = paddedValues[0].toInt(), // Cambiado de toIntOrNull()
                            nombre = paddedValues[1],
                            apellidos = paddedValues[2],
                            fechaNacimiento = paddedValues[3],
                            fechaIncorporacion = paddedValues[4],
                            salario = paddedValues[5].toDouble(), // Cambiado de toDoubleOrNull()
                            paisOrigen = paddedValues[6],
                            rol = paddedValues[7],
                            especializacion = paddedValues[8],
                            posicion = paddedValues[9],
                            dorsal = paddedValues[10],
                            altura = paddedValues[11],
                            peso = paddedValues[12],
                            goles = paddedValues[13],
                            partidosJugados = paddedValues[14]
                        )
                        when (dto.rol) {
                            "Entrenador" -> dto.toEntrenador()
                            "Jugador" -> dto.toJugador()
                            else -> throw PersonalException.PersonalStorageException("Tipo de personal desconocido: ${dto.rol}")
                        }
                    } catch (e: NumberFormatException) {
                        throw PersonalException.PersonalStorageException("Formato CSV inválido: error al convertir valores numéricos")
                    } catch (e: Exception) {
                        throw PersonalException.PersonalStorageException("Error al procesar el archivo CSV: ${e.message}")
                    }
                }
        } catch (e: PersonalException.PersonalStorageException) {
            throw e
        } catch (e: Exception) {
            throw PersonalException.PersonalStorageException("Error al leer el archivo CSV: ${e.message}")
        }
    }

    override fun writeToFile(file: File, personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en fichero CSV: $file" }

        // Primero validamos la extensión XML
        if (!file.name.endsWith(".csv", ignoreCase = true)) {
            logger.error { "El fichero no tiene extensión CSV: $file" }
            throw PersonalException.PersonalStorageException("El fichero no tiene extensión CSV: $file")
        }

        // Luego creamos el directorio si es necesario
        file.parentFile?.mkdirs()

        file.writeText(CSV_HEADER)

        personalList.forEach { personal ->
            val dto = when (personal) {
                is Entrenador -> personal.toCsvDto()
                is Jugador -> personal.toCsvDto()
                else -> throw PersonalException.PersonalStorageException("Tipo de personal desconocido")
            }

            // Ensure the order matches the CSV_HEADER
            val data = listOf(
                dto.id,
                dto.nombre,
                dto.apellidos,
                dto.fechaNacimiento,
                dto.fechaIncorporacion,
                dto.salario,
                dto.paisOrigen,
                dto.rol,
                dto.especializacion ?: "",
                dto.posicion ?: "",
                dto.dorsal?.toString() ?: "",
                dto.altura?.toString() ?: "",
                dto.peso?.toString() ?: "",
                dto.goles?.toString() ?: "",
                dto.partidosJugados?.toString() ?: ""
            ).joinToString(",")

            file.appendText("$data\n")

            ValidatorFactory.validate(file)
        }
        logger.debug { "Personal guardado en fichero CSV: $file" }
    }
}