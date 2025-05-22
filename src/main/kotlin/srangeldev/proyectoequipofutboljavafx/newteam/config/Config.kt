package srangeldev.proyectoequipofutboljavafx.newteam.config

import org.lighthousegames.logging.logging
import java.io.File
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

        // Get the application's base directory
        val baseDir = getApplicationBaseDirectory()
        log.debug { "Base directory: $baseDir" }

        // Resolve paths relative to the base directory
        val dataDir = resolvePathRelativeToBase(baseDir, properties.getProperty("data.directory", "data"))
        val backupDir = resolvePathRelativeToBase(baseDir, properties.getProperty("backup.directory", "backup"))
        val reportsDir = resolvePathRelativeToBase(baseDir, properties.getProperty("reports.directory", "reports"))

        log.debug { "Data directory: $dataDir" }
        log.debug { "Backup directory: $backupDir" }
        log.debug { "Reports directory: $reportsDir" }

        createDirectories(dataDir, backupDir, reportsDir)

        return ConfigProperties(
            dataDir = dataDir,
            backupDir = backupDir,
            reportsDir = reportsDir,
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

    /**
     * Gets the application's base directory.
     * This will be the directory containing the JAR file when running from a JAR,
     * or the project directory when running from an IDE.
     */
    private fun getApplicationBaseDirectory(): String {
        try {
            // Try to get the location of the JAR file
            val codeSource = Config::class.java.protectionDomain.codeSource
            if (codeSource != null && codeSource.location != null) {
                val jarFile = File(codeSource.location.toURI())
                log.debug { "JAR file location: ${jarFile.absolutePath}" }
                // Return the directory containing the JAR file
                return jarFile.parentFile.absolutePath
            }
        } catch (e: Exception) {
            log.error { "Error getting JAR file location: ${e.message}" }
            // Fall back to current working directory
        }

        // Fall back to current working directory
        val currentDir = System.getProperty("user.dir")
        log.debug { "Using current directory: $currentDir" }
        return currentDir
    }

    /**
     * Resolves a path relative to the base directory.
     * If the path is absolute, it is returned unchanged.
     * If the path is relative, it is resolved against the base directory.
     */
    private fun resolvePathRelativeToBase(baseDir: String, path: String): String {
        val file = File(path)
        if (file.isAbsolute) {
            log.debug { "Path is absolute: $path" }
            return path
        }

        log.debug { "Resolving path relative to base: $baseDir + $path" }
        return File(baseDir, path).absolutePath
    }

    data class ConfigProperties(
        val dataDir: String,
        val backupDir: String,
        val reportsDir: String,
        val inputFormats: String,
        val outputFormats: String,
        val databaseUrl: String,
        val databaseInitTables: Boolean,
        val databaseInitData: Boolean,
        val databaseStorageData: String
    )
}
