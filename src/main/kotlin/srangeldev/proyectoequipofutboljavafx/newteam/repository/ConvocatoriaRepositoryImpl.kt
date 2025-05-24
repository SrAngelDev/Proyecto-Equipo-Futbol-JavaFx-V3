package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Implementación del repositorio de convocatorias.
 */
class ConvocatoriaRepositoryImpl(
    private val personalRepository: PersonalRepository
) : ConvocatoriaRepository {
    private val logger = logging()
    private val convocatorias = mutableMapOf<Int, Convocatoria>()

    init {
        logger.debug { "Inicializando repositorio de convocatorias" }
        initDefaultConvocatoria()
    }

    /**
     * Inicializa las tablas de convocatorias.
     */
    private fun initDefaultConvocatoria() {
        logger.debug { "Inicializando tablas de convocatorias" }

        // Comprobamos si la tabla existe
        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Creamos la tabla de convocatorias si no existe
            val createConvocatoriasTableSql = """
                CREATE TABLE IF NOT EXISTS Convocatorias (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha DATE NOT NULL,
                    descripcion TEXT NOT NULL,
                    equipo_id INTEGER NOT NULL,
                    entrenador_id INTEGER NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (equipo_id) REFERENCES Equipos(id) ON DELETE CASCADE,
                    FOREIGN KEY (entrenador_id) REFERENCES Personal(id) ON DELETE CASCADE
                )
            """.trimIndent()

            connection.createStatement().execute(createConvocatoriasTableSql)

            // Creamos la tabla de jugadores convocados si no existe
            val createJugadoresConvocadosTableSql = """
                CREATE TABLE IF NOT EXISTS JugadoresConvocados (
                    convocatoria_id INTEGER NOT NULL,
                    jugador_id INTEGER NOT NULL,
                    es_titular INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY (convocatoria_id, jugador_id),
                    FOREIGN KEY (convocatoria_id) REFERENCES Convocatorias(id) ON DELETE CASCADE,
                    FOREIGN KEY (jugador_id) REFERENCES Personal(id) ON DELETE CASCADE
                )
            """.trimIndent()

            connection.createStatement().execute(createJugadoresConvocadosTableSql)

            logger.debug { "Tablas de convocatorias creadas" }
        }
    }

    override fun getAll(): List<Convocatoria> {
        logger.debug { "Obteniendo todas las convocatorias" }

        // Limpiar la caché para asegurarnos de obtener datos actualizados
        convocatorias.clear()

        val convocatoriasList = mutableListOf<Convocatoria>()

        val sql = "SELECT * FROM Convocatorias"

        DataBaseManager.use { db ->
            val statement = db.connection?.createStatement()
            val resultSet = statement?.executeQuery(sql)

            while (resultSet?.next() == true) {
                val convocatoriaId = resultSet.getInt("id")
                val convocatoria = Convocatoria(
                    id = convocatoriaId,
                    fecha = resultSet.getDate("fecha").toLocalDate(),
                    descripcion = resultSet.getString("descripcion"),
                    equipoId = resultSet.getInt("equipo_id"),
                    entrenadorId = resultSet.getInt("entrenador_id"),
                    jugadores = getJugadoresIds(convocatoriaId),
                    titulares = getTitularesIds(convocatoriaId),
                    createdAt = resultSet.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = resultSet.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )

                // Añadir a la lista y a la caché
                convocatoriasList.add(convocatoria)
                convocatorias[convocatoria.id] = convocatoria
            }
        }

        return convocatoriasList
    }

    override fun getById(id: Int): Convocatoria? {
        logger.debug { "Obteniendo convocatoria por ID: $id" }

        // Primero buscamos en la caché
        if (convocatorias.containsKey(id)) {
            return convocatorias[id]
        }

        // Si no está en la caché, buscamos en la base de datos
        var convocatoria: Convocatoria? = null

        val sql = "SELECT * FROM Convocatorias WHERE id = ?"

        DataBaseManager.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, id)
            val resultSet = preparedStatement?.executeQuery()

            if (resultSet?.next() == true) {
                convocatoria = Convocatoria(
                    id = resultSet.getInt("id"),
                    fecha = resultSet.getDate("fecha").toLocalDate(),
                    descripcion = resultSet.getString("descripcion"),
                    equipoId = resultSet.getInt("equipo_id"),
                    entrenadorId = resultSet.getInt("entrenador_id"),
                    jugadores = getJugadoresIds(id),
                    titulares = getTitularesIds(id),
                    createdAt = resultSet.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = resultSet.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )

                // Añadimos la convocatoria a la caché
                convocatorias[id] = convocatoria!!
            }
        }

        return convocatoria
    }

    override fun save(entidad: Convocatoria): Convocatoria {
        logger.debug { "Guardando convocatoria: ${entidad.descripcion}" }

        // Validar la convocatoria antes de guardarla
        if (!validarConvocatoria(entidad)) {
            throw IllegalArgumentException("La convocatoria no cumple con las reglas establecidas")
        }

        val timeStamp = LocalDateTime.now()
        var generatedId = 0

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Primero insertamos la convocatoria
            val sql = """
                INSERT INTO Convocatorias (fecha, descripcion, equipo_id, entrenador_id, created_at, updated_at) 
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """.trimIndent()

            val preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            preparedStatement.apply {
                setDate(1, java.sql.Date.valueOf(entidad.fecha))
                setString(2, entidad.descripcion)
                setInt(3, entidad.equipoId)
                setInt(4, entidad.entrenadorId)
            }

            preparedStatement.executeUpdate()

            generatedId = preparedStatement.generatedKeys.let {
                it.next()
                it.getInt(1)
            }

            // Luego insertamos los jugadores convocados
            val sqlJugadores = """
                INSERT INTO JugadoresConvocados (convocatoria_id, jugador_id, es_titular) 
                VALUES (?, ?, ?)
            """.trimIndent()

            val preparedStatementJugadores = connection.prepareStatement(sqlJugadores)
            
            entidad.jugadores.forEach { jugadorId ->
                preparedStatementJugadores.apply {
                    setInt(1, generatedId)
                    setInt(2, jugadorId)
                    setInt(3, if (entidad.titulares.contains(jugadorId)) 1 else 0)
                }
                preparedStatementJugadores.addBatch()
            }
            
            preparedStatementJugadores.executeBatch()
        }

        val savedConvocatoria = Convocatoria(
            id = generatedId,
            fecha = entidad.fecha,
            descripcion = entidad.descripcion,
            equipoId = entidad.equipoId,
            entrenadorId = entidad.entrenadorId,
            jugadores = entidad.jugadores,
            titulares = entidad.titulares,
            createdAt = timeStamp,
            updatedAt = timeStamp
        )

        // Añadimos la convocatoria a la caché
        convocatorias[generatedId] = savedConvocatoria

        return savedConvocatoria
    }

    override fun update(id: Int, entidad: Convocatoria): Convocatoria? {
        logger.debug { "Actualizando convocatoria con ID: $id" }

        // Validar la convocatoria antes de actualizarla
        if (!validarConvocatoria(entidad)) {
            throw IllegalArgumentException("La convocatoria no cumple con las reglas establecidas")
        }

        // Verificar si la convocatoria existe
        val existingConvocatoria = getById(id)
        if (existingConvocatoria == null) {
            logger.debug { "No se encontró la convocatoria con ID: $id" }
            return null
        }

        val timeStamp = LocalDateTime.now()
        var rowsAffected = 0

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Primero actualizamos la convocatoria
            val sql = """
                UPDATE Convocatorias 
                SET fecha = ?, descripcion = ?, equipo_id = ?, entrenador_id = ?, updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?
            """.trimIndent()

            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.apply {
                setDate(1, java.sql.Date.valueOf(entidad.fecha))
                setString(2, entidad.descripcion)
                setInt(3, entidad.equipoId)
                setInt(4, entidad.entrenadorId)
                setInt(5, id)
            }

            rowsAffected = preparedStatement.executeUpdate()

            if (rowsAffected > 0) {
                // Eliminamos los jugadores convocados actuales
                val sqlDeleteJugadores = "DELETE FROM JugadoresConvocados WHERE convocatoria_id = ?"
                val preparedStatementDelete = connection.prepareStatement(sqlDeleteJugadores)
                preparedStatementDelete.setInt(1, id)
                preparedStatementDelete.executeUpdate()

                // Insertamos los nuevos jugadores convocados
                val sqlJugadores = """
                    INSERT INTO JugadoresConvocados (convocatoria_id, jugador_id, es_titular) 
                    VALUES (?, ?, ?)
                """.trimIndent()

                val preparedStatementJugadores = connection.prepareStatement(sqlJugadores)
                
                entidad.jugadores.forEach { jugadorId ->
                    preparedStatementJugadores.apply {
                        setInt(1, id)
                        setInt(2, jugadorId)
                        setInt(3, if (entidad.titulares.contains(jugadorId)) 1 else 0)
                    }
                    preparedStatementJugadores.addBatch()
                }
                
                preparedStatementJugadores.executeBatch()
            }
        }

        if (rowsAffected == 0) {
            logger.debug { "No se actualizó ninguna convocatoria con ID: $id" }
            return null
        }

        val updatedConvocatoria = Convocatoria(
            id = id,
            fecha = entidad.fecha,
            descripcion = entidad.descripcion,
            equipoId = entidad.equipoId,
            entrenadorId = entidad.entrenadorId,
            jugadores = entidad.jugadores,
            titulares = entidad.titulares,
            createdAt = existingConvocatoria.createdAt,
            updatedAt = timeStamp
        )

        // Actualizar la caché
        convocatorias[id] = updatedConvocatoria

        return updatedConvocatoria
    }

    override fun delete(id: Int): Convocatoria? {
        logger.debug { "Eliminando convocatoria con ID: $id" }

        // Verificar si la convocatoria existe
        val existingConvocatoria = getById(id)
        if (existingConvocatoria == null) {
            logger.debug { "No se encontró la convocatoria con ID: $id" }
            return null
        }

        var success = false

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Eliminamos la convocatoria (los jugadores convocados se eliminarán en cascada)
            val sql = "DELETE FROM Convocatorias WHERE id = ?"

            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, id)

            val rowsAffected = preparedStatement.executeUpdate()
            success = rowsAffected > 0

            if (success) {
                // Eliminar de la caché
                convocatorias.remove(id)
                logger.debug { "Convocatoria eliminada correctamente: ${existingConvocatoria.descripcion}" }
            } else {
                logger.debug { "No se eliminó ninguna convocatoria con ID: $id" }
            }
        }

        return if (success) existingConvocatoria else null
    }

    override fun getByEquipoId(equipoId: Int): List<Convocatoria> {
        logger.debug { "Obteniendo convocatorias por equipo ID: $equipoId" }

        val convocatoriasList = mutableListOf<Convocatoria>()

        val sql = "SELECT * FROM Convocatorias WHERE equipo_id = ?"

        DataBaseManager.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, equipoId)
            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                val convocatoriaId = resultSet.getInt("id")
                val convocatoria = Convocatoria(
                    id = convocatoriaId,
                    fecha = resultSet.getDate("fecha").toLocalDate(),
                    descripcion = resultSet.getString("descripcion"),
                    equipoId = resultSet.getInt("equipo_id"),
                    entrenadorId = resultSet.getInt("entrenador_id"),
                    jugadores = getJugadoresIds(convocatoriaId),
                    titulares = getTitularesIds(convocatoriaId),
                    createdAt = resultSet.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = resultSet.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )

                // Añadir a la lista y a la caché
                convocatoriasList.add(convocatoria)
                convocatorias[convocatoria.id] = convocatoria
            }
        }

        return convocatoriasList
    }

    override fun validarConvocatoria(convocatoria: Convocatoria): Boolean {
        logger.debug { "Validando convocatoria: ${convocatoria.descripcion}" }

        // Obtener todos los jugadores
        val jugadores = personalRepository.getAll()
            .filterIsInstance<Jugador>()
            .associateBy { it.id }

        return convocatoria.esValida(jugadores)
    }

    override fun getJugadoresConvocados(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores convocados para la convocatoria ID: $convocatoriaId" }

        val jugadoresIds = getJugadoresIds(convocatoriaId)
        
        return jugadoresIds.mapNotNull { jugadorId ->
            val personal = personalRepository.getById(jugadorId)
            if (personal is Jugador) personal else null
        }
    }

    override fun getJugadoresTitulares(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores titulares para la convocatoria ID: $convocatoriaId" }

        val titularesIds = getTitularesIds(convocatoriaId)
        
        return titularesIds.mapNotNull { jugadorId ->
            val personal = personalRepository.getById(jugadorId)
            if (personal is Jugador) personal else null
        }
    }

    /**
     * Obtiene los IDs de los jugadores convocados para una convocatoria.
     *
     * @param convocatoriaId El ID de la convocatoria.
     * @return Una lista de IDs de jugadores convocados.
     */
    private fun getJugadoresIds(convocatoriaId: Int): List<Int> {
        val jugadoresIds = mutableListOf<Int>()

        val sql = "SELECT jugador_id FROM JugadoresConvocados WHERE convocatoria_id = ?"

        DataBaseManager.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, convocatoriaId)
            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                jugadoresIds.add(resultSet.getInt("jugador_id"))
            }
        }

        return jugadoresIds
    }

    /**
     * Obtiene los IDs de los jugadores titulares para una convocatoria.
     *
     * @param convocatoriaId El ID de la convocatoria.
     * @return Una lista de IDs de jugadores titulares.
     */
    private fun getTitularesIds(convocatoriaId: Int): List<Int> {
        val titularesIds = mutableListOf<Int>()

        val sql = "SELECT jugador_id FROM JugadoresConvocados WHERE convocatoria_id = ? AND es_titular = 1"

        DataBaseManager.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, convocatoriaId)
            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                titularesIds.add(resultSet.getInt("jugador_id"))
            }
        }

        return titularesIds
    }
}