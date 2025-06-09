package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalCsvDto
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class PersonalStorageCsvTest {

    @Test
    fun `writeToFile should correctly write Personal list to file`() {
        val tempFile = File.createTempFile("test", ".csv").apply { deleteOnExit() }
        val personalList = listOf(
            Jugador(
                1, "John", "Doe", LocalDate.parse("1990-01-01"), LocalDate.parse("2022-01-01"), 5000.0, "USA",
                LocalDateTime.now(), LocalDateTime.now(), Jugador.Posicion.DELANTERO, 10, 180.0, 75.0, 0, 0
            ),
            Entrenador(2, "Jane", "Smith", LocalDate.parse("1985-05-05"), LocalDate.parse("2021-05-05"), 6000.0, "Canada",
                LocalDateTime.now(), LocalDateTime.now(), Entrenador.Especializacion.ENTRENADOR_PRINCIPAL)
        )

        val storage = PersonalStorageCsv()
        storage.writeToFile(tempFile, personalList)

        val fileContent = tempFile.readText().trim()
        val expectedContent = """
id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados
1,John,Doe,1990-01-01,2022-01-01,5000.0,USA,Jugador,,DELANTERO,10,180.0,75.0,0,0
2,Jane,Smith,1985-05-05,2021-05-05,6000.0,Canada,Entrenador,ENTRENADOR_PRINCIPAL,,,,,,
    """.trimIndent()

        assertEquals(expectedContent, fileContent)
    }

    @Test
    fun `writeToFile should throw exception when file is not CSV`() {
        val tempFile = File.createTempFile("test", ".txt").apply { deleteOnExit() }
        val personalList = listOf(
            Jugador(
                1, "John", "Doe", LocalDate.parse("1990-01-01"), LocalDate.parse("2022-01-01"), 5000.0, "USA",
                LocalDateTime.now(), LocalDateTime.now(), Jugador.Posicion.DELANTERO, 75, 180.0, 90.0, 10, 10
            )
        )

        val storage = PersonalStorageCsv()
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.writeToFile(tempFile, personalList)
        }

        assertEquals("Error en el almacenamiento: El fichero no tiene extensión CSV: $tempFile", exception.message)
    }

    @Test
    fun `writeToFile should handle empty list by writing only the header`() {
        val tempFile = File.createTempFile("test", ".csv").apply { deleteOnExit() }
        val personalList = emptyList<Personal>()

        val storage = PersonalStorageCsv()
        storage.writeToFile(tempFile, personalList)

        val fileContent = tempFile.readText().trim()
        assertEquals(
            "id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados",
            fileContent
        )
    }

    @Test
    fun `readFromFile should return list of Personal objects when file is valid`() {
        val tempFile = File.createTempFile("test", ".csv").apply {
            deleteOnExit()
            writeText(
                """
id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados
1,John,Doe,1990-01-01,2022-01-01,5000.0,USA,Jugador,,DELANTERO,10,180,75,0,0
""".trimIndent()
            )
        }

        val storage = PersonalStorageCsv()
        val result = storage.readFromFile(tempFile)

        assertEquals(1, result.size)
        val jugador = result[0] as Jugador
        assertEquals("John", jugador.nombre)
        assertEquals("Doe", jugador.apellidos)
        assertEquals("USA", jugador.paisOrigen)
    }

    @Test
    fun `readFromFile should ignore blank lines in file`() {
        val tempFile = File.createTempFile("test", ".csv").apply {
            deleteOnExit()
            writeText("""id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados
1,John,Doe,1990-01-01,2022-01-01,5000.0,USA,Entrenador,ENTRENADOR_PRINCIPAL,,,,,,""")
        }

        val storage = PersonalStorageCsv()
        val result = storage.readFromFile(tempFile)

        assertEquals(1, result.size)
        val entrenador = result[0] as Entrenador
        assertEquals("John", entrenador.nombre)
    }

    @Test
    fun `readFromFile should pad missing values with empty strings`() {
        val tempFile = File.createTempFile("test", ".csv").apply {
            deleteOnExit()
            writeText(
                """
            id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados
            1,John,Doe,1990-01-01,2022-01-01,5000.0,USA,Jugador,,DELANTERO,0,0,0,0,0
            """.trimIndent()
            )
        }

        val storage = PersonalStorageCsv()
        val result = storage.readFromFile(tempFile)

        assertEquals(1, result.size)
        assertEquals("John", result[0].nombre)
    }

    @Test
    fun `readFromFile should throw exception for invalid numeric values`() {
        // Crear un archivo temporal para la prueba
        val tempFile = File.createTempFile("test", ".csv").apply {
            deleteOnExit()
            writeText(
                """
            id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados
            abc,John,Doe,1990-01-01,2022-01-01,xyz,USA,Entrenador,ENTRENADOR_PRINCIPAL,,,,,,
        """.trimIndent()
            )
        }

        val storage = PersonalStorageCsv()

        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(tempFile)
        }

        assertEquals(
            "Error en el almacenamiento: Formato CSV inválido: error al convertir valores numéricos", exception.message
        )
    }

    @Test
    fun `readFromFile should throw exception for unknown role`() {
        // Crear un archivo temporal para la prueba
        val tempFile = File.createTempFile("test", ".csv").apply {
            deleteOnExit()
            writeText(
                """
            id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados
            1,John,Doe,1990-01-01,2022-01-01,5000.0,USA,Manager,,,,,,,
        """.trimIndent()
            )
        }

        val storage = PersonalStorageCsv()

        val exception = assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(tempFile)
        }

        assertEquals("Error en el almacenamiento: Error al procesar el archivo CSV: Error en el almacenamiento: Tipo de personal desconocido: Manager", exception.message)
    }
}