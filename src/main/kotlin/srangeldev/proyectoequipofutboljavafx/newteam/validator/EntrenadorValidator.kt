package srangeldev.proyectoequipofutboljavafx.newteam.validator

import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador

/**
 * Clase para validar los datos de un entrenador.
 * Utiliza composición con PersonalValidator para las validaciones básicas de Personal.
 */
class EntrenadorValidator : Validator<Entrenador> {
    private val personalValidator = PersonalValidator()

    /**
     * Valida los datos del entrenador.
     * @param entrenador El objeto Entrenador a validar
     * @throws PersonalException.PersonalNotFoundException si el id es negativo
     * @throws PersonalException.PersonalStorageException si el nombre o apellidos están vacíos
     */
    override fun validate(entrenador: Entrenador) {
        // Validar primero como Personal
        personalValidator.validate(entrenador)

        // No es necesario validar especializacion ya que es un enum y no puede ser nulo
    }
}
