package srangeldev.proyectoequipofutboljavafx.newteam.repository

import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal

/**
 * Interfaz del repositorio de personal que extiende CrudRepository.
 */
interface PersonalRepository : CrudRepository<Int, Personal> {
    /**
     * Limpia la caché de personal para forzar una recarga desde la base de datos.
     */
    fun clearCache()

    /**
     * Obtiene todos los entrenadores del repositorio.
     * 
     * @return Lista de entrenadores.
     */
    fun getAllEntrenadores(): List<Entrenador>
}
