package srangeldev.proyectoequipofutboljavafx.newteam.validator

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import java.time.LocalDate
import java.time.LocalDateTime

internal class EntrenadorValidatorTest {

    /**
     * Unit tests for the `validate` function in the `EntrenadorValidator` class.
     * The `validate` method is responsible for validating an `Entrenador` object
     * using the `PersonalValidator` class.
     */

    @Test
    fun `validate should call personalValidator with valid Entrenador`() {
        // Arrange
        val mockPersonalValidator = mock<PersonalValidator>()
        val entrenadorValidator = EntrenadorValidator()
        val entrenador = Entrenador(
            id = 1,
            nombre = "Carlos",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1980, 5, 20),
            fechaIncorporacion = LocalDate.of(2020, 1, 15),
            salario = 3000.0,
            paisOrigen = "Spain",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
        )

        // Act
        mockPersonalValidator.validate(entrenador)

        // Assert
        verify(mockPersonalValidator).validate(entrenador)
    }

    @Test
    fun `validate should throw PersonalException for invalid Entrenador`() {
        // Arrange
        val entrenadorValidator = EntrenadorValidator()
        val entrenador = Entrenador(
            id = -1,
            nombre = "",
            apellidos = "",
            fechaNacimiento = LocalDate.of(1980, 5, 20),
            fechaIncorporacion = LocalDate.of(2020, 1, 15),
            salario = 3000.0,
            paisOrigen = "Spain",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
        )

        // Act & Assert
        assertThrows(
            PersonalException::class.java
        ) { entrenadorValidator.validate(entrenador) }
    }

    @Test
    fun `validate should handle Entrenador with missing specialization`() {
        // Arrange
        val entrenadorValidator = EntrenadorValidator()
        val entrenador = Entrenador(
            id = 5,
            nombre = "Luis",
            apellidos = "Martinez",
            fechaNacimiento = LocalDate.of(1970, 2, 28),
            fechaIncorporacion = LocalDate.of(2010, 8, 12),
            salario = 5000.0,
            paisOrigen = "Argentina",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            especializacion = Entrenador.Especializacion.ENTRENADOR_ASISTENTE
        )

        // Act
        entrenadorValidator.validate(entrenador)
    }
}