package srangeldev.proyectoequipofutboljavafx.newteam.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class EntrenadorTest {

    @Test
    fun `test entrenador creation`() {
        // Arrange
        val id = 1
        val nombre = "Juan"
        val apellidos = "Pérez"
        val fechaNacimiento = LocalDate.of(1980, 1, 1)
        val fechaIncorporacion = LocalDate.of(2020, 1, 1)
        val salario = 50000.0
        val paisOrigen = "España"
        val especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()
        val imagenUrl = "imagen.jpg"

        // Act
        val entrenador = Entrenador(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = salario,
            paisOrigen = paisOrigen,
            especializacion = especializacion,
            createdAt = createdAt,
            updatedAt = updatedAt,
            imagenUrl = imagenUrl
        )

        // Assert
        assertEquals(id, entrenador.id)
        assertEquals(nombre, entrenador.nombre)
        assertEquals(apellidos, entrenador.apellidos)
        assertEquals(fechaNacimiento, entrenador.fechaNacimiento)
        assertEquals(fechaIncorporacion, entrenador.fechaIncorporacion)
        assertEquals(salario, entrenador.salario)
        assertEquals(paisOrigen, entrenador.paisOrigen)
        assertEquals(especializacion, entrenador.especializacion)
        assertEquals(createdAt, entrenador.createdAt)
        assertEquals(updatedAt, entrenador.updatedAt)
        assertEquals(imagenUrl, entrenador.imagenUrl)
    }

    @Test
    fun `test toString returns nombre and apellidos`() {
        // Arrange
        val nombre = "Juan"
        val apellidos = "Pérez"
        val entrenador = Entrenador(
            id = 1,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = LocalDate.of(1980, 1, 1),
            fechaIncorporacion = LocalDate.of(2020, 1, 1),
            salario = 50000.0,
            paisOrigen = "España",
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Act
        val result = entrenador.toString()

        // Assert
        assertEquals("$nombre $apellidos", result)
    }

    @Test
    fun `test all especializacion values are valid`() {
        // Assert
        assertNotNull(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL)
        assertNotNull(Entrenador.Especializacion.ENTRENADOR_ASISTENTE)
        assertNotNull(Entrenador.Especializacion.ENTRENADOR_PORTEROS)
    }

    @Test
    fun `test entrenador with default imagenUrl`() {
        // Arrange & Act
        val entrenador = Entrenador(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = LocalDate.of(1980, 1, 1),
            fechaIncorporacion = LocalDate.of(2020, 1, 1),
            salario = 50000.0,
            paisOrigen = "España",
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Assert
        assertEquals("", entrenador.imagenUrl)
    }
}
