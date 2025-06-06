package srangeldev.proyectoequipofutboljavafx.viewmodels

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onSuccess
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.NewTeamApplication
import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.newteam.storage.PersonalStorageJson
import java.io.File
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

private val logger = logging()

/**
 * ViewModel para la gestión de personal del equipo.
 * Maneja la lógica de negocio y el estado de la UI para la visualización y filtrado de personal.
 */
class PersonalViewModel(
    private val userRepository: UserRepository? = null // Opcional para compatibilidad con inyección existente
) {

    // Propiedades para login
    val username = SimpleStringProperty("")
    val password = SimpleStringProperty("")
    val loginResult = SimpleObjectProperty<LoginResult?>(null)
    val error = SimpleStringProperty("")

    /**
     * Enum que representa los posibles resultados del inicio de sesión.
     */
    enum class LoginResult {
        EMPTY_FIELDS,
        INVALID_CREDENTIALS,
        ADMIN_LOGIN,
        USER_LOGIN
    }

    /**
     * Inicializa el ViewModel.
     * No carga datos por defecto, según los requisitos.
     */
    fun initialize() {
        logger.debug { "Inicializando PersonalViewModel" }
        // Limpiamos los campos
        username.set("")
        password.set("")
        loginResult.set(null)
        error.set("")
    }

    /**
     * Realiza el inicio de sesión con las credenciales proporcionadas.
     * Utiliza un hilo separado para simular un proceso de autenticación más largo
     * y mostrar el indicador de carga durante un tiempo razonable.
     */
    fun login() {
        logger.debug { "Intentando login con usuario: ${username.get()}" }

        // Validar campos vacíos
        if (username.get().isEmpty() || password.get().isEmpty()) {
            loginResult.set(LoginResult.EMPTY_FIELDS)
            return
        }

        // Crear un hilo separado para el proceso de autenticación
        thread {
            try {
                // Simular un retraso para mostrar el indicador de carga (1 segundo)
                Thread.sleep(1000)

                // Resultado de la autenticación
                val result = authenticateUser()

                // Actualizar la UI en el hilo de JavaFX
                Platform.runLater {
                    loginResult.set(result)
                }
            } catch (e: Exception) {
                logger.error { "Error durante el login: ${e.message}" }

                // Actualizar la UI en el hilo de JavaFX
                Platform.runLater {
                    error.set("Error durante el login: ${e.message}")
                    loginResult.set(LoginResult.INVALID_CREDENTIALS)
                }
            }
        }
    }

    /**
     * Autentica al usuario con las credenciales proporcionadas.
     * 
     * @return El resultado de la autenticación.
     */
    private fun authenticateUser(): LoginResult {
        return try {
            // Verificar credenciales con el repositorio de usuarios
            userRepository?.let { repo ->
                // Usar verifyCredentials en lugar de buscar y comparar manualmente
                val user = repo.verifyCredentials(username.get(), password.get())

                if (user != null) {
                    // Determinar tipo de usuario
                    if (user.role == User.Role.ADMIN) {
                        LoginResult.ADMIN_LOGIN
                    } else {
                        LoginResult.USER_LOGIN
                    }
                } else {
                    LoginResult.INVALID_CREDENTIALS
                }
            } ?: run {
                // Si no hay repositorio, simulamos un login básico
                if (username.get() == "admin" && password.get() == "admin") {
                    LoginResult.ADMIN_LOGIN
                } else if (username.get() == "user" && password.get() == "user") {
                    LoginResult.USER_LOGIN
                } else {
                    LoginResult.INVALID_CREDENTIALS
                }
            }
        } catch (e: Exception) {
            logger.error { "Error durante la autenticación: ${e.message}" }
            throw e
        }
    }

    /**
     * Limpia el campo de contraseña.
     */
    fun clearPassword() {
        password.set("")
    }
}
