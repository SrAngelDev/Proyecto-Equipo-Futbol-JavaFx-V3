package srangeldev.proyectoequipofutboljavafx.newteam.validator

import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal

/**
 * Clase para validar los datos básicos de un miembro del personal.
 */
class PersonalValidator : Validator<Personal> {
    
    /**
     * Valida los datos básicos de un miembro del personal.
     * @param personal El objeto Personal a validar
     * @throws PersonalException.PersonalNotFoundException si el id es negativo
     * @throws PersonalException.PersonalStorageException si el nombre o apellidos están vacíos
     * @throws PersonalException.PersonalStorageException si el salario es negativo o cero
     * @throws PersonalException.PersonalStorageException si el país de origen está vacío
     */
    override fun validate(personal: Personal) {
        if (personal.id < 0) {
            throw PersonalException.PersonalNotFoundException(personal.id)
        }
        
        if (personal.nombre.isEmpty()) {
            throw PersonalException.PersonalStorageException("El nombre no puede estar vacío")
        }
        
        if (personal.apellidos.isEmpty()) {
            throw PersonalException.PersonalStorageException("Los apellidos no pueden estar vacíos")
        }
        
        if (personal.salario <= 0) {
            throw PersonalException.PersonalStorageException("El salario debe ser positivo: ${personal.salario}")
        }
        
        if (personal.paisOrigen.isEmpty()) {
            throw PersonalException.PersonalStorageException("El país de origen no puede estar vacío")
        }
    }
}