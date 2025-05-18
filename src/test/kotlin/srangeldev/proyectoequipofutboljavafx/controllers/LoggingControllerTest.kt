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
        // Llamar a login con contraseña vacía
        val result = testController.login("username", "")

        // Verificar que el resultado indica error por campos vacíos
        assertEquals(LoginResult.EMPTY_FIELDS, result)

        // Verificar que verifyCredentials no fue llamado
        verify(userRepository, never()).verifyCredentials(any(), any())
    }

    @Test
    fun `test login with invalid credentials shows error`() {
        // Configurar el repositorio simulado para devolver null para credenciales inválidas
        whenever(userRepository.verifyCredentials("invalid", "invalid")).thenReturn(null)

        // Llamar a login con credenciales inválidas
        val result = testController.login("invalid", "invalid")

        // Verificar que el resultado indica credenciales inválidas
        assertEquals(LoginResult.INVALID_CREDENTIALS, result)

        // Verificar que verifyCredentials fue llamado
        verify(userRepository).verifyCredentials("invalid", "invalid")
    }

    @Test
    fun `test login with valid admin credentials returns admin result`() {
        // Configurar el repositorio simulado para devolver usuario administrador para credenciales válidas
        whenever(userRepository.verifyCredentials("admin", "admin")).thenReturn(adminUser)

        // Llamar a login con credenciales de administrador válidas
        val result = testController.login("admin", "admin")

        // Verificar que el resultado indica inicio de sesión de administrador
        assertEquals(LoginResult.ADMIN_LOGIN, result)

        // Verificar que verifyCredentials fue llamado
        verify(userRepository).verifyCredentials("admin", "admin")

        // Verificar que el usuario fue establecido en la sesión
        assertEquals(adminUser, Session.getCurrentUser())
    }

    @Test
    fun `test login with valid user credentials returns user result`() {
        // Configurar el repositorio simulado para devolver usuario normal para credenciales válidas
        whenever(userRepository.verifyCredentials("user", "user")).thenReturn(normalUser)

        // Llamar a login con credenciales de usuario válidas
        val result = testController.login("user", "user")

        // Verificar que el resultado indica inicio de sesión de usuario
        assertEquals(LoginResult.USER_LOGIN, result)

        // Verificar que verifyCredentials fue llamado
        verify(userRepository).verifyCredentials("user", "user")

        // Verificar que el usuario fue establecido en la sesión
        assertEquals(normalUser, Session.getCurrentUser())
    }

    // Implementación amigable para pruebas de LoggingController que no depende de componentes JavaFX
    class TestLoggingController(private val userRepository: UserRepository) {

        fun initialize() {
            // Inicializar el repositorio
            userRepository.initDefaultUsers()
        }

        fun login(username: String, password: String): LoginResult {
            // Validar campos
            if (username.isEmpty() || password.isEmpty()) {
                return LoginResult.EMPTY_FIELDS
            }

            // Verificar credenciales
            val user = userRepository.verifyCredentials(username, password)

            if (user == null) {
                return LoginResult.INVALID_CREDENTIALS
            }

            // Establecer usuario en la sesión
            Session.setCurrentUser(user)

            // Devolver resultado basado en el rol del usuario
            return when (user.role) {
                User.Role.ADMIN -> LoginResult.ADMIN_LOGIN
                User.Role.USER -> LoginResult.USER_LOGIN
            }
        }
    }

    // Enum para representar los resultados del inicio de sesión
    enum class LoginResult {
        EMPTY_FIELDS,        // Campos vacíos
        INVALID_CREDENTIALS, // Credenciales inválidas
        ADMIN_LOGIN,         // Inicio de sesión de administrador
        USER_LOGIN           // Inicio de sesión de usuario
    }
}
