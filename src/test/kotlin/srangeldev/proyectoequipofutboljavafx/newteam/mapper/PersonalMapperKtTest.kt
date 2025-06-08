package srangeldev.proyectoequipofutboljavafx.newteam.mapper

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import srangeldev.proyectoequipofutboljavafx.newteam.dto.PersonalCsvDto
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate
import java.time.LocalDateTime

class PersonalMapperKtTest {

    /**
     * Tests for the function parseDate in PersonalMapperKt.
     * This function attempts to parse a date string into a LocalDate object using two different formats.
     * If both parsing attempts fail, it returns the current date.
     */

    @Test
    fun `parseDate should parse ISO_LOCAL_DATE format correctly`() {
        val dateString = "2025-01-15"
        val expectedDate = LocalDate.of(2025, 1, 15)

        val result = parseDate(dateString)

        assertEquals(expectedDate, result)
    }

    @Test
    fun `toCsvDto should correctly map Entrenador to PersonalCsvDto`() {
        val entrenador = Entrenador(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1980, 5, 10),
            fechaIncorporacion = LocalDate.of(2020, 6, 15),
            salario = 50000.0,
            paisOrigen = "USA",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            imagenUrl = "http://example.com/john_doe.jpg"
        )

        val result = entrenador.toCsvDto()

