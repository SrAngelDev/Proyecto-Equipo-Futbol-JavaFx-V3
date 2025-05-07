package srangeldev.database

import org.apache.ibatis.jdbc.ScriptRunner
import org.lighthousegames.logging.logging
import srangeldev.config.Config
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
        initConexion()
        if (Config.configProperties.databaseInitTables) {
            initTablas()
        }
        if (Config.configProperties.databaseInitData) {
            initData()
        }
        close()
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
            val tablas = ClassLoader.getSystemResourceAsStream("tablas.sql")?.bufferedReader()!!
            scriptRunner(tablas, true)
            logger.debug { "Tablas de la base de datos equipo creadas" }
        } catch (e: Exception) {
            logger.error { "Error al crear las tablas de la base de datos: ${e.message}" }
        }
    }

    private fun initData() {
        logger.debug { "Iniciando carga de datos de la base de datos" }
        try {
            val datos = ClassLoader.getSystemResourceAsStream("datos.sql")?.bufferedReader()!!
            scriptRunner(datos, true)
            logger.debug { "Datos de la base de datos equipo cargados" }
        } catch (e: Exception) {
            logger.error { "Error al cargar los datos de la base de datos: ${e.message}" }
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