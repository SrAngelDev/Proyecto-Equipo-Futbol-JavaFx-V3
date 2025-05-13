package srangeldev.utils

import java.security.MessageDigest
import java.util.Base64

/**
 * Utilidad para el cifrado y verificación de contraseñas.
 * 
 * Nota: Esta es una implementación simplificada para demostración.
 * En un entorno de producción, se debería usar una biblioteca de BCrypt real.
 */
object BCryptUtil {
    
    /**
     * Genera un hash de la contraseña proporcionada.
     * 
     * @param password La contraseña a cifrar.
     * @return El hash de la contraseña.
     */
    fun hashPassword(password: String): String {
        // En una implementación real, se usaría BCrypt.hashpw(password, BCrypt.gensalt())
        // Esta es una implementación simplificada para demostración
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return Base64.getEncoder().encodeToString(digest)
    }
    
    /**
     * Verifica si una contraseña coincide con un hash.
     * 
     * @param password La contraseña a verificar.
     * @param hash El hash con el que comparar.
     * @return true si la contraseña coincide con el hash, false en caso contrario.
     */
    fun checkPassword(password: String, hash: String): Boolean {
        // En una implementación real, se usaría BCrypt.checkpw(password, hash)
        // Esta es una implementación simplificada para demostración
        return hashPassword(password) == hash
    }
}