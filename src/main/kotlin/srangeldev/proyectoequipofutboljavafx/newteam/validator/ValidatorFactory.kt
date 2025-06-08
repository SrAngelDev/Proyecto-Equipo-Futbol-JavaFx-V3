package srangeldev.proyectoequipofutboljavafx.newteam.validator
import srangeldev.proyectoequipofutboljavafx.newteam.models.*

/**
 * Factory para crear validadores específicos según el tipo de objeto.
 */
object ValidatorFactory {
    /**
     * Obtiene el validador apropiado para el tipo de objeto especificado.
     * @param instance La instancia para la que se necesita un validador
     * @return Un validador para el tipo especificado
     * @throws IllegalArgumentException si no hay un validador disponible para el tipo especificado
     */
    @Suppress("UNCHECKED_CAST")
    fun getValidator(instance: Any): Validator<*> {
        requireNotNull(instance) { "La instancia no puede ser nula" }

        return when(instance) {
            is Jugador -> JugadorValidator()
            is Convocatoria -> ConvocatoriaValidator()
            is Entrenador -> EntrenadorValidator()
            is Personal -> PersonalValidator()
            is User -> UserValidator()
            else -> throw IllegalArgumentException("No hay un validador disponible para el tipo: ${instance::class.qualifiedName}")
        }
    }

    /**
     * Obtiene el validador apropiado para la clase especificada.
     * @param clazz La clase para la que se necesita un validador
     * @return Un validador para el tipo especificado
     * @throws IllegalArgumentException si no hay un validador disponible para el tipo especificado
     */
    fun getValidator(clazz: Class<*>): Validator<*> {
        return when(clazz) {
            Jugador::class.java -> JugadorValidator()
            Convocatoria::class.java -> ConvocatoriaValidator()
            Entrenador::class.java -> EntrenadorValidator()
            Personal::class.java -> PersonalValidator()
            User::class.java -> UserValidator()
            else -> throw IllegalArgumentException("No hay un validador disponible para el tipo: ${clazz.name}")
        }
    }

    /**
     * Valida un objeto utilizando el validador apropiado.
     * @param item El objeto a validar
     * @throws IllegalArgumentException si no hay un validador disponible para el tipo
     * @throws Exception Si la validación falla
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> validate(item: T) {
        val validator = getValidator(item) as Validator<T>
        validator.validate(item)
    }
}