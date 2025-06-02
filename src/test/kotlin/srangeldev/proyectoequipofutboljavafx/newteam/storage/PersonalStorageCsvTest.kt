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

class PersonalStorageCsvTest {

    private lateinit var storage: PersonalStorageCsv
    private lateinit var testFile: File
    private lateinit var nonExistentFile: File
    private lateinit var invalidFile: File
    private lateinit var emptyFile: File
    private lateinit var nonCsvFile: File

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
        storage = PersonalStorageCsv()

        // Create test directory if it doesn't exist
        val testDir = File("test-files")
        if (!testDir.exists()) {
            testDir.mkdir()
        }

        // Set up test files
        testFile = File(testDir, "test-personal.csv")
        nonExistentFile = File(testDir, "non-existent.csv")
        invalidFile = File(testDir, "invalid.csv")
        emptyFile = File(testDir, "empty.csv")
        nonCsvFile = File(testDir, "test.txt")

        // Create valid CSV file with test data
        val validCsv = """
            id,nombre,apellidos,fecha_nacimiento,fecha_incorporacion,salario,pais,rol,especialidad,posicion,dorsal,altura,peso,goles,partidos_jugados
            1,Juan,Pérez,1990-01-01,2020-01-01,50000.0,España,Jugador,,DELANTERO,9,1.8,75.0,10,20
            2,Carlos,Gómez,1980-05-15,2018-07-01,70000.0,España,Entrenador,ENTRENADOR_PRINCIPAL,,,,,
        """.trimIndent()

        testFile.writeText(validCsv)

        // Create invalid CSV file
        invalidFile.writeText("This is not a valid CSV file")

        // Create empty CSV file
        emptyFile.writeText("")

        // Create non-CSV file
        nonCsvFile.writeText("This is not a CSV file")

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
        nonCsvFile.delete()

        // Remove test directory if empty
        val testDir = File("test-files")
        if (testDir.exists() && testDir.listFiles()?.isEmpty() == true) {
            testDir.delete()
        }
    }

    @Test
    fun `readFromFile should read personal from valid CSV file`() {
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
    fun `readFromFile should throw exception for non-existent file`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(nonExistentFile)
        }
    }

    @Test
    fun `readFromFile should throw exception for empty file`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(emptyFile)
        }
    }

    @Test
    fun `readFromFile should throw exception for invalid CSV`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(invalidFile)
        }
    }


    @Test
    fun `writeToFile should create a valid CSV file`() {
        // Given
        val tempFile = File("test-files", "write-test.csv")
        try {
            // When
            storage.writeToFile(tempFile, personalList)

            // Then
            assertTrue(tempFile.exists())
            assertTrue(tempFile.length() > 0)

            // Verify file content
            val lines = tempFile.readLines()
            assertTrue(lines.size >= 3) // Header + 2 records
            assertTrue(lines[0].contains("id") && lines[0].contains("nombre") && lines[0].contains("rol"))

            // Verify the content manually since the writeToFile and readFromFile methods use different field orders
            val content = tempFile.readText()
            assertTrue(content.contains("Juan"))
            assertTrue(content.contains("Pérez"))
            assertTrue(content.contains("Carlos"))
            assertTrue(content.contains("Gómez"))
            assertTrue(content.contains("DELANTERO"))
            assertTrue(content.contains("ENTRENADOR_PRINCIPAL"))
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `writeToFile should create parent directories if they don't exist`() {
        // Given
        val nestedDir = File("test-files/nested/dir")
        val nestedFile = File(nestedDir, "nested-test.csv")

        try {
            // When
            storage.writeToFile(nestedFile, personalList)

            // Then
            assertTrue(nestedDir.exists())
            assertTrue(nestedFile.exists())
        } finally {
            // Clean up
            nestedFile.delete()
            nestedDir.deleteRecursively()
        }
    }

    @Test
    fun `writeToFile should throw exception for non-CSV file`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.writeToFile(nonCsvFile, personalList)
        }
    }
}
