package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.stage.Stage
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager

/**
 * Controlador para la pantalla de bienvenida.
 * Maneja la navegación a las pantallas de inicio de sesión y registro.
 */
class WelcomeController {
    private val logger = logging()

    @FXML
    private lateinit var loginButton: Button

    @FXML
    private lateinit var registerButton: Button

    @FXML
    private lateinit var logoImage: ImageView

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura los eventos de los botones.
     */
    @FXML
    private fun initialize() {
        logger.debug { "Inicializando WelcomeController" }

        // Configurar el evento del botón de inicio de sesión
        loginButton.setOnAction {
            logger.debug { "Botón de inicio de sesión presionado" }
            navigateToLogin()
        }

        // Configurar el evento del botón de registro
        registerButton.setOnAction {
            logger.debug { "Botón de registro presionado" }
            navigateToRegister()
        }
    }

    /**
     * Navega a la pantalla de inicio de sesión.
     */
    private fun navigateToLogin() {
        try {
            logger.debug { "Navegando a la pantalla de inicio de sesión" }
            val loader = FXMLLoader(RoutesManager.getResource(RoutesManager.View.LOGIN.fxml))
            val stage = loginButton.scene.window as Stage
            stage.scene = Scene(loader.load(), 1920.0, 1080.0)
            stage.title = "Iniciar Sesión"
            logger.debug { "Navegación a la pantalla de inicio de sesión exitosa" }
        } catch (e: Exception) {
            logger.error { "Error al navegar a la pantalla de inicio de sesión: ${e.message}" }
            logger.error { "Stack trace: ${e.stackTraceToString()}" }
        }
    }

    /**
     * Navega a la pantalla de registro.
     */
    private fun navigateToRegister() {
        try {
            logger.debug { "Navegando a la pantalla de registro" }
            val loader = FXMLLoader(RoutesManager.getResource(RoutesManager.View.REGISTER.fxml))
            val stage = registerButton.scene.window as Stage
            stage.scene = Scene(loader.load(), 1920.0, 1080.0)
            stage.title = "Crear Cuenta"
            logger.debug { "Navegación a la pantalla de registro exitosa" }
        } catch (e: Exception) {
            logger.error { "Error al navegar a la pantalla de registro: ${e.message}" }
            logger.error { "Stack trace: ${e.stackTraceToString()}" }
        }
    }
}