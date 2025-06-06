package srangeldev.proyectoequipofutboljavafx.newteam.session

import srangeldev.proyectoequipofutboljavafx.newteam.models.User

/**
 * Clase singleton que gestiona la sesión del usuario actual.
 */
object Session {
    private var currentUser: User? = null

    // Variables para "Recordarme"
    private var rememberedUsername: String? = null
    private var rememberedPassword: String? = null
    private var rememberMe: Boolean = false

    /**
     * Establece el usuario actual.
     * 
     * @param user El usuario a establecer como actual.
     */
    fun setCurrentUser(user: User?) {
        currentUser = user
    }

    /**
     * Obtiene el usuario actual.
     * 
     * @return El usuario actual o null si no hay ningún usuario autenticado.
     */
    fun getCurrentUser(): User? {
        return currentUser
    }

    /**
     * Comprueba si el usuario actual es administrador.
     * 
     * @return true si el usuario actual es administrador, false en caso contrario.
     */
    fun isAdmin(): Boolean {
        return currentUser?.role == User.Role.ADMIN
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    fun logout() {
        currentUser = null
    }

    /**
     * Guarda las credenciales para "Recordarme".
     * 
     * @param username El nombre de usuario a recordar.
     * @param password La contraseña a recordar.
     */
    fun saveCredentials(username: String, password: String) {
        rememberedUsername = username
        rememberedPassword = password
        rememberMe = true
    }

    /**
     * Obtiene el nombre de usuario recordado.
     * 
     * @return El nombre de usuario recordado o null si no hay ninguno.
     */
    fun getRememberedUsername(): String? {
        return rememberedUsername
    }

    /**
     * Obtiene la contraseña recordada.
     * 
     * @return La contraseña recordada o null si no hay ninguna.
     */
    fun getRememberedPassword(): String? {
        return rememberedPassword
    }

    /**
     * Comprueba si hay credenciales recordadas.
     * 
     * @return true si hay credenciales recordadas, false en caso contrario.
     */
    fun hasRememberedCredentials(): Boolean {
        return rememberMe && rememberedUsername != null && rememberedPassword != null
    }

    /**
     * Limpia las credenciales recordadas.
     */
    fun clearCredentials() {
        rememberedUsername = null
        rememberedPassword = null
        rememberMe = false
    }
}
