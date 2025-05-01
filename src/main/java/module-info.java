module srangeldev.proyectoequipofutboljavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens srangeldev.proyectoequipofutboljavafx to javafx.fxml;
    exports srangeldev.proyectoequipofutboljavafx;
}