package srangeldev.proyectoequipofutboljavafx.newteam.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import srangeldev.proyectoequipofutboljavafx.newteam.dao.EntrenadorEntity
import srangeldev.proyectoequipofutboljavafx.newteam.dao.PersonalEntity
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import java.time.LocalDate
import java.time.LocalDateTime

class EntrenadorMapperKtTest {

    @Test
    fun `test toEntrenadorEntity maps Entrenador to EntrenadorEntity successfully`() {
        // Arrange
        val entrenador = Entrenador(
            id = 1,
            nombre = "Carlos",
            apellidos = "Santana",
            fechaNacimiento = LocalDate.of(1980, 1, 15),
            fechaIncorporacion = LocalDate.of(2020, 6, 1),
            salario = 50000.0,
            paisOrigen = "España",
            createdAt = LocalDateTime.of(2020, 6, 1, 10, 0),
            updatedAt = LocalDateTime.of(2023, 4, 10, 12, 0),
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
            imagenUrl = "http://example.com/image.png"
        )

        // Act
        val entrenadorEntity = entrenador.toEntrenadorEntity()

        // Assert
        assertEquals(entrenador.id, entrenadorEntity.id)
        assertEquals(entrenador.especializacion.name, entrenadorEntity.especializacion)
    }

    @Test
    fun `test toEntrenadorEntity handles different specializations`() {
        // Arrange
        val entrenador = Entrenador(
            id = 2,
            nombre = "Maria",
            apellidos = "Lopez",
            fechaNacimiento = LocalDate.of(1985, 10, 20),
            fechaIncorporacion = LocalDate.of(2021, 3, 15),
            salario = 45000.0,
            paisOrigen = "Argentina",
            createdAt = LocalDateTime.of(2021, 3, 15, 9, 0),
            updatedAt = LocalDateTime.of(2023, 5, 8, 14, 30),
            especializacion = Entrenador.Especializacion.ENTRENADOR_ASISTENTE,
            imagenUrl = "http://example.com/picture.png"
        )

        // Act
        val entrenadorEntity = entrenador.toEntrenadorEntity()

        // Assert
        assertEquals(entrenador.id, entrenadorEntity.id)
        assertEquals(entrenador.especializacion.name, entrenadorEntity.especializacion)
    }

    /**
     * Tests for the toModel function in EntrenadorMapperKt class.
     * This function maps an EntrenadorEntity and a related PersonalEntity into an Entrenador model.
     */

