package srangeldev.proyectoequipofutboljavafx.routes

import javafx.animation.FadeTransition
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.WindowEvent
import javafx.util.Duration
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.controllers.SplashScreenController
import srangeldev.proyectoequipofutboljavafx.NewTeamApplication
import srangeldev.proyectoequipofutboljavafx.newteam.session.Session
import java.net.URL
import java.util.*

private val logger = logging()

/**
 * Clase que gestiona las rutas de la aplicación
 */
object RoutesManager {
    private lateinit var mainStage: Stage
    private lateinit var _activeStage: Stage
    val activeStage: Stage
        get() = _activeStage
    lateinit var app: Application

    // Definimos las rutas de las vistas que tengamos
    enum class View(val fxml: String) {
        SPLASH("views/newTeam/splash-screen.fxml"),
        WELCOME("views/newTeam/welcome.fxml"),
        LOGIN("views/newTeam/logging.fxml"),
        REGISTER("views/newTeam/register.fxml"),
        NORMAL("views/newTeam/vista-normal.fxml"),
        ADMIN("views/newTeam/vista-admin.fxml"),
        ACERCA_DE("views/acerca-de/acerca-de-view.fxml"),
    }

    init {
        logger.debug { "Inicializando RoutesManager" }
        Locale.setDefault(Locale.forLanguageTag("es-ES"))
    }

    fun initSplashScreenStage(stage: Stage) {
        logger.debug { "Inicializando MainStage con SplashScreen" }

        // Initialize mainStage and _activeStage
        mainStage = stage
        _activeStage = stage

        val fxmlLoader = FXMLLoader(getResource(View.SPLASH.fxml))

        val scene = Scene(fxmlLoader.load())

        // Configurar el stage
        stage.apply {
            isResizable = false
            icons.add(Image(NewTeamApplication::class.java.getResourceAsStream("icons/newTeamLogo.png")))
            title = "Cargando..."
            this.scene = scene
            show()
        }

        // El controlador SplashScreenController se encargará de la animación y la carga de recursos
        // No necesitamos duplicar esa lógica aquí
    }

    private fun welcomeStage(stage: Stage) {
        val fxmlLoader = FXMLLoader(getResource(View.WELCOME.fxml))
        stage.apply {
            scene = Scene(fxmlLoader.load())
            title = "Bienvenido"
            show()
        }
        _activeStage = stage
    }

    fun getResource(resource: String): URL {
        return app::class.java.getResource(resource)
            ?: throw RuntimeException("No se ha encontrado el recurso: $resource")
    }

    fun onAppExit(
        title: String = "Salir de ${mainStage.title}?",
        headerText: String = "¿Estás seguro de que quieres salir de ${mainStage.title}?",
        contentText: String = "Si sales, se cerrará la aplicación y perderás todos los datos no guardados",
        event: WindowEvent? = null
    ) {
        logger.debug { "Cerrando..." }
        // Cerramos la aplicación
        Alert(Alert.AlertType.CONFIRMATION).apply {
            this.title = title
            this.headerText = headerText
            this.contentText = contentText
        }.showAndWait().ifPresent { opcion ->
            if (opcion == ButtonType.OK) {
                Platform.exit()
            } else {
                event?.consume()
            }
        }
    }

    /**
     * Muestra un diálogo de confirmación para cerrar sesión y volver a la pantalla de login.
     * 
     * @param title El título del diálogo.
     * @param headerText El texto de cabecera del diálogo.
     * @param contentText El texto de contenido del diálogo.
     */
    fun onLogout(
        title: String = "Cerrar sesión",
        headerText: String = "¿Estás seguro de que quieres cerrar sesión?",
        contentText: String = "Si cierras sesión, volverás a la pantalla de inicio de sesión."
    ) {
        logger.debug { "Cerrando sesión..." }
        // Mostramos diálogo de confirmación
        Alert(Alert.AlertType.CONFIRMATION).apply {
            this.title = title
            this.headerText = headerText
            this.contentText = contentText
        }.showAndWait().ifPresent { opcion ->
            if (opcion == ButtonType.OK) {
                // Cerrar sesión
                Session.logout()
                // Volver a la pantalla de bienvenida
                welcomeStage(mainStage)
            }
        }
    }
}
