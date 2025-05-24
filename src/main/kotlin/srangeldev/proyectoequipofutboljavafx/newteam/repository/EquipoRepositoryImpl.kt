package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.Equipo
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Implementación del repositorio de equipos.
 */
class EquipoRepositoryImpl : EquipoRepository {
    private val logger = logging()
    private val equipos = mutableMapOf<Int, Equipo>()

    init {
        logger.debug { "Inicializando repositorio de equipos" }
        initDefaultEquipo()
    }

    /**
     * Inicializa la tabla de equipos y crea un equipo por defecto si no existe ninguno.
     */
    private fun initDefaultEquipo() {
        logger.debug { "Inicializando equipo por defecto" }

        // Comprobamos si la tabla existe
        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Creamos la tabla si no existe
            val createTableSql = """
                CREATE TABLE IF NOT EXISTS Equipos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    fecha_fundacion DATE NOT NULL,
                    escudo_url TEXT DEFAULT '',
                    ciudad TEXT NOT NULL,
                    estadio TEXT NOT NULL,
                    pais TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """.trimIndent()

            connection.createStatement().execute(createTableSql)

            // Comprobamos si hay equipos
            val countSql = "SELECT COUNT(*) FROM Equipos"
            val resultSet = connection.createStatement().executeQuery(countSql)

            if (resultSet.next() && resultSet.getInt(1) == 0) {
                // No hay equipos, creamos un equipo por defecto
                val defaultEquipo = Equipo(
                    nombre = "Mi Equipo",
                    fechaFundacion = LocalDate.of(2000, 1, 1),
                    escudoUrl = "",
                    ciudad = "Mi Ciudad",
                    estadio = "Mi Estadio",
                    pais = "Mi País"
                )

                save(defaultEquipo)

                logger.debug { "Equipo por defecto creado" }
            } else {
                logger.debug { "Ya existe un equipo en la base de datos" }
            }
        }
    }

    override fun getAll(): List<Equipo> {
        logger.debug { "Obteniendo todos los equipos" }

        // Limpiar la caché para asegurarnos de obtener datos actualizados
        equipos.clear()

        val equiposList = mutableListOf<Equipo>()

        val sql = "SELECT * FROM Equipos"

        DataBaseManager.use { db ->
            val statement = db.connection?.createStatement()
            val resultSet = statement?.executeQuery(sql)

            while (resultSet?.next() == true) {
                val equipo = Equipo(
                    id = resultSet.getInt("id"),
                    nombre = resultSet.getString("nombre"),
                    fechaFundacion = resultSet.getDate("fecha_fundacion").toLocalDate(),
                    escudoUrl = resultSet.getString("escudo_url") ?: "",
                    ciudad = resultSet.getString("ciudad"),
                    estadio = resultSet.getString("estadio"),
                    pais = resultSet.getString("pais"),
                    createdAt = resultSet.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = resultSet.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )

                // Añadir a la lista y a la caché
                equiposList.add(equipo)
                equipos[equipo.id] = equipo
            }
        }

        return equiposList
    }

    override fun getById(id: Int): Equipo? {
        logger.debug { "Obteniendo equipo por ID: $id" }

        // Primero buscamos en la caché
        if (equipos.containsKey(id)) {
            return equipos[id]
        }

        // Si no está en la caché, buscamos en la base de datos
        var equipo: Equipo? = null

        val sql = "SELECT * FROM Equipos WHERE id = ?"

        DataBaseManager.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, id)
            val resultSet = preparedStatement?.executeQuery()

            if (resultSet?.next() == true) {
                equipo = Equipo(
                    id = resultSet.getInt("id"),
                    nombre = resultSet.getString("nombre"),
                    fechaFundacion = resultSet.getDate("fecha_fundacion").toLocalDate(),
                    escudoUrl = resultSet.getString("escudo_url") ?: "",
                    ciudad = resultSet.getString("ciudad"),
                    estadio = resultSet.getString("estadio"),
                    pais = resultSet.getString("pais"),
                    createdAt = resultSet.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = resultSet.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )

                // Añadimos el equipo a la caché
                equipos[id] = equipo!!
            }
        }

        return equipo
    }

    override fun save(entidad: Equipo): Equipo {
        logger.debug { "Guardando equipo: ${entidad.nombre}" }

        val timeStamp = LocalDateTime.now()

        var generatedId = 0

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            val sql = """
                INSERT INTO Equipos (nombre, fecha_fundacion, escudo_url, ciudad, estadio, pais, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """.trimIndent()

            val preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            preparedStatement.apply {
                setString(1, entidad.nombre)
                setDate(2, java.sql.Date.valueOf(entidad.fechaFundacion))
                setString(3, entidad.escudoUrl)
                setString(4, entidad.ciudad)
                setString(5, entidad.estadio)
                setString(6, entidad.pais)
            }

            preparedStatement.executeUpdate()

            generatedId = preparedStatement.generatedKeys.let {
                it.next()
                it.getInt(1)
            }
        }

        val savedEquipo = Equipo(
            id = generatedId,
            nombre = entidad.nombre,
            fechaFundacion = entidad.fechaFundacion,
            escudoUrl = entidad.escudoUrl,
            ciudad = entidad.ciudad,
            estadio = entidad.estadio,
            pais = entidad.pais,
            createdAt = timeStamp,
            updatedAt = timeStamp
        )

        // Añadimos el equipo a la caché
        equipos[generatedId] = savedEquipo

        return savedEquipo
    }

    override fun update(id: Int, entidad: Equipo): Equipo? {
        logger.debug { "Actualizando equipo con ID: $id" }

        // Verificar si el equipo existe
        val existingEquipo = getById(id)
        if (existingEquipo == null) {
            logger.debug { "No se encontró el equipo con ID: $id" }
            return null
        }

        val timeStamp = LocalDateTime.now()

        var rowsAffected = 0

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            val sql = """
                UPDATE Equipos 
                SET nombre = ?, fecha_fundacion = ?, escudo_url = ?, ciudad = ?, estadio = ?, pais = ?, updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?
            """.trimIndent()

            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.apply {
                setString(1, entidad.nombre)
                setDate(2, java.sql.Date.valueOf(entidad.fechaFundacion))
                setString(3, entidad.escudoUrl)
                setString(4, entidad.ciudad)
                setString(5, entidad.estadio)
                setString(6, entidad.pais)
                setInt(7, id)
            }

            rowsAffected = preparedStatement.executeUpdate()
        }

        if (rowsAffected == 0) {
            logger.debug { "No se actualizó ningún equipo con ID: $id" }
            return null
        }

        val updatedEquipo = Equipo(
            id = id,
            nombre = entidad.nombre,
            fechaFundacion = entidad.fechaFundacion,
            escudoUrl = entidad.escudoUrl,
            ciudad = entidad.ciudad,
            estadio = entidad.estadio,
            pais = entidad.pais,
            createdAt = existingEquipo.createdAt,
            updatedAt = timeStamp
        )

        // Actualizar la caché
        equipos[id] = updatedEquipo

        return updatedEquipo
    }

    override fun delete(id: Int): Equipo? {
        logger.debug { "Eliminando equipo con ID: $id" }

        // Verificar si el equipo existe
        val existingEquipo = getById(id)
        if (existingEquipo == null) {
            logger.debug { "No se encontró el equipo con ID: $id" }
            return null
        }

        var success = false

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            val sql = "DELETE FROM Equipos WHERE id = ?"

            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, id)

            val rowsAffected = preparedStatement.executeUpdate()
            success = rowsAffected > 0

            if (success) {
                // Eliminar de la caché
                equipos.remove(id)
                logger.debug { "Equipo eliminado correctamente: ${existingEquipo.nombre}" }
            } else {
                logger.debug { "No se eliminó ningún equipo con ID: $id" }
            }
        }

        return if (success) existingEquipo else null
    }
}