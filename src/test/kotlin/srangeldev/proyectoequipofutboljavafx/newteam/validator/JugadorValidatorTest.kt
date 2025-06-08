package srangeldev.proyectoequipofutboljavafx.newteam.validator

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador.Posicion
import java.time.LocalDate
import java.time.LocalDateTime

class JugadorValidatorTest {

    private val validator = JugadorValidator()
    private val now = LocalDateTime.now()

    @Test
    fun `validate should pass on a valid player`() {
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1995, 6, 15),
            fechaIncorporacion = LocalDate.of(2020, 1, 10),
            salario = 100000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Posicion.DEFENSA,
            dorsal = 10,
            altura = 1.85,
            peso = 75.5,
            goles = 5,
            partidosJugados = 20
        )
        assertDoesNotThrow { validator.validate(jugador) }
    }

    @Test
    fun `validate should throw exception if dorsal is out of range`() {
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1995, 6, 15),
            fechaIncorporacion = LocalDate.of(2020, 1, 10),
            salario = 100000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Posicion.DEFENSA,
            dorsal = 100,
            altura = 1.85,
            peso = 75.5,
            goles = 5,
            partidosJugados = 20
        )
        val exception = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(jugador)
        }
        assert(exception.message!!.contains("El dorsal debe estar entre 1 y 99"))
    }

    @Test
    fun `validate should throw exception if altura is not positive`() {
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1995, 6, 15),
            fechaIncorporacion = LocalDate.of(2020, 1, 10),
            salario = 100000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Posicion.DEFENSA,
            dorsal = 10,
            altura = 0.0,
            peso = 75.5,
            goles = 5,
            partidosJugados = 20
        )
        val exception = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(jugador)
        }
        assert(exception.message!!.contains("La altura debe ser positiva"))
    }

    @Test
    fun `validate should throw exception if peso is not positive`() {
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1995, 6, 15),
            fechaIncorporacion = LocalDate.of(2020, 1, 10),
            salario = 100000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Posicion.DEFENSA,
            dorsal = 10,
            altura = 1.85,
            peso = -10.0,
            goles = 5,
            partidosJugados = 20
        )
        val exception = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(jugador)
        }
        assert(exception.message!!.contains("El peso debe ser positivo"))
    }

    @Test
    fun `validate should throw exception if goles are negative`() {
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1995, 6, 15),
            fechaIncorporacion = LocalDate.of(2020, 1, 10),
            salario = 100000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Posicion.DEFENSA,
            dorsal = 10,
            altura = 1.85,
            peso = 75.5,
            goles = -1,
            partidosJugados = 20
        )
        val exception = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(jugador)
        }
        assert(exception.message!!.contains("Los goles no pueden ser negativos"))
    }

    @Test
    fun `validate should throw exception if partidosJugados are negative`() {
        val jugador = Jugador(
            id = 1,
            nombre = "Juan",
            apellidos = "Perez",
            fechaNacimiento = LocalDate.of(1995, 6, 15),
            fechaIncorporacion = LocalDate.of(2020, 1, 10),
            salario = 100000.0,
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Posicion.DEFENSA,
            dorsal = 10,
            altura = 1.85,
            peso = 75.5,
            goles = 5,
            partidosJugados = -5
        )
        val exception = assertThrows(PersonalException.PersonalStorageException::class.java) {
            validator.validate(jugador)
        }
        assert(exception.message!!.contains("Los partidos jugados no pueden ser negativos"))
    }
}