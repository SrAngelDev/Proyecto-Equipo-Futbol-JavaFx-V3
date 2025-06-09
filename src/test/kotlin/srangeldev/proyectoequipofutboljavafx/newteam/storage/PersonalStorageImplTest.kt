package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertFailsWith

class PersonalStorageImplTest {

    @Test
    fun `readFromFile throws exception when file does not exist`() {
        val file = mock(File::class.java).apply {
            `when`(exists()).thenReturn(false)
        }

        val storage = PersonalStorageImpl()

        val exception = assertFailsWith<IllegalArgumentException> {
            storage.readFromFile(file, FileFormat.JSON)
        }

        assertEquals("El archivo no existe: null", exception.message)
    }

    @Test
    fun `readFromFile reads JSON file successfully`() {
        val file = mock(File::class.java).apply {
            `when`(exists()).thenReturn(true)
        }
        val mockStorageJson = mock(PersonalStorageFile::class.java)
        val mockData = listOf(createMockPersonal())
        `when`(mockStorageJson.readFromFile(file)).thenReturn(mockData)

        val storage = PersonalStorageImpl(storageJson = mockStorageJson)

        val result = storage.readFromFile(file, FileFormat.JSON)

        assertEquals(mockData, result)
        verify(mockStorageJson).readFromFile(file)
    }

    @Test
    fun `readFromFile reads CSV file successfully`() {
        val file = mock(File::class.java).apply {
            `when`(exists()).thenReturn(true)
        }
        val mockStorageCsv = mock(PersonalStorageFile::class.java)
        val mockData = listOf(createMockPersonal())
        `when`(mockStorageCsv.readFromFile(file)).thenReturn(mockData)

        val storage = PersonalStorageImpl(storageCsv = mockStorageCsv)

        val result = storage.readFromFile(file, FileFormat.CSV)

        assertEquals(mockData, result)
        verify(mockStorageCsv).readFromFile(file)
    }

    @Test
    fun `readFromFile reads XML file successfully`() {
        val file = mock(File::class.java).apply {
            `when`(exists()).thenReturn(true)
        }
        val mockStorageXml = mock(PersonalStorageFile::class.java)
        val mockData = listOf(createMockPersonal())
        `when`(mockStorageXml.readFromFile(file)).thenReturn(mockData)

        val storage = PersonalStorageImpl(storageXml = mockStorageXml)

        val result = storage.readFromFile(file, FileFormat.XML)

        assertEquals(mockData, result)
        verify(mockStorageXml).readFromFile(file)
    }

    private fun createMockPersonal(): Personal {
        return object : Personal(
            id = 1,
            nombre = "John",
            apellidos = "Doe",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaIncorporacion = LocalDate.of(2020, 1, 1),
            salario = 50000.0,
            paisOrigen = "USA",
            createdAt = LocalDateTime.of(2020, 1, 1, 0, 0),
            updatedAt = LocalDateTime.of(2021, 1, 1, 0, 0),
            imagenUrl = ""
        ) {}
    }

    @Test
    fun `writeToFile writes JSON file successfully`() {
        val file = mock(File::class.java)
        val mockStorageJson = mock(PersonalStorageFile::class.java)
        val personalList = listOf(createMockPersonal())

        val storage = PersonalStorageImpl(storageJson = mockStorageJson)

        storage.writeToFile(file, FileFormat.JSON, personalList)

        verify(mockStorageJson).writeToFile(file, personalList)
    }

    @Test
    fun `writeToFile writes CSV file successfully`() {
        val file = mock(File::class.java)
        val mockStorageCsv = mock(PersonalStorageFile::class.java)
        val personalList = listOf(createMockPersonal())

        val storage = PersonalStorageImpl(storageCsv = mockStorageCsv)

        storage.writeToFile(file, FileFormat.CSV, personalList)

        verify(mockStorageCsv).writeToFile(file, personalList)
    }

    @Test
    fun `writeToFile writes XML file successfully`() {
        val file = mock(File::class.java)
        val mockStorageXml = mock(PersonalStorageFile::class.java)
        val personalList = listOf(createMockPersonal())

        val storage = PersonalStorageImpl(storageXml = mockStorageXml)

        storage.writeToFile(file, FileFormat.XML, personalList)

        verify(mockStorageXml).writeToFile(file, personalList)
    }

    @Test
    fun `writeToFile processes all file formats correctly`() {
        val fileJson = mock(File::class.java)
        val fileCsv = mock(File::class.java)
        val fileXml = mock(File::class.java)

        val mockStorageJson = mock(PersonalStorageFile::class.java)
        val mockStorageCsv = mock(PersonalStorageFile::class.java)
        val mockStorageXml = mock(PersonalStorageFile::class.java)

        val personalList = listOf(createMockPersonal())

        val storage = PersonalStorageImpl(
            storageJson = mockStorageJson,
            storageCsv = mockStorageCsv,
            storageXml = mockStorageXml
        )

        storage.writeToFile(fileJson, FileFormat.JSON, personalList)
        storage.writeToFile(fileCsv, FileFormat.CSV, personalList)
        storage.writeToFile(fileXml, FileFormat.XML, personalList)

        verify(mockStorageJson).writeToFile(fileJson, personalList)
        verify(mockStorageCsv).writeToFile(fileCsv, personalList)
        verify(mockStorageXml).writeToFile(fileXml, personalList)
    }
}