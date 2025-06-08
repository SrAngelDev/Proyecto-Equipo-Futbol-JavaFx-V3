package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate
import java.time.LocalDateTime

class JugadorEntityTest {

    @Test
    fun `should convert JugadorEntity to Jugador with matching properties`() {
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
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2015, 6, 15),
            salario = 50000.0,
            paisOrigen = "Spain",
            tipo = "Jugador",
            imagenUrl = "http://example.com/image.jpg",
            createdAt = LocalDateTime.of(2015, 6, 15, 8, 0),
            updatedAt = LocalDateTime.of(2025, 6, 1, 10, 0)
        )

        // Act
        val jugador = jugadorEntity.toJugador(personalEntity)

        // Assert
        assertEquals(jugadorEntity.id, jugador.id)
        assertEquals(personalEntity.nombre, jugador.nombre)
        assertEquals(personalEntity.apellidos, jugador.apellidos)
        assertEquals(personalEntity.fechaNacimiento, jugador.fechaNacimiento)
        assertEquals(personalEntity.fechaIncorporacion, jugador.fechaIncorporacion)
        assertEquals(personalEntity.salario, jugador.salario)
        assertEquals(personalEntity.paisOrigen, jugador.paisOrigen)
        assertEquals(personalEntity.createdAt, jugador.createdAt)
        assertEquals(personalEntity.updatedAt, jugador.updatedAt)
        assertEquals(Jugador.Posicion.DELANTERO, jugador.posicion)
        assertEquals(jugadorEntity.dorsal, jugador.dorsal)
        assertEquals(jugadorEntity.altura, jugador.altura)
        assertEquals(jugadorEntity.peso, jugador.peso)
        assertEquals(jugadorEntity.goles, jugador.goles)
        assertEquals(jugadorEntity.partidosJugados, jugador.partidosJugados)
        assertEquals(personalEntity.imagenUrl, jugador.imagenUrl)
    }

    @Test
    fun `should throw IllegalArgumentException when converting with invalid position`() {
        // Arrange
        val jugadorEntity = JugadorEntity(
            id = 2,
            posicion = "INVALID_POSITION",
            dorsal = 4,
            altura = 1.90,
            peso = 85.0,
            goles = 10,
            partidosJugados = 200
        )
        val personalEntity = PersonalEntity(
            id = 2,
            nombre = "Jane",
            apellidos = "Smith",
            fechaNacimiento = LocalDate.of(1995, 2, 20),
            fechaIncorporacion = LocalDate.of(2021, 1, 10),
            salario = 40000.0,
            paisOrigen = "Argentina",
            tipo = "Jugador",
            imagenUrl = "http://example.com/image2.jpg",
            createdAt = LocalDateTime.of(2021, 1, 10, 9, 30),
            updatedAt = LocalDateTime.of(2025, 6, 1, 15, 0)
        )

        // Act & Assert
        try {
            jugadorEntity.toJugador(personalEntity)
            assert(false) { "Expected IllegalArgumentException for invalid position" }
        } catch (e: IllegalArgumentException) {
            assertEquals(
                "No enum constant srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador.Posicion.INVALID_POSITION",
                e.message
            )
        }
    }
}