module srangeldev.proyectoequipofutboljavafx {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;

    // Java
    requires java.desktop;

    // Kotlin
    requires kotlin.stdlib;

    // Logger
    requires logging.jvm;
    requires org.slf4j;

    // Kotlin Serialization
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;


    // Result
    requires kotlin.result.jvm;

    // SQL
    requires java.sql; // Como no pongas esto te vas a volver loco con los errores!!
    requires java.base;

    // Koin
    requires koin.core.jvm;

    // Open Vadin
    requires open;

    // JDBI
    requires org.jdbi.v3.sqlobject;
    requires org.jdbi.v3.core;
    requires org.jdbi.v3.kotlin;
    requires org.jdbi.v3.sqlobject.kotlin;
    requires io.leangen.geantyref;
    requires kotlin.reflect;

    // Cache
    requires com.github.benmanes.caffeine;
    requires org.mybatis;


    opens srangeldev.proyectoequipofutboljavafx to javafx.fxml;
    opens srangeldev.proyectoequipofutboljavafx.controllers to javafx.fxml;
    opens srangeldev.proyectoequipofutboljavafx.newteam.models to javafx.base;
    opens srangeldev.proyectoequipofutboljavafx.newteam.dao to org.jdbi.v3.core;

    //Logging
    exports srangeldev.proyectoequipofutboljavafx;
    exports srangeldev.proyectoequipofutboljavafx.controllers;
    exports srangeldev.proyectoequipofutboljavafx.routes;

    // models
    exports srangeldev.proyectoequipofutboljavafx.newteam.models;

}
