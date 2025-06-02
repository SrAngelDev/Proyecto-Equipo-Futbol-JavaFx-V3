package srangeldev.proyectoequipofutboljavafx.newteam.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class EquipoTest {

    @Test
    fun `test equipo creation`() {
        // Arrange
        val id = 1
        val nombre = "Real Madrid"
        val fechaFundacion = LocalDate.of(1902, 3, 6)
        val escudoUrl = "escudo.jpg"
        val ciudad = "Madrid"
        val estadio = "Santiago Bernabéu"
        val pais = "España"
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()

        // Act
        val equipo = Equipo(
            id = id,
            nombre = nombre,
            fechaFundacion = fechaFundacion,
            escudoUrl = escudoUrl,
            ciudad = ciudad,
            estadio = estadio,
            pais = pais,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        // Assert
        assertEquals(id, equipo.id)
        assertEquals(nombre, equipo.nombre)
        assertEquals(fechaFundacion, equipo.fechaFundacion)
        assertEquals(escudoUrl, equipo.escudoUrl)
        assertEquals(ciudad, equipo.ciudad)
        assertEquals(estadio, equipo.estadio)
        assertEquals(pais, equipo.pais)
        assertEquals(createdAt, equipo.createdAt)
        assertEquals(updatedAt, equipo.updatedAt)
    }

    @Test
    fun `test toString method`() {
        // Arrange
        val equipo = Equipo(
            id = 1,
            nombre = "Real Madrid",
            fechaFundacion = LocalDate.of(1902, 3, 6),
            escudoUrl = "escudo.jpg",
            ciudad = "Madrid",
            estadio = "Santiago Bernabéu",
            pais = "España",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Act
        val result = equipo.toString()

        // Assert
        assertTrue(result.contains("Equipo"))
        assertTrue(result.contains("id=1"))
        assertTrue(result.contains("nombre='Real Madrid'"))
        assertTrue(result.contains("fechaFundacion=1902-03-06"))
        assertTrue(result.contains("ciudad='Madrid'"))
        assertTrue(result.contains("estadio='Santiago Bernabéu'"))
        assertTrue(result.contains("pais='España'"))
    }

    @Test
    fun `test equipo with default escudoUrl`() {
        // Arrange & Act
        val equipo = Equipo(
            id = 1,
            nombre = "Real Madrid",
            fechaFundacion = LocalDate.of(1902, 3, 6),
            ciudad = "Madrid",
            estadio = "Santiago Bernabéu",
            pais = "España",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Assert
        assertEquals("", equipo.escudoUrl)
    }

    @Test
    fun `test equipo with default id`() {
        // Arrange & Act
        val equipo = Equipo(
            nombre = "Real Madrid",
            fechaFundacion = LocalDate.of(1902, 3, 6),
            ciudad = "Madrid",
            estadio = "Santiago Bernabéu",
            pais = "España"
        )

        // Assert
        assertEquals(0, equipo.id)
    }

    @Test
    fun `test equipo with default createdAt and updatedAt`() {
        // Arrange
        val before = LocalDateTime.now()

        // Act
        val equipo = Equipo(
            nombre = "Real Madrid",
            fechaFundacion = LocalDate.of(1902, 3, 6),
            ciudad = "Madrid",
            estadio = "Santiago Bernabéu",
            pais = "España"
        )
        val after = LocalDateTime.now()

        // Assert
        assertNotNull(equipo.createdAt)
        assertNotNull(equipo.updatedAt)
        assertTrue(equipo.createdAt.isAfter(before) || equipo.createdAt.isEqual(before))
        assertTrue(equipo.updatedAt.isBefore(after) || equipo.updatedAt.isEqual(after))
    }
}
