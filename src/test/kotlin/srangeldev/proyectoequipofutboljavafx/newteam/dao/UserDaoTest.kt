package srangeldev.proyectoequipofutboljavafx.newteam.dao

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest {
    private lateinit var jdbi: Jdbi
    private lateinit var dao: UserDao

    val user = User(
        id = 1,
        username = "admin",
        password = "password123", // En un caso real, esto estaría cifrado
        role = User.Role.ADMIN,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @BeforeAll
    fun setUp() {
        // Configurar JDBI con base de datos en fichero SQLite
        jdbi = Jdbi.create("jdbc:sqlite:test.db")
        jdbi.installPlugin(KotlinPlugin())
        jdbi.installPlugin(SqlObjectPlugin())

        // Crear tabla Usuarios
        jdbi.useHandle<Exception> { handle ->
            handle.execute("""
                CREATE TABLE IF NOT EXISTS Usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                )
            """)
        }

        // Inicializar DAO
        dao = jdbi.onDemand(UserDao::class.java)
    }

    @AfterEach
    fun tearDown() {
        // Limpiar datos después de cada test
        jdbi.useHandle<Exception> { handle ->
            handle.execute("DELETE FROM Usuarios")
        }
    }

    @Nested
    @DisplayName("Casos correctos")
    inner class CasosCorrectos {
        @Test
        @DisplayName("Insertar usuario")
        fun saveUser() {
            val id = dao.save(user)
            val result = dao.findById(id)
            
            assertNotNull(result, "El usuario no debería ser nulo")
            assertEquals(user.username, result!!.username, "Los nombres de usuario deberían ser iguales")
            assertEquals(user.password, result.password, "Las contraseñas deberían ser iguales")
            assertEquals(user.role, result.role, "Los roles deberían ser iguales")
        }

        @Test
        @DisplayName("Actualizar usuario")
        fun updateUser() {
            val id = dao.save(user)
            
            val updatedUser = user.copy(
                id = id,
                username = "adminUpdated",
                password = "newPassword",
                role = User.Role.USER,
                updatedAt = LocalDateTime.now()
            )
            
            val updateResult = dao.update(updatedUser)
            val result = dao.findById(id)
            
            assertEquals(1, updateResult, "Debería haberse actualizado 1 registro")
            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals("adminUpdated", result!!.username, "El nombre de usuario debería haberse actualizado")
            assertEquals("newPassword", result.password, "La contraseña debería haberse actualizado")
            assertEquals(User.Role.USER, result.role, "El rol debería haberse actualizado")
        }

        @Test
        @DisplayName("Eliminar usuario")
        fun deleteUser() {
            val id = dao.save(user)
            
            val deleteResult = dao.delete(id)
            val result = dao.findById(id)
            
            assertEquals(1, deleteResult, "Debería haberse eliminado 1 registro")
            assertNull(result, "El resultado debería ser nulo después de eliminar")
        }

        @Test
        @DisplayName("Obtener todos los usuarios")
        fun findAll() {
            // Insertar varios usuarios
            dao.save(user)
            dao.save(user.copy(id = 0, username = "user1", role = User.Role.USER))
            dao.save(user.copy(id = 0, username = "user2", role = User.Role.USER))
            
            val result = dao.findAll()
            
            assertEquals(3, result.size, "Deberían haber 3 usuarios")
            assertTrue(result.any { it.username == "admin" }, "Debería existir un usuario admin")
            assertTrue(result.any { it.username == "user1" }, "Debería existir un usuario user1")
            assertTrue(result.any { it.username == "user2" }, "Debería existir un usuario user2")
        }

        @Test
        @DisplayName("Buscar por nombre de usuario")
        fun findByUsername() {
            // Insertar varios usuarios
            dao.save(user)
            dao.save(user.copy(id = 0, username = "user1", role = User.Role.USER))
            
            val result = dao.findByUsername("user1")
            
            assertNotNull(result, "El resultado no debería ser nulo")
            assertEquals("user1", result!!.username, "El nombre de usuario debería ser user1")
            assertEquals(User.Role.USER, result.role, "El rol debería ser USER")
        }

        @Test
        @DisplayName("Contar usuarios")
        fun count() {
            // Insertar varios usuarios
            dao.save(user)
            dao.save(user.copy(id = 0, username = "user1"))
            dao.save(user.copy(id = 0, username = "user2"))
            
            val result = dao.count()
            
            assertEquals(3, result, "Deberían haber 3 usuarios en total")
        }
    }

    @Nested
    @DisplayName("Casos incorrectos")
    inner class CasosIncorrectos {
        @Test
        @DisplayName("Buscar por ID inexistente")
        fun findByIdInexistente() {
            val result = dao.findById(999)
            assertNull(result, "El resultado debería ser nulo para un ID inexistente")
        }

        @Test
        @DisplayName("Actualizar usuario inexistente")
        fun updateInexistente() {
            val result = dao.update(user.copy(id = 999))
            assertEquals(0, result, "No debería haberse actualizado ningún registro")
        }

        @Test
        @DisplayName("Eliminar usuario inexistente")
        fun deleteInexistente() {
            val result = dao.delete(999)
            assertEquals(0, result, "No debería haberse eliminado ningún registro")
        }

        @Test
        @DisplayName("Buscar por nombre de usuario inexistente")
        fun findByUsernameInexistente() {
            val result = dao.findByUsername("usuarioInexistente")
            assertNull(result, "El resultado debería ser nulo para un nombre de usuario inexistente")
        }
    }
}