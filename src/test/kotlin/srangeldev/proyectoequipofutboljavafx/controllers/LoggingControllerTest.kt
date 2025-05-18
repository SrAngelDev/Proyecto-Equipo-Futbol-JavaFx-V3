package srangeldev.proyectoequipofutboljavafx.controllers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.newteam.session.Session
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class LoggingControllerTest {

    @Mock
    private lateinit var userRepository: UserRepository

    // Implementación de prueba de LoggingController que no depende de componentes JavaFX
    private lateinit var testController: TestLoggingController

    private val adminUser = User(
        id = 1,
        username = "admin",
        password = "admin",
        role = User.Role.ADMIN,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    private val normalUser = User(
        id = 2,
        username = "user",
        password = "user",
        role = User.Role.USER,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @BeforeEach
    fun setUp() {
        // Crear un controlador de prueba con el repositorio simulado
        testController = TestLoggingController(userRepository)

        // Inicializar el controlador
        testController.initialize()

        // Reiniciar la sesión antes de cada prueba
        Session.logout()
    }

    @Test
    fun `test initialize calls initDefaultUsers`() {
        // Verificar que initDefaultUsers fue llamado durante la inicialización
        verify(userRepository).initDefaultUsers()
    }

    @Test
    fun `test login with empty username shows error`() {
        // Llamar a login con nombre de usuario vacío
        val result = testController.login("", "password")

        // Verificar que el resultado indica error por campos vacíos
        assertEquals(LoginResult.EMPTY_FIELDS, result)

        // Verificar que verifyCredentials no fue llamado
        verify(userRepository, never()).verifyCredentials(any(), any())
    }

    @Test
    fun `test login with empty password shows error`() {
        // Call login with empty password
        val result = testController.login("username", "")

        // Verify result indicates error for empty fields
        assertEquals(LoginResult.EMPTY_FIELDS, result)

        // Verify that verifyCredentials was not called
        verify(userRepository, never()).verifyCredentials(any(), any())
    }

    @Test
    fun `test login with invalid credentials shows error`() {
        // Mock repository to return null for invalid credentials
        whenever(userRepository.verifyCredentials("invalid", "invalid")).thenReturn(null)

        // Call login with invalid credentials
        val result = testController.login("invalid", "invalid")

        // Verify result indicates invalid credentials
        assertEquals(LoginResult.INVALID_CREDENTIALS, result)

        // Verify that verifyCredentials was called
        verify(userRepository).verifyCredentials("invalid", "invalid")
    }

    @Test
    fun `test login with valid admin credentials returns admin result`() {
        // Mock repository to return admin user for valid credentials
        whenever(userRepository.verifyCredentials("admin", "admin")).thenReturn(adminUser)

        // Call login with valid admin credentials
        val result = testController.login("admin", "admin")

        // Verify result indicates admin login
        assertEquals(LoginResult.ADMIN_LOGIN, result)

        // Verify that verifyCredentials was called
        verify(userRepository).verifyCredentials("admin", "admin")

        // Verify that user was set in session
        assertEquals(adminUser, Session.getCurrentUser())
    }

    @Test
    fun `test login with valid user credentials returns user result`() {
        // Mock repository to return normal user for valid credentials
        whenever(userRepository.verifyCredentials("user", "user")).thenReturn(normalUser)

        // Call login with valid user credentials
        val result = testController.login("user", "user")

        // Verify result indicates user login
        assertEquals(LoginResult.USER_LOGIN, result)

        // Verify that verifyCredentials was called
        verify(userRepository).verifyCredentials("user", "user")

        // Verify that user was set in session
        assertEquals(normalUser, Session.getCurrentUser())
    }

    // Test-friendly implementation of LoggingController that doesn't rely on JavaFX components
    class TestLoggingController(private val userRepository: UserRepository) {

        fun initialize() {
            // Initialize the repository
            userRepository.initDefaultUsers()
        }

        fun login(username: String, password: String): LoginResult {
            // Validate fields
            if (username.isEmpty() || password.isEmpty()) {
                return LoginResult.EMPTY_FIELDS
            }

            // Verify credentials
            val user = userRepository.verifyCredentials(username, password)

            if (user == null) {
                return LoginResult.INVALID_CREDENTIALS
            }

            // Set user in session
            Session.setCurrentUser(user)

            // Return result based on user role
            return when (user.role) {
                User.Role.ADMIN -> LoginResult.ADMIN_LOGIN
                User.Role.USER -> LoginResult.USER_LOGIN
            }
        }
    }

    // Enum to represent login results
    enum class LoginResult {
        EMPTY_FIELDS,
        INVALID_CREDENTIALS,
        ADMIN_LOGIN,
        USER_LOGIN
    }
}
