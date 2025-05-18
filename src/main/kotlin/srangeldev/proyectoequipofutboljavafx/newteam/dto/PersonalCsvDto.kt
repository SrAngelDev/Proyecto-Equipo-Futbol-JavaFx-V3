package srangeldev.proyectoequipofutboljavafx.newteam.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalCsvDto(
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
    val paisOrigen: String,
    @SerialName("rol")
    val rol: String = "",
    @SerialName("especialidad")
    val especializacion: String = "",
    @SerialName("posicion")
    val posicion: String = "",
    @SerialName("dorsal")
    val dorsal: String = "",
    @SerialName("altura")
    val altura: String = "",
    @SerialName("peso")
    val peso: String = "",
    @SerialName("goles")
    val goles: String = "",
    @SerialName("partidos_jugados")
    val partidosJugados: String = "",
    @SerialName("imagen_url")
    val imagenUrl: String = ""
)
