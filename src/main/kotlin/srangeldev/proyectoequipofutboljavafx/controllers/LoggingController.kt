package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.newteam.session.Session
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import srangeldev.proyectoequipofutboljavafx.viewmodels.PersonalViewModel
import srangeldev.proyectoequipofutboljavafx.newteam.models.User

/**
 * Controlador para la pantalla de inicio de sesión.
 * Implementa el patrón MVVM utilizando PersonalViewModel para la lógica de negocio.
 */
class LoggingController : KoinComponent {
    private val logger = logging()

    // Inyectar el ViewModel usando Koin
    private val viewModel: PersonalViewModel by inject()

    // Repositorio de usuarios para operaciones directas
    private val userRepository: UserRepository by inject()


    @FXML
    private lateinit var usuarioField: TextField

    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var ingresarButton: Button

    @FXML
    private lateinit var loadingContainer: HBox

    @FXML
    private lateinit var rememberMeCheckbox: CheckBox

    @FXML
    private lateinit var forgotPasswordLink: Hyperlink

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

        // Cargar credenciales guardadas si existen
        loadSavedCredentials()

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

            // Guardar credenciales si está marcado "Recordarme"
            if (rememberMeCheckbox.isSelected) {
                saveCredentials()
            } else {
                clearSavedCredentials()
            }

