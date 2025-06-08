package srangeldev.proyectoequipofutboljavafx.newteam.dao

import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class EntrenadorEntityTest {

    @Test
    fun `should convert EntrenadorEntity to Entrenador correctly when specialization is ENTRENADOR_PRINCIPAL`() {
        val personalEntity = PersonalEntity(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1980, 5, 20),
            fechaIncorporacion = LocalDate.of(2020, 1, 10),
            salario = 3000.0,
            paisOrigen = "Spain",
            tipo = "ENTRENADOR",
            imagenUrl = "http://example.com/john.jpg",
            createdAt = LocalDateTime.of(2020, 1, 10, 10, 0),
            updatedAt = LocalDateTime.of(2023, 5, 15, 15, 30)
        )

        val entrenadorEntity = EntrenadorEntity(
            id = 1,
            especializacion = "ENTRENADOR_PRINCIPAL"
        )

        val entrenador = entrenadorEntity.toEntrenador(personalEntity)

        assertEquals(1, entrenador.id)
        assertEquals("John", entrenador.nombre)
        assertEquals("Doe", entrenador.apellidos)
        assertEquals(LocalDate.of(1980, 5, 20), entrenador.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 1, 10), entrenador.fechaIncorporacion)
        assertEquals(3000.0, entrenador.salario)
        assertEquals("Spain", entrenador.paisOrigen)
        assertEquals(LocalDateTime.of(2020, 1, 10, 10, 0), entrenador.createdAt)
        assertEquals(LocalDateTime.of(2023, 5, 15, 15, 30), entrenador.updatedAt)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, entrenador.especializacion)
        assertEquals("http://example.com/john.jpg", entrenador.imagenUrl)
    }

    @Test
    fun `should convert EntrenadorEntity to Entrenador correctly when specialization is ENTRENADOR_ASISTENTE`() {
        val personalEntity = PersonalEntity(
            id = 2,
            nombre = "Jane",
            apellidos = "Smith",
            fechaNacimiento = LocalDate.of(1990, 8, 15),
            fechaIncorporacion = LocalDate.of(2021, 2, 5),
            salario = 2500.0,
            paisOrigen = "USA",
            tipo = "ENTRENADOR",
            imagenUrl = "http://example.com/jane.jpg",
            createdAt = LocalDateTime.of(2021, 2, 5, 9, 0),
            updatedAt = LocalDateTime.of(2023, 6, 1, 12, 45)
        )

        val entrenadorEntity = EntrenadorEntity(
            id = 2,
            especializacion = "ENTRENADOR_ASISTENTE"
        )

        val entrenador = entrenadorEntity.toEntrenador(personalEntity)

        assertEquals(2, entrenador.id)
        assertEquals("Jane", entrenador.nombre)
        assertEquals("Smith", entrenador.apellidos)
        assertEquals(LocalDate.of(1990, 8, 15), entrenador.fechaNacimiento)
        assertEquals(LocalDate.of(2021, 2, 5), entrenador.fechaIncorporacion)
        assertEquals(2500.0, entrenador.salario)
        assertEquals("USA", entrenador.paisOrigen)
        assertEquals(LocalDateTime.of(2021, 2, 5, 9, 0), entrenador.createdAt)
        assertEquals(LocalDateTime.of(2023, 6, 1, 12, 45), entrenador.updatedAt)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_ASISTENTE, entrenador.especializacion)
        assertEquals("http://example.com/jane.jpg", entrenador.imagenUrl)
    }

    @Test
    fun `should convert EntrenadorEntity to Entrenador correctly when specialization is ENTRENADOR_PORTEROS`() {
        val personalEntity = PersonalEntity(
            id = 3,
            nombre = "Paul",
            apellidos = "Brown",
            fechaNacimiento = LocalDate.of(1985, 3, 10),
            fechaIncorporacion = LocalDate.of(2019, 11, 15),
            salario = 3200.0,
            paisOrigen = "UK",
            tipo = "ENTRENADOR",
            imagenUrl = "http://example.com/paul.jpg",
            createdAt = LocalDateTime.of(2019, 11, 15, 8, 0),
            updatedAt = LocalDateTime.of(2023, 7, 10, 18, 0)
        )

        val entrenadorEntity = EntrenadorEntity(
            id = 3,
            especializacion = "ENTRENADOR_PORTEROS"
        )

        val entrenador = entrenadorEntity.toEntrenador(personalEntity)

        assertEquals(3, entrenador.id)
        assertEquals("Paul", entrenador.nombre)
        assertEquals("Brown", entrenador.apellidos)
        assertEquals(LocalDate.of(1985, 3, 10), entrenador.fechaNacimiento)
        assertEquals(LocalDate.of(2019, 11, 15), entrenador.fechaIncorporacion)
        assertEquals(3200.0, entrenador.salario)
        assertEquals("UK", entrenador.paisOrigen)
        assertEquals(LocalDateTime.of(2019, 11, 15, 8, 0), entrenador.createdAt)
        assertEquals(LocalDateTime.of(2023, 7, 10, 18, 0), entrenador.updatedAt)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PORTEROS, entrenador.especializacion)
        assertEquals("http://example.com/paul.jpg", entrenador.imagenUrl)
    }
}