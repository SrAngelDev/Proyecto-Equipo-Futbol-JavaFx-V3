package srangeldev.storage

import org.lighthousegames.logging.logging
import srangeldev.dto.PersonalCsvDto
import srangeldev.exceptions.PersonalException
import srangeldev.mapper.toCsvDto
import srangeldev.mapper.toEntrenador
import srangeldev.mapper.toJugador
import srangeldev.models.Entrenador
import srangeldev.models.Jugador
import srangeldev.models.Personal
import java.io.File

class PersonalStorageCsv : PersonalStorageFile {
    private val logger = logging()

    companion object {
        private const val CSV_HEADER =
            "tipo,id,nombre,apellidos,fechaNacimiento,fechaIncorporacion,salario,paisOrigen,especializacion,posicion,dorsal,altura,peso,goles,partidosJugados,minutosJugados\n"
    }

    init {
        logger.debug { "Inicializando almacenamiento de personal en formato CSV" }
    }

    private fun validateFileForReading(file: File) {
        if (!file.exists() || !file.isFile || !file.canRead() || file.length() == 0L || !file.name.endsWith(
                ".csv",
                true
            )
        ) {
            logger.error { "El fichero no existe, o no es un fichero o no se puede leer: $file" }
            throw PersonalException.PersonalStorageException("El fichero no existe, o no es un fichero o no se puede leer: $file")
        }
    }

    override fun readFromFile(file: File): List<Personal> {
        logger.debug { "Leyendo personal de fichero CSV: $file" }
        validateFileForReading(file)

        return file.readLines()
            .drop(1)
            .map { it.split(",") }
            .map { it.map { it.trim() } }
            .map {
                val dto = PersonalCsvDto(
                    id = it[0].toInt(),
                    nombre = it[1],
                    apellidos = it[2],
                    fechaNacimiento = it[3],
                    fechaIncorporacion = it[4],
                    salario = it[5].toDouble(),
                    paisOrigen = it[6],
                    rol = it[7],
                    especializacion = it[8],
                    posicion = it[9],
                    dorsal = it[10],
                    altura = it[11],
                    peso = it[12],
                    goles = it[13],
                    partidosJugados = it[14]
                )
                when (dto.rol) {
                    "Entrenador" -> dto.toEntrenador()
                    "Jugador" -> dto.toJugador()
                    else -> throw PersonalException.PersonalStorageException("Tipo de personal desconocido: ${dto.rol}")
                }
            }
    }

    override fun writeToFile(personalList: List<Personal>) {
        logger.debug { "Escribiendo personal en fichero CSV" }

        val file = File("backup/personal_back.csv")

        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        if (!file.parentFile.isDirectory || !file.name.endsWith(".csv", true)) {
            logger.error { "El directorio padre del fichero no es un directorio o el fichero no tiene extensión CSV: ${file.parentFile.absolutePath}" }
            throw PersonalException.PersonalStorageException("El directorio padre del fichero no es un directorio o el fichero no tiene extensión CSV: ${file.parentFile.absolutePath}")
        }

        file.writeText(CSV_HEADER)

        personalList.forEach { personal ->
            val dto = when (personal) {
                is Entrenador -> personal.toCsvDto()
                is Jugador -> personal.toCsvDto()
                else -> throw PersonalException.PersonalStorageException("Tipo de personal desconocido")
            }

            val data = listOf(
                dto.rol,
                dto.id,
                dto.nombre,
                dto.apellidos,
                dto.fechaNacimiento,
                dto.fechaIncorporacion,
                dto.salario,
                dto.paisOrigen,
                dto.especializacion ?: "",
                dto.posicion ?: "",
                dto.dorsal?.toString() ?: "",
                dto.altura?.toString() ?: "",
                dto.peso?.toString() ?: "",
                dto.goles?.toString() ?: "",
                dto.partidosJugados?.toString() ?: ""
            ).joinToString(",")

            file.appendText("$data\n")
        }
        logger.debug { "Personal guardado en fichero CSV: $file" }
    }
}
