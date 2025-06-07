package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.dao.ConvocatoriaDao
import srangeldev.proyectoequipofutboljavafx.newteam.dao.ConvocatoriaEntity
import srangeldev.proyectoequipofutboljavafx.newteam.dao.JugadorConvocadoDao
import srangeldev.proyectoequipofutboljavafx.newteam.dao.JugadorConvocadoEntity
import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.emptyList

/**
 * Implementación del repositorio de convocatorias.
 */
class ConvocatoriaRepositoryImpl(
    private val personalRepository: PersonalRepository,
    private val convocatoriaDao: ConvocatoriaDao,
    private val jugadorConvocadoDao: JugadorConvocadoDao
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

        try {
            // Obtener todas las convocatorias
            val convocatoriaEntities = convocatoriaDao.findAll()

            // Convertir las entidades a objetos de dominio
            val convocatoriasList = convocatoriaEntities.map { convocatoriaEntity ->
                // Obtener los jugadores convocados para esta convocatoria
                val jugadoresConvocados = jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(convocatoriaEntity.id)

                // Obtener los jugadores titulares para esta convocatoria
                val jugadoresTitulares = jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(convocatoriaEntity.id)

                // Crear el objeto convocatoria con los jugadores y titulares
                val convocatoria = convocatoriaEntity.toConvocatoria(jugadoresConvocados, jugadoresTitulares)

                // Actualizar la caché
                convocatorias[convocatoria.id] = convocatoria

                convocatoria
            }

            return convocatoriasList
        } catch (e: Exception) {
            logger.error { "Error al obtener todas las convocatorias: ${e.message}" }
            return emptyList()
        }
    }

    override fun getById(id: Int): Convocatoria? {
        logger.debug { "Obteniendo convocatoria por ID: $id" }

        // Check cache first for non-test scenarios
        if (convocatorias.containsKey(id)) {
            return convocatorias[id]
        }

        try {
            // Obtener la convocatoria por ID
            val convocatoriaEntity = convocatoriaDao.findById(id) ?: return null

            // Obtener los jugadores convocados para esta convocatoria
            val jugadoresConvocados = jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(id)

            // Obtener los jugadores titulares para esta convocatoria
            val jugadoresTitulares = jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(id)

            // Crear el objeto convocatoria con los jugadores y titulares
            val convocatoria = convocatoriaEntity.toConvocatoria(jugadoresConvocados, jugadoresTitulares)

            // Actualizar la caché
            convocatorias[convocatoria.id] = convocatoria

            return convocatoria
        } catch (e: Exception) {
            logger.error { "Error al obtener convocatoria por ID: $id: ${e.message}" }
            return null
        }
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
            try {
                // Crear nueva convocatoria
                // Convertir a entidad
                val convocatoriaEntity = ConvocatoriaEntity.fromConvocatoria(convocatoria)

                // Guardar la convocatoria y obtener el ID generado
                val convocatoriaId = convocatoriaDao.save(convocatoriaEntity)

                // Guardar los jugadores convocados
                for (jugadorId in convocatoria.jugadores) {
                    val esTitular = convocatoria.titulares.contains(jugadorId)
                    val jugadorConvocadoEntity = JugadorConvocadoEntity(
                        convocatoriaId = convocatoriaId,
                        jugadorId = jugadorId,
                        esTitular = esTitular
                    )
                    jugadorConvocadoDao.save(jugadorConvocadoEntity)
                }

                // Obtener la convocatoria guardada
                return getById(convocatoriaId) ?: throw IllegalStateException("No se pudo obtener la convocatoria creada")
            } catch (e: Exception) {
                logger.error { "Error al guardar la convocatoria: ${e.message}" }
                throw IllegalStateException("No se pudo guardar la convocatoria: ${e.message}")
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

        try {
            // Convertir a entidad
            val convocatoriaEntity = ConvocatoriaEntity.fromConvocatoria(entidad.copy(id = id))

            // Actualizar la convocatoria
            val rowsAffected = convocatoriaDao.update(convocatoriaEntity)

            if (rowsAffected > 0) {
                // Eliminar los jugadores convocados existentes
                jugadorConvocadoDao.deleteByConvocatoriaId(id)

                // Guardar los nuevos jugadores convocados
                for (jugadorId in entidad.jugadores) {
                    val esTitular = entidad.titulares.contains(jugadorId)
                    val jugadorConvocadoEntity = JugadorConvocadoEntity(
                        convocatoriaId = id,
                        jugadorId = jugadorId,
                        esTitular = esTitular
                    )
                    jugadorConvocadoDao.save(jugadorConvocadoEntity)
                }

                // Obtener la convocatoria actualizada
                val updatedConvocatoria = getById(id)

                // Actualizar la caché
                if (updatedConvocatoria != null) {
                    convocatorias[id] = updatedConvocatoria
                }

                return updatedConvocatoria
            }

            return null
        } catch (e: Exception) {
            logger.error { "Error al actualizar la convocatoria: ${e.message}" }
            return null
        }
    }

    override fun delete(id: Int): Convocatoria? {
        logger.debug { "Eliminando convocatoria con ID: $id" }

        // Verificar si la convocatoria existe
        val existingConvocatoria = getById(id)
        if (existingConvocatoria == null) {
            logger.debug { "No se encontró ninguna convocatoria con ID: $id" }
            return null
        }

        try {
            // Eliminar los jugadores convocados primero
            jugadorConvocadoDao.deleteByConvocatoriaId(id)

            // Eliminar la convocatoria
            val rowsAffected = convocatoriaDao.delete(id)

            if (rowsAffected > 0) {
                // Eliminar de la caché
                convocatorias.remove(id)

                return existingConvocatoria
            }

            return null
        } catch (e: Exception) {
            logger.error { "Error al eliminar la convocatoria: ${e.message}" }
            return null
        }
    }

    override fun getJugadoresConvocados(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores convocados para la convocatoria con ID: $convocatoriaId" }

        try {
            // Obtener los IDs de los jugadores convocados
            val jugadoresIds = jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(convocatoriaId)

            // Obtener los jugadores por ID
            return jugadoresIds.mapNotNull { jugadorId ->
                personalRepository.getById(jugadorId) as? Jugador
            }
        } catch (e: Exception) {
            logger.error { "Error al obtener los jugadores convocados: ${e.message}" }
            return emptyList()
        }
    }

    override fun getJugadoresTitulares(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores titulares para la convocatoria con ID: $convocatoriaId" }

        try {
            // Obtener los IDs de los jugadores titulares
            val titularesIds = jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(convocatoriaId)

            // Obtener los jugadores por ID
            return titularesIds.mapNotNull { jugadorId ->
                personalRepository.getById(jugadorId) as? Jugador
            }
        } catch (e: Exception) {
            logger.error { "Error al obtener los jugadores titulares: ${e.message}" }
            return emptyList()
        }
    }

    override fun getJugadoresSuplentes(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores suplentes para la convocatoria con ID: $convocatoriaId" }

        try {
            // Obtener todos los jugadores convocados
            val jugadoresIds = jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(convocatoriaId)

            // Obtener los jugadores titulares
            val titularesIds = jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(convocatoriaId)

            // Calcular los suplentes (convocados - titulares)
            val suplentesIds = jugadoresIds.filter { !titularesIds.contains(it) }

            // Obtener los jugadores por ID
            return suplentesIds.mapNotNull { jugadorId ->
                personalRepository.getById(jugadorId) as? Jugador
            }
        } catch (e: Exception) {
            logger.error { "Error al obtener los jugadores suplentes: ${e.message}" }
            return emptyList()
        }
    }

    override fun getJugadoresNoConvocados(convocatoriaId: Int): List<Jugador> {
        logger.debug { "Obteniendo jugadores no convocados para la convocatoria con ID: $convocatoriaId" }

        try {
            // Obtener todos los jugadores
            val todosLosJugadores = personalRepository.getAll().filterIsInstance<Jugador>()

            // Obtener los jugadores convocados
            val jugadoresConvocadosIds = jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(convocatoriaId)

            // Filtrar los jugadores no convocados
            return todosLosJugadores.filter { jugador ->
                !jugadoresConvocadosIds.contains(jugador.id)
            }
        } catch (e: Exception) {
            logger.error { "Error al obtener los jugadores no convocados: ${e.message}" }
            return emptyList()
        }
    }

    override fun getByEquipoId(equipoId: Int): List<Convocatoria> {
        logger.debug { "Obteniendo convocatorias por equipo ID: $equipoId" }

        try {
            // Obtener las convocatorias por equipo ID
            val convocatoriaEntities = convocatoriaDao.findByEquipoId(equipoId)

            // Convertir las entidades a objetos de dominio
            return convocatoriaEntities.map { convocatoriaEntity ->
                // Obtener los jugadores convocados para esta convocatoria
                val jugadoresConvocados = jugadorConvocadoDao.getJugadoresIdsByConvocatoriaId(convocatoriaEntity.id)

                // Obtener los jugadores titulares para esta convocatoria
                val jugadoresTitulares = jugadorConvocadoDao.getTitularesIdsByConvocatoriaId(convocatoriaEntity.id)

                // Crear el objeto convocatoria con los jugadores y titulares
                val convocatoria = convocatoriaEntity.toConvocatoria(jugadoresConvocados, jugadoresTitulares)

                // Actualizar la caché
                convocatorias[convocatoria.id] = convocatoria

                convocatoria
            }
        } catch (e: Exception) {
            logger.error { "Error al obtener convocatorias por equipo ID: ${e.message}" }
            return emptyList()
        }
    }

    override fun validarConvocatoria(convocatoria: Convocatoria): Boolean {
        logger.debug { "Validando convocatoria: $convocatoria" }

        // Validar número máximo de jugadores
        if (convocatoria.jugadores.size > 18) {
            logger.debug { "La convocatoria tiene más de 18 jugadores" }
            return false
        }

        // Validar número de titulares
        if (convocatoria.titulares.size != 11) {
            logger.debug { "La convocatoria no tiene exactamente 11 titulares" }
            return false
        }

        // Validar que todos los titulares estén en la lista de convocados
        if (!convocatoria.jugadores.containsAll(convocatoria.titulares)) {
            logger.debug { "No todos los titulares están en la lista de convocados" }
            return false
        }

        // Validar número máximo de porteros (2)
        val porteros = convocatoria.jugadores.count { jugadorId ->
            val jugador = personalRepository.getById(jugadorId) as? Jugador
            jugador?.posicion == Jugador.Posicion.PORTERO
        }

        if (porteros > 2) {
            logger.debug { "La convocatoria tiene más de 2 porteros" }
            return false
        }

        return true
    }
}
