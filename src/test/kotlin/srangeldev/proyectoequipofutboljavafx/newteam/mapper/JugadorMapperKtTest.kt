package srangeldev.proyectoequipofutboljavafx.newteam.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import srangeldev.proyectoequipofutboljavafx.newteam.dao.JugadorEntity
import srangeldev.proyectoequipofutboljavafx.newteam.dao.PersonalEntity
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate
import java.time.LocalDateTime

class JugadorMapperKtTest {

    /**
     * Tests for the toModel function in JugadorMapperKt.
     * The `toModel` function maps a `JugadorEntity` and a `PersonalEntity` into a `Jugador` domain object.
     * Each test case validates a specific behavior or scenario of the mapping logic.
     */

    @Test
    fun `toModel should correctly map JugadorEntity and PersonalEntity to Jugador`() {
        // Arrange
        val jugadorEntity = JugadorEntity(
            id = 1,
            posicion = "DELANTERO",
            dorsal = 9,
            altura = 1.85,
            peso = 80.0,
            goles = 50,
            partidosJugados = 100
        )
        val personalEntity = PersonalEntity(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2020, 6, 1),
            salario = 100000.0,
            paisOrigen = "Argentina",
            tipo = "JUGADOR",
            imagenUrl = "http://example.com/image.jpg",
            createdAt = LocalDateTime.of(2020, 6, 1, 12, 0),
            updatedAt = LocalDateTime.of(2023, 6, 1, 12, 0)
        )

        // Act
        val result = jugadorEntity.toModel(personalEntity)

