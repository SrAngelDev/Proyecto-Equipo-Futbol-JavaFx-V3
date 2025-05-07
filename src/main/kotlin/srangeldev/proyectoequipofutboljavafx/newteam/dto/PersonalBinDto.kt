package srangeldev.dto

data class PersonalBinDto(
    val id: Int,
    val nombre: String,
    val apellidos: String,
    val fechaNacimiento: String,
    val fechaIncorporacion: String,
    val salario: Double = 0.0,
    val pais: String,
    val rol: String,
    val especialidad: String = "",
    val posicion: String = "",
    val dorsal: Int = 0,
    val altura: Double = 0.0,
    val peso: Double = 0.0,
    val goles: Int = 0,
    val partidosJugados: Int = 0
)