package srangeldev.proyectoequipofutboljavafx.newteam.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

/**
 * Data class que representa un equipo de personal.
 *
 * @param equipo La lista de objetos PersonalDto que componen el equipo.
 */
@Serializable
@SerialName("equipo")
data class EquipoDtoXml(
    @SerialName("personal")
    @XmlElement(true)
    val equipo: List<PersonalXmlDto>
)