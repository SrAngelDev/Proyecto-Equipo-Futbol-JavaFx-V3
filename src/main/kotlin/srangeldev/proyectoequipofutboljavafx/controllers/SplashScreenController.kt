package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.animation.FadeTransition
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.ProgressBar
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager

/**
 * Controlador para la pantalla de carga inicial (splash screen).
 * Maneja las animaciones de aparición gradual de los elementos visuales
 * y la carga real de recursos en segundo plano mientras muestra la animación.
 */
class SplashScreenController {
    private val logger = logging()

    @FXML
    lateinit var progressBar: ProgressBar

    @FXML
    lateinit var loadingText: Text

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura y ejecuta las animaciones de aparición gradual y comienza la carga real de recursos.
     */
    @FXML
    private fun initialize() {
        // Aplicar animación de aparición gradual a la barra de progreso
        createFadeInTransition(progressBar).play()

        // Aplicar animación de aparición gradual al texto de carga
        createFadeInTransition(loadingText).play()

        // Iniciar la carga real de recursos en segundo plano
        startResourceLoading()
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
     * Inicia la carga real de recursos en segundo plano.
     * Actualiza la barra de progreso y el texto de carga según el progreso real.
     */
    private fun startResourceLoading() {
        // Crear una tarea en segundo plano para cargar recursos
        val loadingTask = object : Task<Boolean>() {
            override fun call(): Boolean {
                try {
                    // Paso 1: Inicialización básica (20%)
                    updateProgress(0.1, 1.0)
                    updateMessage("Inicializando componentes...")
                    Thread.sleep(500) // Simular tiempo de inicialización

                    // Paso 2: Inicializar la base de datos (50%)
                    updateProgress(0.2, 1.0)
                    updateMessage("Conectando a la base de datos...")

                    // Inicializar la base de datos (operación real)
                    val dbManager = DataBaseManager.instance

                    updateProgress(0.5, 1.0)
                    updateMessage("Cargando datos...")
                    Thread.sleep(300) // Pequeña pausa para mostrar el progreso

                    // Paso 3: Cargar recursos adicionales (80%)
                    updateProgress(0.8, 1.0)
                    updateMessage("Preparando aplicación...")

                    // Precargar recursos adicionales si es necesario
                    // Por ejemplo, precargar imágenes, configuraciones, etc.
                    Thread.sleep(500) // Simular carga de recursos adicionales

                    // Paso 4: Finalización (100%)
                    updateProgress(1.0, 1.0)
                    updateMessage("¡Listo!")
                    Thread.sleep(300) // Pequeña pausa antes de continuar

                    return true
                } catch (e: Exception) {
                    logger.error { "Error durante la carga de recursos: ${e.message}" }
                    e.printStackTrace()
                    return false
                }
            }
        }

        // Vincular la barra de progreso y el texto de carga a la tarea
        progressBar.progressProperty().bind(loadingTask.progressProperty())

        // Actualizar el texto de carga según el mensaje de la tarea
        loadingTask.messageProperty().addListener { _, _, newValue ->
            Platform.runLater { loadingText.text = newValue }
        }

        // Configurar acción al finalizar la tarea
        loadingTask.setOnSucceeded {
            // Crear una transición de desvanecimiento suave para la escena actual
            val scene = progressBar.scene
            val fadeOut = FadeTransition(Duration.seconds(1.0), scene.root)
            fadeOut.fromValue = 1.0
            fadeOut.toValue = 0.0
            fadeOut.setOnFinished {
                // Transicionar a la pantalla de bienvenida
                val stage = progressBar.scene.window as Stage
                loadWelcomeScreen(stage)
            }
            fadeOut.play()
        }

        // Manejar errores durante la carga
        loadingTask.setOnFailed {
            logger.error { "Error en la carga de recursos: ${loadingTask.exception?.message}" }
            loadingTask.exception?.printStackTrace()

            // Mostrar mensaje de error y continuar de todos modos
            Platform.runLater { 
                loadingText.text = "Error al cargar recursos. Continuando..."

                // Esperar un momento y continuar
                Timeline(KeyFrame(Duration.seconds(2.0), {
                    val stage = progressBar.scene.window as Stage
                    loadWelcomeScreen(stage)
                })).play()
            }
        }

        // Iniciar la tarea en un hilo separado
        Thread(loadingTask).start()
    }

    /**
     * Carga la pantalla de bienvenida en el stage proporcionado.
     * 
     * @param stage El stage donde se cargará la pantalla de bienvenida
     */
    private fun loadWelcomeScreen(stage: Stage) {
        try {
            // Cargar la vista de bienvenida
            val welcomeResource = RoutesManager.View.WELCOME.fxml
            val resourceUrl = RoutesManager.app::class.java.getResource(welcomeResource)
                ?: throw RuntimeException("No se ha encontrado el recurso: $welcomeResource")

            val fxmlLoader = FXMLLoader(resourceUrl)

            // Configurar el stage con la nueva escena
            stage.apply {
                scene = Scene(fxmlLoader.load())
                title = "Bienvenido"
                show()
            }
        } catch (e: Exception) {
            logger.error { "Error al cargar la pantalla de bienvenida: ${e.message}" }
            e.printStackTrace()

            // En caso de error, intentar cargar la pantalla de login como alternativa
            loadLoginScreen(stage)
        }
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
            logger.error { "Error al cargar la pantalla de login: ${e.message}" }
            e.printStackTrace()
        }
    }
}
