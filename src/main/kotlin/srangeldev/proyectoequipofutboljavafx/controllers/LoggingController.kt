package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.stage.Stage
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.models.User
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepository
import srangeldev.proyectoequipofutboljavafx.newteam.repository.UserRepositoryImpl
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager.app
import srangeldev.proyectoequipofutboljavafx.newteam.session.Session

class LoggingController {
    private val logger = logging()
    private val userRepository: UserRepository = UserRepositoryImpl()

    @FXML
    private lateinit var usuarioField: TextField

    @FXML
    private lateinit var passwordField: PasswordField

    @FXML
    private lateinit var ingresarButton: Button

    @FXML
    private lateinit var logoImage: ImageView

    @FXML
    private fun initialize() {
        logger.debug { "Inicializando LoggingController" }

        // Inicializar el repositorio de usuarios (esto creará la tabla y los usuarios por defecto si no existen)
        userRepository.initDefaultUsers()

        // Configurar el evento del botón ingresar
        ingresarButton.setOnAction { handleIngresar() }
    }

    private fun handleIngresar() {
        logger.debug { "Procesando intento de login" }

        val username = usuarioField.text.trim()
        val password = passwordField.text.trim()

        // Validar campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(
                Alert.AlertType.ERROR,
                "Campos vacíos",
                "Por favor complete todos los campos"
            )
            return
        }

        // Verificar credenciales usando el repositorio de usuarios
        val user = userRepository.verifyCredentials(username, password)

        if (user != null) {
            // Establecer el usuario en la sesión
            Session.setCurrentUser(user)

            // Manejar login exitoso según el rol del usuario
            when (user.role) {
                User.Role.ADMIN -> handleSuccessfulLogin(TipoUsuario.ADMIN)
                User.Role.USER -> handleSuccessfulLogin(TipoUsuario.USUARIO)
            }
        } else {
            showAlert(
                Alert.AlertType.ERROR,
                "Credenciales inválidas",
                "Usuario o contraseña incorrectos"
            )
            passwordField.clear()
        }
    }

    private fun handleSuccessfulLogin(tipoUsuario: TipoUsuario) {
        showAlert(
            Alert.AlertType.INFORMATION,
            "Login exitoso",
            "Bienvenido ${tipoUsuario.nombre}"
        )

        when (tipoUsuario) {
            TipoUsuario.ADMIN -> cargarVistaAdmin()
            TipoUsuario.USUARIO -> cargarVistaUsuario()
        }

        // Limpiar campos sensibles
        passwordField.clear()
    }

    private fun cargarVistaAdmin() {
        try {
            logger.debug { "Intentando cargar vista de administración" }
            val loader = FXMLLoader(app::class.java.getResource("views/newTeam/vista-admin.fxml"))
            val stage = usuarioField.scene.window as Stage
            logger.debug { "Stage obtenido correctamente" }
            stage.scene = Scene(loader.load(), 1280.0, 720.0)
            logger.debug { "Scene cargada correctamente con dimensiones 1280x720" }
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

    private fun cargarVistaUsuario() {
        try {
            logger.debug { "Intentando cargar vista de usuario normal" }
            val loader = FXMLLoader(app::class.java.getResource("views/newTeam/vista-normal.fxml"))
            val stage = usuarioField.scene.window as Stage
            logger.debug { "Stage obtenido correctamente" }
            stage.scene = Scene(loader.load(), 1280.0, 720.0)
            logger.debug { "Scene cargada correctamente con dimensiones 1280x720" }
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

    private fun showAlert(type: Alert.AlertType, title: String, message: String) {
        Alert(type).apply {
            this.title = title
            headerText = null
            contentText = message
        }.showAndWait()
    }

    enum class TipoUsuario(val nombre: String) {
        ADMIN("Administrador"),
        USUARIO("Usuario normal")
    }
}
