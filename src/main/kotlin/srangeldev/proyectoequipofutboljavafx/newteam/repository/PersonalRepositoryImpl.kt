package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.dao.*
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toEntrenadorEntity
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toJugadorEntity
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toModel
import srangeldev.proyectoequipofutboljavafx.newteam.mapper.toPersonalEntity
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import java.time.LocalDateTime

/**
 * Implementación del repositorio de personal.
 */
class PersonalRepositoryImpl(
    private val personalDao: PersonalDao,
    private val entrenadorDao: EntrenadorDao,
    private val jugadorDao: JugadorDao
) : PersonalRepository {
    private val logger = logging()
    private val personal = mutableMapOf<Int, Personal>()
    init {
        logger.debug { "Inicializando repositorio de personal" }
    }

    /**
     * Obtiene una lista de todos los entrenadores.
     *
     * @return Una lista de objetos Entrenador.
     */
    private fun getEntrenadores(): List<Entrenador> {
        logger.debug { "Obteniendo entrenadores de la base de datos" }

        // Eliminar entrenadores existentes del mapa personal
        val entrenadoresToRemove = personal.values.filterIsInstance<Entrenador>().map { it.id }
        entrenadoresToRemove.forEach { personal.remove(it) }

        val entrenadores = mutableListOf<Entrenador>()

        // Obtener todos los entrenadores usando los DAOs
        val entrenadorEntities = entrenadorDao.findAll()
        val personalEntities = personalDao.findByTipo("ENTRENADOR")

        // Mapear las entidades a objetos de dominio
        entrenadorEntities.forEach { entrenadorEntity ->
            // Buscar la entidad personal correspondiente
            val personalEntity = personalEntities.find { it.id == entrenadorEntity.id }
            if (personalEntity != null) {
                val entrenador = entrenadorEntity.toModel(personalEntity)
                personal[entrenador.id] = entrenador
                entrenadores.add(entrenador)
                logger.debug { "Entrenador encontrado: ID=${entrenador.id}, Nombre=${entrenador.nombre}, Apellidos=${entrenador.apellidos}, Especialización=${entrenador.especializacion}" }
            }
        }

        logger.debug { "Total de entrenadores encontrados: ${entrenadores.size}" }
        return entrenadores
    }

    private fun getJugadores(): List<Jugador> {
        logger.debug { "Obteniendo jugadores de la base de datos" }

        // Eliminar jugadores existentes del mapa personal
        val jugadoresToRemove = personal.values.filterIsInstance<Jugador>().map { it.id }
        jugadoresToRemove.forEach { personal.remove(it) }

        val jugadores = mutableListOf<Jugador>()

        // Obtener todos los jugadores usando los DAOs
        val jugadorEntities = jugadorDao.findAll()
        val personalEntities = personalDao.findByTipo("JUGADOR")

        // Mapear las entidades a objetos de dominio
        jugadorEntities.forEach { jugadorEntity ->
            // Buscar la entidad personal correspondiente
            val personalEntity = personalEntities.find { it.id == jugadorEntity.id }
            if (personalEntity != null) {
                val jugador = jugadorEntity.toModel(personalEntity)
                personal[jugador.id] = jugador
                jugadores.add(jugador)
                logger.debug { "Jugador encontrado: ID=${jugador.id}, Nombre=${jugador.nombre}, Apellidos=${jugador.apellidos}, Posición=${jugador.posicion}" }
            }
        }

        logger.debug { "Total de jugadores encontrados: ${jugadores.size}" }
        return jugadores
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

        // Primero verificamos si ya está en la caché
        if (personal.containsKey(id)) {
            return personal[id]
        }

        // Intentamos obtener un entrenador
        val personalEntity = personalDao.findById(id)
        if (personalEntity != null) {
            if (personalEntity.tipo == "ENTRENADOR") {
                val entrenadorEntity = entrenadorDao.findById(id)
                if (entrenadorEntity != null) {
                    val entrenador = entrenadorEntity.toModel(personalEntity)
                    personal[entrenador.id] = entrenador
                    return entrenador
                }
            } else if (personalEntity.tipo == "JUGADOR") {
                val jugadorEntity = jugadorDao.findById(id)
                if (jugadorEntity != null) {
                    val jugador = jugadorEntity.toModel(personalEntity)
                    personal[jugador.id] = jugador
                    return jugador
                }
            }
        }

        return null
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

        // Crear la entidad personal
        val personalEntity = when (entidad) {
            is Jugador -> entidad.toPersonalEntity().copy(id = 0, createdAt = timeStamp, updatedAt = timeStamp)
            is Entrenador -> entidad.toPersonalEntity().copy(id = 0, createdAt = timeStamp, updatedAt = timeStamp)
            else -> throw IllegalArgumentException("Tipo desconocido de Personal")
        }

        // Guardar la entidad personal y obtener el ID generado
        val generatedId = personalDao.save(personalEntity)

        // Guardar la entidad específica según el tipo
        val result = when (entidad) {
            is Jugador -> {
                // Crear la entidad jugador
                val jugadorEntity = entidad.toJugadorEntity().copy(id = generatedId)

                // Guardar la entidad jugador
                jugadorDao.save(jugadorEntity)

                // Crear un nuevo objeto jugador con el ID generado
                val jugador = Jugador(
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
                    updatedAt = timeStamp,
                    imagenUrl = entidad.imagenUrl
                )

                // Guardar en la caché
                personal[jugador.id] = jugador
                jugador
            }

            is Entrenador -> {
                // Crear la entidad entrenador
                val entrenadorEntity = entidad.toEntrenadorEntity().copy(id = generatedId)

                // Guardar la entidad entrenador
                entrenadorDao.save(entrenadorEntity)

                // Crear un nuevo objeto entrenador con el ID generado
                val entrenador = Entrenador(
                    id = generatedId,
                    nombre = entidad.nombre,
                    apellidos = entidad.apellidos,
                    fechaNacimiento = entidad.fechaNacimiento,
                    fechaIncorporacion = entidad.fechaIncorporacion,
                    salario = entidad.salario,
                    paisOrigen = entidad.paisOrigen,
                    especializacion = entidad.especializacion,
                    createdAt = timeStamp,
                    updatedAt = timeStamp,
                    imagenUrl = entidad.imagenUrl
                )

                // Guardar en la caché
                personal[entrenador.id] = entrenador
                entrenador
            }

            else -> throw IllegalArgumentException("Tipo desconocido de Personal")
        }

        return result
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
        // Verificar si existe el personal a actualizar
        val existingPersonal = this.getById(id)
        val timeStamp = LocalDateTime.now()

        if (existingPersonal != null) {
            // Crear la entidad personal actualizada
            val personalEntity = when (entidad) {
                is Jugador -> entidad.toPersonalEntity().copy(id = id, createdAt = existingPersonal.createdAt, updatedAt = timeStamp)
                is Entrenador -> entidad.toPersonalEntity().copy(id = id, createdAt = existingPersonal.createdAt, updatedAt = timeStamp)
                else -> throw IllegalArgumentException("Tipo desconocido de Personal")
            }

            // Actualizar la entidad personal
            val updated = personalDao.update(personalEntity)

            if (updated > 0) {
                // Actualizar la entidad específica según el tipo
                when (entidad) {
                    is Jugador -> {
                        // Crear la entidad jugador actualizada
                        val jugadorEntity = entidad.toJugadorEntity().copy(id = id)

                        // Actualizar la entidad jugador
                        jugadorDao.update(jugadorEntity)

                        // Crear un nuevo objeto jugador con los datos actualizados
                        val jugador = Jugador(
                            id = id,
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
                            createdAt = existingPersonal.createdAt,
                            updatedAt = timeStamp,
                            imagenUrl = entidad.imagenUrl
                        )

                        // Actualizar en la caché
                        personal[id] = jugador
                        return jugador
                    }

                    is Entrenador -> {
                        // Crear la entidad entrenador actualizada
                        val entrenadorEntity = entidad.toEntrenadorEntity().copy(id = id)

                        // Actualizar la entidad entrenador
                        entrenadorDao.update(entrenadorEntity)

                        // Crear un nuevo objeto entrenador con los datos actualizados
                        val entrenador = Entrenador(
                            id = id,
                            nombre = entidad.nombre,
                            apellidos = entidad.apellidos,
                            fechaNacimiento = entidad.fechaNacimiento,
                            fechaIncorporacion = entidad.fechaIncorporacion,
                            salario = entidad.salario,
                            paisOrigen = entidad.paisOrigen,
                            especializacion = entidad.especializacion,
                            createdAt = existingPersonal.createdAt,
                            updatedAt = timeStamp,
                            imagenUrl = entidad.imagenUrl
                        )

                        // Actualizar en la caché
                        personal[id] = entrenador
                        return entrenador
                    }

                    else -> throw IllegalArgumentException("Tipo desconocido de Personal")
                }
            }
        }

        logger.debug { "No se encontró el personal con ID: $id" }
        return null
    }

    /**
     * Elimina un objeto personal por su ID.
     *
     * @param id El ID del objeto personal a eliminar.
     * @return El objeto personal eliminado.
     */
    override fun delete(id: Int): Personal? {
        logger.debug { "Eliminando personal con ID: $id" }
        // Obtener el personal antes de eliminarlo
        val personalToDelete = this.getById(id)

        if (personalToDelete != null) {
            // Primero eliminar de la tabla específica según el tipo
            when (personalToDelete) {
                is Jugador -> jugadorDao.delete(id)
                is Entrenador -> entrenadorDao.delete(id)
                else -> throw IllegalArgumentException("Tipo desconocido de Personal")
            }

            // Luego eliminar de la tabla Personal
            personalDao.delete(id)

            // Eliminar de la caché
            personal.remove(id)

            return personalToDelete
        } else {
            logger.debug { "No se encontró el personal con ID: $id" }
            return null
        }
    }

    /**
     * Limpia la caché de personal para forzar una recarga desde la base de datos.
     */
    override fun clearCache() {
        logger.debug { "Limpiando caché de personal" }
        personal.clear()
    }

    /**
     * Obtiene todos los entrenadores del repositorio.
     * 
     * @return Lista de entrenadores.
     */
    override fun getAllEntrenadores(): List<Entrenador> {
        logger.debug { "Obteniendo todos los entrenadores" }
        // Limpiar la caché para asegurar datos frescos
        clearCache()
        return getEntrenadores()
    }
}
