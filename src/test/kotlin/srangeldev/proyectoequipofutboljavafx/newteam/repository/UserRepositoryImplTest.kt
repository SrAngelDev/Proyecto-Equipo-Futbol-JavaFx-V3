package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.*
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
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
    private lateinit var originalInstance: DataBaseManager
    private lateinit var originalDbUrl: String
    private lateinit var testDbUrl: String
    private lateinit var connection: Connection

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        originalInstance = DataBaseManager.instance
        originalDbUrl = Config.configProperties.databaseUrl

        val testDbFile = tempDir.resolve("test_users.db").toFile().absolutePath
        testDbUrl = "jdbc:sqlite:$testDbFile"

        val urlField = Config.configProperties::class.java.getDeclaredField("databaseUrl")
        urlField.isAccessible = true
        urlField.set(Config.configProperties, testDbUrl)

        val constructor = DataBaseManager::class.java.getDeclaredConstructor()
        constructor.isAccessible = true
        val testInstance = constructor.newInstance()

        val instanceField = DataBaseManager::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, testInstance)

        connection = DriverManager.getConnection(testDbUrl)
        createUsersTable()
        repository = UserRepositoryImpl()
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

        val instanceField = DataBaseManager::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, originalInstance)

        connection.close()
    }

    @Test
    fun `findAll debería devolver todos los usuarios`() {
        insertTestUser(username = "user1", password = "pass", role = User.Role.USER)
        insertTestUser(username = "user2", password = "pass", role = User.Role.USER)

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
        insertTestUser(username = "existinguser", password = "pass", role = User.Role.USER)

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
        insertTestUser(username = "validuser", password = "correctpass", role = User.Role.USER)

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
        val newUser = User(
            username = "newuser",
            password = "newpass",
            role = User.Role.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

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
        insertTestUser(username = "toupdate", password = "pass", role = User.Role.USER)
        val user = repository.getByUsername("toupdate")!!
        val updatedUser = user.copy()

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
        insertTestUser(username = "todelete", password = "pass", role = User.Role.USER)
        val userToDelete = repository.getByUsername("todelete")!!

        val result = repository.delete(userToDelete.id)

        assertTrue(result)
        assertNull(repository.getByUsername("todelete"))
    }

    @Test
    fun `delete debería devolver false para usuario no existente`() {
        val result = repository.delete(-1)
        assertFalse(result)
    }

    @Test
    fun `delete debería mantener otros usuarios`() {
        insertTestUser(username = "user1", password = "pass", role = User.Role.USER)
        insertTestUser(username = "user2", password = "pass", role = User.Role.USER)
        val user1 = repository.getByUsername("user1")!!

        repository.delete(user1.id)

        assertNull(repository.getByUsername("user1"))
        assertNotNull(repository.getByUsername("user2"))
    }
}