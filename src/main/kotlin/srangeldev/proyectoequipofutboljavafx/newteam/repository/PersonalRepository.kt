package srangeldev.repository

import srangeldev.models.Personal

/**
 * Interfaz del repositorio de personal que extiende CrudRepository.
 */
interface PersonalRepository : CrudRepository<Int, Personal>