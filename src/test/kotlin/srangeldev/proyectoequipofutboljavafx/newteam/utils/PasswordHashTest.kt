package srangeldev.proyectoequipofutboljavafx.newteam.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class PasswordHashTest {

    @Test
    fun `test password hashing for default users`() {
        // Given
        val adminPassword = "admin"
        val userPassword = "user"
        
        // When
        val adminHash = BCryptUtil.hashPassword(adminPassword)
        val userHash = BCryptUtil.hashPassword(userPassword)
        
        // Then
        println("[DEBUG_LOG] Hashed password for 'admin': $adminHash")
        println("[DEBUG_LOG] Hashed password for 'user': $userHash")
        
        // Compare with values in data.sql
        val adminExpectedHash = "hO2XCR1ahYdJ+F9mkBqPj39Gb/YfCVQJ15wNQO5kXo8="
        val userExpectedHash = "n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg="
        
        assertEquals(adminExpectedHash, adminHash, "Admin password hash doesn't match expected value")
        assertEquals(userExpectedHash, userHash, "User password hash doesn't match expected value")
    }
    
    @Test
    fun `test password verification`() {
        // Given
        val password = "user"
        val storedHash = "n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg="
        
        // When
        val result = BCryptUtil.checkPassword(password, storedHash)
        
        // Then
        assertTrue(result, "Password verification should succeed")
    }
}