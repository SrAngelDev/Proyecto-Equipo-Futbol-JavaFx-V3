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
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin
import srangeldev.proyectoequipofutboljavafx.Controllers.SplashScreenController
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import java.time.LocalDateTime


class NewTeamApplication : Application(), KoinComponent {

    init {
        println(LocalDateTime.now().toString())
        // creamos Koin
        startKoin {
            printLogger() // Logger de Koin
            //modules(appModule) // Módulos de Koin
        }
    }

    override fun start(stage: Stage) {
        RoutesManager.apply {
            app = this@NewTeamApplication
        }.run {
            initSplashScreenStage(stage)
        }
    }
}
fun main() {
    Application.launch(NewTeamApplication::class.java)
}