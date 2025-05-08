package srangeldev.proyectoequipofutboljavafx

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.fxml.FXML
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar

class NewTeamController {
    // Ahora vamos a crear el controlador de la splash screen, que es el que se va a encargar de cargar la vista principal y la que tiene la barra de progreso
    @FXML
    private lateinit var progressBar: ProgressBar

    //Logica de la barra de progreso de 5 segundos
    fun bindProgress(progress: ReadOnlyDoubleProperty) {
        progressBar.progressProperty().bind(progress)
    }
}