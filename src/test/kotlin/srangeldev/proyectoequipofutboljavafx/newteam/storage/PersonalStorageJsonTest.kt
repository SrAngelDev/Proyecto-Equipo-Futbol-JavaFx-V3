package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertThrows
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersonalStorageJsonTest {

    private lateinit var storage: PersonalStorageJson
    private lateinit var testFile: File
    private lateinit var nonExistentFile: File
    private lateinit var invalidFile: File
    private lateinit var emptyFile: File
    private lateinit var nonJsonFile: File
    
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
    
    private val entrenador = Entrenador(
        id = 2,
        nombre = "Carlos",
        apellidos = "Gómez",
        fechaNacimiento = LocalDate.of(1980, 5, 15),
        fechaIncorporacion = LocalDate.of(2018, 7, 1),
        salario = 70000.0,
        paisOrigen = "España",
        createdAt = now,
        updatedAt = now,
        especializacion = Entrenador.Especializacion.ENTRENADOR_PRINCIPAL
    )
    
    private val personalList = listOf(jugador, entrenador)
    
    @BeforeEach
    fun setUp() {
        storage = PersonalStorageJson()
        
        // Create test directory if it doesn't exist
        val testDir = File("test-files")
        if (!testDir.exists()) {
            testDir.mkdir()
        }
        
        // Set up test files
        testFile = File(testDir, "test-personal.json")
        nonExistentFile = File(testDir, "non-existent.json")
        invalidFile = File(testDir, "invalid.json")
        emptyFile = File(testDir, "empty.json")
        nonJsonFile = File(testDir, "test.txt")
        
        // Create valid JSON file with test data
        val validJson = """
            [
                {
                    "id": 1,
                    "nombre": "Juan",
                    "apellidos": "Pérez",
                    "fecha_nacimiento": "1990-01-01",
                    "fecha_incorporacion": "2020-01-01",
                    "salario": 50000.0,
                    "pais": "España",
                    "rol": "Jugador",
                    "posicion": "DELANTERO",
                    "dorsal": 9,
                    "altura": 1.8,
                    "peso": 75.0,
                    "goles": 10,
                    "partidos_jugados": 20
                },
                {
                    "id": 2,
                    "nombre": "Carlos",
                    "apellidos": "Gómez",
                    "fecha_nacimiento": "1980-05-15",
                    "fecha_incorporacion": "2018-07-01",
                    "salario": 70000.0,
                    "pais": "España",
                    "rol": "Entrenador",
                    "especialidad": "ENTRENADOR_PRINCIPAL"
                }
            ]
        """.trimIndent()
        
        testFile.writeText(validJson)
        
        // Create invalid JSON file
        invalidFile.writeText("{ This is not valid JSON }")
        
        // Create empty JSON file
        emptyFile.writeText("")
        
        // Create non-JSON file
        nonJsonFile.writeText("This is not a JSON file")
        
        // Ensure non-existent file doesn't exist
        if (nonExistentFile.exists()) {
            nonExistentFile.delete()
        }
    }
    
    @AfterEach
    fun tearDown() {
        // Clean up test files
        testFile.delete()
        if (nonExistentFile.exists()) nonExistentFile.delete()
        invalidFile.delete()
        emptyFile.delete()
        nonJsonFile.delete()
        
        // Remove test directory if empty
        val testDir = File("test-files")
        if (testDir.exists() && testDir.listFiles()?.isEmpty() == true) {
            testDir.delete()
        }
    }
    
    @Test
    fun `readFromFile should read personal from valid JSON file`() {
        // When
        val result = storage.readFromFile(testFile)
        
        // Then
        assertEquals(2, result.size)
        
        val resultJugador = result.find { it is Jugador } as Jugador
        assertEquals(1, resultJugador.id)
        assertEquals("Juan", resultJugador.nombre)
        assertEquals("Pérez", resultJugador.apellidos)
        assertEquals(LocalDate.of(1990, 1, 1), resultJugador.fechaNacimiento)
        assertEquals(LocalDate.of(2020, 1, 1), resultJugador.fechaIncorporacion)
        assertEquals(50000.0, resultJugador.salario)
        assertEquals("España", resultJugador.paisOrigen)
        assertEquals(Jugador.Posicion.DELANTERO, resultJugador.posicion)
        assertEquals(9, resultJugador.dorsal)
        assertEquals(1.8, resultJugador.altura)
        assertEquals(75.0, resultJugador.peso)
        assertEquals(10, resultJugador.goles)
        assertEquals(20, resultJugador.partidosJugados)
        
        val resultEntrenador = result.find { it is Entrenador } as Entrenador
        assertEquals(2, resultEntrenador.id)
        assertEquals("Carlos", resultEntrenador.nombre)
        assertEquals("Gómez", resultEntrenador.apellidos)
        assertEquals(LocalDate.of(1980, 5, 15), resultEntrenador.fechaNacimiento)
        assertEquals(LocalDate.of(2018, 7, 1), resultEntrenador.fechaIncorporacion)
        assertEquals(70000.0, resultEntrenador.salario)
        assertEquals("España", resultEntrenador.paisOrigen)
        assertEquals(Entrenador.Especializacion.ENTRENADOR_PRINCIPAL, resultEntrenador.especializacion)
    }
    
    @Test
    fun `readFromFile should create empty file when file doesn't exist`() {
        // When
        val result = storage.readFromFile(nonExistentFile)
        
        // Then
        assertTrue(nonExistentFile.exists())
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `readFromFile should throw exception for empty file`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(emptyFile)
        }
    }
    
    @Test
    fun `readFromFile should throw exception for invalid JSON`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(invalidFile)
        }
    }
    
    @Test
    fun `writeToFile should write personal to file`() {
        // When
        storage.writeToFile(testFile, personalList)
        
        // Then
        val result = storage.readFromFile(testFile)
        assertEquals(2, result.size)
        
        val resultJugador = result.find { it is Jugador } as Jugador
        assertEquals(jugador.id, resultJugador.id)
        assertEquals(jugador.nombre, resultJugador.nombre)
        assertEquals(jugador.apellidos, resultJugador.apellidos)
        
        val resultEntrenador = result.find { it is Entrenador } as Entrenador
        assertEquals(entrenador.id, resultEntrenador.id)
        assertEquals(entrenador.nombre, resultEntrenador.nombre)
        assertEquals(entrenador.apellidos, resultEntrenador.apellidos)
    }
    
    @Test
    fun `writeToFile should create parent directories if they don't exist`() {
        // Given
        val nestedDir = File("test-files/nested/dir")
        val nestedFile = File(nestedDir, "nested-test.json")
        
        try {
            // When
            storage.writeToFile(nestedFile, personalList)
            
            // Then
            assertTrue(nestedDir.exists())
            assertTrue(nestedFile.exists())
            
            val result = storage.readFromFile(nestedFile)
            assertEquals(2, result.size)
        } finally {
            // Clean up
            nestedFile.delete()
            nestedDir.deleteRecursively()
        }
    }
    
    @Test
    fun `writeToFile should throw exception for non-JSON file`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.writeToFile(nonJsonFile, personalList)
        }
    }
}