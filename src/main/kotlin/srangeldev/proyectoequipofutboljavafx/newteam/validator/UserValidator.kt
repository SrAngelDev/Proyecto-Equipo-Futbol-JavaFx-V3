package srangeldev.proyectoequipofutboljavafx.newteam.validator

import srangeldev.proyectoequipofutboljavafx.newteam.exceptions.PersonalException
import srangeldev.proyectoequipofutboljavafx.newteam.models.User

/**
 * Clase para validar los datos de un usuario.
 */
class UserValidator : Validator<User> {
    
    /**
     * Valida los datos del usuario.
     * @param user El objeto User a validar
     * @throws PersonalException.PersonalNotFoundException si el id es negativo
     * @throws PersonalException.PersonalStorageException si el username o password están vacíos
     */
    override fun validate(user: User) {
        if (user.id < 0) {
            throw PersonalException.PersonalNotFoundException(user.id)
        }
        
        if (user.username.isEmpty()) {
            throw PersonalException.PersonalStorageException("El nombre de usuario no puede estar vacío")
        }
        
        if (user.password.isEmpty()) {
            throw PersonalException.PersonalStorageException("La contraseña no puede estar vacía")
        }
        
        // Validar que el username tenga al menos 3 caracteres
        if (user.username.length < 3) {
            throw PersonalException.PersonalStorageException("El nombre de usuario debe tener al menos 3 caracteres")
        }
        
        // Validar que la contraseña tenga al menos 6 caracteres
        if (user.password.length < 6) {
            throw PersonalException.PersonalStorageException("La contraseña debe tener al menos 6 caracteres")
        }
    }
}