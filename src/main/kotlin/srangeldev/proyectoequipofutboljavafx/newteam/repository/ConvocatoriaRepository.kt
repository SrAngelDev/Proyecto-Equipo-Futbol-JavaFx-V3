package srangeldev.proyectoequipofutboljavafx.newteam.repository

import srangeldev.proyectoequipofutboljavafx.newteam.models.Convocatoria
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador

/**
 * Interfaz del repositorio de convocatorias que extiende CrudRepository.
 */
interface ConvocatoriaRepository : CrudRepository<Int, Convocatoria> {
    /**
     * Obtiene todas las convocatorias de un equipo.
     *
     * @param equipoId El ID del equipo.
     * @return Una lista de convocatorias del equipo.
     */
    fun getByEquipoId(equipoId: Int): List<Convocatoria>

    /**
     * Valida que una convocatoria cumpla con las reglas establecidas.
     *
     * @param convocatoria La convocatoria a validar.
     * @return true si la convocatoria es válida, false en caso contrario.
     */
    fun validarConvocatoria(convocatoria: Convocatoria): Boolean

    /**
     * Obtiene los jugadores de una convocatoria.
     *
     * @param convocatoriaId El ID de la convocatoria.
     * @return Una lista de jugadores convocados.
     */
    fun getJugadoresConvocados(convocatoriaId: Int): List<Jugador>

    /**
     * Obtiene los jugadores titulares de una convocatoria.
     *
     * @param convocatoriaId El ID de la convocatoria.
     * @return Una lista de jugadores titulares.
     */
    fun getJugadoresTitulares(convocatoriaId: Int): List<Jugador>

    /**
     * Obtiene los jugadores suplentes de una convocatoria.
     *
     * @param convocatoriaId El ID de la convocatoria.
     * @return Una lista de jugadores suplentes.
     */
    fun getJugadoresSuplentes(convocatoriaId: Int): List<Jugador>

    /**
     * Obtiene los jugadores que no están convocados para una convocatoria.
     *
     * @param convocatoriaId El ID de la convocatoria.
     * @return Una lista de jugadores no convocados.
     */
    fun getJugadoresNoConvocados(convocatoriaId: Int): List<Jugador>
}
