package srangeldev.proyectoequipofutboljavafx.newteam.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZipFileTest {

    private val testDir = "test-zip-dir"
    private val extractDir = "test-extract-dir"
    private val zipFilePath = "test-file.zip"
    private val testFileName1 = "test-file1.txt"
    private val testFileName2 = "test-file2.txt"
    private val testContent1 = "This is test content 1"
    private val testContent2 = "This is test content 2"
    
    @BeforeEach
    fun setUp() {
        // Create test directory and files
        val dir = File(testDir)
        dir.mkdirs()
        
        File(dir, testFileName1).writeText(testContent1)
        File(dir, testFileName2).writeText(testContent2)
        
        // Create extract directory
        File(extractDir).mkdirs()
    }
    
    @AfterEach
    fun tearDown() {
        // Clean up test files and directories
        File(testDir).deleteRecursively()
        File(extractDir).deleteRecursively()
        File(zipFilePath).delete()
    }
    
    @Test
    fun `createZipFile should create a zip file from a directory`() {
        // When
        ZipFile.createZipFile(testDir, zipFilePath)
        
        // Then
        val zipFile = File(zipFilePath)
        assertTrue(zipFile.exists())
        assertTrue(zipFile.length() > 0)
    }
    
    @Test
    fun `createZipFile should throw IllegalArgumentException for non-existent directory`() {
        // Given
        val nonExistentDir = "non-existent-dir"
        
        // When/Then
        assertThrows<IllegalArgumentException> {
            ZipFile.createZipFile(nonExistentDir, zipFilePath)
        }
    }
    
    @Test
    fun `extractFileToPath should extract files from a zip file`() {
        // Given
        ZipFile.createZipFile(testDir, zipFilePath)
        
        // When
        ZipFile.extractFileToPath(zipFilePath, extractDir)
        
        // Then
        val extractedFile1 = File(extractDir, testFileName1)
        val extractedFile2 = File(extractDir, testFileName2)
        
        assertTrue(extractedFile1.exists())
        assertTrue(extractedFile2.exists())
        assertEquals(testContent1, extractedFile1.readText())
        assertEquals(testContent2, extractedFile2.readText())
    }
    
    @Test
    fun `extractFileToPath should create destination directory if it doesn't exist`() {
        // Given
        ZipFile.createZipFile(testDir, zipFilePath)
        val newExtractDir = "new-extract-dir"
        
        try {
            // When
            ZipFile.extractFileToPath(zipFilePath, newExtractDir)
            
            // Then
            val extractDir = File(newExtractDir)
            assertTrue(extractDir.exists())
            assertTrue(extractDir.isDirectory)
            
            val extractedFile1 = File(newExtractDir, testFileName1)
            val extractedFile2 = File(newExtractDir, testFileName2)
            
            assertTrue(extractedFile1.exists())
            assertTrue(extractedFile2.exists())
            assertEquals(testContent1, extractedFile1.readText())
            assertEquals(testContent2, extractedFile2.readText())
        } finally {
            // Clean up
            File(newExtractDir).deleteRecursively()
        }
    }
    
    @Test
    fun `createZipFile and extractFileToPath should work with nested directories`() {
        // Given
        val nestedDir = File(testDir, "nested")
        nestedDir.mkdirs()
        val nestedFileName = "nested-file.txt"
        val nestedContent = "This is nested content"
        File(nestedDir, nestedFileName).writeText(nestedContent)
        
        // When
        ZipFile.createZipFile(testDir, zipFilePath)
        ZipFile.extractFileToPath(zipFilePath, extractDir)
        
        // Then
        val extractedNestedDir = File(extractDir, "nested")
        val extractedNestedFile = File(extractedNestedDir, nestedFileName)
        
        assertTrue(extractedNestedDir.exists())
        assertTrue(extractedNestedDir.isDirectory)
        assertTrue(extractedNestedFile.exists())
        assertEquals(nestedContent, extractedNestedFile.readText())
    }
}