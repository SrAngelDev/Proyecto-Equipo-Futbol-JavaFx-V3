package srangeldev.proyectoequipofutboljavafx.newteam.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class BCryptUtilTest {

    @Test
    fun `hashPassword should generate a hash for a password`() {
        // Given
        val password = "password123"
        
        // When
        val hash = BCryptUtil.hashPassword(password)
        
        // Then
        assertNotEquals(password, hash)
        assertTrue(hash.isNotEmpty())
    }
    
    @Test
    fun `hashPassword should generate the same hash for the same password`() {
        // Given
        val password = "password123"
        
        // When
        val hash1 = BCryptUtil.hashPassword(password)
        val hash2 = BCryptUtil.hashPassword(password)
        
        // Then
        assertEquals(hash1, hash2)
    }
    
    @Test
    fun `hashPassword should generate different hashes for different passwords`() {
        // Given
        val password1 = "password123"
        val password2 = "password456"
        
        // When
        val hash1 = BCryptUtil.hashPassword(password1)
        val hash2 = BCryptUtil.hashPassword(password2)
        
        // Then
        assertNotEquals(hash1, hash2)
    }
    
    @Test
    fun `checkPassword should return true for matching password and hash`() {
        // Given
        val password = "password123"
        val hash = BCryptUtil.hashPassword(password)
        
        // When
        val result = BCryptUtil.checkPassword(password, hash)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `checkPassword should return false for non-matching password and hash`() {
        // Given
        val password1 = "password123"
        val password2 = "password456"
        val hash = BCryptUtil.hashPassword(password1)
        
        // When
        val result = BCryptUtil.checkPassword(password2, hash)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `checkPassword should return false for empty password`() {
        // Given
        val password = "password123"
        val emptyPassword = ""
        val hash = BCryptUtil.hashPassword(password)
        
        // When
        val result = BCryptUtil.checkPassword(emptyPassword, hash)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `hashPassword should handle empty password`() {
        // Given
        val emptyPassword = ""
        
        // When
        val hash = BCryptUtil.hashPassword(emptyPassword)
        
        // Then
        assertTrue(hash.isNotEmpty())
        assertTrue(BCryptUtil.checkPassword(emptyPassword, hash))
    }
    
    @Test
    fun `hashPassword should handle special characters`() {
        // Given
        val passwordWithSpecialChars = "p@ssw0rd!#$%^&*()"
        
        // When
        val hash = BCryptUtil.hashPassword(passwordWithSpecialChars)
        
        // Then
        assertTrue(hash.isNotEmpty())
        assertTrue(BCryptUtil.checkPassword(passwordWithSpecialChars, hash))
    }
}