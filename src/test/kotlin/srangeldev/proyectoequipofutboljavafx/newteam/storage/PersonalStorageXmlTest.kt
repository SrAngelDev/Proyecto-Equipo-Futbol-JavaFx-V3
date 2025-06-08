package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.io.TempDir
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.io.File
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("PersonalStorageXml Test")
class PersonalStorageXmlTest {
    private val storage = PersonalStorageXml()
    private val now = LocalDateTime.now()

    private val jugador = Jugador(
        id = 1,
        nombre = "Juan",
        apellidos = "Pérez",
        fechaNacimiento = LocalDate.of(1990, 1, 1),
        fechaIncorporacion = LocalDate.of(2020, 1, 1),
        salario = 50_000.0,
        paisOrigen = "España",
        createdAt = now,
        updatedAt = now,
        posicion = Jugador.Posicion.DELANTERO,
        dorsal = 9,
        altura = 1.8,
        peso = 75.0,
        goles = 10,
        partidosJugados = 20,
        imagenUrl = "jugador.jpg"
    )

    private val entrenador = Entrenador(
        id = 2,
        nombre = "Carlos",
        apellidos = "Gómez",
        fechaNacimiento = LocalDate.of(1980, 5, 15),
        fechaIncorporacion = LocalDate.of(2018, 7, 1),
        salario = 70_000.0,
        paisOrigen = "España",
        createdAt = now,
        updatedAt = now,
        especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
        imagenUrl = "entrenador.jpg"
    )

    @Nested
    @DisplayName("readFromFile Tests")
    inner class ReadTests {
        @TempDir
        lateinit var tempDir: Path

        @Test
        fun `should read player and coach with attributes`() {
            val file = File(tempDir.toFile(), "test.xml")
            file.writeText("""
                <?xml version="1.0" encoding="UTF-8"?>
                <equipo>
                    <personal id="1">
                        <tipo>Jugador</tipo>
                        <nombre>Juan</nombre>
                        <apellidos>Pérez</apellidos>
                        <fechaNacimiento>1990-01-01</fechaNacimiento>
                        <fechaIncorporacion>2020-01-01</fechaIncorporacion>
                        <salario>50000.0</salario>
                        <pais>España</pais>
                        <posicion>DELANTERO</posicion>
                        <dorsal>9</dorsal>
                        <altura>1.8</altura>
                        <peso>75.0</peso>
                        <goles>10</goles>
                        <partidosJugados>20</partidosJugados>
                        <imagenUrl>jugador.jpg</imagenUrl>
                    </personal>
                    <personal id="2">
                        <tipo>Entrenador</tipo>
                        <nombre>Carlos</nombre>
                        <apellidos>Gómez</apellidos>
                        <fechaNacimiento>1980-05-15</fechaNacimiento>
                        <fechaIncorporacion>2018-07-01</fechaIncorporacion>
                        <salario>70000.0</salario>
                        <pais>España</pais>
                        <especialidad>ENTRENADOR_PRINCIPAL</especialidad>
                        <imagenUrl>entrenador.jpg</imagenUrl>
                    </personal>
                </equipo>
            """.trimIndent())

            val result = storage.readFromFile(file)
            assertEquals(2, result.size)
        }

        @Test
        fun `should throw exception when file doesn't exist`() {
            val file = File(tempDir.toFile(), "nonexistent.xml")
            assertThrows<PersonalException.PersonalStorageException> {
                storage.readFromFile(file)
            }
        }

        @Test
        fun `should throw exception when file is not XML`() {
            val file = File(tempDir.toFile(), "test.txt")
            file.writeText("Not XML")
            assertThrows<PersonalException.PersonalStorageException> {
                storage.readFromFile(file)
            }
        }

        @Test
        fun `should throw exception when XML is empty`() {
            val file = File(tempDir.toFile(), "empty.xml")
            file.writeText("")
            assertThrows<PersonalException.PersonalStorageException> {
                storage.readFromFile(file)
            }
        }

        @Test
        fun `should throw exception for invalid personal type`() {
            val file = File(tempDir.toFile(), "test.xml")
            file.writeText("""
                <?xml version="1.0" encoding="UTF-8"?>
                <equipo>
                    <personal>
                        <tipo>InvalidType</tipo>
                        <nombre>Test</nombre>
                    </personal>
                </equipo>
            """.trimIndent())

            assertThrows<PersonalException.PersonalStorageException> {
                storage.readFromFile(file)
            }
        }
    }

    @Nested
    @DisplayName("writeToFile Tests")
    inner class WriteTests {
        @TempDir
        lateinit var tempDir: Path

        @Test
        fun `should write personnel list to XML file`() {
            val file = File(tempDir.toFile(), "output.xml")
            val list = listOf(jugador, entrenador)

            storage.writeToFile(file, list)
            assertTrue(file.exists())
            assertTrue(file.length() > 0)
        }

        @Test
        fun `should create parent directories if they don't exist`() {
            val file = File(tempDir.toFile(), "nested/dir/output.xml")
            storage.writeToFile(file, listOf(jugador))
            assertTrue(file.exists())
        }

        @Test
        fun `should throw exception for non-XML file`() {
            val file = File(tempDir.toFile(), "output.txt")
            assertThrows<PersonalException.PersonalStorageException> {
                storage.writeToFile(file, listOf(jugador))
            }
        }

        @Test
        fun `should handle empty personnel list`() {
            val file = File(tempDir.toFile(), "empty.xml")
            storage.writeToFile(file, emptyList())
            assertTrue(file.exists())
        }

        @Test
        fun `should write file with different image URL scenarios`() {
            val file = File(tempDir.toFile(), "images.xml")
            val jugadorSinImagen = Jugador(
                id = jugador.id,
                nombre = jugador.nombre,
                apellidos = jugador.apellidos,
                fechaNacimiento = jugador.fechaNacimiento,
                fechaIncorporacion = jugador.fechaIncorporacion,
                salario = jugador.salario,
                paisOrigen = jugador.paisOrigen,
                createdAt = jugador.createdAt,
                updatedAt = jugador.updatedAt,
                posicion = jugador.posicion,
                dorsal = jugador.dorsal,
                altura = jugador.altura,
                peso = jugador.peso,
                goles = jugador.goles,
                partidosJugados = jugador.partidosJugados,
                imagenUrl = ""
            )
            val entrenadorSinImagen = Entrenador(
                id = entrenador.id,
                nombre = entrenador.nombre,
                apellidos = entrenador.apellidos,
                fechaNacimiento = entrenador.fechaNacimiento,
                fechaIncorporacion = entrenador.fechaIncorporacion,
                salario = entrenador.salario,
                paisOrigen = entrenador.paisOrigen,
                createdAt = entrenador.createdAt,
                updatedAt = entrenador.updatedAt,
                especializacion = entrenador.especializacion,
                imagenUrl = ""
            )

            storage.writeToFile(file, listOf(jugadorSinImagen, entrenadorSinImagen))
            assertTrue(file.exists())
            val content = file.readText()
            assertFalse(content.contains("<imagenUrl>"))
        }
    }
}