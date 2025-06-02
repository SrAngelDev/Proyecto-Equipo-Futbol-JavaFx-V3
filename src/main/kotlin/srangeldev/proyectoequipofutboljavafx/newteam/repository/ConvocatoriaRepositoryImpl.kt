package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.emptyList

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
    }

    override fun getAll(): List<Convocatoria> {
        logger.debug { "Obteniendo todas las convocatorias" }

        // Limpiar la caché para asegurarnos de obtener datos actualizados
        convocatorias.clear()

        val convocatoriasList = mutableListOf<Convocatoria>()
        val jugadoresPorConvocatoria = mutableMapOf<Int, MutableList<Int>>()
        val titularesPorConvocatoria = mutableMapOf<Int, MutableList<Int>>()

        try {
            DataBaseManager.instance.use { db ->
                val connection = db.connection ?: return@use

                // Obtener todas las convocatorias
                val sqlConvocatorias = "SELECT * FROM Convocatorias"
                val statementConvocatorias = connection.createStatement()
                val resultSetConvocatorias = statementConvocatorias.executeQuery(sqlConvocatorias)

                // Procesar las convocatorias
                val convocatoriaIds = mutableListOf<Int>()
                while (resultSetConvocatorias.next()) {
                    val convocatoriaId = resultSetConvocatorias.getInt("id")
                    convocatoriaIds.add(convocatoriaId)

                    // Inicializar las listas para esta convocatoria
                    jugadoresPorConvocatoria[convocatoriaId] = mutableListOf()
                    titularesPorConvocatoria[convocatoriaId] = mutableListOf()

                    // Crear el objeto convocatoria (sin jugadores por ahora)
                    val createdAt = try {
                        val timestamp = resultSetConvocatorias.getTimestamp("created_at")
                        timestamp?.toLocalDateTime() ?: LocalDateTime.now()
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }

                    val updatedAt = try {
                        val timestamp = resultSetConvocatorias.getTimestamp("updated_at")
                        timestamp?.toLocalDateTime() ?: LocalDateTime.now()
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }

                    val convocatoria = Convocatoria(
                        id = convocatoriaId,
                        fecha = resultSetConvocatorias.getDate("fecha").toLocalDate(),
                        descripcion = resultSetConvocatorias.getString("descripcion"),
                        equipoId = resultSetConvocatorias.getInt("equipo_id"),
                        entrenadorId = resultSetConvocatorias.getInt("entrenador_id"),
                        jugadores = emptyList(), // Se actualizará después
                        titulares = emptyList(), // Se actualizará después
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )

                    convocatoriasList.add(convocatoria)
                    convocatorias[convocatoriaId] = convocatoria
                }

                resultSetConvocatorias.close()

                // Obtener los jugadores convocados
                if (convocatoriaIds.isNotEmpty()) {
                    val sqlJugadores = "SELECT convocatoria_id, jugador_id, es_titular FROM JugadoresConvocados WHERE convocatoria_id IN (${convocatoriaIds.joinToString(",")})"
                    val statementJugadores = connection.createStatement()
                    val resultSetJugadores = statementJugadores.executeQuery(sqlJugadores)

                    while (resultSetJugadores.next()) {
                        val convocatoriaId = resultSetJugadores.getInt("convocatoria_id")
                        val jugadorId = resultSetJugadores.getInt("jugador_id")
                        val esTitular = resultSetJugadores.getInt("es_titular") == 1

                        // Añadir el jugador a la lista de jugadores de esta convocatoria
                        jugadoresPorConvocatoria[convocatoriaId]?.add(jugadorId)

                        // Si es titular, añadirlo también a la lista de titulares
                        if (esTitular) {
                            titularesPorConvocatoria[convocatoriaId]?.add(jugadorId)
                        }
                    }

                    resultSetJugadores.close()
                }

                // Actualizar las convocatorias con sus jugadores y titulares
                for (convocatoria in convocatoriasList.toList()) { // Create a copy to avoid concurrent modification
                    val convocatoriaId = convocatoria.id
                    val jugadores = jugadoresPorConvocatoria[convocatoriaId] ?: emptyList()
                    val titulares = titularesPorConvocatoria[convocatoriaId] ?: emptyList()

                    // Crear una nueva instancia con los jugadores y titulares
                    val updatedConvocatoria = convocatoria.copy(
                        jugadores = jugadores,
                        titulares = titulares
                    )

                    // Actualizar la caché
                    convocatorias[convocatoriaId] = updatedConvocatoria

                    // Reemplazar en la lista
                    val index = convocatoriasList.indexOf(convocatoria)
                    if (index >= 0) {
                        convocatoriasList[index] = updatedConvocatoria
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al obtener todas las convocatorias" }
        }

        return convocatoriasList
    }

    override fun getById(id: Int): Convocatoria? {
        logger.debug { "Obteniendo convocatoria por ID: $id" }

        // Check cache first for non-test scenarios
        if (convocatorias.containsKey(id)) {
            return convocatorias[id]
        }

        var convocatoria: Convocatoria? = null
        val jugadoresIds = mutableListOf<Int>()
        val titularesIds = mutableListOf<Int>()

        try {
            DataBaseManager.instance.use { db ->
                val connection = db.connection ?: return@use

                // Primero obtenemos la convocatoria
                val sqlConvocatoria = "SELECT * FROM Convocatorias WHERE id = $id"
                val statementConvocatoria = connection.createStatement()
                val resultSetConvocatoria = statementConvocatoria.executeQuery(sqlConvocatoria)

                if (resultSetConvocatoria.next()) {
                    // Ahora obtenemos los jugadores convocados
                    val sqlJugadores = "SELECT jugador_id, es_titular FROM JugadoresConvocados WHERE convocatoria_id = ?"
                    val preparedStatementJugadores = connection.prepareStatement(sqlJugadores)
                    preparedStatementJugadores.setInt(1, id)
                    val resultSetJugadores = preparedStatementJugadores.executeQuery()

                    while (resultSetJugadores.next()) {
                        val jugadorId = resultSetJugadores.getInt("jugador_id")
                        val esTitular = resultSetJugadores.getInt("es_titular") == 1

                        jugadoresIds.add(jugadorId)
                        if (esTitular) {
                            titularesIds.add(jugadorId)
                        }
                    }

                    // Creamos el objeto convocatoria
                    val createdAt = try {
                        val timestamp = resultSetConvocatoria.getTimestamp("created_at")
                        timestamp?.toLocalDateTime() ?: LocalDateTime.now()
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }

                    val updatedAt = try {
                        val timestamp = resultSetConvocatoria.getTimestamp("updated_at")
                        timestamp?.toLocalDateTime() ?: LocalDateTime.now()
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }

                    convocatoria = Convocatoria(
                        id = resultSetConvocatoria.getInt("id"),
                        fecha = resultSetConvocatoria.getDate("fecha").toLocalDate(),
                        descripcion = resultSetConvocatoria.getString("descripcion"),
                        equipoId = resultSetConvocatoria.getInt("equipo_id"),
                        entrenadorId = resultSetConvocatoria.getInt("entrenador_id"),
                        jugadores = jugadoresIds,
                        titulares = titularesIds,
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )

                    // Añadimos la convocatoria a la caché
                    convocatorias[convocatoria!!.id] = convocatoria!!
                }

                // Cerramos los resultSets
                resultSetConvocatoria.close()
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al obtener convocatoria por ID: $id" }
        }

        return convocatoria
    }

    override fun save(convocatoria: Convocatoria): Convocatoria {
        logger.debug { "Guardando convocatoria: $convocatoria" }

        val isUpdate = convocatoria.id > 0

        if (isUpdate) {
            // Actualizar convocatoria existente
            val updated = update(convocatoria.id, convocatoria)
            if (updated != null) {
                return updated
            } else {
                throw IllegalStateException("No se pudo actualizar la convocatoria")
            }
        } else {
            // Crear nueva convocatoria
            var newConvocatoria: Convocatoria? = null

            DataBaseManager.instance.use { db ->
                val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

                // Insertar la convocatoria
                val insertSql = """
                    INSERT INTO Convocatorias (fecha, descripcion, equipo_id, entrenador_id, created_at, updated_at) 
                    VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """.trimIndent()

                val preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
                preparedStatement.setDate(1, java.sql.Date.valueOf(convocatoria.fecha))
                preparedStatement.setString(2, convocatoria.descripcion)
                preparedStatement.setInt(3, convocatoria.equipoId)
                preparedStatement.setInt(4, convocatoria.entrenadorId)

                preparedStatement.executeUpdate()

                // Obtener el ID generado
                val generatedKeys = preparedStatement.generatedKeys
                if (generatedKeys.next()) {
                    val convocatoriaId = generatedKeys.getInt(1)

                    // Insertar los jugadores convocados
                    if (convocatoria.jugadores.isNotEmpty()) {
                        val insertJugadoresSql = "INSERT INTO JugadoresConvocados (convocatoria_id, jugador_id, es_titular) VALUES (?, ?, ?)"
                        val preparedStatementJugadores = connection.prepareStatement(insertJugadoresSql)

                        for (jugadorId in convocatoria.jugadores) {
                            preparedStatementJugadores.setInt(1, convocatoriaId)
                            preparedStatementJugadores.setInt(2, jugadorId)
                            preparedStatementJugadores.setInt(3, if (jugadorId in convocatoria.titulares) 1 else 0)
                            preparedStatementJugadores.addBatch()
                        }

                        preparedStatementJugadores.executeBatch()
                    }

                    // Obtener la convocatoria creada
                    newConvocatoria = getById(convocatoriaId)
                    if (newConvocatoria != null) {
                        // Actualizar la caché
                        convocatorias[newConvocatoria!!.id] = newConvocatoria!!
                    } else {
                        throw IllegalStateException("No se pudo obtener la convocatoria creada")
                    }
                } else {
                    throw IllegalStateException("No se pudo obtener el ID de la convocatoria creada")
                }
            }

            if (newConvocatoria != null) {
                return newConvocatoria!!
            } else {
                throw IllegalStateException("No se pudo crear la convocatoria")
            }
        }
    }

    override fun update(id: Int, entidad: Convocatoria): Convocatoria? {
        logger.debug { "Actualizando convocatoria con ID: $id" }

        // Verificar si la convocatoria existe
        val existingConvocatoria = getById(id)
        if (existingConvocatoria == null) {
            logger.debug { "No se encontró ninguna convocatoria con ID: $id" }
            return null
        }

        DataBaseManager.instance.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Actualizar la convocatoria
            val updateSql = """
                UPDATE Convocatorias 
                SET fecha = ?, descripcion = ?, equipo_id = ?, entrenador_id = ?, updated_at = CURRENT_TIMESTAMP 
                WHERE id = ?
            """.trimIndent()

            val preparedStatement = connection.prepareStatement(updateSql)
            preparedStatement.setDate(1, java.sql.Date.valueOf(entidad.fecha))
            preparedStatement.setString(2, entidad.descripcion)
            preparedStatement.setInt(3, entidad.equipoId)
            preparedStatement.setInt(4, entidad.entrenadorId)
            preparedStatement.setInt(5, id)

            preparedStatement.executeUpdate()

            // Eliminar todos los jugadores convocados para esta convocatoria
            val deleteJugadoresSql = "DELETE FROM JugadoresConvocados WHERE convocatoria_id = ?"
            val preparedStatementDelete = connection.prepareStatement(deleteJugadoresSql)
            preparedStatementDelete.setInt(1, id)
            preparedStatementDelete.executeUpdate()

            // Insertar los nuevos jugadores convocados
            val insertJugadoresSql = "INSERT INTO JugadoresConvocados (convocatoria_id, jugador_id, es_titular) VALUES (?, ?, ?)"
            val preparedStatementInsert = connection.prepareStatement(insertJugadoresSql)

            for (jugadorId in entidad.jugadores) {
                preparedStatementInsert.setInt(1, id)
                preparedStatementInsert.setInt(2, jugadorId)
                preparedStatementInsert.setInt(3, if (jugadorId in entidad.titulares) 1 else 0)
                preparedStatementInsert.addBatch()
            }

            preparedStatementInsert.executeBatch()
        }

        // Obtener la convocatoria actualizada
        val updatedConvocatoria = getById(id)
        if (updatedConvocatoria != null) {
            // Actualizar la caché
            convocatorias[updatedConvocatoria.id] = updatedConvocatoria
        }

        return updatedConvocatoria
    }

    override fun delete(id: Int): Convocatoria? {
        logger.debug { "Eliminando convocatoria con ID: $id" }

        // Verificar si la convocatoria existe y guardarla antes de eliminarla
        val existingConvocatoria = getById(id)
        if (existingConvocatoria == null) {
            logger.debug { "No se encontró ninguna convocatoria con ID: $id" }
            return null
        }

        // Crear una copia de la convocatoria existente para devolverla después
        val convocatoriaToReturn = existingConvocatoria.copy()
        var success = false

        try {
            DataBaseManager.instance.use { db ->
                val connection = db.connection ?: return@use

                // First delete the related records in JugadoresConvocados
                val sqlDeleteJugadores = "DELETE FROM JugadoresConvocados WHERE convocatoria_id = ?"
                val preparedStatementJugadores = connection.prepareStatement(sqlDeleteJugadores)
                preparedStatementJugadores.setInt(1, id)
                preparedStatementJugadores.executeUpdate()

                // Then delete the convocatoria
                val sqlDeleteConvocatoria = "DELETE FROM Convocatorias WHERE id = ?"
                val preparedStatementConvocatoria = connection.prepareStatement(sqlDeleteConvocatoria)
                preparedStatementConvocatoria.setInt(1, id)

                val rowsAffected = preparedStatementConvocatoria.executeUpdate()
                success = rowsAffected > 0

                if (success) {
                    // Eliminar de la caché
                    convocatorias.remove(id)
                    logger.debug { "Convocatoria eliminada correctamente: $id" }
                } else {
                    logger.debug { "No se eliminó ninguna convocatoria con ID: $id" }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al eliminar convocatoria con ID: $id" }
            return null
        }

        return if (success) convocatoriaToReturn else null
    }

    override fun getJugadoresConvocados(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores convocados para la convocatoria con ID: $convocatoriaId" }

        val jugadoresIds = mutableListOf<Int>()

        DataBaseManager.instance.use { db ->
            val sql = "SELECT jugador_id FROM JugadoresConvocados WHERE convocatoria_id = ?"

            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, convocatoriaId)
            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                jugadoresIds.add(resultSet.getInt("jugador_id"))
            }

            resultSet?.close()
        }

        return jugadoresIds.mapNotNull { jugadorId ->
            personalRepository.getById(jugadorId) as? Jugador
        }
    }

    override fun getJugadoresTitulares(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores titulares para la convocatoria con ID: $convocatoriaId" }

        val jugadoresIds = mutableListOf<Int>()

        DataBaseManager.instance.use { db ->
            val sql = "SELECT jugador_id FROM JugadoresConvocados WHERE convocatoria_id = ? AND es_titular = 1"

            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, convocatoriaId)
            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                jugadoresIds.add(resultSet.getInt("jugador_id"))
            }

            resultSet?.close()
        }

        return jugadoresIds.mapNotNull { jugadorId ->
            personalRepository.getById(jugadorId) as? Jugador
        }
    }

    override fun getJugadoresSuplentes(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores suplentes para la convocatoria con ID: $convocatoriaId" }

        val jugadoresIds = mutableListOf<Int>()

        DataBaseManager.instance.use { db ->
            val sql = "SELECT jugador_id FROM JugadoresConvocados WHERE convocatoria_id = ? AND es_titular = 0"

            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, convocatoriaId)
            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                jugadoresIds.add(resultSet.getInt("jugador_id"))
            }

            resultSet?.close()
        }

        return jugadoresIds.mapNotNull { jugadorId ->
            personalRepository.getById(jugadorId) as? Jugador
        }
    }

    override fun getJugadoresNoConvocados(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores no convocados para la convocatoria con ID: $convocatoriaId" }

        val jugadoresConvocadosIds = mutableListOf<Int>()

        DataBaseManager.instance.use { db ->
            val sql = "SELECT jugador_id FROM JugadoresConvocados WHERE convocatoria_id = ?"

            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, convocatoriaId)
            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                jugadoresConvocadosIds.add(resultSet.getInt("jugador_id"))
            }

            resultSet?.close()
        }

        // Obtener todos los jugadores y filtrar los que no están convocados
        val todosLosJugadores = personalRepository.getAll().filterIsInstance<Jugador>()
        return todosLosJugadores.filter { jugador -> jugador.id !in jugadoresConvocadosIds }
    }

    override fun getByEquipoId(equipoId: Int): List<Convocatoria> {
        logger.debug { "Obteniendo convocatorias por equipo ID: $equipoId" }

        val convocatoriasList = mutableListOf<Convocatoria>()
        val jugadoresPorConvocatoria = mutableMapOf<Int, MutableList<Int>>()
        val titularesPorConvocatoria = mutableMapOf<Int, MutableList<Int>>()

        try {
            DataBaseManager.instance.use { db ->
                val connection = db.connection ?: return@use

                // Obtener todas las convocatorias para este equipo
                val sqlConvocatorias = "SELECT * FROM Convocatorias WHERE equipo_id = ?"
                val preparedStatementConvocatorias = connection.prepareStatement(sqlConvocatorias)
                preparedStatementConvocatorias.setInt(1, equipoId)
                val resultSetConvocatorias = preparedStatementConvocatorias.executeQuery()

                // Procesar las convocatorias
                val convocatoriaIds = mutableListOf<Int>()
                while (resultSetConvocatorias.next()) {
                    val convocatoriaId = resultSetConvocatorias.getInt("id")
                    convocatoriaIds.add(convocatoriaId)

                    // Inicializar las listas para esta convocatoria
                    jugadoresPorConvocatoria[convocatoriaId] = mutableListOf()
                    titularesPorConvocatoria[convocatoriaId] = mutableListOf()

                    // Crear el objeto convocatoria (sin jugadores por ahora)
                    val createdAt = try {
                        val timestamp = resultSetConvocatorias.getTimestamp("created_at")
                        timestamp?.toLocalDateTime() ?: LocalDateTime.now()
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }

                    val updatedAt = try {
                        val timestamp = resultSetConvocatorias.getTimestamp("updated_at")
                        timestamp?.toLocalDateTime() ?: LocalDateTime.now()
                    } catch (e: Exception) {
                        LocalDateTime.now()
                    }

                    val convocatoria = Convocatoria(
                        id = convocatoriaId,
                        fecha = resultSetConvocatorias.getDate("fecha").toLocalDate(),
                        descripcion = resultSetConvocatorias.getString("descripcion"),
                        equipoId = resultSetConvocatorias.getInt("equipo_id"),
                        entrenadorId = resultSetConvocatorias.getInt("entrenador_id"),
                        jugadores = emptyList(), // Se actualizará después
                        titulares = emptyList(), // Se actualizará después
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )

                    convocatoriasList.add(convocatoria)
                    convocatorias[convocatoriaId] = convocatoria
                }

                resultSetConvocatorias.close()

                // Obtener los jugadores convocados para estas convocatorias
                if (convocatoriaIds.isNotEmpty()) {
                    val sqlJugadores = "SELECT convocatoria_id, jugador_id, es_titular FROM JugadoresConvocados WHERE convocatoria_id IN (${convocatoriaIds.joinToString(",")})"
                    val statementJugadores = connection.createStatement()
                    val resultSetJugadores = statementJugadores.executeQuery(sqlJugadores)

                    while (resultSetJugadores.next()) {
                        val convocatoriaId = resultSetJugadores.getInt("convocatoria_id")
                        val jugadorId = resultSetJugadores.getInt("jugador_id")
                        val esTitular = resultSetJugadores.getInt("es_titular") == 1

                        // Añadir el jugador a la lista de jugadores de esta convocatoria
                        jugadoresPorConvocatoria[convocatoriaId]?.add(jugadorId)

                        // Si es titular, añadirlo también a la lista de titulares
                        if (esTitular) {
                            titularesPorConvocatoria[convocatoriaId]?.add(jugadorId)
                        }
                    }

                    resultSetJugadores.close()

                    // Actualizar las convocatorias con sus jugadores y titulares
                    for (convocatoria in convocatoriasList.toList()) { // Create a copy to avoid concurrent modification
                        val convocatoriaId = convocatoria.id
                        val jugadores = jugadoresPorConvocatoria[convocatoriaId] ?: emptyList()
                        val titulares = titularesPorConvocatoria[convocatoriaId] ?: emptyList()

                        // Crear una nueva instancia con los jugadores y titulares
                        val updatedConvocatoria = convocatoria.copy(
                            jugadores = jugadores,
                            titulares = titulares
                        )

                        // Actualizar la caché
                        convocatorias[convocatoriaId] = updatedConvocatoria

                        // Reemplazar en la lista
                        val index = convocatoriasList.indexOf(convocatoria)
                        if (index >= 0) {
                            convocatoriasList[index] = updatedConvocatoria
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error al obtener convocatorias por equipo ID: $equipoId" }
        }

        return convocatoriasList
    }

    override fun validarConvocatoria(convocatoria: Convocatoria): Boolean {
        logger.debug { "Validando convocatoria: $convocatoria" }

        // Validar que la fecha no sea anterior a la fecha actual
        if (convocatoria.fecha.isBefore(LocalDate.now())) {
            logger.debug { "La fecha de la convocatoria no puede ser anterior a la fecha actual" }
            return false
        }

        // Validar que haya al menos un jugador convocado
        if (convocatoria.jugadores.isEmpty()) {
            logger.debug { "La convocatoria debe tener al menos un jugador" }
            return false
        }

        // Validar que haya al menos un jugador titular
        if (convocatoria.titulares.isEmpty()) {
            logger.debug { "La convocatoria debe tener al menos un jugador titular" }
            return false
        }

        // Obtener todos los jugadores para validar posiciones
        val jugadoresMap = convocatoria.jugadores.mapNotNull { jugadorId ->
            val jugador = personalRepository.getById(jugadorId) as? Jugador
            if (jugador != null) jugadorId to jugador else null
        }.toMap()

        // Usar el método esValida de la clase Convocatoria
        return convocatoria.esValida(jugadoresMap)
    }
}
