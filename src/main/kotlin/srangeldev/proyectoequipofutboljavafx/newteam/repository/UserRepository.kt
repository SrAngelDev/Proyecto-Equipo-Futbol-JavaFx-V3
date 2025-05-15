package srangeldev.proyectoequipofutboljavafx.newteam.repository

import srangeldev.proyectoequipofutboljavafx.newteam.models.User

/**
 * Interfaz para el repositorio de usuarios.
 */
interface UserRepository {
    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param username El nombre de usuario.
     * @return El usuario con el nombre de usuario especificado, o null si no se encuentra.
     */
    fun getByUsername(username: String): User?

    /**
     * Verifica las credenciales de un usuario.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña.
     * @return El usuario si las credenciales son válidas, o null si no lo son.
     */
    fun verifyCredentials(username: String, password: String): User?

    /**
     * Guarda un nuevo usuario.
     *
     * @param user El usuario a guardar.
     * @return El usuario guardado.
     */
    fun save(user: User): User

    /**
     * Inicializa la tabla de usuarios con datos por defecto si está vacía.
     */
    fun initDefaultUsers()
}
