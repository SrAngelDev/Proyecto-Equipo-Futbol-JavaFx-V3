package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import srangeldev.proyectoequipofutboljavafx.viewmodels.PersonalViewModel

/**
 * Controlador para la pantalla de inicio de sesión.
 * Implementa el patrón MVVM utilizando PersonalViewModel para la lógica de negocio.
 */
class LoggingController : KoinComponent {
    private val logger = logging()

    // Inyectar el ViewModel usando Koin
    private val viewModel: PersonalViewModel by inject()

    @FXML
    private lateinit var usuarioField: TextField

    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var ingresarButton: Button

    @FXML
    private lateinit var logoImage: ImageView

    @FXML
    private lateinit var loadingContainer: HBox

    @FXML
    private lateinit var loginProgress: ProgressIndicator

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura los bindings con el ViewModel y los eventos de la interfaz.
     */
    @FXML
    private fun initialize() {
        logger.debug { "Inicializando LoggingController" }

        // Inicializar el ViewModel
        viewModel.initialize()

        // Configurar bindings bidireccionales con el ViewModel
        usuarioField.textProperty().bindBidirectional(viewModel.username)
        passwordField.textProperty().bindBidirectional(viewModel.password)

        // Observar cambios en el resultado del login
        viewModel.loginResult.addListener { _, _, newValue ->
            newValue?.let { handleLoginResult(it) }
        }

        // Observar errores
        viewModel.error.addListener { _, _, newValue ->
            if (newValue.isNotEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", newValue)
            }
        }

        // Configurar el evento del botón ingresar
        ingresarButton.setOnAction { 
            // Mostrar el indicador de carga
            showLoadingIndicator(true)

            // Deshabilitar el botón mientras se procesa
            ingresarButton.isDisable = true

            // Intentar iniciar sesión
            viewModel.login()
        }
    }

    /**
     * Maneja el resultado del inicio de sesión según el valor devuelto por el ViewModel.
     * 
     * @param result El resultado del inicio de sesión
     */
    private fun handleLoginResult(result: PersonalViewModel.LoginResult) {
        // Ocultar el indicador de carga y habilitar el botón
        showLoadingIndicator(false)
        ingresarButton.isDisable = false

        when (result) {
            PersonalViewModel.LoginResult.EMPTY_FIELDS -> {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Campos vacíos",
                    "Por favor complete todos los campos"
                )
            }
            PersonalViewModel.LoginResult.INVALID_CREDENTIALS -> {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Credenciales inválidas",
                    "Usuario o contraseña incorrectos"
                )
                viewModel.clearPassword()
            }
            PersonalViewModel.LoginResult.ADMIN_LOGIN -> {
                showAlert(
                    Alert.AlertType.INFORMATION,
                    "Login exitoso",
                    "Bienvenido Administrador"
                )
                cargarVistaAdmin()
                viewModel.clearPassword()
            }
            PersonalViewModel.LoginResult.USER_LOGIN -> {
                showAlert(
                    Alert.AlertType.INFORMATION,
                    "Login exitoso",
                    "Bienvenido Usuario normal"
                )
                cargarVistaUsuario()
                viewModel.clearPassword()
            }
        }
    }

    /**
     * Carga la vista de administrador después de un inicio de sesión exitoso como administrador.
     * Maneja posibles errores durante la carga de la vista.
     */
    private fun cargarVistaAdmin() {
        try {
            logger.debug { "Intentando cargar vista de administración" }
            val loader = FXMLLoader(RoutesManager.getResource(srangeldev.proyectoequipofutboljavafx.routes.RoutesManager.View.ADMIN.fxml))
            val stage = usuarioField.scene.window as Stage
            logger.debug { "Stage obtenido correctamente" }
            stage.scene = Scene(loader.load(), 1920.0, 1080.0)
            logger.debug { "Scene cargada correctamente con dimensiones 1920x1080" }
            stage.title = "Panel de Administración"
            logger.debug { "Vista de administración cargada correctamente" }
        } catch (e: Exception) {
            logger.error { "Error al cargar la vista de administración: ${e.message}" }
            logger.error { "Stack trace: ${e.stackTraceToString()}" }
            showAlert(
                Alert.AlertType.ERROR,
                "Error de navegación",
                "No se pudo cargar la vista de administración: ${e.message}"
            )
        }
    }

    /**
     * Carga la vista de usuario normal después de un inicio de sesión exitoso como usuario estándar.
     * Maneja posibles errores durante la carga de la vista.
     */
    private fun cargarVistaUsuario() {
        try {
            logger.debug { "Intentando cargar vista de usuario normal" }
            val loader = FXMLLoader(srangeldev.proyectoequipofutboljavafx.routes.RoutesManager.getResource(srangeldev.proyectoequipofutboljavafx.routes.RoutesManager.View.NORMAL.fxml))
            val stage = usuarioField.scene.window as Stage
            logger.debug { "Stage obtenido correctamente" }
            stage.scene = Scene(loader.load(), 1920.0, 1080.0)
            logger.debug { "Scene cargada correctamente con dimensiones 1920x1080" }
            stage.title = "Panel de Usuario Normal"
            logger.debug { "Vista de usuario normal cargada correctamente" }
        } catch (e: Exception) {
            logger.error { "Error al cargar la vista de usuario normal: ${e.message}" }
            logger.error { "Stack trace: ${e.stackTraceToString()}" }
            showAlert(
                Alert.AlertType.ERROR,
                "Error de navegación",
                "No se pudo cargar la vista de usuario normal: ${e.message}"
            )
        }
    }

    /**
     * Muestra u oculta el indicador de carga durante el proceso de inicio de sesión.
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