        assertEquals(1, result.id)
        assertEquals("John", result.nombre)
        assertEquals("Doe", result.apellidos)
        assertEquals("1980-05-10", result.fechaNacimiento)
        assertEquals("2020-06-15", result.fechaIncorporacion)
        assertEquals(50000.0, result.salario)
        assertEquals("USA", result.paisOrigen)
        assertEquals("Entrenador", result.rol)
        assertEquals("ENTRENADOR_PRINCIPAL", result.especializacion)
        assertEquals("http://example.com/john_doe.jpg", result.imagenUrl)
    }

    @Test
    fun `toCsvDto should correctly map Jugador to PersonalCsvDto`() {
        val jugador = Jugador(
            id = 2,
            nombre = "Jane",
            apellidos = "Smith",
            fechaNacimiento = LocalDate.of(1995, 3, 20),
            fechaIncorporacion = LocalDate.of(2021, 8, 1),
            salario = 70000.0,
            paisOrigen = "UK",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.75,
            peso = 70.0,
            goles = 50,
            partidosJugados = 100,
            imagenUrl = "http://example.com/jane_smith.jpg"
        )

        val result = jugador.toCsvDto()

        assertEquals(2, result.id)
        assertEquals("Jane", result.nombre)
        assertEquals("Smith", result.apellidos)
        assertEquals("1995-03-20", result.fechaNacimiento)
        assertEquals("2021-08-01", result.fechaIncorporacion)
        assertEquals(70000.0, result.salario)
        assertEquals("UK", result.paisOrigen)
        assertEquals("Jugador", result.rol)
        assertEquals("DELANTERO", result.posicion)
        assertEquals("10", result.dorsal)
        assertEquals("1.75", result.altura)
        assertEquals("70.0", result.peso)
        assertEquals("50", result.goles)
        assertEquals("100", result.partidosJugados)
        assertEquals("http://example.com/jane_smith.jpg", result.imagenUrl)
    }

    @Test
    fun `parseDate should parse yyyy-MM-dd format correctly`() {
        val dateString = "2025-01-15"
        val expectedDate = LocalDate.of(2025, 1, 15)

        val result = parseDate(dateString)

        assertEquals(expectedDate, result)
    }

    @Test
    fun `parseDate should return current date on invalid date string`() {
        val dateString = "invalid-date-string"

        val result = parseDate(dateString)

        assertEquals(LocalDate.now(), result) // Assumes test runs quickly enough for LocalDate.now() to match
    }

    @Test
    fun `parseDate should return current date when partially invalid`() {
        val dateString = "15-2025-01"

        val result = parseDate(dateString)

        assertEquals(LocalDate.now(), result)
    }

    @Test
    fun `toJsonDto should correctly map Entrenador to PersonalJsonDto`() {
        val entrenador = Entrenador(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1980, 5, 10),
            fechaIncorporacion = LocalDate.of(2020, 6, 15),
            salario = 50000.0,
            paisOrigen = "USA",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            imagenUrl = "http://example.com/john_doe.jpg"
        )

        val result = entrenador.toJsonDto()

        assertEquals(1, result.id)
        assertEquals("John", result.nombre)
        assertEquals("Doe", result.apellidos)
        assertEquals("1980-05-10", result.fechaNacimiento)
        assertEquals("2020-06-15", result.fechaIncorporacion)
        assertEquals(50000.0, result.salario)
        assertEquals("USA", result.pais)
        assertEquals("Entrenador", result.rol)
        assertEquals("ENTRENADOR_PRINCIPAL", result.especializacion)
        assertEquals("http://example.com/john_doe.jpg", result.imagenUrl)
    }

    @Test
    fun `toJsonDto should correctly map Jugador to PersonalJsonDto`() {
        val jugador = Jugador(
            id = 2,
            nombre = "Jane",
            apellidos = "Smith",
            fechaNacimiento = LocalDate.of(1995, 3, 20),
            fechaIncorporacion = LocalDate.of(2021, 8, 1),
            salario = 70000.0,
            paisOrigen = "UK",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.75,
            peso = 70.0,
            goles = 50,
            partidosJugados = 100,
            imagenUrl = "http://example.com/jane_smith.jpg"
        )

        val result = jugador.toJsonDto()

        assertEquals(2, result.id)
        assertEquals("Jane", result.nombre)
        assertEquals("Smith", result.apellidos)
        assertEquals("1995-03-20", result.fechaNacimiento)
        assertEquals("2021-08-01", result.fechaIncorporacion)
        assertEquals(70000.0, result.salario)
        assertEquals("UK", result.pais)
        assertEquals("Jugador", result.rol)
        assertEquals("DELANTERO", result.posicion)
        assertEquals(10, result.dorsal)
        assertEquals(1.75, result.altura)
        assertEquals(70.0, result.peso)
        assertEquals(50, result.goles)
        assertEquals(100, result.partidosJugados)
        assertEquals("http://example.com/jane_smith.jpg", result.imagenUrl)
    }

    @Test
    fun `toXmlDto should correctly map Entrenador to PersonalXmlDto`() {
        val entrenador = Entrenador(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1980, 5, 10),
            fechaIncorporacion = LocalDate.of(2020, 6, 15),
            salario = 50000.0,
            paisOrigen = "USA",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            imagenUrl = "http://example.com/john_doe.jpg"
        )

        val result = entrenador.toXmlDto()

        assertEquals(1, result.id)
        assertEquals("Entrenador", result.tipo)
        assertEquals("John", result.nombre)
        assertEquals("Doe", result.apellidos)
        assertEquals("1980-05-10", result.fechaNacimiento)
        assertEquals("2020-06-15", result.fechaIncorporacion)
        assertEquals(50000.0, result.salario)
        assertEquals("USA", result.pais)
        assertEquals("ENTRENADOR_PRINCIPAL", result.especialidad)
        assertEquals("http://example.com/john_doe.jpg", result.imagenUrl)
    }

    @Test
    fun `toXmlDto should correctly map Jugador to PersonalXmlDto`() {
        val jugador = Jugador(
            id = 2,
            nombre = "Jane",
            apellidos = "Smith",
            fechaNacimiento = LocalDate.of(1995, 3, 20),
            fechaIncorporacion = LocalDate.of(2021, 8, 1),
            salario = 70000.0,
            paisOrigen = "UK",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.75,
            peso = 70.0,
            goles = 50,
            partidosJugados = 100,
            imagenUrl = "http://example.com/jane_smith.jpg"
        )

        val result = jugador.toXmlDto()

        assertEquals(2, result.id)
        assertEquals("Jugador", result.tipo)
        assertEquals("Jane", result.nombre)
        assertEquals("Smith", result.apellidos)
        assertEquals("1995-03-20", result.fechaNacimiento)
        assertEquals("2021-08-01", result.fechaIncorporacion)
        assertEquals(70000.0, result.salario)
        assertEquals("UK", result.pais)
        assertEquals("DELANTERO", result.posicion)
        assertEquals("10", result.dorsal)
        assertEquals("1.75", result.altura)
        assertEquals("70.0", result.peso)
        assertEquals("50", result.goles)
        assertEquals("100", result.partidosJugados)
        assertEquals("http://example.com/jane_smith.jpg", result.imagenUrl)
    }

    @Test
    fun `toEntrenador should correctly map a valid PersonalCsvDto to Entrenador`() {
        val dto = PersonalCsvDto(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = "1980-05-10",
            fechaIncorporacion = "2020-06-15",
            salario = 50000.0,
            paisOrigen = "USA",
            especializacion = "ENTRENADOR_PRINCIPAL",
            imagenUrl = "http://example.com/john_doe.jpg"
        )

        val result = dto.toEntrenador()

        assertEquals(1, result.id)
        assertEquals("John", result.nombre)
        assertEquals("Doe", result.apellidos)
        assertEquals(LocalDate.of(1980, 5, 10), result.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 6, 15), result.fechaIncorporacion)
        assertEquals(50000.0, result.salario)
        assertEquals("USA", result.paisOrigen)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, result.especializacion)
        assertEquals("http://example.com/john_doe.jpg", result.imagenUrl)
    }

    @Test
    fun `toEntrenador should throw exception on invalid especializacion`() {
        val dto = PersonalCsvDto(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = "1980-05-10",
            fechaIncorporacion = "2020-06-15",
            salario = 50000.0,
            paisOrigen = "USA",
            especializacion = "INVALID",
            imagenUrl = "http://example.com/john_doe.jpg"
        )

        val exception = assertThrows<IllegalArgumentException> {
            dto.toEntrenador()
        }

        assertEquals("Especialización no válida: INVALID", exception.message)
    }

    @Test
    fun `toEntrenador should assign default especializacion when empty or null`() {
        val dto = PersonalCsvDto(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = "1980-05-10",
            fechaIncorporacion = "2020-06-15",
            salario = 50000.0,
            paisOrigen = "USA",
            especializacion = "",
            imagenUrl = "http://example.com/john_doe.jpg"
        )

        val result = dto.toEntrenador()

        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, result.especializacion)
    }

    @Test
    fun `toEntrenador should correctly map especializacion with lowercase`() {
        val dto = PersonalCsvDto(
            id = 5,
            nombre = "Emily",
            apellidos = "Johnson",
            fechaNacimiento = "1975-12-23",
            fechaIncorporacion = "2019-03-05",
            salario = 60000.0,
            paisOrigen = "UK",
            especializacion = "entrenador_asistente",
            imagenUrl = "http://example.com/emily_johnson.jpg"
        )

        val result = dto.toEntrenador()

        assertEquals(Entrenador.Especializacion.ENTRENADOR_ASISTENTE, result.especializacion)
    }

    @Test
    fun `toJugador should correctly map a valid PersonalCsvDto to Jugador`() {
        val dto = PersonalCsvDto(
            id = 2,
            nombre = "Jane",
            apellidos = "Smith",
            fechaNacimiento = "1995-03-20",
            fechaIncorporacion = "2021-08-01",
            salario = 70000.0,
            paisOrigen = "UK",
            posicion = "DELANTERO",
            dorsal = "10",
            altura = "1.75",
            peso = "70.0",
            goles = "50",
            partidosJugados = "100",
            imagenUrl = "http://example.com/jane_smith.jpg"
        )

        val result = dto.toJugador()

        assertEquals(2, result.id)
        assertEquals("Jane", result.nombre)
        assertEquals("Smith", result.apellidos)
        assertEquals(LocalDate.of(1995, 3, 20), result.fechaNacimiento)
        assertEquals(LocalDate.of(2021, 8, 1), result.fechaIncorporacion)
        assertEquals(70000.0, result.salario)
        assertEquals("UK", result.paisOrigen)
        assertEquals(Jugador.Posicion.DELANTERO, result.posicion)
        assertEquals(10, result.dorsal)
        assertEquals(1.75, result.altura)
        assertEquals(70.0, result.peso)
        assertEquals(50, result.goles)
        assertEquals(100, result.partidosJugados)
        assertEquals("http://example.com/jane_smith.jpg", result.imagenUrl)
    }

    @Test
    fun `toJugador should throw exception on invalid position`() {
        val dto = PersonalCsvDto(
            id = 4,
            nombre = "Alice",
            apellidos = "Brown",
            fechaNacimiento = "1990-10-25",
            fechaIncorporacion = "2015-05-30",
            salario = 80000.0,
            paisOrigen = "AUS",
            posicion = "INVALID",
            dorsal = "7",
            altura = "1.80",
            peso = "65.0",
            goles = "30",
            partidosJugados = "50",
            imagenUrl = "http://example.com/alice_brown.jpg"
        )

        val exception = assertThrows<IllegalArgumentException> {
            dto.toJugador()
        }

        assertEquals(
            "No enum constant srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador.Posicion.INVALID",
            exception.message
        )
    }

    @Test
    fun `toJugador should throw exception on invalid dorsal field`() {
        val dto = PersonalCsvDto(
            id = 7,
            nombre = "Chris",
            apellidos = "Evans",
            fechaNacimiento = "1990-10-20",
            fechaIncorporacion = "2020-06-10",
            salario = 85000.0,
            paisOrigen = "USA",
            posicion = "DELANTERO",
            dorsal = "NotAnInt",
            altura = "1.90",
            peso = "80.0",
            goles = "20",
            partidosJugados = "30",
            imagenUrl = "http://example.com/chris_evans.jpg"
        )

        val exception = assertThrows<NumberFormatException> {
            dto.toJugador()
        }

        assertEquals("For input string: \"NotAnInt\"", exception.message)
    }

    @Test
    fun `toJugador should throw exception on incorrect format for numeric values`() {
        val dto = PersonalCsvDto(
            id = 10,
            nombre = "Emma",
            apellidos = "Taylor",
            fechaNacimiento = "1998-04-15",
            fechaIncorporacion = "2022-01-20",
            salario = 70000.0,
            paisOrigen = "UK",
            posicion = "DELANTERO",
            dorsal = "N/A",
            altura = "N/A",
            peso = "N/A",
            goles = "N/A",
            partidosJugados = "N/A",
            imagenUrl = "http://example.com/emma_taylor.jpg"
        )

        assertThrows<NumberFormatException> {
            dto.toJugador()
        }
    }
}