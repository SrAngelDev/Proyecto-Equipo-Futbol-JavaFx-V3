package srangeldev.proyectoequipofutboljavafx.newteam.validator

import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal

/**
 * Clase para validar los datos del personal.
 * Implementa la interfaz Validator para el tipo Personal.
 */
class PersonalValidator : Validator<Personal> {
    /**
     * Valida los datos del personal.
     * @param personal El objeto Personal a validar
     * @throws PersonalException.PersonalNotFoundException si el id es negativo
     * @throws PersonalException.PersonalStorageException si el nombre o apellidos están vacíos
     */
    override fun validate(personal: Personal) {
        if (personal.id < 0) {
            throw PersonalException.PersonalNotFoundException(personal.id)
        }
        if (personal.nombre.isEmpty() || personal.apellidos.isEmpty()) {
            throw PersonalException.PersonalStorageException("Persona no encontrada con nombre: ${personal.nombre}, apellidos: ${personal.apellidos}")
        }
    }
}
