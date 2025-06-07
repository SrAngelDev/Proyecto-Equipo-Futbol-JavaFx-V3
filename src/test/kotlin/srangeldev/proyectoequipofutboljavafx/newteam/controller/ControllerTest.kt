package srangeldev.proyectoequipofutboljavafx.newteam.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.service.PersonalServiceImpl
import srangeldev.proyectoequipofutboljavafx.newteam.storage.FileFormat
import java.io.File

class ControllerTest {

    /**
     * Test class for the Controller's `cargarDatos` method.
     * `cargarDatos` imports data from files of different formats (CSV, XML, JSON)
     * and throws an exception in case of unsupported formats or errors during the process.
     */

    @Test
    fun `test cargarDatos with valid CSV format`() {
        val mockService = mock<PersonalServiceImpl>()
        val controller = spy(Controller())
        doReturn("/path/to/personal.csv").whenever(controller).constructFilePath(eq("CSV"))
        controller.service = mockService

        controller.cargarDatos("CSV")

        verify(mockService).importFromFile(eq("/path/to/personal.csv"), eq(FileFormat.CSV))
    }

    @Test
    fun `test cargarDatos with valid XML format`() {
        val mockService = mock<PersonalServiceImpl>()
        val controller = spy(Controller())
        doReturn("/path/to/personal.xml").whenever(controller).constructFilePath(eq("XML"))
        controller.service = mockService

        controller.cargarDatos("XML")

        verify(mockService).importFromFile(eq("/path/to/personal.xml"), eq(FileFormat.XML))
    }

    @Test
    fun `test cargarDatos with valid JSON format`() {
        val mockService = mock<PersonalServiceImpl>()
        val controller = spy(Controller())
        doReturn("/path/to/personal.json").whenever(controller).constructFilePath(eq("JSON"))
        controller.service = mockService

        controller.cargarDatos("JSON")

        verify(mockService).importFromFile(eq("/path/to/personal.json"), eq(FileFormat.JSON))
    }

    @Test
    fun `test cargarDatos with unsupported format`() {
        val controller = Controller()

        val exception = assertThrows<IllegalArgumentException> {
            controller.cargarDatos("YAML")
        }
        assertEquals("Formato no soportado: YAML", exception.message)
    }

    @Test
    fun `test cargarDatos when service throws exception`() {
        val mockService = mock<PersonalServiceImpl>()
        val controller = spy(Controller())
        doReturn("/path/to/personal.csv").whenever(controller).constructFilePath(eq("CSV"))
        controller.service = mockService

        whenever(mockService.importFromFile(eq("/path/to/personal.csv"), eq(FileFormat.CSV)))
            .thenThrow(RuntimeException("Error importing data"))

        val exception = assertThrows<RuntimeException> {
            controller.cargarDatos("CSV")
        }
        assertEquals("Error importing data", exception.message)
    }

    @Test
    fun `test cargarDatos with null or empty format`() {
        val controller = Controller()

        var exception = assertThrows<IllegalArgumentException> {
            controller.cargarDatos("")
        }
        assertEquals("Formato no soportado: ", exception.message)

        exception = assertThrows<IllegalArgumentException> {
            controller.cargarDatos("   ")
        }
        assertEquals("Formato no soportado: ", exception.message)
    }

    @Test
    fun `test cargarDatos with case insensitive formats`() {
        val mockService = mock<PersonalServiceImpl>()
        val controller = spy(Controller())
        doReturn("/path/to/personal.csv").whenever(controller).constructFilePath(eq("CSV"))
        controller.service = mockService

        controller.cargarDatos("csv")

        verify(mockService).importFromFile(eq("/path/to/personal.csv"), eq(FileFormat.CSV))
    }

    @Test
    fun `test cargarDatos logs success message`() {
        val mockService = mock<PersonalServiceImpl>()
        val controller = spy(Controller())
        doReturn("/path/to/personal.json").whenever(controller).constructFilePath(eq("JSON"))
        controller.service = mockService

        controller.cargarDatos("JSON")

        // Depending on the logging library, you would verify proper logging behavior here
        verify(mockService).importFromFile(eq("/path/to/personal.json"), eq(FileFormat.JSON))
    }

    @Test
    fun `test cargarDatos with whitespace in format`() {
        val mockService = mock<PersonalServiceImpl>()
        val controller = spy(Controller())
        doReturn("/path/to/personal.json").whenever(controller).constructFilePath(eq("JSON"))
        controller.service = mockService

        controller.cargarDatos("  json  ")

        verify(mockService).importFromFile(eq("/path/to/personal.json"), eq(FileFormat.JSON))
    }

    @Test
    fun `test getService returns the correct service instance`() {
        val controller = Controller()
        val mockService = mock<PersonalServiceImpl>()
        controller.service = mockService
        val serviceInstance = controller.service

        assertNotNull(serviceInstance, "Service instance should not be null")
        assertTrue(serviceInstance is PersonalServiceImpl, "Service instance should be of type PersonalServiceImpl")
    }

    @Test
    fun `test getService does not return a null value`() {
        val controller = Controller()
        val mockService = mock<PersonalServiceImpl>()
        controller.service = mockService
        assertNotNull(controller.service, "Service should not be null")
    }
}
