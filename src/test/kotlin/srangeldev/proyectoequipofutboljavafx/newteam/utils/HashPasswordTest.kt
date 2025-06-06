package srangeldev.proyectoequipofutboljavafx.newteam.utils

import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class HashPasswordTest {

    @Test
    fun `print hashed passwords for data sql`() {
        val adminPassword = "admin"
        val userPassword = "user"
        
        val adminHash = BCryptUtil.hashPassword(adminPassword)
        val userHash = BCryptUtil.hashPassword(userPassword)
        
        println("[DEBUG_LOG] Hashed password for 'admin': $adminHash")
        println("[DEBUG_LOG] Hashed password for 'user': $userHash")
        println("[DEBUG_LOG] Current timestamp: ${LocalDateTime.now()}")
    }
}