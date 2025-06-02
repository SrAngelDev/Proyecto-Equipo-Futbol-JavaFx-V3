package srangeldev.proyectoequipofutboljavafx.newteam.dto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PersonalJsonDtoTest {

    @Test
    fun `test PersonalJsonDto creation with all parameters`() {
        // Arrange
        val id = 1
        val nombre = "Juan"
        val apellidos = "Pérez"
        val fechaNacimiento = "1980-01-01"
        val fechaIncorporacion = "2020-01-01"
        val salario = 50000.0
        val pais = "España"
        val rol = "ADMIN"
        val especializacion = "ENTRENADOR_PRINCIPAL"
        val posicion = "PORTERO"
        val dorsal = 10
        val altura = 180.0
        val peso = 75.0
        val goles = 50
        val partidosJugados = 100
        val imagenUrl = "imagen.jpg"

        // Act
        val dto = PersonalJsonDto(
            id = id,
            nombre = nombre,
            apellidos = apellidos,
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = salario,
            pais = pais,
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
        assertEquals(pais, dto.pais)
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
    fun `test PersonalJsonDto with default values`() {
        // Arrange & Act
        val dto = PersonalJsonDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            pais = "España"
        )

        // Assert
        assertEquals(0.0, dto.salario)
        assertEquals("", dto.rol)
        assertEquals("", dto.especializacion)
        assertEquals("", dto.posicion)
        assertEquals(0, dto.dorsal)
        assertEquals(0.0, dto.altura)
        assertEquals(0.0, dto.peso)
        assertEquals(0, dto.goles)
        assertEquals(0, dto.partidosJugados)
        assertEquals("", dto.imagenUrl)
    }

    @Test
    fun `test PersonalJsonDto copy`() {
        // Arrange
        val original = PersonalJsonDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España",
            rol = "ADMIN",
            especializacion = "ENTRENADOR_PRINCIPAL",
            posicion = "PORTERO",
            dorsal = 10,
            altura = 180.0,
            peso = 75.0,
            goles = 50,
            partidosJugados = 100,
            imagenUrl = "imagen.jpg"
        )

        // Act
        val copy = original.copy(
            nombre = "Pedro",
            salario = 60000.0,
            posicion = "DEFENSA",
            dorsal = 5,
            goles = 20
        )

        // Assert
        assertEquals(original.id, copy.id)
        assertEquals("Pedro", copy.nombre)
        assertEquals(original.apellidos, copy.apellidos)
        assertEquals(original.fechaNacimiento, copy.fechaNacimiento)
        assertEquals(original.fechaIncorporacion, copy.fechaIncorporacion)
        assertEquals(60000.0, copy.salario)
        assertEquals(original.pais, copy.pais)
        assertEquals(original.rol, copy.rol)
        assertEquals(original.especializacion, copy.especializacion)
        assertEquals("DEFENSA", copy.posicion)
        assertEquals(5, copy.dorsal)
        assertEquals(original.altura, copy.altura)
        assertEquals(original.peso, copy.peso)
        assertEquals(20, copy.goles)
        assertEquals(original.partidosJugados, copy.partidosJugados)
        assertEquals(original.imagenUrl, copy.imagenUrl)
    }

    @Test
    fun `test PersonalJsonDto equals and hashCode`() {
        // Arrange
        val dto1 = PersonalJsonDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España",
            dorsal = 10,
            altura = 180.0,
            peso = 75.0,
            goles = 50,
            partidosJugados = 100
        )

        val dto2 = PersonalJsonDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España",
            dorsal = 10,
            altura = 180.0,
            peso = 75.0,
            goles = 50,
            partidosJugados = 100
        )

        val dto3 = PersonalJsonDto(
            id = 2,
            nombre = "Pedro",
            apellidos = "Gómez",
            fechaNacimiento = "1985-01-01",
            fechaIncorporacion = "2021-01-01",
            salario = 60000.0,
            pais = "Portugal",
            dorsal = 5,
            altura = 175.0,
            peso = 70.0,
            goles = 30,
            partidosJugados = 80
        )

        // Assert
        assertEquals(dto1, dto2)
        assertNotEquals(dto1, dto3)
        assertEquals(dto1.hashCode(), dto2.hashCode())
        assertNotEquals(dto1.hashCode(), dto3.hashCode())
    }

    @Test
    fun `test PersonalJsonDto toString`() {
        // Arrange
        val dto = PersonalJsonDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España",
            dorsal = 10,
            altura = 180.0,
            peso = 75.0,
            goles = 50,
            partidosJugados = 100
        )

        // Act
        val result = dto.toString()

        // Assert
        assertTrue(result.contains("PersonalJsonDto"))
        assertTrue(result.contains("id=1"))
        assertTrue(result.contains("nombre=Juan"))
        assertTrue(result.contains("apellidos=Pérez"))
        assertTrue(result.contains("fechaNacimiento=1980-01-01"))
        assertTrue(result.contains("fechaIncorporacion=2020-01-01"))
        assertTrue(result.contains("salario=50000.0"))
        assertTrue(result.contains("pais=España"))
        assertTrue(result.contains("dorsal=10"))
        assertTrue(result.contains("altura=180.0"))
        assertTrue(result.contains("peso=75.0"))
        assertTrue(result.contains("goles=50"))
        assertTrue(result.contains("partidosJugados=100"))
    }

    @Test
    fun `test PersonalJsonDto with null numeric values`() {
        // Arrange & Act
        val dto = PersonalJsonDto(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            pais = "España",
            dorsal = null,
            altura = null,
            peso = null,
            goles = null,
            partidosJugados = null
        )

        // Assert
        assertNull(dto.dorsal)
        assertNull(dto.altura)
        assertNull(dto.peso)
        assertNull(dto.goles)
        assertNull(dto.partidosJugados)
    }
}
