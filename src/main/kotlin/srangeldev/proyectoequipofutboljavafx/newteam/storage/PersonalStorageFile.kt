package srangeldev.storage

import srangeldev.models.Personal
import java.io.File

/**
 * Interfaz para el almacenamiento de datos de personal en archivos.
 */
interface PersonalStorageFile {
    /**
     * Lee una lista de objetos Personal desde un archivo.
     *
     * @param file El archivo desde el cual leer los datos.
     * @return Una lista de objetos Personal leídos desde el archivo.
     */
    fun readFromFile(file: File): List<Personal>

    /**
     * Escribe una lista de objetos Personal en un archivo.
     *
     * @param personalList La lista de objetos Personal a escribir en el archivo.
     * @return El archivo donde se ha escrito la lista de objetos Personal.
     */
    fun writeToFile(personalList: List<Personal>)
}