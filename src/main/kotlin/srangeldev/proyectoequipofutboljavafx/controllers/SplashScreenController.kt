package srangeldev.proyectoequipofutboljavafx.controllers

import javafx.animation.FadeTransition
import javafx.fxml.FXML
import javafx.scene.control.ProgressBar
import javafx.scene.text.Text
import javafx.util.Duration

class SplashScreenController {
    @FXML
    lateinit var progressBar: ProgressBar

    @FXML
    lateinit var loadingText: Text

    @FXML
    private fun initialize() {
        // Apply fade-in animation to the progress bar
        createFadeInTransition(progressBar).play()

        // Apply fade-in animation to the loading text
        createFadeInTransition(loadingText).play()
    }

    private fun createFadeInTransition(node: Any): FadeTransition {
        val fadeTransition = FadeTransition(Duration.seconds(1.0), node as javafx.scene.Node)
        fadeTransition.fromValue = 0.0
        fadeTransition.toValue = 1.0
        return fadeTransition
    }
}
