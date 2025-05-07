package srangeldev.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class que representa un equipo de personal.
 *
 * @param equipo La lista de objetos PersonalDto que componen el equipo.
 */
@Serializable
@SerialName("equipo")
data class EquipoDtoCsv(
    @SerialName("personal")
    val equipo: List<PersonalCsvDto>
)