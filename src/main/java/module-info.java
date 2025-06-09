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


    opens srangeldev.proyectoequipofutboljavafx to javafx.fxml, koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.controllers to javafx.fxml, koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.viewmodels to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.di to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.models to javafx.base, koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.controller to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.service to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.repository to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.dao to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.database to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.cache to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.storage to koin.core.jvm;
    opens srangeldev.proyectoequipofutboljavafx.newteam.config to koin.core.jvm;
    exports srangeldev.proyectoequipofutboljavafx.newteam.dao;
    exports srangeldev.proyectoequipofutboljavafx.newteam.controller;
    exports srangeldev.proyectoequipofutboljavafx.newteam.service;
    exports srangeldev.proyectoequipofutboljavafx.newteam.repository;
    exports srangeldev.proyectoequipofutboljavafx.newteam.database;
    exports srangeldev.proyectoequipofutboljavafx.newteam.cache;
    exports srangeldev.proyectoequipofutboljavafx.newteam.storage;
    exports srangeldev.proyectoequipofutboljavafx.newteam.config;
    exports srangeldev.proyectoequipofutboljavafx.newteam.models;
    exports srangeldev.proyectoequipofutboljavafx.viewmodels;

    //Logging
    exports srangeldev.proyectoequipofutboljavafx;
    exports srangeldev.proyectoequipofutboljavafx.controllers;
    exports srangeldev.proyectoequipofutboljavafx.routes;

    // models

}
