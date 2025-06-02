package srangeldev.proyectoequipofutboljavafx.newteam.repository

import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Implementation of ConvocatoriaRepository for testing purposes.
 * This implementation uses an in-memory map to store convocatorias.
 */
class ConvocatoriaRepositoryTestImpl(
    private val personalRepository: PersonalRepository
) : ConvocatoriaRepository {
    private val convocatorias = mutableMapOf<Int, Convocatoria>()
    private var nextId = 1

    override fun getAll(): List<Convocatoria> {
        return convocatorias.values.toList()
    }

    override fun getById(id: Int): Convocatoria? {
        return convocatorias[id]
    }

    override fun save(convocatoria: Convocatoria): Convocatoria {
        val id = if (convocatoria.id > 0) convocatoria.id else nextId++
        val savedConvocatoria = convocatoria.copy(
            id = id,
            createdAt = convocatoria.createdAt ?: LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        convocatorias[id] = savedConvocatoria
        return savedConvocatoria
    }

    override fun update(id: Int, entidad: Convocatoria): Convocatoria? {
        if (!convocatorias.containsKey(id)) {
            return null
        }
        val updatedConvocatoria = entidad.copy(
            id = id,
            createdAt = convocatorias[id]?.createdAt ?: LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        convocatorias[id] = updatedConvocatoria
        return updatedConvocatoria
    }

    override fun delete(id: Int): Convocatoria? {
        return convocatorias.remove(id)
    }

    override fun getJugadoresConvocados(convocatoriaId: Int): List<Jugador> {
        val convocatoria = convocatorias[convocatoriaId] ?: return emptyList()
        return convocatoria.jugadores.mapNotNull { jugadorId ->
            personalRepository.getById(jugadorId) as? Jugador
        }
    }

    override fun getJugadoresTitulares(convocatoriaId: Int): List<Jugador> {
        val convocatoria = convocatorias[convocatoriaId] ?: return emptyList()
        return convocatoria.titulares.mapNotNull { jugadorId ->
            personalRepository.getById(jugadorId) as? Jugador
        }
    }

    override fun getJugadoresSuplentes(convocatoriaId: Int): List<Jugador> {
        val convocatoria = convocatorias[convocatoriaId] ?: return emptyList()
        val suplentes = convocatoria.jugadores.filter { it !in convocatoria.titulares }
        return suplentes.mapNotNull { jugadorId ->
            personalRepository.getById(jugadorId) as? Jugador
        }
    }

    override fun getJugadoresNoConvocados(convocatoriaId: Int): List<Jugador> {
        val convocatoria = convocatorias[convocatoriaId] ?: return emptyList()
        val todosLosJugadores = personalRepository.getAll().filterIsInstance<Jugador>()
        return todosLosJugadores.filter { jugador -> jugador.id !in convocatoria.jugadores }
    }

    override fun getByEquipoId(equipoId: Int): List<Convocatoria> {
        return convocatorias.values.filter { it.equipoId == equipoId }
    }

    override fun validarConvocatoria(convocatoria: Convocatoria): Boolean {
        // Validar que la fecha no sea anterior a la fecha actual
        if (convocatoria.fecha.isBefore(LocalDate.now())) {
            return false
        }

        // Validar que haya al menos un jugador convocado
        if (convocatoria.jugadores.isEmpty()) {
            return false
        }

        // Validar que haya al menos un jugador titular
        if (convocatoria.titulares.isEmpty()) {
            return false
        }

        // Validar que todos los titulares estén en la lista de jugadores
        if (!convocatoria.titulares.all { it in convocatoria.jugadores }) {
            return false
        }

        return true
    }

    // Helper method for testing
    fun addConvocatoria(convocatoria: Convocatoria) {
        convocatorias[convocatoria.id] = convocatoria
    }

    // Helper method for testing
    fun clearConvocatorias() {
        convocatorias.clear()
        nextId = 1
    }
}