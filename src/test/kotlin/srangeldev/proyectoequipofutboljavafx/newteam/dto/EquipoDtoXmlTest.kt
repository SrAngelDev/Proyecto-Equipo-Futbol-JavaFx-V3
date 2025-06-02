package srangeldev.proyectoequipofutboljavafx.newteam.dto

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EquipoDtoXmlTest {

    @Test
    fun `test EquipoDtoXml creation with empty list`() {
        // Arrange & Act
        val dto = EquipoDtoXml(
            equipo = emptyList()
        )

        // Assert
        assertTrue(dto.equipo.isEmpty())
    }

    @Test
    fun `test EquipoDtoXml creation with personal list`() {
        // Arrange
        val personal1 = PersonalXmlDto(
            id = 1,
            tipo = "jugador",
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = "1980-01-01",
            fechaIncorporacion = "2020-01-01",
            salario = 50000.0,
            pais = "España",
            posicion = "PORTERO"
        )

        val personal2 = PersonalXmlDto(
            id = 2,
            tipo = "entrenador",
            nombre = "Pedro",
            apellidos = "Gómez",
            fechaNacimiento = "1975-01-01",
            fechaIncorporacion = "2019-01-01",
            salario = 60000.0,
            pais = "Portugal",
            especialidad = "ENTRENADOR_PRINCIPAL"
        )

        val personalList = listOf(personal1, personal2)

        // Act
        val dto = EquipoDtoXml(
            equipo = personalList
        )

        // Assert
        assertEquals(2, dto.equipo.size)
        assertEquals(personal1, dto.equipo[0])
        assertEquals(personal2, dto.equipo[1])
    }

    @Test
    fun `test EquipoDtoXml copy`() {
        // Arrange
        val personal1 = PersonalXmlDto(
            id = 1,
            tipo = "jugador",
            nombre = "Juan",
            apellidos = "Pérez"
        )

        val personal2 = PersonalXmlDto(
            id = 2,
            tipo = "entrenador",
            nombre = "Pedro",
            apellidos = "Gómez"
        )

        val originalList = listOf(personal1, personal2)
        val original = EquipoDtoXml(equipo = originalList)

        val personal3 = PersonalXmlDto(
            id = 3,
            tipo = "jugador",
            nombre = "Carlos",
            apellidos = "Rodríguez"
        )

        val newList = listOf(personal1, personal3)

        // Act
        val copy = original.copy(equipo = newList)

        // Assert
        assertEquals(2, copy.equipo.size)
        assertEquals(personal1, copy.equipo[0])
        assertEquals(personal3, copy.equipo[1])
        assertNotEquals(original.equipo, copy.equipo)
    }

    @Test
    fun `test EquipoDtoXml equals and hashCode`() {
        // Arrange
        val personal1 = PersonalXmlDto(
            id = 1,
            tipo = "jugador",
            nombre = "Juan",
            apellidos = "Pérez"
        )

        val personal2 = PersonalXmlDto(
            id = 2,
            tipo = "entrenador",
            nombre = "Pedro",
            apellidos = "Gómez"
        )

        val list1 = listOf(personal1, personal2)
        val list2 = listOf(personal1, personal2)
        val list3 = listOf(personal2, personal1) // Same elements but different order

        val dto1 = EquipoDtoXml(equipo = list1)
        val dto2 = EquipoDtoXml(equipo = list2)
        val dto3 = EquipoDtoXml(equipo = list3)

        // Assert
        assertEquals(dto1, dto2)
        assertNotEquals(dto1, dto3) // Different order means different list
        assertEquals(dto1.hashCode(), dto2.hashCode())
        assertNotEquals(dto1.hashCode(), dto3.hashCode())
    }

    @Test
    fun `test EquipoDtoXml toString`() {
        // Arrange
        val personal1 = PersonalXmlDto(
            id = 1,
            tipo = "jugador",
            nombre = "Juan",
            apellidos = "Pérez"
        )

        val personal2 = PersonalXmlDto(
            id = 2,
            tipo = "entrenador",
            nombre = "Pedro",
            apellidos = "Gómez"
        )

        val dto = EquipoDtoXml(equipo = listOf(personal1, personal2))

        // Act
        val result = dto.toString()

        // Assert
        assertTrue(result.contains("EquipoDtoXml"))
        assertTrue(result.contains("equipo="))
        assertTrue(result.contains("PersonalXmlDto"))
        assertTrue(result.contains("id=1"))
        assertTrue(result.contains("nombre=Juan"))
        assertTrue(result.contains("id=2"))
        assertTrue(result.contains("nombre=Pedro"))
    }
}