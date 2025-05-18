package srangeldev.proyectoequipofutboljavafx

import javafx.application.Application
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import srangeldev.proyectoequipofutboljavafx.di.appModule
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import java.time.LocalDateTime


class NewTeamApplication : Application(), KoinComponent {

    init {
        println(LocalDateTime.now().toString())
        // creamos Koin
        startKoin {
            printLogger(Level.INFO) // Logger de Koin
            modules(appModule) // Módulos de Koin
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
