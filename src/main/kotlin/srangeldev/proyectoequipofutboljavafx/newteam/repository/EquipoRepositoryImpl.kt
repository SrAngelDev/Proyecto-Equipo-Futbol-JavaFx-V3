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
     * Inicializa un equipo por defecto si no existe ninguno.
     * Las tablas ya se crean a través del archivo tablas.sql
     */
    fun initDefaultEquipo() {
        logger.debug { "Inicializando equipo por defecto" }

        // Comprobamos si hay equipos
        DataBaseManager.instance.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

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

        DataBaseManager.instance.use { db ->
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

        DataBaseManager.instance.use { db ->
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
                equipos[equipo!!.id] = equipo!!
            }
        }

        return equipo
    }

    override fun save(equipo: Equipo): Equipo {
        logger.debug { "Guardando equipo: $equipo" }

        val isUpdate = equipo.id > 0

        if (isUpdate) {
            // Actualizar equipo existente
            val updated = update(equipo.id, equipo)
            if (updated != null) {
                return updated
            } else {
                throw IllegalStateException("No se pudo actualizar el equipo")
            }
        } else {
            // Crear nuevo equipo
            var newEquipo: Equipo? = null

            DataBaseManager.instance.use { db ->
                val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

                // Insertar el equipo
                val insertSql = """
                    INSERT INTO Equipos (nombre, fecha_fundacion, escudo_url, ciudad, estadio, pais, created_at, updated_at) 
                    VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """.trimIndent()

                val preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
                preparedStatement.setString(1, equipo.nombre)
                preparedStatement.setDate(2, java.sql.Date.valueOf(equipo.fechaFundacion))
                preparedStatement.setString(3, equipo.escudoUrl)
                preparedStatement.setString(4, equipo.ciudad)
                preparedStatement.setString(5, equipo.estadio)
                preparedStatement.setString(6, equipo.pais)

                preparedStatement.executeUpdate()

                // Obtener el ID generado
                val generatedKeys = preparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    val equipoId = generatedKeys.getInt(1)

                    // Obtener el equipo creado
                    newEquipo = getById(equipoId)
                    if (newEquipo != null) {
                        // Actualizar la caché
                        equipos[newEquipo!!.id] = newEquipo!!
                    } else {
                        throw IllegalStateException("No se pudo obtener el equipo creado")
                    }
                } else {
                    throw IllegalStateException("No se pudo obtener el ID del equipo creado")
                }
            }

            if (newEquipo != null) {
                return newEquipo!!
            } else {
                throw IllegalStateException("No se pudo crear el equipo")
            }
        }
    }

    override fun update(id: Int, entidad: Equipo): Equipo? {
        logger.debug { "Actualizando equipo con ID: $id" }

        // Verificar si el equipo existe
        val existingEquipo = getById(id)
        if (existingEquipo == null) {
            logger.debug { "No se encontró ningún equipo con ID: $id" }
            return null
        }

        DataBaseManager.instance.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Actualizar el equipo
            val updateSql = """
                UPDATE Equipos 
                SET nombre = ?, fecha_fundacion = ?, escudo_url = ?, ciudad = ?, estadio = ?, pais = ?, updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?
            """.trimIndent()

            val preparedStatement = connection.prepareStatement(updateSql)
            preparedStatement.setString(1, entidad.nombre)
            preparedStatement.setDate(2, java.sql.Date.valueOf(entidad.fechaFundacion))
            preparedStatement.setString(3, entidad.escudoUrl)
            preparedStatement.setString(4, entidad.ciudad)
            preparedStatement.setString(5, entidad.estadio)
            preparedStatement.setString(6, entidad.pais)
            preparedStatement.setInt(7, id)

            preparedStatement.executeUpdate()
        }

        // Obtener el equipo actualizado
        val updatedEquipo = getById(id)
        if (updatedEquipo != null) {
            // Actualizar la caché
            equipos[updatedEquipo.id] = updatedEquipo
        }

        return updatedEquipo
    }

    override fun delete(id: Int): Equipo? {
        logger.debug { "Eliminando equipo con ID: $id" }

        // Verificar si el equipo existe
        val existingEquipo = getById(id)
        if (existingEquipo == null) {
            logger.debug { "No se encontró ningún equipo con ID: $id" }
            return null
        }

        var success = false

        DataBaseManager.instance.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Eliminar el equipo
            val sql = "DELETE FROM Equipos WHERE id = ?"

            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, id)

            val rowsAffected = preparedStatement.executeUpdate()
            success = rowsAffected > 0

            if (success) {
                // Eliminar de la caché
                equipos.remove(id)
                logger.debug { "Equipo eliminado correctamente: $id" }
            } else {
                logger.debug { "No se eliminó ningún equipo con ID: $id" }
            }
        }

        return if (success) existingEquipo else null
    }
}
