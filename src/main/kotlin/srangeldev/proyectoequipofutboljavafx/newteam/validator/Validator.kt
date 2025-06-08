package srangeldev.proyectoequipofutboljavafx.newteam.validator

/**
 * Interface genérica para validadores.
 * Define el contrato que deben cumplir todos los validadores específicos.
 * @param T El tipo de objeto que se va a validar
 */
interface Validator<T> {
    /**
     * Valida un objeto del tipo especificado.
     * @param item El objeto a validar
     * @throws Exception Si la validación falla
     */
    fun validate(item: T)
}