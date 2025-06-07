package srangeldev.proyectoequipofutboljavafx.newteam.database

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.sqlobject.SqlObjectPlugin
import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.config.Config
import java.io.File

/**
 * Clase que gestiona la conexión con la base de datos
 * y la creación de tablas y carga de datos
 */
class JdbiManager private constructor() {
    private val logger = logging()
    
    // La creo de forma lazy para que se cree cuando se necesite
    val jdbi by lazy { Jdbi.create(Config.configProperties.databaseUrl) }
    
    init {
        logger.debug { "Inicializando JdbiManager" }
        // instalamos los plugins
        jdbi.installPlugin(KotlinPlugin()) // Necesario para trabajar con Kotlin
        jdbi.installPlugin(SqlObjectPlugin()) // Necesario para trabajar con SQLObject, DAO
        
        // Check if database file exists
        val dbUrl = Config.configProperties.databaseUrl
        var dbExists = false

        if (dbUrl.startsWith("jdbc:sqlite:")) {
            val dbFilePath = dbUrl.substring("jdbc:sqlite:".length)
            val dbFile = File(dbFilePath)
            dbExists = dbFile.exists()
            logger.debug { "Database file exists: $dbExists at path: $dbFilePath" }
        }
        
        if (Config.configProperties.databaseInitTables) {
            logger.debug { "Creando tablas" }
            // Leemos el fichero de resources
            executeSqlScriptFromResources("srangeldev/proyectoequipofutboljavafx/tablas.sql")
        }
        
        // Only initialize data if explicitly configured and this is a new database
        if (!dbExists && Config.configProperties.databaseInitData) {
            logger.debug { "Cargando datos" }
            executeSqlScriptFromResources("srangeldev/proyectoequipofutboljavafx/data.sql")
        } else {
            logger.debug { "Skipping data initialization as per configuration or database already exists" }
        }
    }
    
    companion object {
        private var instance: JdbiManager? = null
        
        fun getInstance(): JdbiManager {
            if (instance == null) {
                instance = JdbiManager()
            }
            return instance!!
        }
    }
    
    /**
     * Ejecuta un script SQL
     * @param scriptFilePath Ruta del fichero
     */
    fun executeSqlScript(scriptFilePath: String) {
        logger.debug { "Ejecutando script SQL: $scriptFilePath" }
        val script = File(scriptFilePath).readText()
        jdbi.useHandle<Exception> { handle ->
            handle.createScript(script).execute()
        }
    }
    
    /**
     * Ejectua un script SQL desde un recurso
     * @param resourcePath Ruta del recursos
     */
    fun executeSqlScriptFromResources(resourcePath: String) {
        logger.debug { "Ejecutando script SQL desde recursos: $resourcePath" }
        val inputStream = ClassLoader.getSystemResourceAsStream(resourcePath)?.bufferedReader()
        if (inputStream != null) {
            val script = inputStream.readText()
            jdbi.useHandle<Exception> { handle ->
                handle.createScript(script).execute()
            }
        } else {
            logger.error { "No se pudo encontrar el archivo $resourcePath" }
            // Try to find the file in the filesystem as a fallback
            val file = File("src/main/resources/$resourcePath")
            if (file.exists()) {
                executeSqlScript(file.absolutePath)
                logger.debug { "Script SQL ejecutado desde archivo: ${file.absolutePath}" }
            } else {
                logger.error { "No se pudo encontrar el archivo $resourcePath en el sistema de archivos" }
            }
        }
    }
    
    /**
     * Deletes the database file.
     * This method should be called when the application is shutting down.
     */
    fun deleteDatabase() {
        // Extract the database file path from the URL
        val dbUrl = Config.configProperties.databaseUrl
        if (dbUrl.startsWith("jdbc:sqlite:")) {
            val dbFilePath = dbUrl.substring("jdbc:sqlite:".length)
            val dbFile = File(dbFilePath)
            if (dbFile.exists()) {
                logger.debug { "Deleting existing database file: $dbFilePath" }
                if (dbFile.delete()) {
                    logger.debug { "Database file deleted successfully" }
                }
            }
        }
    }
}

/**
 * Función para proporcionar una instancia de Jdbi
 */
fun provideJdbi(): Jdbi {
    val logger = logging()
    logger.debug { "Proporcionando instancia de Jdbi" }
    return JdbiManager.getInstance().jdbi
}