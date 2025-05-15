package srangeldev.service

import srangeldev.proyectoequipofutboljavafx.newteam.models.Personal
import srangeldev.storage.FileFormat

/**
 * Interfaz para el servicio de gestión de personal.
 */
interface PersonalService {

    /**
     * Importa una lista de objetos Personal desde un archivo en el formato especificado.
     *
     * @param filePath La ruta del archivo desde el cual importar los datos.
     * @param format El formato del archivo (por defecto JSON).
     */
    fun importFromFile(filePath: String, format: FileFormat = FileFormat.JSON)

    /**
     * Exporta una lista de objetos Personal a un archivo en el formato especificado.
     *
     * @param filePath La ruta del archivo en el cual exportar los datos.
     * @param format El formato del archivo (por defecto JSON).
     */
    fun exportToFile(filePath: String, format: FileFormat = FileFormat.JSON)

    /**
     * Obtiene una lista de todos los objetos Personal.
     *
     * @return Una lista de todos los objetos Personal.
     */
    fun getAll(): List<Personal>

    /**
     * Obtiene un objeto Personal por su ID.
     *
     * @param id El ID del objeto Personal a obtener.
     * @return El objeto Personal con el ID especificado, o null si no se encuentra.
     */
    fun getById(id: Int): Personal?

    /**
     * Guarda un objeto Personal.
     *
     * @param personal El objeto Personal a guardar.
     * @return El objeto Personal guardado.
     */
    fun save(personal: Personal): Personal

    /**
     * Actualiza un objeto Personal por su ID.
     *
     * @param id El ID del objeto Personal a actualizar.
     * @param personal El objeto Personal con los datos actualizados.
     * @return El objeto Personal actualizado, o null si no se encuentra.
     */
    fun update(id: Int, personal: Personal): Personal?

    /**
     * Elimina un objeto Personal por su ID.
     *
     * @param id El ID del objeto Personal a eliminar.
     * @return El objeto Personal eliminado.
     */
    fun delete(id: Int): Personal
}