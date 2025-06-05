package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.animation.FadeTransition
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ProgressBar
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager

/**
 * Controlador para la pantalla de carga inicial (splash screen).
 * Maneja las animaciones de aparición gradual de los elementos visuales
 * y la animación de la barra de progreso.
 */
class SplashScreenController {
    @FXML
    lateinit var progressBar: ProgressBar

    @FXML
    lateinit var loadingText: Text

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura y ejecuta las animaciones de aparición gradual y la animación de la barra de progreso.
     */
    @FXML
    private fun initialize() {
        // Aplicar animación de aparición gradual a la barra de progreso
        createFadeInTransition(progressBar).play()

        // Aplicar animación de aparición gradual al texto de carga
        createFadeInTransition(loadingText).play()

        // Iniciar la animación de la barra de progreso
        startProgressAnimation()
    }

    /**
     * Crea una transición de aparición gradual para un nodo JavaFX.
     * 
     * @param node El nodo al que se aplicará la animación
     * @return La transición configurada lista para ser reproducida
     */
    private fun createFadeInTransition(node: Any): FadeTransition {
        val fadeTransition = FadeTransition(Duration.seconds(1.0), node as javafx.scene.Node)
        fadeTransition.fromValue = 0.0
        fadeTransition.toValue = 1.0
        return fadeTransition
    }

    /**
     * Inicia la animación de la barra de progreso.
     * La barra se llena gradualmente de 0% a 100% en 5 segundos.
     * Al finalizar, se transiciona a la pantalla de login.
     */
    private fun startProgressAnimation() {
        // Creamos un Timeline para animar la barra de progreso
        val timeline = Timeline(
            KeyFrame(Duration.ZERO, KeyValue(progressBar.progressProperty(), 0.0)),
            KeyFrame(Duration.seconds(1.0), KeyValue(progressBar.progressProperty(), 0.3)),
            KeyFrame(Duration.seconds(2.0), KeyValue(progressBar.progressProperty(), 0.6)),
            KeyFrame(Duration.seconds(3.5), KeyValue(progressBar.progressProperty(), 0.8)),
            KeyFrame(Duration.seconds(5.0), KeyValue(progressBar.progressProperty(), 1.0))
        )

        // Actualizar el texto de carga según el progreso
        timeline.currentTimeProperty().addListener { _, _, newValue ->
            val progress = newValue.toSeconds() / 5.0
            when {
                progress < 0.3 -> loadingText.text = "Inicializando..."
                progress < 0.6 -> loadingText.text = "Cargando datos..."
                progress < 0.9 -> loadingText.text = "Preparando aplicación..."
                else -> loadingText.text = "¡Listo!"
            }
        }

        // Configurar acción al finalizar la animación
        timeline.setOnFinished {
            // Crear una transición de desvanecimiento para la escena actual
            val scene = progressBar.scene
            val fadeOut = FadeTransition(Duration.seconds(1.0), scene.root)
            fadeOut.fromValue = 1.0
            fadeOut.toValue = 0.0
            fadeOut.setOnFinished {
                // Transicionar a la pantalla de login
                val stage = progressBar.scene.window as Stage
                loadLoginScreen(stage)
            }
            fadeOut.play()
        }

        // Iniciar la animación
        timeline.play()
    }

    /**
     * Carga la pantalla de login en el stage proporcionado.
     * 
     * @param stage El stage donde se cargará la pantalla de login
     */
    private fun loadLoginScreen(stage: Stage) {
        try {
            // Cargar la vista de login
            val loginResource = RoutesManager.View.LOGIN.fxml
            val resourceUrl = RoutesManager.app::class.java.getResource(loginResource)
                ?: throw RuntimeException("No se ha encontrado el recurso: $loginResource")

            val fxmlLoader = FXMLLoader(resourceUrl)

            // Configurar el stage con la nueva escena
            stage.apply {
                scene = Scene(fxmlLoader.load())
                title = "Login"
                show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // En caso de error, mostrar un mensaje en la consola
            println("Error al cargar la pantalla de login: ${e.message}")
        }
    }
}
