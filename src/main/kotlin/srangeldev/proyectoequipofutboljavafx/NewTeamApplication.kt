package srangeldev.proyectoequipofutboljavafx

import javafx.application.Application
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import srangeldev.proyectoequipofutboljavafx.di.appModule
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.routes.RoutesManager
import java.time.LocalDateTime


/**
 * Clase principal de la aplicación que inicializa el framework de inyección de dependencias Koin
 * y configura la pantalla inicial.
 */
class NewTeamApplication : Application(), KoinComponent {

    init {
        println(LocalDateTime.now().toString())
        // Inicializamos Koin para la inyección de dependencias
        startKoin {
            printLogger(Level.INFO) // Configuramos el nivel de log para Koin
            modules(appModule) // Cargamos los módulos de la aplicación
        }
    }

    /**
     * Método que se ejecuta al iniciar la aplicación JavaFX
     * @param stage El escenario principal de la aplicación
     */
    override fun start(stage: Stage) {
        // Configuramos y lanzamos la pantalla de inicio
        RoutesManager.apply {
            app = this@NewTeamApplication
        }.run {
            initSplashScreenStage(stage)
        }
    }

    /**
     * Método que se ejecuta al cerrar la aplicación JavaFX
     * Cierra la conexión con la base de datos y elimina el archivo de la base de datos
     */
    override fun stop() {
        println("Cerrando la aplicación y eliminando la base de datos")
        try {
            // Eliminar la base de datos (esto también cierra la conexión)
            DataBaseManager.instance.deleteDatabase()
            println("Base de datos eliminada correctamente")
        } catch (e: Exception) {
            println("Error al eliminar la base de datos: ${e.message}")
            e.printStackTrace()
        }
        super.stop()
    }
}
/**
 * Función principal que inicia la aplicación JavaFX
 */
fun main() {
    Application.launch(NewTeamApplication::class.java)
}
