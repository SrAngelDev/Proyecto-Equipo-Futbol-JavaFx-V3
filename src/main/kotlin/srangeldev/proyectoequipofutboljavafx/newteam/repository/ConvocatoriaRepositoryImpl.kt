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
    }

    override fun getAll(): List<Convocatoria> {
        logger.debug { "Obteniendo todas las convocatorias" }

        // Limpiar la caché para asegurarnos de obtener datos actualizados
        convocatorias.clear()

        val convocatoriasList = mutableListOf<Convocatoria>()
        val convocatoriaIds = mutableListOf<Int>()
        val jugadoresPorConvocatoria = mutableMapOf<Int, MutableList<Int>>()
        val titularesPorConvocatoria = mutableMapOf<Int, MutableList<Int>>()

        DataBaseManager.use { db ->
            // Primero obtenemos todas las convocatorias
            val sqlConvocatorias = "SELECT * FROM Convocatorias"
            val statementConvocatorias = db.connection?.createStatement()
            val resultSetConvocatorias = statementConvocatorias?.executeQuery(sqlConvocatorias)

            // Guardamos los IDs de las convocatorias para luego obtener sus jugadores
            while (resultSetConvocatorias?.next() == true) {
                val convocatoriaId = resultSetConvocatorias.getInt("id")
                convocatoriaIds.add(convocatoriaId)

                // Inicializamos las listas para esta convocatoria
                jugadoresPorConvocatoria[convocatoriaId] = mutableListOf()
                titularesPorConvocatoria[convocatoriaId] = mutableListOf()
            }

            // Cerramos el resultSet de convocatorias
            resultSetConvocatorias?.close()

            // Ahora obtenemos todos los jugadores convocados de una sola vez
            if (convocatoriaIds.isNotEmpty()) {
                val sqlJugadores = "SELECT convocatoria_id, jugador_id, es_titular FROM JugadoresConvocados WHERE convocatoria_id IN (${convocatoriaIds.joinToString(",")})"
                val statementJugadores = db.connection?.createStatement()
                val resultSetJugadores = statementJugadores?.executeQuery(sqlJugadores)

                while (resultSetJugadores?.next() == true) {
                    val convocatoriaId = resultSetJugadores.getInt("convocatoria_id")
                    val jugadorId = resultSetJugadores.getInt("jugador_id")
                    val esTitular = resultSetJugadores.getInt("es_titular") == 1

                    // Añadimos el jugador a la lista de jugadores de esta convocatoria
                    jugadoresPorConvocatoria[convocatoriaId]?.add(jugadorId)

                    // Si es titular, lo añadimos también a la lista de titulares
                    if (esTitular) {
                        titularesPorConvocatoria[convocatoriaId]?.add(jugadorId)
                    }
                }

                // Cerramos el resultSet de jugadores
                resultSetJugadores?.close()
            }

            // Finalmente, volvemos a obtener las convocatorias para crear los objetos completos
            val sqlConvocatoriasAgain = "SELECT * FROM Convocatorias"
            val statementConvocatoriasAgain = db.connection?.createStatement()
            val resultSetConvocatoriasAgain = statementConvocatoriasAgain?.executeQuery(sqlConvocatoriasAgain)

            while (resultSetConvocatoriasAgain?.next() == true) {
                val convocatoriaId = resultSetConvocatoriasAgain.getInt("id")
                val convocatoria = Convocatoria(
                    id = convocatoriaId,
                    fecha = resultSetConvocatoriasAgain.getDate("fecha").toLocalDate(),
                    descripcion = resultSetConvocatoriasAgain.getString("descripcion"),
                    equipoId = resultSetConvocatoriasAgain.getInt("equipo_id"),
                    entrenadorId = resultSetConvocatoriasAgain.getInt("entrenador_id"),
                    jugadores = jugadoresPorConvocatoria[convocatoriaId] ?: emptyList(),
                    titulares = titularesPorConvocatoria[convocatoriaId] ?: emptyList(),
                    createdAt = resultSetConvocatoriasAgain.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = resultSetConvocatoriasAgain.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )

                // Añadir a la lista y a la caché
                convocatoriasList.add(convocatoria)
                convocatorias[convocatoria.id] = convocatoria
            }

            // Cerramos el resultSet de convocatorias
            resultSetConvocatoriasAgain?.close()
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
        val jugadoresIds = mutableListOf<Int>()
        val titularesIds = mutableListOf<Int>()

        DataBaseManager.use { db ->
            // Primero obtenemos la convocatoria
            val sqlConvocatoria = "SELECT * FROM Convocatorias WHERE id = ?"
            val preparedStatementConvocatoria = db.connection?.prepareStatement(sqlConvocatoria)
            preparedStatementConvocatoria?.setInt(1, id)
            val resultSetConvocatoria = preparedStatementConvocatoria?.executeQuery()

            if (resultSetConvocatoria?.next() == true) {
                // Ahora obtenemos los jugadores convocados
                val sqlJugadores = "SELECT jugador_id, es_titular FROM JugadoresConvocados WHERE convocatoria_id = ?"
                val preparedStatementJugadores = db.connection?.prepareStatement(sqlJugadores)
                preparedStatementJugadores?.setInt(1, id)
                val resultSetJugadores = preparedStatementJugadores?.executeQuery()

                while (resultSetJugadores?.next() == true) {
                    val jugadorId = resultSetJugadores.getInt("jugador_id")
                    val esTitular = resultSetJugadores.getInt("es_titular") == 1

                    jugadoresIds.add(jugadorId)
                    if (esTitular) {
                        titularesIds.add(jugadorId)
                    }
                }

                // Creamos el objeto convocatoria
                convocatoria = Convocatoria(
                    id = resultSetConvocatoria.getInt("id"),
                    fecha = resultSetConvocatoria.getDate("fecha").toLocalDate(),
                    descripcion = resultSetConvocatoria.getString("descripcion"),
                    equipoId = resultSetConvocatoria.getInt("equipo_id"),
                    entrenadorId = resultSetConvocatoria.getInt("entrenador_id"),
                    jugadores = jugadoresIds,
                    titulares = titularesIds,
                    createdAt = resultSetConvocatoria.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = resultSetConvocatoria.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )

                // Añadimos la convocatoria a la caché
                convocatorias[convocatoria!!.id] = convocatoria!!
            }

            // Cerramos los resultSets
            resultSetConvocatoria?.close()
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

            DataBaseManager.use { db ->
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

        DataBaseManager.use { db ->
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

        // Verificar si la convocatoria existe
        val existingConvocatoria = getById(id)
        if (existingConvocatoria == null) {
            logger.debug { "No se encontró ninguna convocatoria con ID: $id" }
            return null
        }

        var success = false

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Eliminar la convocatoria (las restricciones de clave foránea eliminarán automáticamente los jugadores convocados)
            val sql = "DELETE FROM Convocatorias WHERE id = ?"

            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, id)

            val rowsAffected = preparedStatement.executeUpdate()
            success = rowsAffected > 0

            if (success) {
                // Eliminar de la caché
                convocatorias.remove(id)
                logger.debug { "Convocatoria eliminada correctamente: $id" }
            } else {
                logger.debug { "No se eliminó ninguna convocatoria con ID: $id" }
            }
        }

        return if (success) existingConvocatoria else null
    }

    override fun getJugadoresConvocados(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores convocados para la convocatoria con ID: $convocatoriaId" }

        val jugadoresIds = mutableListOf<Int>()

        DataBaseManager.use { db ->
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

        DataBaseManager.use { db ->
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

    fun getJugadoresSuplentes(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores suplentes para la convocatoria con ID: $convocatoriaId" }

        val jugadoresIds = mutableListOf<Int>()

        DataBaseManager.use { db ->
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

    fun getJugadoresNoConvocados(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores no convocados para la convocatoria con ID: $convocatoriaId" }

        val jugadoresConvocadosIds = mutableListOf<Int>()

        DataBaseManager.use { db ->
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

        DataBaseManager.use { db ->
            val sql = "SELECT id FROM Convocatorias WHERE equipo_id = ?"

            val preparedStatement = db.connection?.prepareStatement(sql)
            preparedStatement?.setInt(1, equipoId)
            val resultSet = preparedStatement?.executeQuery()

            while (resultSet?.next() == true) {
                val convocatoriaId = resultSet.getInt("id")
                val convocatoria = getById(convocatoriaId)
                if (convocatoria != null) {
                    convocatoriasList.add(convocatoria)
                }
            }

            resultSet?.close()
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

        // Validar que todos los titulares estén en la lista de jugadores
        for (titularId in convocatoria.titulares) {
            if (titularId !in convocatoria.jugadores) {
                logger.debug { "El jugador titular con ID $titularId no está en la lista de jugadores convocados" }
                return false
            }
        }

        return true
    }

    // Los métodos getJugadoresIds y getTitularesIds han sido eliminados porque causaban problemas
    // con las conexiones a la base de datos. Ahora cada método público implementa su propia
    // lógica para obtener los datos que necesita en una sola conexión.
}