        // Assert
        assertEquals(1, result.id)
        assertEquals("Juan", result.nombre)
        assertEquals("Perez", result.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), result.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 6, 1), result.fechaIncorporacion)
        assertEquals(100000.0, result.salario)
        assertEquals("Argentina", result.paisOrigen)
        assertEquals("http://example.com/image.jpg", result.imagenUrl)
        assertEquals(LocalDateTime.of(2020, 6, 1, 12, 0), result.createdAt)
        assertEquals(LocalDateTime.of(2023, 6, 1, 12, 0), result.updatedAt)
        assertEquals(Jugador.Posicion.DELANTERO, result.posicion)
        assertEquals(9, result.dorsal)
        assertEquals(1.85, result.altura)
        assertEquals(80.0, result.peso)
        assertEquals(50, result.goles)
        assertEquals(100, result.partidosJugados)
    }

    @Test
    fun `toModel should correctly handle JugadorEntity with default values`() {
        // Arrange
        val jugadorEntity = JugadorEntity(
            id = 2,
            posicion = "DEFENSA",
            dorsal = 5,
            altura = 1.78,
            peso = 70.0,
            goles = 0,
            partidosJugados = 25
        )
        val personalEntity = PersonalEntity(
            id = 2,
            nombre = "Carlos",
            apellidos = "Gomez",
            fechaNacimiento = LocalDate.of(1995, 2, 15),
            fechaIncorporacion = LocalDate.of(2023, 1, 1),
            salario = 75000.0,
            paisOrigen = "Spain",
            tipo = "JUGADOR",
            imagenUrl = "",
            createdAt = LocalDateTime.of(2023, 1, 1, 10, 30),
            updatedAt = LocalDateTime.of(2025, 6, 1, 18, 45)
        )

        // Act
        val result = jugadorEntity.toModel(personalEntity)

        // Assert
        assertEquals(2, result.id)
        assertEquals("Carlos", result.nombre)
        assertEquals("Gomez", result.apellidos)
        assertEquals(LocalDate.of(1995, 2, 15), result.fechaNacimiento)
        assertEquals(LocalDate.of(2023, 1, 1), result.fechaIncorporacion)
        assertEquals(75000.0, result.salario)
        assertEquals("Spain", result.paisOrigen)
        assertEquals("", result.imagenUrl)
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 30), result.createdAt)
        assertEquals(LocalDateTime.of(2025, 6, 1, 18, 45), result.updatedAt)
        assertEquals(Jugador.Posicion.DEFENSA, result.posicion)
        assertEquals(5, result.dorsal)
        assertEquals(1.78, result.altura)
        assertEquals(70.0, result.peso)
        assertEquals(0, result.goles)
        assertEquals(25, result.partidosJugados)
    }

    @Test
    fun `toModel should throw IllegalArgumentException for invalid Posicion value`() {
        // Arrange
        val jugadorEntity = JugadorEntity(
            id = 3,
            posicion = "INVALID_POSITION",
            dorsal = 7,
            altura = 1.90,
            peso = 75.0,
            goles = 10,
            partidosJugados = 50
        )
        val personalEntity = PersonalEntity(
            id = 3,
            nombre = "Luis",
            apellidos = "Martinez",
            fechaNacimiento = LocalDate.of(2000, 5, 20),
            fechaIncorporacion = LocalDate.of(2024, 1, 1),
            salario = 85000.0,
            paisOrigen = "Mexico",
            tipo = "JUGADOR",
            imagenUrl = "http://example.com/player.jpg",
            createdAt = LocalDateTime.of(2024, 1, 1, 9, 0),
            updatedAt = LocalDateTime.of(2025, 5, 20, 15, 30)
        )

        // Act & Assert
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            jugadorEntity.toModel(personalEntity)
        }
        assertEquals(
            "No enum constant srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador.Posicion.INVALID_POSITION",
            exception.message
        )
    }

    @Test
    fun `toJugadorEntity should correctly map Jugador to JugadorEntity`() {
        // Arrange
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2020, 6, 1),
            salario = 100000.0,
            paisOrigen = "Argentina",
            createdAt = LocalDateTime.of(2020, 6, 1, 12, 0),
            updatedAt = LocalDateTime.of(2023, 6, 1, 12, 0),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 1.85,
            peso = 80.0,
            goles = 50,
            partidosJugados = 100,
            imagenUrl = "http://example.com/image.jpg"
        )

        // Act
        val result = jugador.toJugadorEntity()

        // Assert
        assertEquals(1, result.id)
        assertEquals("DELANTERO", result.posicion)
        assertEquals(9, result.dorsal)
        assertEquals(1.85, result.altura)
        assertEquals(80.0, result.peso)
        assertEquals(50, result.goles)
        assertEquals(100, result.partidosJugados)
    }

    @Test
    fun `toJugadorEntity should correctly handle default values`() {
        // Arrange
        val jugador = Jugador(
            id = 2,
            nombre = "Carlos",
            apellidos = "Gomez",
            fechaNacimiento = LocalDate.of(1995, 2, 15),
            fechaIncorporacion = LocalDate.of(2023, 1, 1),
            salario = 75000.0,
            paisOrigen = "Spain",
            createdAt = LocalDateTime.of(2023, 1, 1, 10, 30),
            updatedAt = LocalDateTime.of(2025, 6, 1, 18, 45),
            posicion = Jugador.Posicion.DEFENSA,
            dorsal = 5,
            altura = 1.78,
            peso = 70.0,
            goles = 0,
            partidosJugados = 25,
            imagenUrl = ""
        )

        // Act
        val result = jugador.toJugadorEntity()

        // Assert
        assertEquals(2, result.id)
        assertEquals("DEFENSA", result.posicion)
        assertEquals(5, result.dorsal)
        assertEquals(1.78, result.altura)
        assertEquals(70.0, result.peso)
        assertEquals(0, result.goles)
        assertEquals(25, result.partidosJugados)
    }

    @Test
    fun `toPersonalEntity should correctly map Jugador to PersonalEntity`() {
        // Arrange
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2020, 6, 1),
            salario = 100000.0,
            paisOrigen = "Argentina",
            createdAt = LocalDateTime.of(2020, 6, 1, 12, 0),
            updatedAt = LocalDateTime.of(2023, 6, 1, 12, 0),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 1.85,
            peso = 80.0,
            goles = 50,
            partidosJugados = 100,
            imagenUrl = "http://example.com/image.jpg"
        )

        // Act
        val result = jugador.toPersonalEntity()

        // Assert
        assertEquals(1, result.id)
        assertEquals("Juan", result.nombre)
        assertEquals("Perez", result.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), result.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 6, 1), result.fechaIncorporacion)
        assertEquals(100000.0, result.salario)
        assertEquals("Argentina", result.paisOrigen)
        assertEquals("JUGADOR", result.tipo)
        assertEquals("http://example.com/image.jpg", result.imagenUrl)
        assertEquals(LocalDateTime.of(2020, 6, 1, 12, 0), result.createdAt)
        assertEquals(LocalDateTime.of(2023, 6, 1, 12, 0), result.updatedAt)
    }

    @Test
    fun `toPersonalEntity should correctly handle default values`() {
        // Arrange
        val jugador = Jugador(
            id = 2,
            nombre = "Carlos",
            apellidos = "Gomez",
            fechaNacimiento = LocalDate.of(1995, 2, 15),
            fechaIncorporacion = LocalDate.of(2023, 1, 1),
            salario = 75000.0,
            paisOrigen = "Spain",
            createdAt = LocalDateTime.of(2023, 1, 1, 10, 30),
            updatedAt = LocalDateTime.of(2025, 6, 1, 18, 45),
            posicion = Jugador.Posicion.DEFENSA,
            dorsal = 5,
            altura = 1.78,
            peso = 70.0,
            goles = 0,
            partidosJugados = 25,
            imagenUrl = ""
        )

        // Act
        val result = jugador.toPersonalEntity()

        // Assert
        assertEquals(2, result.id)
        assertEquals("Carlos", result.nombre)
        assertEquals("Gomez", result.apellidos)
        assertEquals(LocalDate.of(1995, 2, 15), result.fechaNacimiento)
        assertEquals(LocalDate.of(2023, 1, 1), result.fechaIncorporacion)
        assertEquals(75000.0, result.salario)
        assertEquals("Spain", result.paisOrigen)
        assertEquals("JUGADOR", result.tipo)
        assertEquals("", result.imagenUrl)
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 30), result.createdAt)
        assertEquals(LocalDateTime.of(2025, 6, 1, 18, 45), result.updatedAt)
    }
}