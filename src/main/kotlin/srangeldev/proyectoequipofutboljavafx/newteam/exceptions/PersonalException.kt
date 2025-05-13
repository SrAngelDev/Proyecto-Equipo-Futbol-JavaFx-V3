package srangeldev.exceptions

/**
 * Clase sellada que representa excepciones relacionadas con el personal.
 *
 * @param message El mensaje de la excepción.
 */
sealed class PersonalException(message: String) : Exception(message) {

    /**
     * Excepción lanzada cuando no se encuentra una persona con el ID especificado.
     *
     * @param id El ID de la persona no encontrada.
     */
    class PersonalNotFoundException(id: Int) : PersonalException("Persona no encontrada con id: $id")

    /**
     * Excepción lanzada cuando ocurre un error en el almacenamiento de datos de personal.
     *
     * @param message El mensaje de error.
     */
    class PersonalStorageException(message: String) : PersonalException("Error en el almacenamiento: $message")

    /**
     * Excepción lanzada cuando los datos de personal no son válidos.
     *
     * @param message El mensaje de error de validación.
     */
    class PersonalValidationException(message: String) : PersonalException("Error de validación: $message")
}
