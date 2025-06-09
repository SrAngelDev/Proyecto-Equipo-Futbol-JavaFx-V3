package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.newteam.dao.UserDao
import srangeldev.proyectoequipofutboljavafx.newteam.database.JdbiManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.utils.BCryptUtil
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepositoryImplTest {
    private lateinit var repository: UserRepositoryImpl
    private lateinit var userDao: UserDao
    private lateinit var originalInstance: JdbiManager
    private lateinit var originalDbUrl: String
    private lateinit var testDbUrl: String
    private lateinit var connection: Connection

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        originalInstance = JdbiManager.getInstance()
        originalDbUrl = Config.configProperties.databaseUrl

        val testDbFile = tempDir.resolve("test_users.db").toFile().absolutePath
        testDbUrl = "jdbc:sqlite:$testDbFile"

        val urlField = Config.configProperties::class.java.getDeclaredField("databaseUrl")
        urlField.isAccessible = true
        urlField.set(Config.configProperties, testDbUrl)

        val constructor = JdbiManager::class.java.getDeclaredConstructor()
        constructor.isAccessible = true
        val testInstance = constructor.newInstance()

        val instanceField = JdbiManager::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, testInstance)

        connection = DriverManager.getConnection(testDbUrl)
        createUsersTable()

        // Create a mock UserDao
        userDao = mock()
        repository = UserRepositoryImpl(userDao)
    }

    private fun createUsersTable() {
        val sql = """
            CREATE TABLE IF NOT EXISTS Users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                createdAt TEXT NOT NULL,
                updatedAt TEXT NOT NULL
            )
        """.trimIndent()

        connection.createStatement().execute(sql)
    }

    private fun insertTestUser(
        username: String,
        password: String,
        role: User.Role
    ) {
        val sql = """
            INSERT INTO Users (username, password, role, createdAt, updatedAt)
            VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        connection.prepareStatement(sql).use { stmt ->
            stmt.setString(1, username)
            stmt.setString(2, password)
            stmt.setString(3, role.name)
            stmt.setString(4, LocalDateTime.now().toString())
            stmt.setString(5, LocalDateTime.now().toString())
            stmt.executeUpdate()
        }
    }

    @AfterEach
    fun tearDown() {
        val urlField = Config.configProperties::class.java.getDeclaredField("databaseUrl")
        urlField.isAccessible = true
        urlField.set(Config.configProperties, originalDbUrl)

        val instanceField = JdbiManager::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, originalInstance)

        connection.close()
    }

    @Test
    fun `findAll debería devolver todos los usuarios`() {
        // Create test users
        val user1 = User(1, "user1", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val user2 = User(2, "user2", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val usersList = listOf(user1, user2)

        // Set up mock behavior
        whenever(userDao.findAll()).thenReturn(usersList)

        val result = repository.findAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.username == "user1" })
        assertTrue(result.any { it.username == "user2" })
    }

    @Test
    fun `findAll debería devolver lista vacía cuando no hay usuarios`() {
        val result = repository.findAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getByUsername debería devolver usuario existente`() {
        // Create test user
        val existingUser = User(1, "existinguser", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())

        // Set up mock behavior
        whenever(userDao.findByUsername("existinguser")).thenReturn(existingUser)

        val result = repository.getByUsername("existinguser")

        assertNotNull(result)
        assertEquals("existinguser", result.username)
    }

    @Test
    fun `getByUsername debería devolver null cuando no existe`() {
        val result = repository.getByUsername("nonexistent")
        assertNull(result)
    }

    @Test
    fun `verifyCredentials debería devolver true para credenciales válidas`() {
        val hashedPassword = "GLTw6k8ixrje86Y8rN+fNTltfUmd4rm8g7OBFwlqcqE="
        val validUser = User(1, "validuser", hashedPassword, User.Role.USER, LocalDateTime.now(), LocalDateTime.now())

        // Set up mock behavior
        whenever(userDao.findByUsername("validuser")).thenReturn(validUser)

        val result: User? = repository.verifyCredentials("validuser", "correctpass")

        assertNotNull(result)
    }

    @Test
    fun `verifyCredentials debería devolver false para contraseña incorrecta`() {
        insertTestUser(username = "validuser", password = "correctpass", role = User.Role.USER)

        val result: User? = repository.verifyCredentials("validuser", "wrongpass")

        assertNull(result)
    }

    @Test
    fun `verifyCredentials debería devolver false para usuario inexistente`() {
        val result: User? = repository.verifyCredentials("nonexistent", "anypass")
        assertNull(result)
    }

    @Test
    fun `save debería guardar nuevo usuario correctamente`() {
        // Create test user
        val newUser = User(
            id = 0, // New user has id 0
            username = "newuser",
            password = "newpass",
            role = User.Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Create saved user with ID
        val savedUser = User(
            id = 1, // Saved user has id 1
            username = "newuser",
            password = "\$2a\$10\$k.58cTqDFMGMVpbHUXlH8eOY3JA6qnGaDf8lLWLyCUUJJRGzH4qHK", // Hashed password
            role = User.Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Set up mock behavior
        whenever(userDao.save(any())).thenReturn(1) // Return ID 1
        whenever(userDao.findById(1)).thenReturn(savedUser)
        whenever(userDao.findByUsername("newuser")).thenReturn(savedUser)

        val result = repository.save(newUser)

        assertNotNull(result.id)
        assertEquals("newuser", result.username)

        val retrieved = repository.getByUsername("newuser")
        assertNotNull(retrieved)
    }

    @Test
    fun `save debería lanzar excepción para username duplicado`() {
        insertTestUser(username = "duplicate", password = "pass", role = User.Role.USER)
        val newUser = User(
            username = "duplicate",
            password = "pass",
            role = User.Role.USER
        )

        assertThrows<RuntimeException> {
            repository.save(newUser)
        }
    }

    @Test
    fun `update debería actualizar usuario existente`() {
        // Create existing user
        val existingUser = User(
            id = 1,
            username = "toupdate",
            password = "\$2a\$10\$k.58cTqDFMGMVpbHUXlH8eOY3JA6qnGaDf8lLWLyCUUJJRGzH4qHK", // Hashed password
            role = User.Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // Create updated user
        val updatedUser = existingUser.copy(
            updatedAt = LocalDateTime.now().plusMinutes(5)
        )

        // Set up mock behavior
        whenever(userDao.findById(1)).thenReturn(existingUser)
        whenever(userDao.findByUsername("toupdate")).thenReturn(existingUser)
        whenever(userDao.update(any())).thenReturn(1) // 1 row affected

        // After update, return the updated user
        whenever(userDao.findById(1)).thenReturn(updatedUser)
        whenever(userDao.findByUsername("toupdate")).thenReturn(updatedUser)

        val result = repository.update(updatedUser.id, updatedUser)

        assertNotNull(result)

        val retrieved = repository.getByUsername("toupdate")
        assertNotNull(retrieved)
    }

    @Test
    fun `update debería devolver null para usuario no existente`() {
        val nonExistingUser = User(
            id = 999,
            username = "nonexistent",
            password = "pass",
            role = User.Role.USER
        )

        val result = repository.update(nonExistingUser.id, user = nonExistingUser)
        assertNull(result)
    }

    @Test
    fun `delete debería devolver false para usuario no existente`() {
        val result = repository.delete(-1)
        assertFalse(result)
    }

    @Test
    fun `delete debería devolver true para usuario existente`() {
        // Arrange
        val user = User(1, "user1", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        whenever(userDao.findById(1)).thenReturn(user)
        whenever(userDao.delete(1)).thenReturn(1)

        // Act
        val result = repository.delete(1)

        // Assert
        assertTrue(result)
        verify(userDao).delete(1)
    }

    @Test
    fun `delete debería actualizar correctamente la caché tras eliminar`() {
        // Arrange
        val user = User(2, "user2", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        repository.findAll() // Fill cache
        whenever(userDao.findById(2)).thenReturn(user)
        whenever(userDao.delete(2)).thenReturn(1)

        // Act
        repository.delete(2)

        // Assert
        assertNull(repository.getByUsername("user2"))
    }

    @Test
    fun `delete debería mantener otros usuarios`() {
        val user1 = User(1, "user1", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val user2 = User(2, "user2", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())

        whenever(userDao.findById(1)).thenReturn(user1)
        whenever(userDao.findByUsername("user1")).thenReturn(null)
        whenever(userDao.findByUsername("user2")).thenReturn(user2)
        whenever(userDao.delete(1)).thenReturn(1)

        repository.delete(user1.id)

        assertNull(repository.getByUsername("user1"))
        assertNotNull(repository.getByUsername("user2"))
    }

    @Test
    fun `findAll should throw RuntimeException when database error occurs`() {
        // Given
        val mockUserDao = mock<UserDao> {
            on { findAll() } doThrow RuntimeException("Error de base de datos")
        }
        val repository = UserRepositoryImpl(mockUserDao)

        // When/Then
        val exception = assertThrows<RuntimeException> {
            repository.findAll()
        }
        assertTrue(exception.message?.contains("Error al obtener los usuarios") ?: false)
    }

    @Test
    fun `getByUsername should return null when database error occurs`() {
        // Given
        val mockUserDao = mock<UserDao> {
            on { findByUsername(any()) } doThrow RuntimeException("Error de base de datos")
        }
        val repository = UserRepositoryImpl(mockUserDao)

        // When
        val result = repository.getByUsername("testuser")

        // Then
        assertNull(result)
    }

    @Test
    fun `verifyCredentials should return null when password does not match`() {
        // Given
        val hashedPassword = BCryptUtil.hashPassword("password123")
        val user = User(1, "testuser", hashedPassword, User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val mockUserDao = mock<UserDao> {
            on { findByUsername("testuser") } doReturn user
        }
        val repository = UserRepositoryImpl(mockUserDao)

        // When
        val result = repository.verifyCredentials("testuser", "wrongpassword")

        // Then
        assertNull(result)
    }

    @Test
    fun `save should handle existing user with unchanged password`() {
        // Given
        val existingUser =
            User(1, "testuser", "hashedpassword", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val mockUserDao = mock<UserDao> {
            on { findById(1) } doReturn existingUser
            on { update(any()) } doReturn 1
        }
        val repository = UserRepositoryImpl(mockUserDao)

        // When
        val result = repository.save(existingUser)

        // Then
        assertNotNull(result)
        assertEquals(existingUser.password, result.password)
    }

    @Test
    fun `update should hash new password when password changes`() {
        // Given
        val existingUser =
            User(1, "testuser", "oldhashedpassword", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val updatedUser = existingUser.copy(password = "newpassword")
        val mockUserDao = mock<UserDao> {
            on { findById(1) } doReturn existingUser
            on { update(any()) } doReturn 1
        }
        val repository = UserRepositoryImpl(mockUserDao)

        // When
        val result = repository.update(1, updatedUser)

        // Then
        assertNotNull(result)
        verify(mockUserDao).update(argThat { password != "oldhashedpassword" })
    }

    @Test
    fun `update should return null when database error occurs`() {
        // Given
        val user = User(1, "testuser", "password", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val mockUserDao = mock<UserDao> {
            on { findById(1) } doReturn user
            on { update(any()) } doThrow RuntimeException("Error de base de datos")
        }
        val repository = UserRepositoryImpl(mockUserDao)

        // When
        val result = repository.update(1, user)

        // Then
        assertNull(result)
    }

    @Test
    fun `delete should handle database error`() {
        // Given
        val user = User(1, "testuser", "password", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val mockUserDao = mock<UserDao> {
            on { findById(1) } doReturn user
            on { delete(1) } doThrow RuntimeException("Error de base de datos")
        }
        val repository = UserRepositoryImpl(mockUserDao)

        // When
        val result = repository.delete(1)

        // Then
        assertFalse(result)
    }

    @Test
    fun `getById should return cached user when available`() {
        // Given
        val user = User(1, "testuser", "password", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val mockUserDao = mock<UserDao> {
            on { findAll() } doReturn listOf(user)
        }
        val repository = UserRepositoryImpl(mockUserDao)
        repository.findAll() // Populate cache

        // When
        val result = repository.getById(1)

        // Then
        assertNotNull(result)
        assertEquals(user, result)
        verify(mockUserDao, never()).findById(1) // Should not hit database
    }

    @Test
    fun `getById should return null when database error occurs`() {
        // Given
        val mockUserDao = mock<UserDao> {
            on { findById(any()) } doThrow RuntimeException("Error de base de datos")
        }
        val repository = UserRepositoryImpl(mockUserDao)

        // When
        val result = repository.getById(1)

        // Then
        assertNull(result)
    }
}
