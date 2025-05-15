package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.database.DataBaseManager
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.sql.Statement
import java.time.LocalDateTime
// import java.time.format.DateTimeFormatter (no longer needed)

/**
 * Implementación del repositorio de personal.
 */
class PersonalRespositoryImpl : PersonalRepository {
    private val logger = logging()
    private val personal = mutableMapOf<Int, Personal>()
    // SQLite stores timestamps in the format "YYYY-MM-DD HH:MM:SS"
    // No longer needed as we're using getTimestamp instead of getString for datetime fields
    // private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        logger.debug { "Inicializando repositorio de personal" }
    }

    /**
     * Obtiene una lista de todos los entrenadores.
     *
     * @return Una lista de objetos Entrenador.
     */
    private fun getEntrenadores(): List<Entrenador> {
        val sql = """
            SELECT p.*, e.especializacion 
            FROM Personal p
            INNER JOIN Entrenadores e ON p.id = e.id
            WHERE p.tipo = 'ENTRENADOR'
        """.trimIndent()

        DataBaseManager.use { db ->
            val res = db.connection?.prepareStatement(sql)!!.executeQuery()
            while (res.next()) {
                val entrenador = Entrenador(
                    id = res.getInt("id"),
                    nombre = res.getString("nombre"),
                    apellidos = res.getString("apellidos"),
                    fechaNacimiento = res.getDate("fecha_nacimiento").toLocalDate(),
                    fechaIncorporacion = res.getDate("fecha_incorporacion").toLocalDate(),
                    salario = res.getDouble("salario"),
                    paisOrigen = res.getString("pais_origen"),
                    especializacion = Entrenador.Especializacion.valueOf(res.getString("especializacion")),
                    createdAt = res.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = res.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )
                personal[entrenador.id] = entrenador
            }
        }
        return personal.values.filterIsInstance<Entrenador>()
    }

    private fun getJugadores(): List<Jugador> {
        val sql = """
            SELECT p.*, j.posicion, j.dorsal, j.altura, j.peso, j.goles, j.partidos_jugados 
            FROM Personal p
            INNER JOIN Jugadores j ON p.id = j.id
            WHERE p.tipo = 'JUGADOR'
        """.trimIndent()

        DataBaseManager.use { db ->
            val res = db.connection?.prepareStatement(sql)!!.executeQuery()
            while (res.next()) {
                val jugador = Jugador(
                    id = res.getInt("id"),
                    nombre = res.getString("nombre"),
                    apellidos = res.getString("apellidos"),
                    fechaNacimiento = res.getDate("fecha_nacimiento").toLocalDate(),
                    fechaIncorporacion = res.getDate("fecha_incorporacion").toLocalDate(),
                    salario = res.getDouble("salario"),
                    paisOrigen = res.getString("pais_origen"),
                    posicion = Jugador.Posicion.valueOf(res.getString("posicion")),
                    dorsal = res.getInt("dorsal"),
                    altura = res.getDouble("altura"),
                    peso = res.getDouble("peso"),
                    goles = res.getInt("goles"),
                    partidosJugados = res.getInt("partidos_jugados"),
                    createdAt = res.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = res.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )
                personal[jugador.id] = jugador
            }
        }
        return personal.values.filterIsInstance<Jugador>()
    }

    override fun getAll(): List<Personal> {
        logger.debug { "Obteniendo a todo el personal" }
        if (personal.isEmpty()) {
            logger.debug { "Cargando personal desde la base de datos" }
            getEntrenadores()
            getJugadores()
        }
        return personal.values.toList()
    }

    /**
     * Obtiene un objeto personal por su ID.
     *
     * @param id El ID del objeto personal a obtener.
     * @return El objeto personal con el ID especificado, o null si no se encuentra.
     */
    override fun getById(id: Int): Personal? {
        logger.debug { "Obteniendo personal por ID: $id" }

        // First try to find an Entrenador
        val sqlEntrenador = """
            SELECT p.*, e.especializacion 
            FROM Personal p
            INNER JOIN Entrenadores e ON p.id = e.id
            WHERE p.id = ?
        """.trimIndent()

        var entrenador: Entrenador? = null
        DataBaseManager.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sqlEntrenador)
            preparedStatement?.setInt(1, id)
            val res = preparedStatement?.executeQuery()

            if (res?.next() == true) {
                entrenador = Entrenador(
                    id = res.getInt("id"),
                    nombre = res.getString("nombre"),
                    apellidos = res.getString("apellidos"),
                    fechaNacimiento = res.getDate("fecha_nacimiento").toLocalDate(),
                    fechaIncorporacion = res.getDate("fecha_incorporacion").toLocalDate(),
                    salario = res.getDouble("salario"),
                    paisOrigen = res.getString("pais_origen"),
                    especializacion = Entrenador.Especializacion.valueOf(res.getString("especializacion")),
                    createdAt = res.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = res.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )
            }
        }

        if (entrenador != null) {
            return entrenador
        }

        // If not found, try to find a Jugador
        val sqlJugador = """
            SELECT p.*, j.posicion, j.dorsal, j.altura, j.peso, j.goles, j.partidos_jugados 
            FROM Personal p
            INNER JOIN Jugadores j ON p.id = j.id
            WHERE p.id = ?
        """.trimIndent()

        var jugador: Jugador? = null
        DataBaseManager.use { db ->
            val preparedStatement = db.connection?.prepareStatement(sqlJugador)
            preparedStatement?.setInt(1, id)
            val res = preparedStatement?.executeQuery()

            if (res?.next() == true) {
                jugador = Jugador(
                    id = res.getInt("id"),
                    nombre = res.getString("nombre"),
                    apellidos = res.getString("apellidos"),
                    fechaNacimiento = res.getDate("fecha_nacimiento").toLocalDate(),
                    fechaIncorporacion = res.getDate("fecha_incorporacion").toLocalDate(),
                    salario = res.getDouble("salario"),
                    paisOrigen = res.getString("pais_origen"),
                    posicion = Jugador.Posicion.valueOf(res.getString("posicion")),
                    dorsal = res.getInt("dorsal"),
                    altura = res.getDouble("altura"),
                    peso = res.getDouble("peso"),
                    goles = res.getInt("goles"),
                    partidosJugados = res.getInt("partidos_jugados"),
                    createdAt = res.getTimestamp("created_at")?.toLocalDateTime() ?: LocalDateTime.now(),
                    updatedAt = res.getTimestamp("updated_at")?.toLocalDateTime() ?: LocalDateTime.now()
                )
            }
        }

        return jugador
    }

    /**
     * Guarda un objeto personal.
     *
     * @param entidad El objeto personal a guardar.
     * @return El objeto personal guardado.
     */
    override fun save(entidad: Personal): Personal {
        logger.debug { "Guardando personal: $entidad" }
        val timeStamp = LocalDateTime.now()

        DataBaseManager.use { db ->
            val connection = db.connection ?: throw IllegalStateException("Conexión a la base de datos no disponible")

            // Primero insertamos en la tabla Personal
            val sqlPersonal = """
                                INSERT INTO Personal (nombre, apellidos, fecha_nacimiento, fecha_incorporacion, 
                                salario, pais_origen, tipo, created_at, updated_at) 
                                VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                            """.trimIndent()

            val preparedStatementPersonal = connection.prepareStatement(sqlPersonal, Statement.RETURN_GENERATED_KEYS)
            preparedStatementPersonal.apply {
                setString(1, entidad.nombre)
                setString(2, entidad.apellidos)
                setDate(3, java.sql.Date.valueOf(entidad.fechaNacimiento))
                setDate(4, java.sql.Date.valueOf(entidad.fechaIncorporacion))
                setDouble(5, entidad.salario)
                setString(6, entidad.paisOrigen)
                setString(7, if (entidad is Jugador) "JUGADOR" else "ENTRENADOR")
            }
            preparedStatementPersonal.executeUpdate()

            val generatedId = preparedStatementPersonal.generatedKeys.let {
                it.next()
                it.getInt(1)
            }

            // Luego insertamos en la tabla específica
            when (entidad) {
                is Jugador -> {
                    val sqlJugador = """
                                        INSERT INTO Jugadores (id, posicion, dorsal, altura, peso, goles, partidos_jugados) 
                                        VALUES (?, ?, ?, ?, ?, ?, ?)
                                    """.trimIndent()

                    connection.prepareStatement(sqlJugador).apply {
                        setInt(1, generatedId)
                        setString(2, entidad.posicion.name)
                        setInt(3, entidad.dorsal)
                        setDouble(4, entidad.altura)
                        setDouble(5, entidad.peso)
                        setInt(6, entidad.goles)
                        setInt(7, entidad.partidosJugados)
                        executeUpdate()
                    }

                    Jugador(
                        id = generatedId,
                        nombre = entidad.nombre,
                        apellidos = entidad.apellidos,
                        fechaNacimiento = entidad.fechaNacimiento,
                        fechaIncorporacion = entidad.fechaIncorporacion,
                        salario = entidad.salario,
                        paisOrigen = entidad.paisOrigen,
                        posicion = entidad.posicion,
                        dorsal = entidad.dorsal,
                        altura = entidad.altura,
                        peso = entidad.peso,
                        goles = entidad.goles,
                        partidosJugados = entidad.partidosJugados,
                        createdAt = timeStamp,
                        updatedAt = timeStamp
                    )
                }

                is Entrenador -> {
                    val sqlEntrenador = "INSERT INTO Entrenadores (id, especializacion) VALUES (?, ?)"

                    connection.prepareStatement(sqlEntrenador).apply {
                        setInt(1, generatedId)
                        setString(2, entidad.especializacion.name)
                        executeUpdate()
                    }

                    Entrenador(
                        id = generatedId,
                        nombre = entidad.nombre,
                        apellidos = entidad.apellidos,
                        fechaNacimiento = entidad.fechaNacimiento,
                        fechaIncorporacion = entidad.fechaIncorporacion,
                        salario = entidad.salario,
                        paisOrigen = entidad.paisOrigen,
                        especializacion = entidad.especializacion,
                        createdAt = timeStamp,
                        updatedAt = timeStamp
                    )
                }

                else -> throw IllegalArgumentException("Tipo desconocido de Personal")
            }
        }
        // Devolvemos el objeto personal guardado
        return entidad
    }

    /**
     * Actualiza un objeto personal por su ID.
     *
     * @param id El ID del objeto personal a actualizar.
     * @param entidad El objeto personal con los datos actualizados.
     * @return El objeto personal actualizado, o null si no se encuentra.
     */
    override fun update(id: Int, entidad: Personal): Personal? {
        logger.debug { "Actualizando personal con ID: $id" }
        // Aqui nos pasa lo mismo que en el save, tenemos que diferenciar entre si es un jugador o un entrenador
        val personal: Personal? = this.getById(id)
        val timeStamp = LocalDateTime.now()

        if (personal != null) {
            // First update the Personal table
            val sqlPersonal = """
                UPDATE Personal 
                SET nombre = ?, apellidos = ?, fecha_nacimiento = ?, fecha_incorporacion = ?, 
                    salario = ?, pais_origen = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
            """.trimIndent()

            DataBaseManager.use { db ->
                val preparedStatement = db.connection?.prepareStatement(sqlPersonal)!!
                preparedStatement.setString(1, entidad.nombre)
                preparedStatement.setString(2, entidad.apellidos)
                preparedStatement.setDate(3, java.sql.Date.valueOf(entidad.fechaNacimiento))
                preparedStatement.setDate(4, java.sql.Date.valueOf(entidad.fechaIncorporacion))
                preparedStatement.setDouble(5, entidad.salario)
                preparedStatement.setString(6, entidad.paisOrigen)
                preparedStatement.setInt(7, id)
                preparedStatement.executeUpdate()
            }

            // Then update the specific table
            val sql = when (entidad) {
                is Jugador -> """
                    UPDATE Jugadores 
                    SET posicion = ?, dorsal = ?, altura = ?, peso = ?, goles = ?, partidos_jugados = ? 
                    WHERE id = ?
                """.trimIndent()
                is Entrenador -> """
                    UPDATE Entrenadores 
                    SET especializacion = ? 
                    WHERE id = ?
                """.trimIndent()
                else -> throw IllegalArgumentException("Tipo desconocido de Personal")
            }

            DataBaseManager.use { db ->
                val preparedStatement = db.connection?.prepareStatement(sql)!!

                if (entidad is Jugador) {
                    preparedStatement.setString(1, entidad.posicion.name)
                    preparedStatement.setInt(2, entidad.dorsal)
                    preparedStatement.setDouble(3, entidad.altura)
                    preparedStatement.setDouble(4, entidad.peso)
                    preparedStatement.setInt(5, entidad.goles)
                    preparedStatement.setInt(6, entidad.partidosJugados)
                    preparedStatement.setInt(7, id)
                } else if (entidad is Entrenador) {
                    preparedStatement.setString(1, entidad.especializacion.name)
                    preparedStatement.setInt(2, id)
                }

                preparedStatement.executeUpdate()
            }
            return entidad
        } else {
            logger.debug { "No se encontró el personal con ID: $id" }
            return null
        }
    }

    /**
     * Elimina un objeto personal por su ID.
     *
     * @param id El ID del objeto personal a eliminar.
     * @return El objeto personal eliminado.
     */
    override fun delete(id: Int): Personal? {
        logger.debug { "Eliminando personal con ID: $id" }
        // Aqui hay que eliminar la id tanto de la tabla de personal como la propia de jugador o entrenador
        val personal: Personal? = this.getById(id)
        if (personal != null) {
            val sql = when (personal) {
                is Jugador -> "DELETE FROM Jugadores WHERE id = ?"
                is Entrenador -> "DELETE FROM Entrenadores WHERE id = ?"
                else -> throw IllegalArgumentException("Tipo desconocido de Personal")
            }
            DataBaseManager.use { db ->
                val preparedStatement = db.connection?.prepareStatement(sql)!!
                preparedStatement.setInt(1, id)
                preparedStatement.executeUpdate()
            }
            return personal
        } else {
            logger.debug { "No se encontró el personal con ID: $id" }
            return null
        }
    }
}
