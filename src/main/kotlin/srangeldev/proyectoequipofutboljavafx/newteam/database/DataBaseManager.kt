package srangeldev.proyectoequipofutboljavafx.newteam.database

import org.apache.ibatis.jdbc.ScriptRunner
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import java.io.File
import java.io.PrintWriter
import java.io.Reader
import java.sql.Connection
import java.sql.DriverManager

object DataBaseManager: AutoCloseable {
    private val logger = logging()

    // Realizamos conexión con la base de datos
    var connection: Connection? = null
        private set

    init {
        initDatabase()
    }

    private fun initDatabase() {
        // Delete the database file if it exists
        deleteDatabase()

        initConexion()
        if (Config.configProperties.databaseInitTables) {
            initTablas()
        }
        if (Config.configProperties.databaseInitData) {
            initData()
        }
        close()
    }

    private fun deleteDatabase() {
        // Extract the database file path from the URL
        val dbUrl = Config.configProperties.databaseUrl
        if (dbUrl.startsWith("jdbc:sqlite:")) {
            val dbFilePath = dbUrl.substring("jdbc:sqlite:".length)
            val dbFile = File(dbFilePath)
            if (dbFile.exists()) {
                logger.debug { "Deleting existing database file: $dbFilePath" }
                if (dbFile.delete()) {
                    logger.debug { "Database file deleted successfully" }
                } else {
                    logger.error { "Failed to delete database file" }
                }
            } else {
                logger.debug { "Database file does not exist, no need to delete" }
            }
        }
    }

    private fun initConexion() {
        logger.debug { "Iniciando conexión con la base de datos en ${Config.configProperties.databaseUrl}" }
        if (connection == null || connection!!.isClosed) {
            connection = DriverManager.getConnection(Config.configProperties.databaseUrl)
            logger.debug { "Conexión con la base de datos iniciada" }
        } else {
            logger.debug { "La conexión con la base de datos ya está iniciada" }
        }
    }

    private fun initTablas() {
        logger.debug { "Creando tablas de la base de datos" }
        try {
            // Use the class's classloader to get the resource stream
            val tablas = this::class.java.getResourceAsStream("/srangeldev/proyectoequipofutboljavafx/tablas.sql")
            if (tablas != null) {
                scriptRunner(tablas.bufferedReader(), true)
                logger.debug { "Tablas de la base de datos equipo creadas" }
            } else {
                logger.error { "No se pudo encontrar el archivo tablas.sql" }
                // Try to find the file in the filesystem as a fallback
                val file = java.io.File("src/main/resources/srangeldev/proyectoequipofutboljavafx/tablas.sql")
                if (file.exists()) {
                    scriptRunner(file.bufferedReader(), true)
                    logger.debug { "Tablas de la base de datos equipo creadas desde archivo" }
                } else {
                    logger.error { "No se pudo encontrar el archivo tablas.sql en el sistema de archivos" }
                }
            }
        } catch (e: Exception) {
            logger.error { "Error al crear las tablas de la base de datos: ${e.message}" }
            e.printStackTrace()
        }
    }

    private fun initData() {
        logger.debug { "Iniciando carga de datos de la base de datos" }
        try {
            // Use the class's classloader to get the resource stream
            val datos = this::class.java.getResourceAsStream("/srangeldev/proyectoequipofutboljavafx/data.sql")
            if (datos != null) {
                scriptRunner(datos.bufferedReader(), true)
                logger.debug { "Datos de la base de datos equipo cargados" }
            } else {
                logger.error { "No se pudo encontrar el archivo data.sql" }
                // Try to find the file in the filesystem as a fallback
                val file = java.io.File("src/main/resources/srangeldev/proyectoequipofutboljavafx/data.sql")
                if (file.exists()) {
                    scriptRunner(file.bufferedReader(), true)
                    logger.debug { "Datos de la base de datos equipo cargados desde archivo" }
                } else {
                    logger.error { "No se pudo encontrar el archivo data.sql en el sistema de archivos" }
                }
            }
        } catch (e: Exception) {
            logger.error { "Error al cargar los datos de la base de datos: ${e.message}" }
            e.printStackTrace()
        }
    }

    override fun close() {
        logger.debug { "Cerrando conexión con la base de datos" }
        if (!connection!!.isClosed) {
            connection!!.close()
        }
        logger.debug { "Conexión con la base de datos cerrada" }
    }

    /**
     * Función para usar la base de datos y cerrarla al finalizar la operación
     */

    fun <T> use(block: (DataBaseManager) -> T) {
        initConexion()
        this.connection.use { block(this) }
        close()
    }

    /**
     * Función para ejecutar un script SQL en la base de datos
     */

    private fun scriptRunner(reader: Reader, logWriter: Boolean = false) {
        logger.debug { "Ejecutando script SQL con log: $logWriter" }
        val sr = ScriptRunner(connection)
        sr.setLogWriter(if (logWriter) PrintWriter(System.out) else null)
        sr.runScript(reader)
    }
}
