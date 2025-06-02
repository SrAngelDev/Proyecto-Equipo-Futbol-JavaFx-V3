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

class PersonalStorageXmlTest {

    private lateinit var storage: PersonalStorageXml
    private lateinit var testFile: File
    private lateinit var nonExistentFile: File
    private lateinit var invalidFile: File
    private lateinit var emptyFile: File
    private lateinit var nonXmlFile: File
    
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
        storage = PersonalStorageXml()
        
        // Create test directory if it doesn't exist
        val testDir = File("test-files")
        if (!testDir.exists()) {
            testDir.mkdir()
        }
        
        // Set up test files
        testFile = File(testDir, "test-personal.xml")
        nonExistentFile = File(testDir, "non-existent.xml")
        invalidFile = File(testDir, "invalid.xml")
        emptyFile = File(testDir, "empty.xml")
        nonXmlFile = File(testDir, "test.txt")
        
        // Create valid XML file with test data
        val validXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <equipo>
                <personal>
                    <id>1</id>
                    <tipo>Jugador</tipo>
                    <nombre>Juan</nombre>
                    <apellidos>Pérez</apellidos>
                    <fechaNacimiento>1990-01-01</fechaNacimiento>
                    <fechaIncorporacion>2020-01-01</fechaIncorporacion>
                    <salario>50000.0</salario>
                    <pais>España</pais>
                    <posicion>DELANTERO</posicion>
                    <dorsal>9</dorsal>
                    <altura>1.8</altura>
                    <peso>75.0</peso>
                    <goles>10</goles>
                    <partidosJugados>20</partidosJugados>
                </personal>
                <personal>
                    <id>2</id>
                    <tipo>Entrenador</tipo>
                    <nombre>Carlos</nombre>
                    <apellidos>Gómez</apellidos>
                    <fechaNacimiento>1980-05-15</fechaNacimiento>
                    <fechaIncorporacion>2018-07-01</fechaIncorporacion>
                    <salario>70000.0</salario>
                    <pais>España</pais>
                    <especialidad>ENTRENADOR_PRINCIPAL</especialidad>
                </personal>
            </equipo>
        """.trimIndent()
        
        testFile.writeText(validXml)
        
        // Create invalid XML file
        invalidFile.writeText("<This is not valid XML>")
        
        // Create empty XML file
        emptyFile.writeText("")
        
        // Create non-XML file
        nonXmlFile.writeText("This is not an XML file")
        
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
        nonXmlFile.delete()
        
        // Remove test directory if empty
        val testDir = File("test-files")
        if (testDir.exists() && testDir.listFiles()?.isEmpty() == true) {
            testDir.delete()
        }
    }
    
    @Test
    fun `readFromFile should read personal from valid XML file`() {
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
    fun `readFromFile should throw exception for invalid XML`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.readFromFile(invalidFile)
        }
    }
    
    @Test
    fun `writeToFile should write personal to file`() {
        // Given
        val tempFile = File("test-files", "write-test.xml")
        try {
            // When
            storage.writeToFile(tempFile, personalList)
            
            // Then
            assertTrue(tempFile.exists())
            assertTrue(tempFile.length() > 0)
            
            // Verify file content
            val content = tempFile.readText()
            assertTrue(content.contains("<equipo>"))
            assertTrue(content.contains("<personal>"))
            assertTrue(content.contains("<nombre>Juan</nombre>"))
            assertTrue(content.contains("<nombre>Carlos</nombre>"))
        } finally {
            tempFile.delete()
        }
    }
    
    @Test
    fun `writeToFile and readFromFile should work together`() {
        // Given
        val tempFile = File("test-files", "write-read-test.xml")
        try {
            // When
            storage.writeToFile(tempFile, personalList)
            val result = storage.readFromFile(tempFile)
            
            // Then
            assertEquals(2, result.size)
            
            // Verify jugador
            val resultJugador = result.find { it is Jugador } as? Jugador
            assertTrue(resultJugador != null)
            assertEquals(jugador.id, resultJugador?.id)
            assertEquals(jugador.nombre, resultJugador?.nombre)
            assertEquals(jugador.apellidos, resultJugador?.apellidos)
            
            // Verify entrenador
            val resultEntrenador = result.find { it is Entrenador } as? Entrenador
            assertTrue(resultEntrenador != null)
            assertEquals(entrenador.id, resultEntrenador?.id)
            assertEquals(entrenador.nombre, resultEntrenador?.nombre)
            assertEquals(entrenador.apellidos, resultEntrenador?.apellidos)
        } finally {
            tempFile.delete()
        }
    }
    
    @Test
    fun `writeToFile should create parent directories if they don't exist`() {
        // Given
        val nestedDir = File("test-files/nested/dir")
        val nestedFile = File(nestedDir, "nested-test.xml")
        
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
    fun `writeToFile should throw exception for non-XML file`() {
        // When/Then
        assertThrows<PersonalException.PersonalStorageException> {
            storage.writeToFile(nonXmlFile, personalList)
        }
    }
}