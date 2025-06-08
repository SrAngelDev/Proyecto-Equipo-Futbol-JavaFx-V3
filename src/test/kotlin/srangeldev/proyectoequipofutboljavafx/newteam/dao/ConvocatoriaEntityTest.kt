package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria

class ConvocatoriaEntityTest {

    @Test
    fun `toConvocatoria should correctly map a ConvocatoriaEntity to Convocatoria`() {
        // Given
        val entity = ConvocatoriaEntity(
            id = 1,
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = "Test Convocatoria",
            equipoId = 10,
            entrenadorId = 20,
            createdAt = LocalDateTime.of(2025, 6, 7, 10, 0),
            updatedAt = LocalDateTime.of(2025, 6, 8, 12, 0)
        )
        val jugadores = listOf(1, 2, 3, 4)
        val titulares = listOf(1, 2)

        // When
        val result = entity.toConvocatoria(jugadores, titulares)

        // Then
        assertEquals(entity.id, result.id)
        assertEquals(entity.fecha, result.fecha)
        assertEquals(entity.descripcion, result.descripcion)
        assertEquals(entity.equipoId, result.equipoId)
        assertEquals(entity.entrenadorId, result.entrenadorId)
        assertEquals(jugadores, result.jugadores)
        assertEquals(titulares, result.titulares)
        assertEquals(entity.createdAt, result.createdAt)
        assertEquals(entity.updatedAt, result.updatedAt)
    }

    @Test
    fun `toConvocatoria should handle empty lists for jugadores and titulares`() {
        // Given
        val entity = ConvocatoriaEntity(
            id = 2,
            fecha = LocalDate.of(2025, 6, 9),
            descripcion = "Empty Test",
            equipoId = 11,
            entrenadorId = 21,
            createdAt = LocalDateTime.of(2025, 6, 7, 11, 0),
            updatedAt = LocalDateTime.of(2025, 6, 8, 13, 0)
        )
        val jugadores = emptyList<Int>()
        val titulares = emptyList<Int>()

        // When
        val result = entity.toConvocatoria(jugadores, titulares)

        // Then
        assertEquals(entity.id, result.id)
        assertEquals(entity.fecha, result.fecha)
        assertEquals(entity.descripcion, result.descripcion)
        assertEquals(entity.equipoId, result.equipoId)
        assertEquals(entity.entrenadorId, result.entrenadorId)
        assertEquals(jugadores, result.jugadores)
        assertEquals(titulares, result.titulares)
        assertEquals(entity.createdAt, result.createdAt)
        assertEquals(entity.updatedAt, result.updatedAt)
    }

    @Test
    fun `test fromConvocatoria with valid Convocatoria`() {
        // Arrange
        val convocatoria = Convocatoria(
            id = 1,
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = "Test Convocatoria",
            equipoId = 10,
            entrenadorId = 20,
            jugadores = listOf(1, 2, 3),
            titulares = listOf(1, 2),
            createdAt = LocalDateTime.of(2025, 6, 1, 12, 0),
            updatedAt = LocalDateTime.of(2025, 6, 5, 15, 0)
        )

        // Act
        val result = ConvocatoriaEntity.fromConvocatoria(convocatoria)

        // Assert
        assertEquals(convocatoria.id, result.id)
        assertEquals(convocatoria.fecha, result.fecha)
        assertEquals(convocatoria.descripcion, result.descripcion)
        assertEquals(convocatoria.equipoId, result.equipoId)
        assertEquals(convocatoria.entrenadorId, result.entrenadorId)
        assertEquals(convocatoria.createdAt, result.createdAt)
        assertEquals(convocatoria.updatedAt, result.updatedAt)
    }

    @Test
    fun `test fromConvocatoria with default values Convocatoria`() {
        // Arrange
        val convocatoria = Convocatoria(
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = "",
            equipoId = 0,
            entrenadorId = 0
        )

        // Act
        val result = ConvocatoriaEntity.fromConvocatoria(convocatoria)

        // Assert
        assertEquals(convocatoria.id, result.id)
        assertEquals(convocatoria.fecha, result.fecha)
        assertEquals(convocatoria.descripcion, result.descripcion)
        assertEquals(convocatoria.equipoId, result.equipoId)
        assertEquals(convocatoria.entrenadorId, result.entrenadorId)
        assertEquals(convocatoria.createdAt, result.createdAt)
        assertEquals(convocatoria.updatedAt, result.updatedAt)
    }

    @Test
    fun `test fromConvocatoria with long descripcion`() {
        // Arrange
        val longDescription = "A".repeat(500)
        val convocatoria = Convocatoria(
            id = 2,
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = longDescription,
            equipoId = 30,
            entrenadorId = 40,
            createdAt = LocalDateTime.of(2025, 6, 2, 10, 30),
            updatedAt = LocalDateTime.of(2025, 6, 6, 20, 45)
        )

        // Act
        val result = ConvocatoriaEntity.fromConvocatoria(convocatoria)

        // Assert
        assertEquals(convocatoria.id, result.id)
        assertEquals(convocatoria.fecha, result.fecha)
        assertEquals(convocatoria.descripcion, result.descripcion)
        assertEquals(convocatoria.equipoId, result.equipoId)
        assertEquals(convocatoria.entrenadorId, result.entrenadorId)
        assertEquals(convocatoria.createdAt, result.createdAt)
        assertEquals(convocatoria.updatedAt, result.updatedAt)
    }
}