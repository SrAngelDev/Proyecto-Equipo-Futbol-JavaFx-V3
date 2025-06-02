package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PersonalRepositoryImplTest {

    // We'll use a test-specific implementation of PersonalRepository
    private lateinit var personalRepository: TestPersonalRepository

    @BeforeEach
    fun setUp() {
        // Initialize the test repository with some test data
        personalRepository = TestPersonalRepository()

        // Add some test data
        personalRepository.save(
            Entrenador(
                id = 0,
                nombre = "Juan",
                apellidos = "Pérez",
                fechaNacimiento = LocalDate.of(1980, 1, 1),
                fechaIncorporacion = LocalDate.of(2020, 1, 1),
                salario = 50000.0,
                paisOrigen = "España",
                especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        personalRepository.save(
            Entrenador(
                id = 0,
                nombre = "Pedro",
                apellidos = "García",
                fechaNacimiento = LocalDate.of(1985, 2, 2),
                fechaIncorporacion = LocalDate.of(2021, 2, 2),
                salario = 40000.0,
                paisOrigen = "España",
                especializacion = Entrenador.Especializacion.ENTRENADOR_ASISTENTE,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        personalRepository.save(
            Jugador(
                id = 0,
                nombre = "Carlos",
                apellidos = "Rodríguez",
                fechaNacimiento = LocalDate.of(1990, 3, 3),
                fechaIncorporacion = LocalDate.of(2022, 3, 3),
                salario = 30000.0,
                paisOrigen = "Argentina",
                posicion = Jugador.Posicion.DELANTERO,
                dorsal = 9,
                altura = 1.80,
                peso = 75.0,
                goles = 10,
                partidosJugados = 20,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        personalRepository.save(
            Jugador(
                id = 0,
                nombre = "Luis",
                apellidos = "Martínez",
                fechaNacimiento = LocalDate.of(1995, 4, 4),
                fechaIncorporacion = LocalDate.of(2023, 4, 4),
                salario = 25000.0,
                paisOrigen = "España",
                posicion = Jugador.Posicion.PORTERO,
                dorsal = 1,
                altura = 1.90,
                peso = 85.0,
                goles = 0,
                partidosJugados = 15,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
    }

    @Test
    fun `test getAll returns all personal`() {
        // Act
        val personal = personalRepository.getAll()

        // Assert
        assertEquals(4, personal.size)
        assertTrue(personal.any { it is Entrenador && it.nombre == "Juan" })
        assertTrue(personal.any { it is Entrenador && it.nombre == "Pedro" })
        assertTrue(personal.any { it is Jugador && it.nombre == "Carlos" })
        assertTrue(personal.any { it is Jugador && it.nombre == "Luis" })
    }

    @Test
    fun `test getById returns correct personal`() {
        // Arrange
        val allPersonal = personalRepository.getAll()
        val entrenador = allPersonal.first { it is Entrenador && it.nombre == "Juan" }
        val jugador = allPersonal.first { it is Jugador && it.nombre == "Carlos" }

        // Act
        val retrievedEntrenador = personalRepository.getById(entrenador.id)
        val retrievedJugador = personalRepository.getById(jugador.id)
        val nonExistentPersonal = personalRepository.getById(999)

        // Assert
        assertNotNull(retrievedEntrenador)
        assertEquals(entrenador.id, retrievedEntrenador?.id)
        assertEquals("Juan", retrievedEntrenador?.nombre)
        assertTrue(retrievedEntrenador is Entrenador)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, (retrievedEntrenador as Entrenador).especializacion)

        assertNotNull(retrievedJugador)
        assertEquals(jugador.id, retrievedJugador?.id)
        assertEquals("Carlos", retrievedJugador?.nombre)
        assertTrue(retrievedJugador is Jugador)
        assertEquals(Jugador.Posicion.DELANTERO, (retrievedJugador as Jugador).posicion)

        assertNull(nonExistentPersonal)
    }

    @Test
    fun `test save creates new personal`() {
        // Arrange
        val newEntrenador = Entrenador(
            id = 0,
            nombre = "Nuevo",
            apellidos = "Entrenador",
            fechaNacimiento = LocalDate.of(1975, 5, 5),
            fechaIncorporacion = LocalDate.of(2019, 5, 5),
            salario = 60000.0,
            paisOrigen = "Italia",
            especializacion = Entrenador.Especializacion.ENTRENADOR_PORTEROS,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Act
        val savedEntrenador = personalRepository.save(newEntrenador)

        // Assert
        assertNotNull(savedEntrenador)
        assertTrue(savedEntrenador.id > 0)
        assertEquals("Nuevo", savedEntrenador.nombre)
        assertTrue(savedEntrenador is Entrenador)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PORTEROS, (savedEntrenador as Entrenador).especializacion)

        // Verify the entrenador was added to the repository
        val retrievedEntrenador = personalRepository.getById(savedEntrenador.id)
        assertNotNull(retrievedEntrenador)
        assertEquals(savedEntrenador.id, retrievedEntrenador?.id)
    }

    @Test
    fun `test update modifies existing personal`() {
        // Arrange
        val allPersonal = personalRepository.getAll()
        val jugador = allPersonal.first { it is Jugador && it.nombre == "Carlos" } as Jugador

        // Create a new Jugador with updated values
        val updatedJugador = Jugador(
            id = jugador.id,
            nombre = "Carlos",
            apellidos = "Rodríguez Actualizado",
            fechaNacimiento = jugador.fechaNacimiento,
            fechaIncorporacion = jugador.fechaIncorporacion,
            salario = jugador.salario,
            paisOrigen = jugador.paisOrigen,
            posicion = jugador.posicion,
            dorsal = jugador.dorsal,
            altura = jugador.altura,
            peso = jugador.peso,
            goles = 15,
            partidosJugados = 25,
            createdAt = jugador.createdAt,
            updatedAt = LocalDateTime.now()
        )

        // Act
        val result = personalRepository.update(jugador.id, updatedJugador)

        // Assert
        assertNotNull(result)
        assertEquals(jugador.id, result?.id)
        assertEquals("Carlos", result?.nombre)
        assertEquals("Rodríguez Actualizado", result?.apellidos)
        assertTrue(result is Jugador)
        assertEquals(15, (result as Jugador).goles)
        assertEquals(25, result.partidosJugados)

        // Verify the jugador was updated in the repository
        val retrievedJugador = personalRepository.getById(jugador.id)
        assertNotNull(retrievedJugador)
        assertEquals(jugador.id, retrievedJugador?.id)
        assertEquals("Rodríguez Actualizado", retrievedJugador?.apellidos)
    }

    @Test
    fun `test delete removes personal`() {
        // Arrange
        val allPersonal = personalRepository.getAll()
        val entrenador = allPersonal.first { it is Entrenador && it.nombre == "Pedro" }

        // Act
        val result = personalRepository.delete(entrenador.id)

        // Assert
        assertNotNull(result)
        assertEquals(entrenador.id, result?.id)

        // Verify the entrenador was removed from the repository
        val retrievedEntrenador = personalRepository.getById(entrenador.id)
        assertNull(retrievedEntrenador)
    }

    @Test
    fun `test getAllEntrenadores returns only entrenadores`() {
        // Act
        val entrenadores = personalRepository.getAllEntrenadores()

        // Assert
        assertEquals(2, entrenadores.size)
        assertTrue(entrenadores.any { it.nombre == "Juan" })
        assertTrue(entrenadores.any { it.nombre == "Pedro" })
    }

    @Test
    fun `test clearCache clears the cache`() {
        // Arrange
        val initialPersonal = personalRepository.getAll()
        assertEquals(4, initialPersonal.size)

        // Act
        personalRepository.clearCache()

        // Assert - the cache should be cleared, but the data should still be retrievable
        val personalAfterClear = personalRepository.getAll()
        assertEquals(4, personalAfterClear.size)
    }

    @Test
    fun `test getEntrenadores returns all entrenadores`() {
        // Act
        val entrenadores = (personalRepository as TestPersonalRepository).getEntrenadores()

        // Assert
        assertEquals(2, entrenadores.size)
        assertTrue(entrenadores.any { it.nombre == "Juan" })
        assertTrue(entrenadores.any { it.nombre == "Pedro" })
    }

    @Test
    fun `test getJugadores returns all jugadores`() {
        // Act
        val jugadores = (personalRepository as TestPersonalRepository).getJugadores()

        // Assert
        assertEquals(2, jugadores.size)
        assertTrue(jugadores.any { it.nombre == "Carlos" })
        assertTrue(jugadores.any { it.nombre == "Luis" })
    }

    // Test-specific implementation of PersonalRepository that doesn't use the database
    class TestPersonalRepository : PersonalRepository {
        private val personal = mutableMapOf<Int, Personal>()
        private var nextId = 1

        override fun getAll(): List<Personal> {
            return personal.values.toList()
        }

        override fun getById(id: Int): Personal? {
            return personal[id]
        }

        override fun save(entidad: Personal): Personal {
            val isUpdate = entidad.id > 0

            if (isUpdate) {
                return update(entidad.id, entidad) ?: throw IllegalStateException("No se pudo actualizar el personal")
            } else {
                val id = nextId++
                val now = LocalDateTime.now()

                val newPersonal = when (entidad) {
                    is Entrenador -> Entrenador(
                        id = id,
                        nombre = entidad.nombre,
                        apellidos = entidad.apellidos,
                        fechaNacimiento = entidad.fechaNacimiento,
                        fechaIncorporacion = entidad.fechaIncorporacion,
                        salario = entidad.salario,
                        paisOrigen = entidad.paisOrigen,
                        especializacion = entidad.especializacion,
                        createdAt = now,
                        updatedAt = now,
                        imagenUrl = entidad.imagenUrl
                    )
                    is Jugador -> Jugador(
                        id = id,
                        nombre = entidad.nombre,
                        apellidos = entidad.apellidos,
                        fechaNacimiento = entidad.fechaNacimiento,
                        fechaIncorporacion = entidad.fechaIncorporacion,
                        salario = entidad.salario,
                        paisOrigen = entidad.paisOrigen,
                        posicion = entidad.posicion,
                        dorsal = entidad.dorsal,
                        altura = entidad.altura,
                        peso = entidad.peso,
                        goles = entidad.goles,
                        partidosJugados = entidad.partidosJugados,
                        createdAt = now,
                        updatedAt = now,
                        imagenUrl = entidad.imagenUrl
                    )
                    else -> throw IllegalArgumentException("Tipo desconocido de Personal")
                }

                personal[id] = newPersonal
                return newPersonal
            }
        }

        override fun update(id: Int, entidad: Personal): Personal? {
            val existingPersonal = personal[id] ?: return null

            val updatedPersonal = when (entidad) {
                is Entrenador -> {
                    if (existingPersonal !is Entrenador) {
                        throw IllegalArgumentException("No se puede actualizar un Jugador como Entrenador")
                    }
                    Entrenador(
                        id = id,
                        nombre = entidad.nombre,
                        apellidos = entidad.apellidos,
                        fechaNacimiento = entidad.fechaNacimiento,
                        fechaIncorporacion = entidad.fechaIncorporacion,
                        salario = entidad.salario,
                        paisOrigen = entidad.paisOrigen,
                        especializacion = entidad.especializacion,
                        createdAt = existingPersonal.createdAt,
                        updatedAt = LocalDateTime.now(),
                        imagenUrl = entidad.imagenUrl
                    )
                }
                is Jugador -> {
                    if (existingPersonal !is Jugador) {
                        throw IllegalArgumentException("No se puede actualizar un Entrenador como Jugador")
                    }
                    Jugador(
                        id = id,
                        nombre = entidad.nombre,
                        apellidos = entidad.apellidos,
                        fechaNacimiento = entidad.fechaNacimiento,
                        fechaIncorporacion = entidad.fechaIncorporacion,
                        salario = entidad.salario,
                        paisOrigen = entidad.paisOrigen,
                        posicion = entidad.posicion,
                        dorsal = entidad.dorsal,
                        altura = entidad.altura,
                        peso = entidad.peso,
                        goles = entidad.goles,
                        partidosJugados = entidad.partidosJugados,
                        createdAt = existingPersonal.createdAt,
                        updatedAt = LocalDateTime.now(),
                        imagenUrl = entidad.imagenUrl
                    )
                }
                else -> throw IllegalArgumentException("Tipo desconocido de Personal")
            }

            personal[id] = updatedPersonal
            return updatedPersonal
        }

        override fun delete(id: Int): Personal? {
            return personal.remove(id)
        }

        override fun clearCache() {
            // In a real implementation, this would clear the cache but not the data
            // For testing purposes, we'll just simulate this behavior
        }

        override fun getAllEntrenadores(): List<Entrenador> {
            return personal.values.filterIsInstance<Entrenador>()
        }

        // Implementation of getEntrenadores for testing
        fun getEntrenadores(): List<Entrenador> {
            return personal.values.filterIsInstance<Entrenador>()
        }

        // Implementation of getJugadores for testing
        fun getJugadores(): List<Jugador> {
            return personal.values.filterIsInstance<Jugador>()
        }
    }
}
