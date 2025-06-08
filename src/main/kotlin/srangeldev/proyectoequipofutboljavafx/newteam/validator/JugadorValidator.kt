package srangeldev.proyectoequipofutboljavafx.newteam.validator

import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Jugador

/**
 * Clase para validar los datos de un jugador.
 * Extiende PersonalValidator para heredar las validaciones básicas de Personal.
 */
class JugadorValidator : Validator<Jugador> {
    private val personalValidator = PersonalValidator()
    
    /**
     * Valida los datos del jugador.
     * @param jugador El objeto Jugador a validar
     * @throws PersonalException.PersonalNotFoundException si el id es negativo
     * @throws PersonalException.PersonalStorageException si el nombre o apellidos están vacíos
     * @throws PersonalException.PersonalStorageException si el dorsal es negativo o mayor que 99
     * @throws PersonalException.PersonalStorageException si la altura o peso son negativos
     */
    override fun validate(jugador: Jugador) {
        // Validar primero como Personal
        personalValidator.validate(jugador)
        
        // Validaciones específicas de Jugador
        if (jugador.dorsal < 1 || jugador.dorsal > 99) {
            throw PersonalException.PersonalStorageException("El dorsal debe estar entre 1 y 99: ${jugador.dorsal}")
        }
        
        if (jugador.altura <= 0) {
            throw PersonalException.PersonalStorageException("La altura debe ser positiva: ${jugador.altura}")
        }
        
        if (jugador.peso <= 0) {
            throw PersonalException.PersonalStorageException("El peso debe ser positivo: ${jugador.peso}")
        }
        
        if (jugador.goles < 0) {
            throw PersonalException.PersonalStorageException("Los goles no pueden ser negativos: ${jugador.goles}")
        }
        
        if (jugador.partidosJugados < 0) {
            throw PersonalException.PersonalStorageException("Los partidos jugados no pueden ser negativos: ${jugador.partidosJugados}")
        }
    }
}