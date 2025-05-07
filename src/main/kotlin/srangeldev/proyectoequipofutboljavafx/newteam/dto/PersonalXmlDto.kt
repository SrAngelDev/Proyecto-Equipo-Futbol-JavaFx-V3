package srangeldev.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import org.lighthousegames.logging.logging

@Serializable
@SerialName("personal")
data class PersonalXmlDto(
    @SerialName("id")
    val id: Int,
    @SerialName("tipo")
    @XmlElement(true)
    val tipo: String = "",
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
    val especialidad: String = "",
    @SerialName("posicion")
    @XmlElement(true)
    val posicion: String = "",
    @SerialName("dorsal")
    @XmlElement(true)
    val dorsal: String = "",
    @SerialName("altura")
    @XmlElement(true)
    val altura: String = "",
    @SerialName("peso")
    @XmlElement(true)
    val peso: String = "",
    @SerialName("goles")
    @XmlElement(true)
    val goles: String = "",
    @SerialName("partidosJugados")
    @XmlElement(true)
    val partidosJugados: String = ""
)