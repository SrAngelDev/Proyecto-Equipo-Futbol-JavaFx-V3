package srangeldev.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import srangeldev.proyectoequipofutboljavafx.newteam.models.Entrenador

@Serializable
data class PersonalJsonDto(
    @SerialName("id")
    val id: Int,
    @SerialName("nombre")
    val nombre: String,
    @SerialName("apellidos")
    val apellidos: String,
    @SerialName("fecha_nacimiento")
    val fechaNacimiento: String,
    @SerialName("fecha_incorporacion")
    val fechaIncorporacion: String,
    @SerialName("salario")
    val salario: Double = 0.0,
    @SerialName("pais")
    val pais: String,
    @SerialName("rol")
    val rol: String = "",
    @SerialName("especialidad")
    val especializacion: String = "",
    @SerialName("posicion")
    val posicion: String = "",
    @SerialName("dorsal")
    val dorsal: Int? = 0,
    @SerialName("altura")
    val altura: Double? = 0.0,
    @SerialName("peso")
    val peso: Double? = 0.0,
    @SerialName("goles")
    val goles: Int? = 0,
    @SerialName("partidos_jugados")
    val partidosJugados: Int? = 0,
)