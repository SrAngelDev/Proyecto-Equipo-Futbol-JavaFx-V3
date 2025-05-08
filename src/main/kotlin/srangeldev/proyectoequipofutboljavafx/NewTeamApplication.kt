package srangeldev.proyectoequipofutboljavafx

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.util.Duration
import srangeldev.proyectoequipofutboljavafx.Controllers.SplashScreenController

class NewTeamApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(NewTeamApplication::class.java.getResource("views/splash-screen.fxml"))
        val scene = Scene(fxmlLoader.load())
        val controller = fxmlLoader.getController<SplashScreenController>()

        stage.apply {
            isResizable = false
            icons.add(Image(NewTeamApplication::class.java.getResourceAsStream("icons/newTeamLogo.png")))
            title = "Cargando..."
            this.scene = scene
            show()
        }

        // Create timeline for smooth progress bar animation
        val timeline = Timeline(
            KeyFrame(
                Duration.seconds(5.0),
                KeyValue(controller.progressBar.progressProperty(), 1.0)
            )
        )
        timeline.setOnFinished {
            loggingStage(stage)
        }

        timeline.play()
    }

    private fun loggingStage(stage: Stage) {
        val fxmlLoader = FXMLLoader(NewTeamApplication::class.java.getResource("views/logging.fxml"))
        stage.apply {
            scene = Scene(fxmlLoader.load(), 600.0, 400.0)
            title = "Login"
            show()
        }
    }
}

fun main() {
    Application.launch(NewTeamApplication::class.java)
}