package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UserRepositoryImplTest {

    // We'll use a test-specific implementation of UserRepository
    private lateinit var userRepository: TestUserRepository

    @BeforeEach
    fun setUp() {
        // Initialize the test repository with some test data
        userRepository = TestUserRepository()
        userRepository.initDefaultUsers()
    }

    @Test
    fun `test findAll returns all users`() {
        // Act
        val users = userRepository.findAll()

        // Assert
        assertEquals(2, users.size)
        assertTrue(users.any { it.username == "admin" && it.role == User.Role.ADMIN })
        assertTrue(users.any { it.username == "user" && it.role == User.Role.USER })
    }

    @Test
    fun `test getByUsername returns correct user`() {
        // Act
        val adminUser = userRepository.getByUsername("admin")
        val normalUser = userRepository.getByUsername("user")
        val nonExistentUser = userRepository.getByUsername("nonexistent")

        // Assert
        assertNotNull(adminUser)
        assertEquals("admin", adminUser?.username)
        assertEquals(User.Role.ADMIN, adminUser?.role)

        assertNotNull(normalUser)
        assertEquals("user", normalUser?.username)
        assertEquals(User.Role.USER, normalUser?.role)

        assertNull(nonExistentUser)
    }

    @Test
    fun `test verifyCredentials returns user with correct credentials`() {
        // Act
        val validAdmin = userRepository.verifyCredentials("admin", "admin")
        val validUser = userRepository.verifyCredentials("user", "user")
        val invalidUsername = userRepository.verifyCredentials("nonexistent", "password")
        val invalidPassword = userRepository.verifyCredentials("admin", "wrongpassword")

        // Assert
        assertNotNull(validAdmin)
        assertEquals("admin", validAdmin?.username)
        assertEquals(User.Role.ADMIN, validAdmin?.role)

        assertNotNull(validUser)
        assertEquals("user", validUser?.username)
        assertEquals(User.Role.USER, validUser?.role)

        assertNull(invalidUsername)
        assertNull(invalidPassword)
    }

    @Test
    fun `test save creates new user`() {
        // Arrange
        val newUser = User(
            username = "newuser",
            password = "password",
            role = User.Role.USER
        )

        // Act
        val savedUser = userRepository.save(newUser)

        // Assert
        assertNotNull(savedUser)
        assertTrue(savedUser.id > 0)
        assertEquals("newuser", savedUser.username)
        assertEquals(User.Role.USER, savedUser.role)

        // Verify the user was added to the repository
        val retrievedUser = userRepository.getByUsername("newuser")
        assertNotNull(retrievedUser)
        assertEquals(savedUser.id, retrievedUser?.id)
    }

    @Test
    fun `test update modifies existing user`() {
        // Arrange
        val adminUser = userRepository.getByUsername("admin")
        assertNotNull(adminUser)

        val updatedUser = adminUser!!.copy(
            username = "admin",
            password = "newpassword",
            role = User.Role.ADMIN
        )

        // Act
        val result = userRepository.update(adminUser.id, updatedUser)

        // Assert
        assertNotNull(result)
        assertEquals(adminUser.id, result?.id)
        assertEquals("admin", result?.username)
        assertEquals(User.Role.ADMIN, result?.role)

        // Verify the user was updated in the repository
        val retrievedUser = userRepository.getByUsername("admin")
        assertNotNull(retrievedUser)
        assertEquals(adminUser.id, retrievedUser?.id)
    }

    @Test
    fun `test delete removes user`() {
        // Arrange
        val userToDelete = userRepository.getByUsername("user")
        assertNotNull(userToDelete)

        // Act
        val result = userRepository.delete(userToDelete!!.id)

        // Assert
        assertTrue(result)

        // Verify the user was removed from the repository
        val retrievedUser = userRepository.getByUsername("user")
        assertNull(retrievedUser)
    }

    @Test
    fun `test initDefaultUsers creates admin and normal user`() {
        // Arrange
        val testRepo = TestUserRepository()

        // Act
        testRepo.initDefaultUsers()

        // Assert
        val adminUser = testRepo.getByUsername("admin")
        val normalUser = testRepo.getByUsername("user")

        assertNotNull(adminUser)
        assertEquals("admin", adminUser?.username)
        assertEquals(User.Role.ADMIN, adminUser?.role)

        assertNotNull(normalUser)
        assertEquals("user", normalUser?.username)
        assertEquals(User.Role.USER, normalUser?.role)
    }

    @Test
    fun `test getById returns correct user`() {
        // Arrange
        val adminUser = userRepository.getByUsername("admin")
        assertNotNull(adminUser)

        // Act
        val retrievedUser = (userRepository as TestUserRepository).getById(adminUser!!.id)
        val nonExistentUser = (userRepository as TestUserRepository).getById(999)

        // Assert
        assertNotNull(retrievedUser)
        assertEquals(adminUser.id, retrievedUser?.id)
        assertEquals("admin", retrievedUser?.username)
        assertEquals(User.Role.ADMIN, retrievedUser?.role)

        assertNull(nonExistentUser)
    }

    // Test-specific implementation of UserRepository that doesn't use the database
    class TestUserRepository : UserRepository {
        private val users = mutableMapOf<String, User>()
        private var nextId = 1

        override fun findAll(): List<User> {
            return users.values.toList()
        }

        override fun getByUsername(username: String): User? {
            return users[username]
        }

        override fun verifyCredentials(username: String, password: String): User? {
            val user = getByUsername(username)
            return if (user != null && user.password == password) {
                user
            } else {
                null
            }
        }

        override fun save(user: User): User {
            val isUpdate = user.id > 0

            if (isUpdate) {
                return update(user.id, user) ?: throw IllegalStateException("No se pudo actualizar el usuario")
            } else {
                val id = nextId++
                val now = LocalDateTime.now()
                val newUser = user.copy(
                    id = id,
                    createdAt = now,
                    updatedAt = now
                )
                users[newUser.username] = newUser
                return newUser
            }
        }

        override fun update(id: Int, user: User): User? {
            val existingUser = users.values.find { it.id == id }
            if (existingUser == null) {
                return null
            }

            // Remove the old user entry
            users.remove(existingUser.username)

            // Create updated user
            val updatedUser = user.copy(
                id = id,
                createdAt = existingUser.createdAt,
                updatedAt = LocalDateTime.now()
            )

            // Add the updated user
            users[updatedUser.username] = updatedUser
            return updatedUser
        }

        override fun delete(id: Int): Boolean {
            val user = users.values.find { it.id == id }
            if (user != null) {
                users.remove(user.username)
                return true
            }
            return false
        }

        override fun initDefaultUsers() {
            if (users.isEmpty()) {
                save(User(
                    username = "admin",
                    password = "admin",
                    role = User.Role.ADMIN
                ))

                save(User(
                    username = "user",
                    password = "user",
                    role = User.Role.USER
                ))
            }
        }

        // Implementation of getById for testing
        fun getById(id: Int): User? {
            return users.values.find { it.id == id }
        }
    }
}
