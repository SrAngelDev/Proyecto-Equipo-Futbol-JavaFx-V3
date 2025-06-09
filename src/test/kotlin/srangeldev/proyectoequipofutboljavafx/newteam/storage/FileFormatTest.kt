package srangeldev.proyectoequipofutboljavafx.newteam.storage

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FileFormatTest {

    @Test
    fun `FileFormat enum should have expected values`() {
        // Then
        assertEquals(3, FileFormat.entries.size)
        assertNotNull(FileFormat.valueOf("JSON"))
        assertNotNull(FileFormat.valueOf("CSV"))
        assertNotNull(FileFormat.valueOf("XML"))
    }
    
    @Test
    fun `FileFormat JSON should be accessible`() {
        // Then
        assertEquals(FileFormat.JSON, FileFormat.valueOf("JSON"))
    }
    
    @Test
    fun `FileFormat CSV should be accessible`() {
        // Then
        assertEquals(FileFormat.CSV, FileFormat.valueOf("CSV"))
    }
    
    @Test
    fun `FileFormat XML should be accessible`() {
        // Then
        assertEquals(FileFormat.XML, FileFormat.valueOf("XML"))
    }
}