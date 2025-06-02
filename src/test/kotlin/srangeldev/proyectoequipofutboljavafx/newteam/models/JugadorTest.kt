package srangeldev.proyectoequipofutboljavafx.newteam.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class JugadorTest {

    @Test
    fun `test jugador creation`() {
        // Arrange
        val id = 1
        val nombre = "Lionel"
        val apellidos = "Messi"
        val fechaNacimiento = LocalDate.of(1987, 6, 24)
        val fechaIncorporacion = LocalDate.of(2021, 8, 10)
        val salario = 100000.0
        val paisOrigen = "Argentina"
        val posicion = Jugador.Posicion.DELANTERO
        val dorsal = 10
        val altura = 1.70
        val peso = 72.0
        val goles = 700
        val partidosJugados = 800
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()
        val imagenUrl = "messi.jpg"

        // Act
        val jugador = Jugador(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = salario,
            paisOrigen = paisOrigen,
            posicion = posicion,
            dorsal = dorsal,
            altura = altura,
            peso = peso,
            goles = goles,
            partidosJugados = partidosJugados,
            createdAt = createdAt,
            updatedAt = updatedAt,
            imagenUrl = imagenUrl
        )

        // Assert
        assertEquals(id, jugador.id)
        assertEquals(nombre, jugador.nombre)
        assertEquals(apellidos, jugador.apellidos)
        assertEquals(fechaNacimiento, jugador.fechaNacimiento)
        assertEquals(fechaIncorporacion, jugador.fechaIncorporacion)
        assertEquals(salario, jugador.salario)
        assertEquals(paisOrigen, jugador.paisOrigen)
        assertEquals(posicion, jugador.posicion)
        assertEquals(dorsal, jugador.dorsal)
        assertEquals(altura, jugador.altura)
        assertEquals(peso, jugador.peso)
        assertEquals(goles, jugador.goles)
        assertEquals(partidosJugados, jugador.partidosJugados)
        assertEquals(createdAt, jugador.createdAt)
        assertEquals(updatedAt, jugador.updatedAt)
        assertEquals(imagenUrl, jugador.imagenUrl)
    }

    @Test
    fun `test all posicion values are valid`() {
        // Assert
        assertNotNull(Jugador.Posicion.PORTERO)
        assertNotNull(Jugador.Posicion.DEFENSA)
        assertNotNull(Jugador.Posicion.CENTROCAMPISTA)
        assertNotNull(Jugador.Posicion.DELANTERO)
    }

    @Test
    fun `test toString method`() {
        // Arrange
        val jugador = Jugador(
            id = 1,
            nombre = "Lionel",
            apellidos = "Messi",
            fechaNacimiento = LocalDate.of(1987, 6, 24),
            fechaIncorporacion = LocalDate.of(2021, 8, 10),
            salario = 100000.0,
            paisOrigen = "Argentina",
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.70,
            peso = 72.0,
            goles = 700,
            partidosJugados = 800,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Act
        val result = jugador.toString()

        // Assert
        assertTrue(result.contains("Jugador"))
        assertTrue(result.contains("id=1"))
        assertTrue(result.contains("nombre='Lionel'"))
        assertTrue(result.contains("apellidos='Messi'"))
        assertTrue(result.contains("posicion=DELANTERO"))
        assertTrue(result.contains("dorsal=10"))
    }

    @Test
    fun `test jugador with default imagenUrl`() {
        // Arrange & Act
        val jugador = Jugador(
            id = 1,
            nombre = "Lionel",
            apellidos = "Messi",
            fechaNacimiento = LocalDate.of(1987, 6, 24),
            fechaIncorporacion = LocalDate.of(2021, 8, 10),
            salario = 100000.0,
            paisOrigen = "Argentina",
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.70,
            peso = 72.0,
            goles = 700,
            partidosJugados = 800,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Assert
        assertEquals("", jugador.imagenUrl)
    }
}
