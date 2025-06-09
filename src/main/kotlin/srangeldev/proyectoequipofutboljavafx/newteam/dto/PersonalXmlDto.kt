package srangeldev.proyectoequipofutboljavafx.newteam.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

@Serializable
@SerialName("personal")
data class PersonalXmlDto(
    @SerialName("id")
    val id: Int,
    @SerialName("tipo")
    @XmlElement(true)
    var tipo: String = "",
    @SerialName("nombre")
    @XmlElement(true)
    val nombre: String = "",
    @SerialName("apellidos")
    @XmlElement(true)
    val apellidos: String = "",
    @SerialName("fechaNacimiento")
    @XmlElement(true)
    val fechaNacimiento: String = "",
    @SerialName("fechaIncorporacion")
    @XmlElement(true)
    val fechaIncorporacion: String = "",
    @SerialName("salario")
    @XmlElement(true)
    val salario: Double = 0.0,
    @SerialName("pais")
    @XmlElement(true)
    val pais: String = "",
    @SerialName("especialidad")
    @XmlElement(true)
    var especialidad: String = "",
    @SerialName("posicion")
    @XmlElement(true)
    var posicion: String = "",
    @SerialName("dorsal")
    @XmlElement(true)
    var dorsal: String = "",
    @SerialName("altura")
    @XmlElement(true)
    var altura: String = "",
    @SerialName("peso")
    @XmlElement(true)
    var peso: String = "",
    @SerialName("goles")
    @XmlElement(true)
    var goles: String = "",
    @SerialName("partidosJugados")
    @XmlElement(true)
    var partidosJugados: String = "",
    @SerialName("imagenUrl")
    @XmlElement(true)
    val imagenUrl: String = ""
)
