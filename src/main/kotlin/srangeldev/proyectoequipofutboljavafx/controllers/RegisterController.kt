package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.text.Text
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager

/**
 * Controlador para la pantalla de registro de usuarios.
 * Maneja la creación de nuevas cuentas de usuario.
 */
class RegisterController : KoinComponent {
    private val logger = logging()

    // Inyectar el repositorio de usuarios usando Koin
    private val userRepository: UserRepository by inject()

    @FXML
    private lateinit var usernameField: TextField

    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var confirmPasswordField: PasswordField

    @FXML
    private lateinit var registerButton: Button

    @FXML
    private lateinit var backToLoginLink: Hyperlink

    @FXML
    private lateinit var errorMessage: Text

    @FXML
    private lateinit var loadingContainer: HBox

    @FXML
    private lateinit var registerProgress: ProgressIndicator

    @FXML
    private lateinit var logoImage: ImageView

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura los eventos de los botones y enlaces.
     */
    @FXML
    private fun initialize() {
        logger.debug { "Inicializando RegisterController" }

        // Configurar el evento del botón de registro
        registerButton.setOnAction {
            logger.debug { "Botón de registro presionado" }
            register()
        }

        // Configurar el evento del enlace para volver al login
        backToLoginLink.setOnAction {
            logger.debug { "Enlace para volver al login presionado" }
            navigateToLogin()
        }
    }

    /**
     * Realiza el registro de un nuevo usuario.
     */
    private fun register() {
        // Mostrar el indicador de carga
        showLoadingIndicator(true)

        // Deshabilitar el botón mientras se procesa
        registerButton.isDisable = true

        // Limpiar mensaje de error anterior
        errorMessage.text = ""

        try {
            // Validar campos
            val username = usernameField.text.trim()
            val password = passwordField.text
            val confirmPassword = confirmPasswordField.text

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showError("Por favor complete todos los campos")
                return
            }

            if (password != confirmPassword) {
                showError("Las contraseñas no coinciden")
                return
            }

            // Verificar si el usuario ya existe
            val existingUser = userRepository.getByUsername(username)
            if (existingUser != null) {
                showError("El nombre de usuario ya está en uso")
                return
            }

            // Crear el nuevo usuario con rol USER
            val newUser = User(
                username = username,
                password = password, // El repositorio se encargará de hashear la contraseña
                role = User.Role.USER
            )

            // Guardar el usuario
            userRepository.save(newUser)

            // Mostrar mensaje de éxito
            showAlert(
                Alert.AlertType.INFORMATION,
                "Registro exitoso",
                "Su cuenta ha sido creada correctamente. Ahora puede iniciar sesión."
            )

            // Navegar a la pantalla de login
            navigateToLogin()

        } catch (e: Exception) {
            logger.error { "Error al registrar usuario: ${e.message}" }
            showError("Error al registrar usuario: ${e.message}")
        } finally {
            // Ocultar el indicador de carga y habilitar el botón
            showLoadingIndicator(false)
            registerButton.isDisable = false
        }
    }

    /**
     * Muestra un mensaje de error en la interfaz.
     */
    private fun showError(message: String) {
        errorMessage.text = message
        showLoadingIndicator(false)
        registerButton.isDisable = false
    }

    /**
     * Navega a la pantalla de inicio de sesión.
     */
    private fun navigateToLogin() {
        try {
            logger.debug { "Navegando a la pantalla de inicio de sesión" }
            val loader = FXMLLoader(RoutesManager.getResource(RoutesManager.View.LOGIN.fxml))
            val stage = usernameField.scene.window as Stage
            stage.scene = Scene(loader.load())
            stage.title = "Iniciar Sesión"
            logger.debug { "Navegación a la pantalla de inicio de sesión exitosa" }
        } catch (e: Exception) {
            logger.error { "Error al navegar a la pantalla de inicio de sesión: ${e.message}" }
        }
    }

    /**
     * Muestra u oculta el indicador de carga durante el proceso de registro.
     * 
     * @param show True para mostrar el indicador, False para ocultarlo
     */
    private fun showLoadingIndicator(show: Boolean) {
        loadingContainer.isVisible = show
        loadingContainer.isManaged = show
    }

    /**
     * Muestra una alerta con el tipo, título y mensaje especificados.
     * 
     * @param type El tipo de alerta (ERROR, INFORMATION, etc.)
     * @param title El título de la alerta
     * @param message El mensaje a mostrar en la alerta
     */
    private fun showAlert(type: Alert.AlertType, title: String, message: String) {
        Alert(type).apply {
            this.title = title
            headerText = null
            contentText = message
        }.showAndWait()
    }
}
