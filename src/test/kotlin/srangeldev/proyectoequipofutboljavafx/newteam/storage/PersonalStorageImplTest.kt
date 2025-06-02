package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class PersonalStorageImplTest {

    private lateinit var storageJson: PersonalStorageFile
    private lateinit var storageCsv: PersonalStorageFile
    private lateinit var storageXml: PersonalStorageFile
    private lateinit var storage: PersonalStorageImpl

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
    private val personalList = listOf(jugador)

    @BeforeEach
    fun setUp() {
        storageJson = mock()
        storageCsv = mock()
        storageXml = mock()
        storage = PersonalStorageImpl(storageJson, storageCsv, storageXml)
    }

    @Test
    fun `readFromFile should use JSON storage for JSON format`() {
        // Given
        val file = File("test.json")
        whenever(storageJson.readFromFile(file)).thenReturn(personalList)

        // When
        val result = storage.readFromFile(file, FileFormat.JSON)

        // Then
        assertEquals(personalList, result)
        verify(storageJson).readFromFile(file)
        verify(storageCsv, never()).readFromFile(any())
        verify(storageXml, never()).readFromFile(any())
    }

    @Test
    fun `readFromFile should use CSV storage for CSV format`() {
        // Given
        val file = File("test.csv")
        whenever(storageCsv.readFromFile(file)).thenReturn(personalList)

        // When
        val result = storage.readFromFile(file, FileFormat.CSV)

        // Then
        assertEquals(personalList, result)
        verify(storageCsv).readFromFile(file)
        verify(storageJson, never()).readFromFile(any())
        verify(storageXml, never()).readFromFile(any())
    }

    @Test
    fun `readFromFile should use XML storage for XML format`() {
        // Given
        val file = File("test.xml")
        whenever(storageXml.readFromFile(file)).thenReturn(personalList)

        // When
        val result = storage.readFromFile(file, FileFormat.XML)

        // Then
        assertEquals(personalList, result)
        verify(storageXml).readFromFile(file)
        verify(storageJson, never()).readFromFile(any())
        verify(storageCsv, never()).readFromFile(any())
    }

    @Test
    fun `readFromFile should use JSON storage for DEFAULT format with json extension`() {
        // Given
        val file = File("test.json")
        whenever(storageJson.readFromFile(file)).thenReturn(personalList)

        // When
        val result = storage.readFromFile(file, FileFormat.DEFAULT)

        // Then
        assertEquals(personalList, result)
        verify(storageJson).readFromFile(file)
        verify(storageCsv, never()).readFromFile(any())
        verify(storageXml, never()).readFromFile(any())
    }

    @Test
    fun `readFromFile should use CSV storage for DEFAULT format with csv extension`() {
        // Given
        val file = File("test.csv")
        whenever(storageCsv.readFromFile(file)).thenReturn(personalList)

        // When
        val result = storage.readFromFile(file, FileFormat.DEFAULT)

        // Then
        assertEquals(personalList, result)
        verify(storageCsv).readFromFile(file)
        verify(storageJson, never()).readFromFile(any())
        verify(storageXml, never()).readFromFile(any())
    }

    @Test
    fun `readFromFile should use XML storage for DEFAULT format with xml extension`() {
        // Given
        val file = File("test.xml")
        whenever(storageXml.readFromFile(file)).thenReturn(personalList)

        // When
        val result = storage.readFromFile(file, FileFormat.DEFAULT)

        // Then
        assertEquals(personalList, result)
        verify(storageXml).readFromFile(file)
        verify(storageJson, never()).readFromFile(any())
        verify(storageCsv, never()).readFromFile(any())
    }

    @Test
    fun `readFromFile should use JSON storage for DEFAULT format with unknown extension`() {
        // Given
        val file = File("test.unknown")
        whenever(storageJson.readFromFile(file)).thenReturn(personalList)

        // When
        val result = storage.readFromFile(file, FileFormat.DEFAULT)

        // Then
        assertEquals(personalList, result)
        verify(storageJson).readFromFile(file)
        verify(storageCsv, never()).readFromFile(any())
        verify(storageXml, never()).readFromFile(any())
    }

    @Test
    fun `writeToFile should use JSON storage for JSON format`() {
        // Given
        val file = File("test.json")

        // When
        storage.writeToFile(file, FileFormat.JSON, personalList)

        // Then
        verify(storageJson).writeToFile(file, personalList)
        verify(storageCsv, never()).writeToFile(any(), any())
        verify(storageXml, never()).writeToFile(any(), any())
    }

    @Test
    fun `writeToFile should use CSV storage for CSV format`() {
        // Given
        val file = File("test.csv")

        // When
        storage.writeToFile(file, FileFormat.CSV, personalList)

        // Then
        verify(storageCsv).writeToFile(file, personalList)
        verify(storageJson, never()).writeToFile(any(), any())
        verify(storageXml, never()).writeToFile(any(), any())
    }

    @Test
    fun `writeToFile should use XML storage for XML format`() {
        // Given
        val file = File("test.xml")

        // When
        storage.writeToFile(file, FileFormat.XML, personalList)

        // Then
        verify(storageXml).writeToFile(file, personalList)
        verify(storageJson, never()).writeToFile(any(), any())
        verify(storageCsv, never()).writeToFile(any(), any())
    }

    @Test
    fun `writeToFile should use JSON storage for DEFAULT format`() {
        // Given
        val file = File("test.txt")

        // When
        storage.writeToFile(file, FileFormat.DEFAULT, personalList)

        // Then
        verify(storageJson).writeToFile(file, personalList)
        verify(storageCsv, never()).writeToFile(any(), any())
        verify(storageXml, never()).writeToFile(any(), any())
    }
}