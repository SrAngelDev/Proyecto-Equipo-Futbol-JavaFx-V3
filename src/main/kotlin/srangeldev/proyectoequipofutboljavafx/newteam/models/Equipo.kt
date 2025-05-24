package srangeldev.proyectoequipofutboljavafx.newteam.models

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Clase que representa a un equipo de fútbol.
 */
data class Equipo(
    val id: Int = 0,
    val nombre: String,
    val fechaFundacion: LocalDate,
    val escudoUrl: String = "",
    val ciudad: String,
    val estadio: String,
    val pais: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)