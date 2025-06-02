package srangeldev.proyectoequipofutboljavafx.newteam.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UserTest {

    @Test
    fun `test user creation`() {
        // Arrange
        val id = 1
        val username = "admin"
        val password = "password123"
        val role = User.Role.ADMIN
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()

        // Act
        val user = User(
            id = id,
            username = username,
            password = password,
            role = role,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        // Assert
        assertEquals(id, user.id)
        assertEquals(username, user.username)
        assertEquals(password, user.password)
        assertEquals(role, user.role)
        assertEquals(createdAt, user.createdAt)
        assertEquals(updatedAt, user.updatedAt)
    }

    @Test
    fun `test all role values are valid`() {
        // Assert
        assertNotNull(User.Role.ADMIN)
        assertNotNull(User.Role.USER)
    }

    @Test
    fun `test toString method`() {
        // Arrange
        val user = User(
            id = 1,
            username = "admin",
            password = "password123",
            role = User.Role.ADMIN,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Act
        val result = user.toString()

        // Assert
        assertTrue(result.contains("User"))
        assertTrue(result.contains("id=1"))
        assertTrue(result.contains("username='admin'"))
        // No debería contener la contraseña por seguridad
        assertFalse(result.contains("password='password123'"))
        assertTrue(result.contains("role=ADMIN"))
    }

    @Test
    fun `test user equality`() {
        // Arrange
        val createdAt = LocalDateTime.now()
        val updatedAt = LocalDateTime.now()

        val user1 = User(
            id = 1,
            username = "admin",
            password = "password123",
            role = User.Role.ADMIN,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val user2 = User(
            id = 1,
            username = "admin",
            password = "password123",
            role = User.Role.ADMIN,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val user3 = User(
            id = 2,
            username = "user",
            password = "password456",
            role = User.Role.USER,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        // Assert
        assertEquals(user1, user2)
        assertNotEquals(user1, user3)
        assertEquals(user1.hashCode(), user2.hashCode())
        assertNotEquals(user1.hashCode(), user3.hashCode())
    }

    @Test
    fun `test user with default id`() {
        // Arrange & Act
        val user = User(
            username = "admin",
            password = "password123",
            role = User.Role.ADMIN
        )

        // Assert
        assertEquals(0, user.id)
    }

    @Test
    fun `test user with default createdAt and updatedAt`() {
        // Arrange
        val before = LocalDateTime.now()

        // Act
        val user = User(
            username = "admin",
            password = "password123",
            role = User.Role.ADMIN
        )
        val after = LocalDateTime.now()

        // Assert
        assertNotNull(user.createdAt)
        assertNotNull(user.updatedAt)
        assertTrue(user.createdAt.isAfter(before) || user.createdAt.isEqual(before))
        assertTrue(user.updatedAt.isBefore(after) || user.updatedAt.isEqual(after))
    }
}
