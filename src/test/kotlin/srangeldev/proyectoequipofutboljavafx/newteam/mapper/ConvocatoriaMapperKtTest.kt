package srangeldev.proyectoequipofutboljavafx.newteam.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import srangeldev.proyectoequipofutboljavafx.newteam.dao.ConvocatoriaEntity
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import java.time.LocalDate
import java.time.LocalDateTime

class ConvocatoriaMapperKtTest {

    /**
     * Tests for the `toModel` extension function in ConvocatoriaEntity.
     * This function maps a ConvocatoriaEntity object to a Convocatoria domain model,
     * optionally allowing customization of jugadores and titulares lists.
     */

    @Test
    fun `toModel maps all fields correctly with default parameters`() {
        val entity = ConvocatoriaEntity(
            id = 1,
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = "Test Convocatoria",
            equipoId = 100,
            entrenadorId = 200,
            createdAt = LocalDateTime.of(2025, 6, 7, 12, 0, 0),
            updatedAt = LocalDateTime.of(2025, 6, 8, 13, 0, 0)
        )

        val model = entity.toModel()

        assertEquals(entity.id, model.id)
        assertEquals(entity.fecha, model.fecha)
        assertEquals(entity.descripcion, model.descripcion)
        assertEquals(entity.equipoId, model.equipoId)
        assertEquals(entity.entrenadorId, model.entrenadorId)
        assertEquals(emptyList<Int>(), model.jugadores)
        assertEquals(emptyList<Int>(), model.titulares)
        assertEquals(entity.createdAt, model.createdAt)
        assertEquals(entity.updatedAt, model.updatedAt)
    }

    @Test
    fun `toModel maps all fields correctly with non-empty jugadores and titulares`() {
        val entity = ConvocatoriaEntity(
            id = 2,
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = "Another Test Convocatoria",
            equipoId = 101,
            entrenadorId = 201,
            createdAt = LocalDateTime.of(2025, 6, 6, 10, 30, 0),
            updatedAt = LocalDateTime.of(2025, 6, 8, 14, 45, 0)
        )
        val jugadores = listOf(1, 2, 3)
        val titulares = listOf(1, 3)

        val model = entity.toModel(jugadores = jugadores, titulares = titulares)

        assertEquals(entity.id, model.id)
        assertEquals(entity.fecha, model.fecha)
        assertEquals(entity.descripcion, model.descripcion)
        assertEquals(entity.equipoId, model.equipoId)
        assertEquals(entity.entrenadorId, model.entrenadorId)
        assertEquals(jugadores, model.jugadores)
        assertEquals(titulares, model.titulares)
        assertEquals(entity.createdAt, model.createdAt)
        assertEquals(entity.updatedAt, model.updatedAt)
    }

    @Test
    fun `toModel maps empty jugadores and titulares when lists are explicitly provided as empty`() {
        val entity = ConvocatoriaEntity(
            id = 3,
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = "Empty List Test",
            equipoId = 102,
            entrenadorId = 202,
            createdAt = LocalDateTime.of(2025, 6, 5, 9, 0, 0),
            updatedAt = LocalDateTime.of(2025, 6, 8, 15, 0, 0)
        )

        val model = entity.toModel(jugadores = emptyList(), titulares = emptyList())

        assertEquals(entity.id, model.id)
        assertEquals(entity.fecha, model.fecha)
        assertEquals(entity.descripcion, model.descripcion)
        assertEquals(entity.equipoId, model.equipoId)
        assertEquals(entity.entrenadorId, model.entrenadorId)
        assertEquals(emptyList<Int>(), model.jugadores)
        assertEquals(emptyList<Int>(), model.titulares)
        assertEquals(entity.createdAt, model.createdAt)
        assertEquals(entity.updatedAt, model.updatedAt)
    }

    @Test
    fun `toEntity maps all fields correctly`() {
        val model = Convocatoria(
            id = 4,
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = "Complete Mapping Test",
            equipoId = 103,
            entrenadorId = 203,
            jugadores = listOf(4, 5, 6),
            titulares = listOf(5, 6),
            createdAt = LocalDateTime.of(2025, 6, 4, 8, 0, 0),
            updatedAt = LocalDateTime.of(2025, 6, 8, 18, 0, 0)
        )

        val entity = model.toEntity()

        assertEquals(model.id, entity.id)
        assertEquals(model.fecha, entity.fecha)
        assertEquals(model.descripcion, entity.descripcion)
        assertEquals(model.equipoId, entity.equipoId)
        assertEquals(model.entrenadorId, entity.entrenadorId)
        assertEquals(model.createdAt, entity.createdAt)
        assertEquals(model.updatedAt, entity.updatedAt)
    }

    @Test
    fun `toEntity with default jugadores and titulares`() {
        val model = Convocatoria(
            id = 5,
            fecha = LocalDate.of(2025, 6, 8),
            descripcion = "Default Values Test",
            equipoId = 104,
            entrenadorId = 204,
            createdAt = LocalDateTime.of(2025, 6, 3, 7, 0, 0),
            updatedAt = LocalDateTime.of(2025, 6, 8, 19, 0, 0)
        )

        val entity = model.toEntity()

        assertEquals(model.id, entity.id)
        assertEquals(model.fecha, entity.fecha)
        assertEquals(model.descripcion, entity.descripcion)
        assertEquals(model.equipoId, entity.equipoId)
        assertEquals(model.entrenadorId, entity.entrenadorId)
        assertEquals(model.createdAt, entity.createdAt)
        assertEquals(model.updatedAt, entity.updatedAt)
    }
}