    @Test
    fun `test toModel maps EntrenadorEntity and PersonalEntity to Entrenador successfully`() {
        // Arrange
        val entrenadorEntity = EntrenadorEntity(
            id = 1,
            especializacion = "ENTRENADOR_PRINCIPAL"
        )

        val personalEntity = PersonalEntity(
            id = 1,
            nombre = "Carlos",
            apellidos = "Santana",
            fechaNacimiento = LocalDate.of(1980, 1, 15),
            fechaIncorporacion = LocalDate.of(2020, 6, 1),
            salario = 50000.0,
            paisOrigen = "España",
            tipo = "ENTRENADOR",
            imagenUrl = "http://example.com/image.png",
            createdAt = LocalDateTime.of(2020, 6, 1, 10, 0),
            updatedAt = LocalDateTime.of(2023, 4, 10, 12, 0)
        )

        // Act
        val entrenador = entrenadorEntity.toModel(personalEntity)

        // Assert
        assertEquals(entrenadorEntity.id, entrenador.id)
        assertEquals(personalEntity.nombre, entrenador.nombre)
        assertEquals(personalEntity.apellidos, entrenador.apellidos)
        assertEquals(personalEntity.fechaNacimiento, entrenador.fechaNacimiento)
        assertEquals(personalEntity.fechaIncorporacion, entrenador.fechaIncorporacion)
        assertEquals(personalEntity.salario, entrenador.salario)
        assertEquals(personalEntity.paisOrigen, entrenador.paisOrigen)
        assertEquals(personalEntity.createdAt, entrenador.createdAt)
        assertEquals(personalEntity.updatedAt, entrenador.updatedAt)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, entrenador.especializacion)
        assertEquals(personalEntity.imagenUrl, entrenador.imagenUrl)
    }

    @Test
    fun `test toModel handles different specialization`() {
        // Arrange
        val entrenadorEntity = EntrenadorEntity(
            id = 2,
            especializacion = "ENTRENADOR_ASISTENTE"
        )

        val personalEntity = PersonalEntity(
            id = 2,
            nombre = "Maria",
            apellidos = "Lopez",
            fechaNacimiento = LocalDate.of(1985, 10, 20),
            fechaIncorporacion = LocalDate.of(2021, 3, 15),
            salario = 45000.0,
            paisOrigen = "Argentina",
            tipo = "ENTRENADOR",
            imagenUrl = "http://example.com/picture.png",
            createdAt = LocalDateTime.of(2021, 3, 15, 9, 0),
            updatedAt = LocalDateTime.of(2023, 5, 8, 14, 30)
        )

        // Act
        val entrenador = entrenadorEntity.toModel(personalEntity)

        // Assert
        assertEquals(entrenadorEntity.id, entrenador.id)
        assertEquals(personalEntity.nombre, entrenador.nombre)
        assertEquals(personalEntity.apellidos, entrenador.apellidos)
        assertEquals(personalEntity.fechaNacimiento, entrenador.fechaNacimiento)
        assertEquals(personalEntity.fechaIncorporacion, entrenador.fechaIncorporacion)
        assertEquals(personalEntity.salario, entrenador.salario)
        assertEquals(personalEntity.paisOrigen, entrenador.paisOrigen)
        assertEquals(personalEntity.createdAt, entrenador.createdAt)
        assertEquals(personalEntity.updatedAt, entrenador.updatedAt)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_ASISTENTE, entrenador.especializacion)
        assertEquals(personalEntity.imagenUrl, entrenador.imagenUrl)
    }

    @Test
    fun `test toModel handles empty image URL`() {
        // Arrange
        val entrenadorEntity = EntrenadorEntity(
            id = 3,
            especializacion = "ENTRENADOR_PORTEROS"
        )

        val personalEntity = PersonalEntity(
            id = 3,
            nombre = "Luis",
            apellidos = "Garcia",
            fechaNacimiento = LocalDate.of(1975, 7, 30),
            fechaIncorporacion = LocalDate.of(2019, 8, 10),
            salario = 60000.0,
            paisOrigen = "Chile",
            tipo = "ENTRENADOR",
            imagenUrl = "",
            createdAt = LocalDateTime.of(2019, 8, 10, 8, 0),
            updatedAt = LocalDateTime.of(2024, 2, 20, 10, 25)
        )

        // Act
        val entrenador = entrenadorEntity.toModel(personalEntity)

        // Assert
        assertEquals(entrenadorEntity.id, entrenador.id)
        assertEquals(personalEntity.nombre, entrenador.nombre)
        assertEquals(personalEntity.apellidos, entrenador.apellidos)
        assertEquals(personalEntity.fechaNacimiento, entrenador.fechaNacimiento)
        assertEquals(personalEntity.fechaIncorporacion, entrenador.fechaIncorporacion)
        assertEquals(personalEntity.salario, entrenador.salario)
        assertEquals(personalEntity.paisOrigen, entrenador.paisOrigen)
        assertEquals(personalEntity.createdAt, entrenador.createdAt)
        assertEquals(personalEntity.updatedAt, entrenador.updatedAt)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PORTEROS, entrenador.especializacion)
        assertEquals(personalEntity.imagenUrl, entrenador.imagenUrl)
    }

    @Test
    fun `test toPersonalEntity maps Entrenador to PersonalEntity successfully`() {
        // Arrange
        val entrenador = Entrenador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1990, 5, 20),
            fechaIncorporacion = LocalDate.of(2022, 1, 10),
            salario = 55000.0,
            paisOrigen = "Mexico",
            especializacion = Entrenador.Especializacion.ENTRENADOR_ASISTENTE,
            createdAt = LocalDateTime.of(2022, 1, 10, 8, 30),
            updatedAt = LocalDateTime.of(2023, 3, 15, 15, 45),
            imagenUrl = "http://example.com/imageJuan.png"
        )

        // Act
        val personalEntity = entrenador.toPersonalEntity()

        // Assert
        assertEquals(entrenador.id, personalEntity.id)
        assertEquals(entrenador.nombre, personalEntity.nombre)
        assertEquals(entrenador.apellidos, personalEntity.apellidos)
        assertEquals(entrenador.fechaNacimiento, personalEntity.fechaNacimiento)
        assertEquals(entrenador.fechaIncorporacion, personalEntity.fechaIncorporacion)
        assertEquals(entrenador.salario, personalEntity.salario)
        assertEquals(entrenador.paisOrigen, personalEntity.paisOrigen)
        assertEquals("ENTRENADOR", personalEntity.tipo)
        assertEquals(entrenador.imagenUrl, personalEntity.imagenUrl)
        assertEquals(entrenador.createdAt, personalEntity.createdAt)
        assertEquals(entrenador.updatedAt, personalEntity.updatedAt)
    }

    @Test
    fun `test toPersonalEntity handles default empty values`() {
        // Arrange
        val entrenador = Entrenador(
            id = 2,
            nombre = "Ana",
            apellidos = "Gomez",
            fechaNacimiento = LocalDate.of(1987, 3, 15),
            fechaIncorporacion = LocalDate.of(2020, 7, 1),
            salario = 60000.0,
            paisOrigen = "Colombia",
            especializacion = Entrenador.Especializacion.ENTRENADOR_PORTEROS,
            createdAt = LocalDateTime.of(2020, 7, 1, 9, 0),
            updatedAt = LocalDateTime.of(2023, 8, 20, 10, 15),
            imagenUrl = ""
        )

        // Act
        val personalEntity = entrenador.toPersonalEntity()

        // Assert
        assertEquals(entrenador.id, personalEntity.id)
        assertEquals(entrenador.nombre, personalEntity.nombre)
        assertEquals(entrenador.apellidos, personalEntity.apellidos)
        assertEquals(entrenador.fechaNacimiento, personalEntity.fechaNacimiento)
        assertEquals(entrenador.fechaIncorporacion, personalEntity.fechaIncorporacion)
        assertEquals(entrenador.salario, personalEntity.salario)
        assertEquals(entrenador.paisOrigen, personalEntity.paisOrigen)
        assertEquals("ENTRENADOR", personalEntity.tipo)
        assertEquals(entrenador.imagenUrl, personalEntity.imagenUrl)
        assertEquals(entrenador.createdAt, personalEntity.createdAt)
        assertEquals(entrenador.updatedAt, personalEntity.updatedAt)
    }
}