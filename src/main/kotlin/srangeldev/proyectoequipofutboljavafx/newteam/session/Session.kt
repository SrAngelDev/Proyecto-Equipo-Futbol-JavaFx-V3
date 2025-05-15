package srangeldev.session

import srangeldev.proyectoequipofutboljavafx.newteam.models.User

/**
 * Clase singleton que gestiona la sesión del usuario actual.
 */
object Session {
    private var currentUser: User? = null
    
    /**
     * Establece el usuario actual.
     * 
     * @param user El usuario a establecer como actual.
     */
    fun setCurrentUser(user: User) {
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
     * Comprueba si hay un usuario autenticado.
     * 
     * @return true si hay un usuario autenticado, false en caso contrario.
     */
    fun isAuthenticated(): Boolean {
        return currentUser != null
    }
    
    /**
     * Cierra la sesión del usuario actual.
     */
    fun logout() {
        currentUser = null
    }
}