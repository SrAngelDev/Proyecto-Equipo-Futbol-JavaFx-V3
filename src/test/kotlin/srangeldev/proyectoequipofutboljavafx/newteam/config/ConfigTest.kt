package srangeldev.proyectoequipofutboljavafx.newteam.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class ConfigTest {
    
    @Test
    fun `test config properties are loaded`() {
        // Access the config properties to trigger lazy loading
        val properties = Config.configProperties
        
        // Verify that the properties are not null
        assertNotNull(properties)
        
        // Verify that the properties have default values if not specified in the properties file
        assertNotNull(properties.dataDir)
        assertNotNull(properties.backupDir)
        assertNotNull(properties.reportsDir)
        assertNotNull(properties.inputFormats)
        assertNotNull(properties.outputFormats)
        assertNotNull(properties.databaseUrl)
        
        // Verify that the database URL has a default value
        assertTrue(properties.databaseUrl.contains("jdbc:sqlite:"))
        
        // Verify that the input and output formats contain expected values
        assertTrue(properties.inputFormats.contains("CSV"))
        assertTrue(properties.outputFormats.contains("JSON"))
    }
    
    @Test
    fun `test directories are created`() {
        // Access the config properties to trigger directory creation
        val properties = Config.configProperties
        
        // Verify that the directories exist
        assertTrue(File(properties.dataDir).exists())
        assertTrue(File(properties.backupDir).exists())
        assertTrue(File(properties.reportsDir).exists())
    }
    
    @Test
    fun `test ConfigProperties data class`() {
        // Create a ConfigProperties instance
        val properties = Config.ConfigProperties(
            dataDir = "data",
            backupDir = "backup",
            reportsDir = "reports",
            inputFormats = "CSV,XML",
            outputFormats = "JSON,XML",
            databaseUrl = "jdbc:sqlite:test.db",
            databaseInitTables = true,
            databaseInitData = false,
            databaseStorageData = "testdata"
        )
        
        // Verify that the properties are set correctly
        assertEquals("data", properties.dataDir)
        assertEquals("backup", properties.backupDir)
        assertEquals("reports", properties.reportsDir)
        assertEquals("CSV,XML", properties.inputFormats)
        assertEquals("JSON,XML", properties.outputFormats)
        assertEquals("jdbc:sqlite:test.db", properties.databaseUrl)
        assertTrue(properties.databaseInitTables)
        assertFalse(properties.databaseInitData)
        assertEquals("testdata", properties.databaseStorageData)
        
        // Test equals and hashCode
        val sameProperties = Config.ConfigProperties(
            dataDir = "data",
            backupDir = "backup",
            reportsDir = "reports",
            inputFormats = "CSV,XML",
            outputFormats = "JSON,XML",
            databaseUrl = "jdbc:sqlite:test.db",
            databaseInitTables = true,
            databaseInitData = false,
            databaseStorageData = "testdata"
        )
        
        assertEquals(properties, sameProperties)
        assertEquals(properties.hashCode(), sameProperties.hashCode())
        
        // Test copy
        val copiedProperties = properties.copy(dataDir = "newdata")
        assertEquals("newdata", copiedProperties.dataDir)
        assertEquals(properties.backupDir, copiedProperties.backupDir)
    }
}