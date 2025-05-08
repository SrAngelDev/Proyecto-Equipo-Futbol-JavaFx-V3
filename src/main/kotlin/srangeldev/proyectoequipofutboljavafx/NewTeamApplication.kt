package srangeldev.proyectoequipofutboljavafx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

class NewTeamApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(NewTeamApplication::class.java.getResource("views/splash-screen.fxml"))
        val scene = Scene(fxmlLoader.load())
        stage.icons.add(Image(NewTeamApplication::class.java.getResourceAsStream("icons/newTeamLogo.png")))
        stage.title = "Cargando..."
        stage.scene = scene
        stage.show()

        //Ahora vamos a simular la barra de progreso de carga de 5 segundos
        Thread {
            val controller = fxmlLoader.getController<NewTeamController>()

            val task = object : javafx.concurrent.Task<Void>() {
                override fun call(): Void? {
                    for (i in 1..100) {
                        Thread.sleep(50) // Simula el tiempo de carga
                        updateProgress(i.toDouble(), 100.0)
                    }
                    return null
                }
            }

            // Vincula el progreso del Task al ProgressBar
            controller.bindProgress(task.progressProperty())

            task.setOnSucceeded {
                // Carga la vista principal cuando termine el progreso
                val mainLoader = FXMLLoader(NewTeamApplication::class.java.getResource("views/main-view.fxml"))
                val mainScene = Scene(mainLoader.load())
                stage.icons.add(Image(NewTeamApplication::class.java.getResourceAsStream("icons/newTeamLogo.png")))
                stage.title = "New Team"
                stage.scene = mainScene
                stage.show()
            }

            Thread(task).start()
        }.start()
    }
}

fun main() {
    Application.launch(NewTeamApplication::class.java)
}