            // Intentar iniciar sesión
            viewModel.login()
        }

        // Configurar el evento del enlace "Olvidó su contraseña"
        forgotPasswordLink.setOnAction {
            showForgotPasswordDialog()
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
            val loader = FXMLLoader(RoutesManager.getResource(RoutesManager.View.ADMIN.fxml))
            val root = loader.load<Parent>()
            val stage = usuarioField.scene.window as Stage
            stage.scene = Scene(root)
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
            val loader = FXMLLoader(RoutesManager.getResource(RoutesManager.View.NORMAL.fxml))
            val root = loader.load<Parent>()
            val stage = usuarioField.scene.window as Stage
            stage.scene = Scene(root)
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

    /**
     * Carga las credenciales guardadas si existen.
     */
    private fun loadSavedCredentials() {
        logger.debug { "Cargando credenciales guardadas" }

        if (Session.hasRememberedCredentials()) {
            val savedUsername = Session.getRememberedUsername()
            val savedPassword = Session.getRememberedPassword()

            if (savedUsername != null && savedPassword != null) {
                usuarioField.text = savedUsername
                passwordField.text = savedPassword
                rememberMeCheckbox.isSelected = true
            }
        }
    }

    /**
     * Guarda las credenciales cuando se marca "Recordarme".
     */
    private fun saveCredentials() {
        logger.debug { "Guardando credenciales" }
        Session.saveCredentials(usuarioField.text, passwordField.text)
    }

    /**
     * Limpia las credenciales guardadas.
     */
    private fun clearSavedCredentials() {
        logger.debug { "Limpiando credenciales guardadas" }
        Session.clearCredentials()
    }

    /**
     * Muestra el diálogo para restablecer la contraseña olvidada.
     */
    private fun showForgotPasswordDialog() {
        logger.debug { "Mostrando diálogo de contraseña olvidada" }

        // Crear diálogo para ingresar el nombre de usuario
        val dialog = Dialog<String>()
        dialog.title = "Recuperar Contraseña"
        dialog.headerText = null // Eliminar el headerText predeterminado

        // Aplicar estilos al diálogo
        val dialogPane = dialog.dialogPane
        dialogPane.stylesheets.add(RoutesManager.getResource("styles/logging.css").toExternalForm())
        dialogPane.styleClass.addAll("login-container", "main-container", "dialog-pane")

        // Establecer un tamaño mínimo para el diálogo
        dialogPane.prefWidth = 450.0
        dialogPane.prefHeight = 300.0

        // Crear un encabezado personalizado con estilo
        val headerText = Label("Ingrese su nombre de usuario")
        headerText.styleClass.addAll("dialog-header-text")

        // Agregar un separador estilizado
        val separator = Separator()
        separator.styleClass.addAll("yellow-separator", "dialog-separator")

        // Crear contenedor para el encabezado
        val headerArea = VBox(10.0)
        headerArea.styleClass.addAll("header-container", "brand-container", "dialog-header-area")
        headerArea.children.addAll(headerText, separator)

        // Establecer el encabezado personalizado
        dialogPane.header = headerArea

        // Configurar botones
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        // Estilizar los botones
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.styleClass.addAll("login-button", "button-container")

        val cancelButton = dialogPane.lookupButton(ButtonType.CANCEL)
        cancelButton.styleClass.addAll("login-button", "button-container")

        // Crear campo de texto para el nombre de usuario
        val usernameField = TextField()
        usernameField.promptText = "Nombre de usuario"
        usernameField.styleClass.add("dark-text-field")

        // Crear etiqueta para el campo
        val usernameLabel = Label("Nombre de Usuario:")
        usernameLabel.styleClass.addAll("field-label", "quote-text")

        // Crear contenido del diálogo con mejor espaciado y estilo
        val content = VBox(15.0)
        content.styleClass.addAll("form-container", "fields-container", "dialog-content-area")
        content.children.addAll(usernameLabel, usernameField)
        dialogPane.content = content

        // Configurar resultado del diálogo
        dialog.setResultConverter { buttonType ->
            if (buttonType == ButtonType.OK) usernameField.text else null
        }

        // Mostrar diálogo y procesar resultado
        val result = dialog.showAndWait()

        result.ifPresent { username ->
            if (username.isNotEmpty()) {
                // Verificar si el usuario existe
                val user = userRepository.getByUsername(username)
                if (user != null) {
                    // Verificar si el usuario es administrador
                    if (user.role == User.Role.ADMIN) {
                        // No permitir cambio de contraseña para administradores
                        showAlert(
                            Alert.AlertType.ERROR,
                            "Operación no permitida",
                            "Los usuarios administradores no pueden cambiar su contraseña por este método."
                        )
                    } else {
                        // Usuario normal, mostrar diálogo para cambiar contraseña
                        showPasswordResetDialog(user.id, username)
                    }
                } else {
                    // Usuario no encontrado
                    showAlert(
                        Alert.AlertType.ERROR,
                        "Usuario no encontrado",
                        "No existe ningún usuario con ese nombre de usuario."
                    )
                }
            } else {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Campo vacío",
                    "Por favor ingrese un nombre de usuario."
                )
            }
        }
    }

    /**
     * Muestra el diálogo para restablecer la contraseña.
     * 
     * @param userId El ID del usuario
     * @param username El nombre de usuario
     */
    private fun showPasswordResetDialog(userId: Int, username: String) {
        logger.debug { "Mostrando diálogo de restablecimiento de contraseña para el usuario: $username" }

        // Crear diálogo para ingresar la nueva contraseña
        val dialog = Dialog<Pair<String, String>>()
        dialog.title = "Restablecer Contraseña"
        dialog.headerText = null // Eliminar el headerText predeterminado

        // Aplicar estilos al diálogo
        val dialogPane = dialog.dialogPane
        dialogPane.stylesheets.add(RoutesManager.getResource("styles/logging.css").toExternalForm())
        dialogPane.styleClass.addAll("login-container", "main-container", "dialog-pane")

        // Establecer un tamaño mínimo para el diálogo
        dialogPane.prefWidth = 450.0
        dialogPane.prefHeight = 350.0

        // Crear un encabezado personalizado con estilo
        val headerText = Label("Ingrese su nueva contraseña")
        headerText.styleClass.addAll("dialog-header-text")

        // Agregar un separador estilizado
        val separator = Separator()
        separator.styleClass.addAll("yellow-separator", "dialog-separator")

        // Crear contenedor para el encabezado
        val headerArea = VBox(10.0)
        headerArea.styleClass.addAll("header-container", "brand-container", "dialog-header-area")
        headerArea.children.addAll(headerText, separator)

        // Establecer el encabezado personalizado
        dialogPane.header = headerArea

        // Configurar botones
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        // Estilizar los botones
        val okButton = dialogPane.lookupButton(ButtonType.OK)
        okButton.styleClass.addAll("login-button", "button-container")

        val cancelButton = dialogPane.lookupButton(ButtonType.CANCEL)
        cancelButton.styleClass.addAll("login-button", "button-container")

        // Crear campos de texto para las contraseñas con contenedores estilizados
        val passwordField = PasswordField()
        passwordField.promptText = "Nueva contraseña"
        passwordField.styleClass.add("dark-text-field")

        val confirmPasswordField = PasswordField()
        confirmPasswordField.promptText = "Confirmar contraseña"
        confirmPasswordField.styleClass.add("dark-text-field")

        // Crear etiquetas para los campos
        val passwordLabel = Label("Nueva contraseña:")
        passwordLabel.styleClass.addAll("field-label", "quote-text")

        val confirmPasswordLabel = Label("Confirmar contraseña:")
        confirmPasswordLabel.styleClass.addAll("field-label", "quote-text")

        // Crear contenido del diálogo con mejor espaciado y estilo
        val content = VBox(15.0)
        content.styleClass.addAll("form-container", "fields-container", "dialog-content-area")
        content.children.addAll(
            passwordLabel,
            passwordField,
            confirmPasswordLabel,
            confirmPasswordField
        )
        dialogPane.content = content

        // Configurar resultado del diálogo
        dialog.setResultConverter { buttonType ->
            if (buttonType == ButtonType.OK) Pair(passwordField.text, confirmPasswordField.text) else null
        }

        // Mostrar diálogo y procesar resultado
        val result = dialog.showAndWait()

        result.ifPresent { (password, confirmPassword) ->
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Campos vacíos",
                    "Por favor complete todos los campos."
                )
            } else if (password != confirmPassword) {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Contraseñas no coinciden",
                    "Las contraseñas ingresadas no coinciden."
                )
            } else {
                // Actualizar la contraseña del usuario
                try {
                    // Obtener el usuario por nombre de usuario
                    val user = userRepository.getByUsername(username)
                    if (user != null) {
                        // Crear un nuevo objeto User con la contraseña actualizada
                        val updatedUser = user.copy(password = password)

                        // Actualizar el usuario en la base de datos usando el ID proporcionado
                        val result = userRepository.update(userId, updatedUser)

                        if (result != null) {
                            logger.debug { "Contraseña actualizada correctamente para el usuario: $username" }
                            showAlert(
                                Alert.AlertType.INFORMATION,
                                "Contraseña actualizada",
                                "Su contraseña ha sido actualizada correctamente."
                            )
                        } else {
                            logger.error { "No se pudo actualizar la contraseña para el usuario: $username" }
                            showAlert(
                                Alert.AlertType.ERROR,
                                "Error",
                                "No se pudo actualizar la contraseña. Por favor, inténtelo de nuevo."
                            )
                        }
                    }
                } catch (e: Exception) {
                    logger.error { "Error al actualizar la contraseña: ${e.message}" }
                    showAlert(
                        Alert.AlertType.ERROR,
                        "Error",
                        "Ocurrió un error al actualizar la contraseña: ${e.message}"
                    )
                }
            }
        }
    }
}
