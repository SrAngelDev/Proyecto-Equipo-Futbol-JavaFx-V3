package srangeldev.proyectoequipofutboljavafx.newteam.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class PersonalTest {

    @Test
    fun `test personal creation`() {
        // Arrange
        val id = 1
        val nombre = "Juan"
        val apellidos = "Pérez"
        val fechaNacimiento = LocalDate.of(1980, 1, 1)
        val fechaIncorporacion = LocalDate.of(2020, 1, 1)
        val salario = 50000.0
        val paisOrigen = "España"
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()
        val imagenUrl = "imagen.jpg"

        // Act
        val personal = TestPersonal(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = salario,
            paisOrigen = paisOrigen,
            createdAt = createdAt,
            updatedAt = updatedAt,
            imagenUrl = imagenUrl
        )

        // Assert
        assertEquals(id, personal.id)
        assertEquals(nombre, personal.nombre)
        assertEquals(apellidos, personal.apellidos)
        assertEquals(fechaNacimiento, personal.fechaNacimiento)
        assertEquals(fechaIncorporacion, personal.fechaIncorporacion)
        assertEquals(salario, personal.salario)
        assertEquals(paisOrigen, personal.paisOrigen)
        assertEquals(createdAt, personal.createdAt)
        assertEquals(updatedAt, personal.updatedAt)
        assertEquals(imagenUrl, personal.imagenUrl)
    }

    @Test
    fun `test personal with default imagenUrl`() {
        // Arrange & Act
        val personal = TestPersonal(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = LocalDate.of(1980, 1, 1),
            fechaIncorporacion = LocalDate.of(2020, 1, 1),
            salario = 50000.0,
            paisOrigen = "España",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Assert
        assertEquals("", personal.imagenUrl)
    }

    @Test
    fun `test personal with default id`() {
        // Arrange & Act
        val personal = TestPersonal(
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = LocalDate.of(1980, 1, 1),
            fechaIncorporacion = LocalDate.of(2020, 1, 1),
            salario = 50000.0,
            paisOrigen = "España",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Assert
        assertEquals(0, personal.id)
    }

    // Clase de prueba que extiende Personal para poder probarla (ya que Personal es abstracta)
    private class TestPersonal(
        id: Int = 0,
        nombre: String,
        apellidos: String,
        fechaNacimiento: LocalDate,
        fechaIncorporacion: LocalDate,
        salario: Double,
        paisOrigen: String,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime,
        imagenUrl: String = ""
    ) : Personal(id, nombre, apellidos, fechaNacimiento, fechaIncorporacion, salario, paisOrigen, createdAt, updatedAt, imagenUrl)
}