package srangeldev.proyectoequipofutboljavafx.newteam.validator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate
import java.time.LocalDateTime

class PersonalValidatorTest {
    @Test
    fun validate() {
        val validator = PersonalValidator()

        // Test valid personal
        val validPersonal = Jugador(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2023, 1, 1),
            salario = 1000.0,
            paisOrigen = "USA",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.8,
            peso = 75.0,
            goles = 0,
            partidosJugados = 0
        )
        assertDoesNotThrow { validator.validate(validPersonal) }
       // Test invalid id
        val invalidIdPersonal = Jugador(
            id = -1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2023, 1, 1),
            salario = 1000.0,
            paisOrigen = "USA",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.8,
            peso = 75.0,
            goles = 0,
            partidosJugados = 0
        )
        assertThrows<PersonalException.PersonalNotFoundException> {
            validator.validate(invalidIdPersonal)
        }

        // Test empty name
        val emptyNamePersonal = Jugador(
            id = 1,
            nombre = "",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2023, 1, 1),
            salario = 1000.0,
            paisOrigen = "USA",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.8,
            peso = 75.0,
            goles = 0,
            partidosJugados = 0
        )
        assertThrows<PersonalException.PersonalStorageException> {
            validator.validate(emptyNamePersonal)
        }

        // Test empty surname
        val emptySurnamePersonal = Jugador(
            id = 1,
            nombre = "John",
            apellidos = "",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2023, 1, 1),
            salario = 1000.0,
            paisOrigen = "USA",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 10,
            altura = 1.8,
            peso = 75.0,
            goles = 0,
            partidosJugados = 0
        )
        assertThrows<PersonalException.PersonalStorageException> {
            validator.validate(emptySurnamePersonal)
        }
    }
}