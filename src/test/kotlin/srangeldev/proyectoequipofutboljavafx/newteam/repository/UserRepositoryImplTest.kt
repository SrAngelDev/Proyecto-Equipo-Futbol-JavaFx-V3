package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.utils.BCryptUtil
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryImplTest {
    private lateinit var repository: UserRepositoryImpl
    private val testDbFile = File("test_users.db")

    @BeforeAll
    fun setUp() {
        if (testDbFile.exists()) {
            testDbFile.delete()
        }

        DataBaseManager.instance.use { db ->
            db.connection?.createStatement()?.execute("""
                CREATE TABLE IF NOT EXISTS Usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                )
            """)
        }
    }

    @BeforeEach
    fun init() {
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val adminPass = BCryptUtil.hashPassword("admin")
        val userPass = BCryptUtil.hashPassword("user")

        DataBaseManager.instance.use { db ->
            db.connection?.createStatement()?.execute("DELETE FROM Usuarios")
            db.connection?.createStatement()?.execute("""
                INSERT INTO Usuarios (username, password, role, created_at, updated_at)
                VALUES 
                ('admin', '$adminPass', 'ADMIN', '$now', '$now'),
                ('user', '$userPass', 'USER', '$now', '$now')
            """)
        }
        repository = UserRepositoryImpl()
    }

    @Test
    fun `test getByUsername con usuario no existente`() {
        val user = repository.getByUsername("noexiste")
        assertNull(user)
    }

    @Test
    fun `test verifyCredentials con credenciales incorrectas`() {
        val user = repository.verifyCredentials("admin", "wrong")
        assertNull(user)
    }

    @Test
    fun `test update usuario no existente`() {
        val user = User(
            id = 999,
            username = "noexiste",
            password = "test",
            role = User.Role.USER
        )
        val result = repository.update(user.id, user)
        assertNull(result)
    }

    @Test
    fun `test delete usuario no existente`() {
        val deleted = repository.delete(999)
        assertFalse(deleted)
    }
    
    @AfterAll
    fun tearDown() {
        if (testDbFile.exists()) {
            testDbFile.delete()
        }
    }
}
