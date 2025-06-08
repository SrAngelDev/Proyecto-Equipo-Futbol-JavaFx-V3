package srangeldev.proyectoequipofutboljavafx.newteam.validator

import srangeldev.proyectoequipofutboljavafx.newteam.models.*
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ValidatorFactoryTest {

    /**
     * Tests for the getValidator function in the ValidatorFactory class.
     * The getValidator function is responsible for returning the appropriate Validator
     * implementation for a given type or instance of a class.
     */

    @Test
    fun `should return JugadorValidator when type is Jugador`() {
        // Act
        val validator = ValidatorFactory.getValidator(Jugador::class.java)

        // Assert
        assertTrue(validator is JugadorValidator)
    }

    @Test
    fun `should return EntrenadorValidator when type is Entrenador`() {
        // Act
        val validator = ValidatorFactory.getValidator(Entrenador::class.java)

        // Assert
        assertTrue(validator is EntrenadorValidator)
    }

    @Test
    fun `should return PersonalValidator when type is Personal`() {
        // Act
        val validator = ValidatorFactory.getValidator(Personal::class.java)

        // Assert
        assertTrue(validator is PersonalValidator)
    }

    @Test
    fun `should return UserValidator when type is User`() {
        // Act
        val validator = ValidatorFactory.getValidator(User::class.java)

        // Assert
        assertTrue(validator is UserValidator)
    }

    @Test
    fun `should return ConvocatoriaValidator when type is Convocatoria`() {
        // Act
        val validator = ValidatorFactory.getValidator(Convocatoria::class.java)

        // Assert
        assertTrue(validator is ConvocatoriaValidator)
    }

    @Test
    fun `should throw IllegalArgumentException for an unsupported type`() {
        // Arrange
        class UnknownType

        // Assert & Act
        assertFailsWith<IllegalArgumentException>("No hay un validador disponible para el tipo: srangeldev.proyectoequipofutboljavafx.newteam.validator.ValidatorFactoryTest\$1UnknownType") {
            ValidatorFactory.getValidator(UnknownType::class.java)
        }
    }

    @Test
    fun `should return JugadorValidator when instance is Jugador`() {
        // Arrange
        // Arrange
        val instance = Jugador(
            id = 1,
            nombre = "Test",
            apellidos = "Test",
            fechaNacimiento = LocalDate.parse("2000-01-01"),
            fechaIncorporacion = LocalDate.parse("2023-01-01"),
            salario = 1000.0,
            paisOrigen = "España",
            createdAt = LocalDateTime.parse("2023-01-01T00:00:00"),
            updatedAt = LocalDateTime.parse("2023-01-01T00:00:00"),
            posicion = Jugador.Posicion.DELANTERO,
            dorsal = 9,
            altura = 180.0,
            peso = 75.0,
            goles = 0,
            partidosJugados = 0
        )

        // Act
        val validator = ValidatorFactory.getValidator(instance)

        // Assert
        assertTrue(validator is JugadorValidator)
    }

    @Test
    fun `should return EntrenadorValidator when instance is Entrenador`() {
        // Arrange
        // Arrange
        val instance = Entrenador(
            id = 1,
            nombre = "Test",
            apellidos = "Test",
            fechaNacimiento = LocalDate.parse("2000-01-01"),
            fechaIncorporacion = LocalDate.parse("2023-01-01"),
            salario = 1000.0,
            paisOrigen = "España",
            createdAt = LocalDateTime.parse("2023-01-01T00:00:00"),
            updatedAt = LocalDateTime.parse("2023-01-01T00:00:00"),
            especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
        )

        // Act
        val validator = ValidatorFactory.getValidator(instance)

        // Assert
        assertTrue(validator is EntrenadorValidator)
    }

    @Test
    fun `should return UserValidator when instance is User`() {
        // Arrange
        // Arrange
        val instance = User(
            username = "test",
            password = "test123",
            role = User.Role.USER
        )

        // Act
        val validator = ValidatorFactory.getValidator(instance)

        // Assert
        assertTrue(validator is UserValidator)
    }

    @Test
    fun `should return ConvocatoriaValidator when instance is Convocatoria`() {
        // Arrange
        // Arrange
        val instance = Convocatoria(
            fecha = LocalDate.parse("2023-01-01") ,
            descripcion = "Test convocatoria",
            equipoId = 1,
            entrenadorId = 1
        )

        // Act
        val validator = ValidatorFactory.getValidator(instance)

        // Assert
        assertTrue(validator is ConvocatoriaValidator)
    }

    @Test
    fun `should throw IllegalArgumentException for an unsupported instance`() {
        // Arrange
        class UnknownInstance

        val instance = UnknownInstance()

        // Assert & Act
        assertFailsWith<IllegalArgumentException>("No hay un validador disponible para el tipo: srangeldev.proyectoequipofutboljavafx.newteam.validator.ValidatorFactoryTest\$1UnknownInstance") {
            ValidatorFactory.getValidator(instance)
        }
    }
}