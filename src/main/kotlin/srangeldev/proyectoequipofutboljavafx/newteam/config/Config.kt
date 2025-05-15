package srangeldev.proyectoequipofutboljavafx.newteam.config

import org.lighthousegames.logging.logging
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

object Config {

    private val log = logging()

    val configProperties: ConfigProperties by lazy { loadConfig() }

    private fun loadConfig(): ConfigProperties {
        log.debug { "Cargando configuración" }
        val properties = Properties()

        val propertiesStream = this::class.java.getResourceAsStream("/srangeldev/proyectoequipofutboljavafx/config.properties")
            ?: throw RuntimeException("No se ha encontrado el fichero de configuración")

        properties.load(propertiesStream)

        val currentDir = System.getProperty("user.dir")
        val dataDir = Path.of(currentDir, properties.getProperty("data.directory", "data")).toString()
        val backupDir = Path.of(currentDir, properties.getProperty("backup.directory", "backup")).toString()

        createDirectories(dataDir, backupDir)

        return ConfigProperties(
            dataDir = dataDir,
            backupDir = backupDir,
            inputFormats = properties.getProperty("input.formats", "CSV,XML,JSON"),
            outputFormats = properties.getProperty("output.formats", "CSV,XML,JSON"),
            databaseUrl = properties.getProperty("database.url", "jdbc:sqlite:equipo.db"),
            databaseInitTables = properties.getProperty("database.init.tables", "true").toBoolean(),
            databaseInitData = properties.getProperty("database.init.data", "true").toBoolean(),
            databaseStorageData = properties.getProperty("storage.data", "data")
        )
    }

    private fun createDirectories(vararg directories: String) {
        directories.forEach {
            log.debug { "Creando directorio: $it" }
            Files.createDirectories(Path.of(it))
        }
    }

    data class ConfigProperties(
        val dataDir: String,
        val backupDir: String,
        val inputFormats: String,
        val outputFormats: String,
        val databaseUrl: String,
        val databaseInitTables: Boolean,
        val databaseInitData: Boolean,
        val databaseStorageData: String
    )
}
