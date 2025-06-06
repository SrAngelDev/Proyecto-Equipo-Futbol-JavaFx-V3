package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.fxml.FXML
import javafx.scene.control.Hyperlink
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.control.Alert
import org.lighthousegames.logging.logging
import java.awt.Desktop
import java.net.URI

/**
 * Controller for the About dialog
 */
class AboutDialogController {
    private val logger = logging()

    @FXML
    private lateinit var angelImage: ImageView

    @FXML
    private lateinit var jorgeImage: ImageView

    @FXML
    private lateinit var angelGithubLink: Hyperlink

    @FXML
    private lateinit var jorgeGithubLink: Hyperlink

    /**
     * Initialize the controller
     */
    @FXML
    fun initialize() {
        // Load developer images
        loadDeveloperImages()

        // Set up GitHub links
        setupGithubLinks()
    }

    /**
     * Load developer images
     */
    private fun loadDeveloperImages() {
        try {
            val angelImageUrl = javaClass.getResource("/srangeldev/proyectoequipofutboljavafx/images/angel.png")
            if (angelImageUrl != null) {
                angelImage.image = Image(angelImageUrl.toString())
            } else {
                angelImage.image = Image("/srangeldev/proyectoequipofutboljavafx/images/sin-image.png")
            }
        } catch (e: Exception) {
            logger.error { "Error loading Angel's image: ${e.message}" }
            angelImage.image = Image("/srangeldev/proyectoequipofutboljavafx/images/sin-image.png")
        }

        try {
            val jorgeImageUrl = javaClass.getResource("/srangeldev/proyectoequipofutboljavafx/images/jorge.png")
            if (jorgeImageUrl != null) {
                jorgeImage.image = Image(jorgeImageUrl.toString())
            } else {
                jorgeImage.image = Image("/srangeldev/proyectoequipofutboljavafx/images/sin-image.png")
            }
        } catch (e: Exception) {
            logger.error { "Error loading Jorge's image: ${e.message}" }
            jorgeImage.image = Image("/srangeldev/proyectoequipofutboljavafx/images/sin-image.png")
        }
    }

    /**
     * Set up GitHub links
     */
    private fun setupGithubLinks() {
        angelGithubLink.setOnAction {
            try {
                Desktop.getDesktop().browse(URI("https://github.com/srangeldev"))
            } catch (e: Exception) {
                logger.error { "Error opening browser: ${e.message}" }
                showErrorDialog("Error", "No se pudo abrir el navegador: ${e.message}")
            }
        }

        jorgeGithubLink.setOnAction {
            try {
                Desktop.getDesktop().browse(URI("https://github.com/jorgemorgado25"))
            } catch (e: Exception) {
                logger.error { "Error opening browser: ${e.message}" }
                showErrorDialog("Error", "No se pudo abrir el navegador: ${e.message}")
            }
        }
    }

    /**
     * Show error dialog
     */
    private fun showErrorDialog(title: String, message: String) {
        Alert(Alert.AlertType.ERROR).apply {
            this.title = title
            headerText = null
            contentText = message
        }.showAndWait()
    }
}