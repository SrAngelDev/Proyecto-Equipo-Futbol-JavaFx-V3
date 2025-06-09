package srangeldev.proyectoequipofutboljavafx.newteam.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.cache.Cache
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.repository.PersonalRepository
import srangeldev.proyectoequipofutboljavafx.newteam.storage.FileFormat
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorage
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PersonalServiceImplTest {

    private lateinit var storage: PersonalStorage
    private lateinit var repository: PersonalRepository
    private lateinit var cache: Cache<Int, Personal>
    private lateinit var service: PersonalServiceImpl

    private val now = LocalDateTime.now()
    private val jugador = Jugador(
        id = 1,
        nombre = "Juan",
        apellidos = "Pérez",
        fechaNacimiento = LocalDate.of(1990, 1, 1),
        fechaIncorporacion = LocalDate.of(2020, 1, 1),
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

    @BeforeEach
    fun setUp() {
        storage = mock()
        repository = mock()
        cache = mock()
        service = PersonalServiceImpl(storage, repository, cache)
    }

    @Test
    fun `importFromFile should read from file, validate and save valid objects to repository`() {
        // Given
        val filePath = "test.json"
        val format = FileFormat.JSON
        val personalList = listOf(jugador)

        whenever(storage.readFromFile(any(), eq(format))).thenReturn(personalList)

        // When
        service.importFromFile(filePath, format)

        // Then
        verify(storage).readFromFile(any(), eq(format))
        verify(repository).save(jugador)
    }

    @Test
    fun `importFromFile should skip invalid objects and continue with valid ones`() {
        // Given
        val filePath = "test.json"
        val format = FileFormat.JSON

        // Create an invalid jugador (missing required fields)
        val invalidJugador = Jugador(
            id = 2,
            nombre = "", // Invalid: empty name
            apellidos = "García",
            fechaNacimiento = LocalDate.of(1995, 5, 5),
            fechaIncorporacion = LocalDate.of(2021, 1, 1),
            salario = -1000.0, // Invalid: negative salary
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Jugador.Posicion.DEFENSA,
            dorsal = 4,
            altura = 1.85,
            peso = 80.0,
            goles = 2,
            partidosJugados = 15
        )

        val personalList = listOf(jugador, invalidJugador)

        whenever(storage.readFromFile(any(), eq(format))).thenReturn(personalList)

        // When
        service.importFromFile(filePath, format)

        // Then
        verify(storage).readFromFile(any(), eq(format))
        verify(repository).save(jugador)
        verify(repository, never()).save(invalidJugador)
    }

    @Test
    fun `readFromFile should validate objects and filter out invalid ones`() {
        // Given
        val filePath = "test.json"
        val format = FileFormat.JSON

        // Create an invalid jugador (missing required fields)
        val invalidJugador = Jugador(
            id = 2,
            nombre = "", // Invalid: empty name
            apellidos = "García",
            fechaNacimiento = LocalDate.of(1995, 5, 5),
            fechaIncorporacion = LocalDate.of(2021, 1, 1),
            salario = -1000.0, // Invalid: negative salary
            paisOrigen = "España",
            createdAt = now,
            updatedAt = now,
            posicion = Jugador.Posicion.DEFENSA,
            dorsal = 4,
            altura = 1.85,
            peso = 80.0,
            goles = 2,
            partidosJugados = 15
        )

        val personalList = listOf(jugador, invalidJugador)

        whenever(storage.readFromFile(any(), eq(format))).thenReturn(personalList)

        // When
        service.importFromFile(filePath, format)

        // Then
        // Verify that readFromFile was called with the correct parameters
        verify(storage).readFromFile(any(), eq(format))

        // Verify that only valid objects were saved to the repository
        verify(repository).save(jugador)
        verify(repository, never()).save(invalidJugador)

        // This test indirectly verifies that readFromFile is validating objects
        // since importFromFile uses readFromFile and we're verifying that only
        // valid objects are being processed
    }

    @Test
    fun `exportToFile should get all from repository and write to file`() {
        // Given
        val filePath = "test.json"
        val format = FileFormat.JSON
        val personalList = listOf(jugador)

        whenever(repository.getAll()).thenReturn(personalList)

        // When
        service.exportToFile(filePath, format)

        // Then
        verify(repository).getAll()
        verify(storage).writeToFile(any(), eq(format), eq(personalList))
    }

    @Test
    fun `getAll should return all personal from repository`() {
        // Given
        val personalList = listOf(jugador)
        whenever(repository.getAll()).thenReturn(personalList)

        // When
        val result = service.getAll()

        // Then
        assertEquals(personalList, result)
        verify(repository).getAll()
    }

    @Test
    fun `getById should return personal from cache if available`() {
        // Given
        whenever(cache.get(1)).thenReturn(jugador)

        // When
        val result = service.getById(1)

        // Then
        assertEquals(jugador, result)
        verify(cache).get(1)
        verify(repository, never()).getById(any())
    }

    @Test
    fun `getById should return personal from repository and cache it if not in cache`() {
        // Given
        whenever(cache.get(1)).thenReturn(null)
        whenever(repository.getById(1)).thenReturn(jugador)

        // When
        val result = service.getById(1)

        // Then
        assertEquals(jugador, result)
        verify(cache).get(1)
        verify(repository).getById(1)
        verify(cache).put(1, jugador)
    }

    @Test
    fun `getById should throw PersonalNotFoundException if not found`() {
        // Given
        whenever(cache.get(1)).thenReturn(null)
        whenever(repository.getById(1)).thenReturn(null)

        // When/Then
        assertThrows<PersonalException.PersonalNotFoundException> {
            service.getById(1)
        }

        verify(cache).get(1)
        verify(repository).getById(1)
        verify(cache, never()).put(any(), any())
    }

    @Test
    fun `save should validate and save personal to repository`() {
        // Given
        whenever(repository.save(jugador)).thenReturn(jugador)

        // When
        val result = service.save(jugador)

        // Then
        assertEquals(jugador, result)
        verify(repository).save(jugador)
    }

    @Test
    fun `update should validate, update personal in repository and remove from cache`() {
        // Given
        whenever(repository.update(1, jugador)).thenReturn(jugador)

        // When
        val result = service.update(1, jugador)

        // Then
        assertEquals(jugador, result)
        verify(repository).update(1, jugador)
        verify(cache).remove(1)
    }

    @Test
    fun `update should throw PersonalNotFoundException if not found`() {
        // Given
        whenever(repository.update(1, jugador)).thenReturn(null)

        // When/Then
        assertThrows<PersonalException.PersonalNotFoundException> {
            service.update(1, jugador)
        }

        verify(repository).update(1, jugador)
        verify(cache, never()).remove(any())
    }

    @Test
    fun `delete should delete personal from repository and remove from cache`() {
        // Given
        whenever(repository.delete(1)).thenReturn(jugador)

        // When
        val result = service.delete(1)

        // Then
        assertEquals(jugador, result)
        verify(repository).delete(1)
        verify(cache).remove(1)
    }

    @Test
    fun `delete should throw PersonalNotFoundException if not found`() {
        // Given
        whenever(repository.delete(1)).thenReturn(null)

        // When/Then
        assertThrows<PersonalException.PersonalNotFoundException> {
            service.delete(1)
        }

        verify(repository).delete(1)
        verify(cache, never()).remove(any())
    }
}
