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
    fun `delete debería eliminar usuario existente`() {
        // Create test user
        val userToDelete = User(1, "todelete", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())

        // Set up mock behavior for first call to getById
        whenever(userDao.findById(1)).thenReturn(userToDelete)

        // Set up mock behavior for delete
        whenever(userDao.delete(1)).thenReturn(1) // Return 1 row affected

        // Set up mock behavior for findByUsername - first call returns user, second call returns null
        val findByUsernameMock = whenever(userDao.findByUsername("todelete"))
        findByUsernameMock.thenReturn(userToDelete) // First call
        findByUsernameMock.thenReturn(null) // Second call after deletion

        // Execute delete
        val result = repository.delete(userToDelete.id)

        // Verify result
        assertTrue(result)

        // Verify user is no longer found
        assertNull(repository.getByUsername("todelete"))
    }

    @Test
    fun `delete debería devolver false para usuario no existente`() {
        val result = repository.delete(-1)
        assertFalse(result)
    }

    @Test
    fun `delete debería mantener otros usuarios`() {
        // Create test users
        val user1 = User(1, "user1", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())
        val user2 = User(2, "user2", "pass", User.Role.USER, LocalDateTime.now(), LocalDateTime.now())

        // Set up mock behavior for getById
        whenever(userDao.findById(1)).thenReturn(user1)

        // Set up mock behavior for delete
        whenever(userDao.delete(1)).thenReturn(1) // Return 1 row affected

        // Set up mock behavior for findByUsername for user1 - first call returns user, second call returns null
        val findByUsername1Mock = whenever(userDao.findByUsername("user1"))
        findByUsername1Mock.thenReturn(user1) // First call
        findByUsername1Mock.thenReturn(null) // Second call after deletion

        // Set up mock behavior for findByUsername for user2 - always returns user2
        whenever(userDao.findByUsername("user2")).thenReturn(user2)

        // Execute delete
        repository.delete(user1.id)

        // Verify user1 is deleted
        assertNull(repository.getByUsername("user1"))

        // Verify user2 still exists
        assertNotNull(repository.getByUsername("user2"))
    }
}
