package srangeldev.proyectoequipofutboljavafx.newteam.dto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PersonalXmlDtoTest {

    @Test
    fun `test PersonalXmlDto creation with all parameters`() {
        // Arrange
        val id = 1
        val tipo = "jugador"
        val nombre = "Juan"
        val apellidos = "Pérez"
        val fechaNacimiento = "1980-01-01"
        val fechaIncorporacion = "2020-01-01"
        val salario = 50000.0
        val pais = "España"
        val especialidad = "ENTRENADOR_PRINCIPAL"
        val posicion = "PORTERO"
        val dorsal = "10"
        val altura = "180"
        val peso = "75"
        val goles = "50"
        val partidosJugados = "100"
        val imagenUrl = "imagen.jpg"

        // Act
        val dto = PersonalXmlDto(
            id = id,
            tipo = tipo,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = salario,
            pais = pais,
            especialidad = especialidad,
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
        assertEquals(tipo, dto.tipo)
        assertEquals(nombre, dto.nombre)
        assertEquals(apellidos, dto.apellidos)
        assertEquals(fechaNacimiento, dto.fechaNacimiento)
        assertEquals(fechaIncorporacion, dto.fechaIncorporacion)
        assertEquals(salario, dto.salario)
        assertEquals(pais, dto.pais)
        assertEquals(especialidad, dto.especialidad)
        assertEquals(posicion, dto.posicion)
        assertEquals(dorsal, dto.dorsal)
        assertEquals(altura, dto.altura)
        assertEquals(peso, dto.peso)
        assertEquals(goles, dto.goles)
        assertEquals(partidosJugados, dto.partidosJugados)
        assertEquals(imagenUrl, dto.imagenUrl)
    }

    @Test
    fun `test PersonalXmlDto with default values`() {
        // Arrange & Act
        val dto = PersonalXmlDto(
            id = 1
        )

        // Assert
        assertEquals("", dto.tipo)
        assertEquals("", dto.nombre)
        assertEquals("", dto.apellidos)
        assertEquals("", dto.fechaNacimiento)
        assertEquals("", dto.fechaIncorporacion)
        assertEquals(0.0, dto.salario)
        assertEquals("", dto.pais)
        assertEquals("", dto.especialidad)
        assertEquals("", dto.posicion)
        assertEquals("", dto.dorsal)
        assertEquals("", dto.altura)
        assertEquals("", dto.peso)
        assertEquals("", dto.goles)
        assertEquals("", dto.partidosJugados)
        assertEquals("", dto.imagenUrl)
    }

    @Test
    fun `test PersonalXmlDto copy`() {
        // Arrange
        val original = PersonalXmlDto(
            id = 1,
            tipo = "jugador",
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España",
            especialidad = "ENTRENADOR_PRINCIPAL",
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
            tipo = "entrenador",
            nombre = "Pedro",
            salario = 60000.0,
            especialidad = "ENTRENADOR_ASISTENTE"
        )

        // Assert
        assertEquals(original.id, copy.id)
        assertEquals("entrenador", copy.tipo)
        assertEquals("Pedro", copy.nombre)
        assertEquals(original.apellidos, copy.apellidos)
        assertEquals(original.fechaNacimiento, copy.fechaNacimiento)
        assertEquals(original.fechaIncorporacion, copy.fechaIncorporacion)
        assertEquals(60000.0, copy.salario)
        assertEquals(original.pais, copy.pais)
        assertEquals("ENTRENADOR_ASISTENTE", copy.especialidad)
        assertEquals(original.posicion, copy.posicion)
        assertEquals(original.dorsal, copy.dorsal)
        assertEquals(original.altura, copy.altura)
        assertEquals(original.peso, copy.peso)
        assertEquals(original.goles, copy.goles)
        assertEquals(original.partidosJugados, copy.partidosJugados)
        assertEquals(original.imagenUrl, copy.imagenUrl)
    }

    @Test
    fun `test PersonalXmlDto equals and hashCode`() {
        // Arrange
        val dto1 = PersonalXmlDto(
            id = 1,
            tipo = "jugador",
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España"
        )

        val dto2 = PersonalXmlDto(
            id = 1,
            tipo = "jugador",
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España"
        )

        val dto3 = PersonalXmlDto(
            id = 2,
            tipo = "entrenador",
            nombre = "Pedro",
            apellidos = "Gómez",
            fechaNacimiento = "1985-01-01",
            fechaIncorporacion = "2021-01-01",
            salario = 60000.0,
            pais = "Portugal"
        )

        // Assert
        assertEquals(dto1, dto2)
        assertNotEquals(dto1, dto3)
        assertEquals(dto1.hashCode(), dto2.hashCode())
        assertNotEquals(dto1.hashCode(), dto3.hashCode())
    }

    @Test
    fun `test PersonalXmlDto toString`() {
        // Arrange
        val dto = PersonalXmlDto(
            id = 1,
            tipo = "jugador",
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España"
        )

        // Act
        val result = dto.toString()

        // Assert
        assertTrue(result.contains("PersonalXmlDto"))
        assertTrue(result.contains("id=1"))
        assertTrue(result.contains("tipo=jugador"))
        assertTrue(result.contains("nombre=Juan"))
        assertTrue(result.contains("apellidos=Pérez"))
        assertTrue(result.contains("fechaNacimiento=1980-01-01"))
        assertTrue(result.contains("fechaIncorporacion=2020-01-01"))
        assertTrue(result.contains("salario=50000.0"))
        assertTrue(result.contains("pais=España"))
    }
}