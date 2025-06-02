package srangeldev.proyectoequipofutboljavafx.newteam.validator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.validator.validate
import java.time.LocalDate
import java.time.LocalDateTime

class PersonaValidatorTest {

    private val now = LocalDateTime.now()
    private val fechaNacimiento = LocalDate.of(1990, 1, 1)
    private val fechaIncorporacion = LocalDate.of(2020, 1, 1)

    @Test
    fun `validate should not throw exception for valid personal`() {
        // Given
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 50000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 10,
            partidosJugados = 20
        )

        // When/Then - No exception should be thrown
        jugador.validate()
    }

    @Test
    fun `validate should throw PersonalNotFoundException for negative id`() {
        // Given
        val jugador = Jugador(
            id = -1,
            nombre = "Juan",
            apellidos = "Pérez",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 50000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 10,
            partidosJugados = 20
        )

        // When/Then
        val exception = assertThrows<PersonalException.PersonalNotFoundException> {
            jugador.validate()
        }
        assert(exception.message!!.contains("-1"))
    }

    @Test
    fun `validate should throw PersonalStorageException for empty nombre`() {
        // Given
        val jugador = Jugador(
            id = 1,
            nombre = "",
            apellidos = "Pérez",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 50000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 10,
            partidosJugados = 20
        )

        // When/Then
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            jugador.validate()
        }
        assert(exception.message!!.contains("nombre:"))
    }

    @Test
    fun `validate should throw PersonalStorageException for empty apellidos`() {
        // Given
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "",
            fechaNacimiento = fechaNacimiento,
            fechaIncorporacion = fechaIncorporacion,
            salario = 50000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 1.80,
            peso = 75.0,
            goles = 10,
            partidosJugados = 20
        )

        // When/Then
        val exception = assertThrows<PersonalException.PersonalStorageException> {
            jugador.validate()
        }
        assert(exception.message!!.contains("apellidos:"))
    }
}