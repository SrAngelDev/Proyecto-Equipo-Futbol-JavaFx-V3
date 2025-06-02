package srangeldev.proyectoequipofutboljavafx.newteam.dto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PersonalCsvDtoTest {

    @Test
    fun `test PersonalCsvDto creation with all parameters`() {
        // Arrange
        val id = 1
        val nombre = "Juan"
        val apellidos = "Pérez"
        val fechaNacimiento = "1980-01-01"
        val fechaIncorporacion = "2020-01-01"
        val salario = 50000.0
        val paisOrigen = "España"
        val rol = "ADMIN"
        val especializacion = "ENTRENADOR_PRINCIPAL"
        val posicion = "PORTERO"
        val dorsal = "10"
        val altura = "180"
        val peso = "75"
        val goles = "50"
        val partidosJugados = "100"
        val imagenUrl = "imagen.jpg"

        // Act
        val dto = PersonalCsvDto(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = salario,
            paisOrigen = paisOrigen,
            rol = rol,
            especializacion = especializacion,
            posicion = posicion,
            dorsal = dorsal,
            altura = altura,
            peso = peso,
            goles = goles,
            partidosJugados = partidosJugados,
            imagenUrl = imagenUrl
        )

        // Assert
        assertEquals(id, dto.id)
        assertEquals(nombre, dto.nombre)
        assertEquals(apellidos, dto.apellidos)
        assertEquals(fechaNacimiento, dto.fechaNacimiento)
        assertEquals(fechaIncorporacion, dto.fechaIncorporacion)
        assertEquals(salario, dto.salario)
        assertEquals(paisOrigen, dto.paisOrigen)
        assertEquals(rol, dto.rol)
        assertEquals(especializacion, dto.especializacion)
        assertEquals(posicion, dto.posicion)
        assertEquals(dorsal, dto.dorsal)
        assertEquals(altura, dto.altura)
        assertEquals(peso, dto.peso)
        assertEquals(goles, dto.goles)
        assertEquals(partidosJugados, dto.partidosJugados)
        assertEquals(imagenUrl, dto.imagenUrl)
    }

    @Test
    fun `test PersonalCsvDto with default values`() {
        // Arrange & Act
        val dto = PersonalCsvDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            paisOrigen = "España"
        )

        // Assert
        assertEquals(0.0, dto.salario)
        assertEquals("", dto.rol)
        assertEquals("", dto.especializacion)
        assertEquals("", dto.posicion)
        assertEquals("", dto.dorsal)
        assertEquals("", dto.altura)
        assertEquals("", dto.peso)
        assertEquals("", dto.goles)
        assertEquals("", dto.partidosJugados)
        assertEquals("", dto.imagenUrl)
    }

    @Test
    fun `test PersonalCsvDto copy`() {
        // Arrange
        val original = PersonalCsvDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            paisOrigen = "España",
            rol = "ADMIN",
            especializacion = "ENTRENADOR_PRINCIPAL",
            posicion = "PORTERO",
            dorsal = "10",
            altura = "180",
            peso = "75",
            goles = "50",
            partidosJugados = "100",
            imagenUrl = "imagen.jpg"
        )

        // Act
        val copy = original.copy(
            nombre = "Pedro",
            salario = 60000.0,
            posicion = "DEFENSA"
        )

        // Assert
        assertEquals(original.id, copy.id)
        assertEquals("Pedro", copy.nombre)
        assertEquals(original.apellidos, copy.apellidos)
        assertEquals(original.fechaNacimiento, copy.fechaNacimiento)
        assertEquals(original.fechaIncorporacion, copy.fechaIncorporacion)
        assertEquals(60000.0, copy.salario)
        assertEquals(original.paisOrigen, copy.paisOrigen)
        assertEquals(original.rol, copy.rol)
        assertEquals(original.especializacion, copy.especializacion)
        assertEquals("DEFENSA", copy.posicion)
        assertEquals(original.dorsal, copy.dorsal)
        assertEquals(original.altura, copy.altura)
        assertEquals(original.peso, copy.peso)
        assertEquals(original.goles, copy.goles)
        assertEquals(original.partidosJugados, copy.partidosJugados)
        assertEquals(original.imagenUrl, copy.imagenUrl)
    }

    @Test
    fun `test PersonalCsvDto equals and hashCode`() {
        // Arrange
        val dto1 = PersonalCsvDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            paisOrigen = "España"
        )

        val dto2 = PersonalCsvDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            paisOrigen = "España"
        )

        val dto3 = PersonalCsvDto(
            id = 2,
            nombre = "Pedro",
            apellidos = "Gómez",
            fechaNacimiento = "1985-01-01",
            fechaIncorporacion = "2021-01-01",
            salario = 60000.0,
            paisOrigen = "Portugal"
        )

        // Assert
        assertEquals(dto1, dto2)
        assertNotEquals(dto1, dto3)
        assertEquals(dto1.hashCode(), dto2.hashCode())
        assertNotEquals(dto1.hashCode(), dto3.hashCode())
    }

    @Test
    fun `test PersonalCsvDto toString`() {
        // Arrange
        val dto = PersonalCsvDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            paisOrigen = "España"
        )

        // Act
        val result = dto.toString()

        // Assert
        assertTrue(result.contains("PersonalCsvDto"))
        assertTrue(result.contains("id=1"))
        assertTrue(result.contains("nombre=Juan"))
        assertTrue(result.contains("apellidos=Pérez"))
        assertTrue(result.contains("fechaNacimiento=1980-01-01"))
        assertTrue(result.contains("fechaIncorporacion=2020-01-01"))
        assertTrue(result.contains("salario=50000.0"))
        assertTrue(result.contains("paisOrigen=España"))
    }
}