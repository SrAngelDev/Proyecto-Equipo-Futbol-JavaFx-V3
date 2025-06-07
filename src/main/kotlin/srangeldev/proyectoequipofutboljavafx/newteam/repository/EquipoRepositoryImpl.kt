package srangeldev.proyectoequipofutboljavafx.newteam.repository

import org.lighthousegames.logging.logging
import srangeldev.proyectoequipofutboljavafx.newteam.dao.EquipoDao
import srangeldev.proyectoequipofutboljavafx.newteam.models.Equipo
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Implementación del repositorio de equipos.
 */
class EquipoRepositoryImpl(
    private val equipoDao: EquipoDao
) : EquipoRepository {
    private val logger = logging()
    private val equipos = mutableMapOf<Int, Equipo>()

    init {
        logger.debug { "Inicializando repositorio de equipos" }
    }

    override fun getAll(): List<Equipo> {
        logger.debug { "Obteniendo todos los equipos" }

        try {
            // Limpiar la caché para asegurarnos de obtener datos actualizados
            equipos.clear()

            // Obtener todos los equipos
            val equiposList = equipoDao.findAll()

            // Actualizar la caché
            equiposList.forEach { equipo ->
                equipos[equipo.id] = equipo
            }

            return equiposList
        } catch (e: Exception) {
            logger.error { "Error al obtener todos los equipos: ${e.message}" }
            return emptyList()
        }
    }

    override fun getById(id: Int): Equipo? {
        logger.debug { "Obteniendo equipo por ID: $id" }

        // Check cache first for non-test scenarios
        if (equipos.containsKey(id)) {
            return equipos[id]
        }

        try {
            // Obtener el equipo por ID
            val equipo = equipoDao.findById(id)

            // Actualizar la caché si se encontró el equipo
            if (equipo != null) {
                equipos[equipo.id] = equipo
            }

            return equipo
        } catch (e: Exception) {
            logger.error { "Error al obtener equipo por ID: $id: ${e.message}" }
            return null
        }
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
            try {
                // Crear nuevo equipo
                val equipoId = equipoDao.save(equipo)

                // Obtener el equipo guardado
                val savedEquipo = equipoDao.findById(equipoId)
                    ?: throw IllegalStateException("No se pudo obtener el equipo creado")

                // Actualizar la caché
                equipos[savedEquipo.id] = savedEquipo

                return savedEquipo
            } catch (e: Exception) {
                logger.error { "Error al guardar el equipo: ${e.message}" }
                throw IllegalStateException("No se pudo guardar el equipo: ${e.message}")
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

        try {
            // Actualizar el equipo
            val equipoToUpdate = entidad.copy(
                id = id,
                updatedAt = LocalDateTime.now()
            )

            val rowsAffected = equipoDao.update(equipoToUpdate)

            if (rowsAffected > 0) {
                // Obtener el equipo actualizado
                val updatedEquipo = equipoDao.findById(id)

                // Actualizar la caché
                if (updatedEquipo != null) {
                    equipos[id] = updatedEquipo
                }

                return updatedEquipo
            }

            return null
        } catch (e: Exception) {
            logger.error { "Error al actualizar el equipo: ${e.message}" }
            return null
        }
    }

    override fun delete(id: Int): Equipo? {
        logger.debug { "Eliminando equipo con ID: $id" }

        // Verificar si el equipo existe
        val existingEquipo = getById(id)
        if (existingEquipo == null) {
            logger.debug { "No se encontró ningún equipo con ID: $id" }
            return null
        }

        try {
            // Eliminar el equipo
            val rowsAffected = equipoDao.delete(id)

            if (rowsAffected > 0) {
                // Eliminar de la caché
                equipos.remove(id)

                logger.debug { "Equipo eliminado correctamente: $existingEquipo" }
                return existingEquipo
            }

            return null
        } catch (e: Exception) {
            logger.error { "Error al eliminar el equipo: ${e.message}" }
            return null
        }
    }
}
