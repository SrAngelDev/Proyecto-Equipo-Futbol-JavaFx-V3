package srangeldev.proyectoequipofutboljavafx.newteam.session

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import java.time.LocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionTest {
    @BeforeTest
    fun setup() {
        Session.clearCredentials()
    }

    private val adminUser = User(
        id = 1,
        username = "admin",
        password = "password123",
        role = User.Role.ADMIN,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    private val regularUser = User(
        id = 2,
        username = "user",
        password = "password456",
        role = User.Role.USER,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
    
    @BeforeEach
    fun setUp() {
        // Ensure session is clean before each test
        Session.logout()
    }
    
    @Test
    fun `setCurrentUser should set the current user`() {
        // When
        Session.setCurrentUser(adminUser)
        
        // Then
        val currentUser = Session.getCurrentUser()
        assertEquals(adminUser, currentUser)
    }
    
    @Test
    fun `getCurrentUser should return null when no user is set`() {
        // When
        val currentUser = Session.getCurrentUser()
        
        // Then
        assertNull(currentUser)
    }
    
    @Test
    fun `getCurrentUser should return the current user when set`() {
        // Given
        Session.setCurrentUser(regularUser)
        
        // When
        val currentUser = Session.getCurrentUser()
        
        // Then
        assertEquals(regularUser, currentUser)
    }
    
    @Test
    fun `isAdmin should return true for admin user`() {
        // Given
        Session.setCurrentUser(adminUser)
        
        // When
        val isAdmin = Session.isAdmin()
        
        // Then
        assertTrue(isAdmin)
    }
    
    @Test
    fun `isAdmin should return false for regular user`() {
        // Given
        Session.setCurrentUser(regularUser)
        
        // When
        val isAdmin = Session.isAdmin()
        
        // Then
        assertFalse(isAdmin)
    }
    
    @Test
    fun `isAdmin should return false when no user is set`() {
        // When
        val isAdmin = Session.isAdmin()
        
        // Then
        assertFalse(isAdmin)
    }
    
    @Test
    fun `logout should clear the current user`() {
        // Given
        Session.setCurrentUser(adminUser)
        
        // When
        Session.logout()
        
        // Then
        assertNull(Session.getCurrentUser())
    }
    
    @Test
    fun `session should maintain state between method calls`() {
        // Given
        Session.setCurrentUser(adminUser)
        
        // When
        val currentUser = Session.getCurrentUser()
        val isAdmin = Session.isAdmin()
        
        // Then
        assertEquals(adminUser, currentUser)
        assertTrue(isAdmin)
    }
    
    @Test
    fun `session should update when user is changed`() {
        // Given
        Session.setCurrentUser(adminUser)
        
        // When
        Session.setCurrentUser(regularUser)
        
        // Then
        assertEquals(regularUser, Session.getCurrentUser())
        assertFalse(Session.isAdmin())
    }

    @Test
    fun `setCurrentUser should overwrite previously set user`() {
        // Given
        Session.setCurrentUser(adminUser)

        // When
        Session.setCurrentUser(regularUser)

        // Then
        val currentUser = Session.getCurrentUser()
        assertEquals(regularUser, currentUser)
    }

    @Test
    fun `setCurrentUser should handle null and clear current user`() {
        // Given
        Session.setCurrentUser(adminUser)

        // When
        Session.setCurrentUser(null)

        // Then
        val currentUser = Session.getCurrentUser()
        assertNull(currentUser)
    }

    @Test
    fun `saveCredentials should store username and password`() {
        // When
        Session.saveCredentials("testUser", "testPass")

        // Then
        assertEquals("testUser", Session.getRememberedUsername())
        assertEquals("testPass", Session.getRememberedPassword())
        assertTrue(Session.hasRememberedCredentials())
    }

    @Test
    fun `getRememberedUsername should return null when no credentials saved`() {
        // When
        val username = Session.getRememberedUsername()

        // Then
        assertNull(username)
    }

    @Test
    fun `getRememberedPassword should return null when no credentials saved`() {
        // When
        val password = Session.getRememberedPassword()

        // Then
        assertNull(password)
    }

    @Test
    fun `hasRememberedCredentials should return false when no credentials saved`() {
        // When
        val hasCredentials = Session.hasRememberedCredentials()

        // Then
        assertFalse(hasCredentials)
    }

    @Test
    fun `clearCredentials should remove saved credentials`() {
        // Given
        Session.saveCredentials("testUser", "testPass")

        // When
        Session.clearCredentials()

        // Then
        assertNull(Session.getRememberedUsername())
        assertNull(Session.getRememberedPassword())
        assertFalse(Session.hasRememberedCredentials())
    }
}